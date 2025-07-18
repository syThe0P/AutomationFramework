package org.pom.utils;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Base64;
import org.json.JSONArray;
import org.json.JSONObject;
import org.testng.ITestContext;
import org.testng.ITestListener;
import org.testng.ITestResult;

public class BugReporter implements ITestListener {

    private static final String JIRA_DOMAIN = ConfigLoader.get("jira.domain");
    private static final String JIRA_EMAIL = ConfigLoader.get("jira.email");
    private static final String JIRA_API_TOKEN = ConfigLoader.get("jira.api.token");
    private static final String PROJECT_KEY = ConfigLoader.get("jira.project.key");
    private static final String EPIC_LINK = ConfigLoader.get("jira.epic.link");
    private static final String ATTRIBUTION_CATEGORY = ConfigLoader.get("jira.attribution.category");
    private static BugReporter instance;

    public BugReporter() {}

    public static BugReporter getInstance() {
        if (instance == null) instance = new BugReporter();
        return instance;
    }

    @Override
    public void onTestFailure(ITestResult result) {
        reportIfTestFailed(result);
    }

    public static void reportIfTestFailed(ITestResult result) {
        if (result.getStatus() == ITestResult.FAILURE) {
            String summary = result.getMethod().getMethodName() + " - Failure step: " +
                    (result.getThrowable() != null ? result.getThrowable().getMessage() : "Unknown error");
            String description = "Failure cause: " +
                    (result.getThrowable() != null ? result.getThrowable().toString() : "Unknown");
            createJiraBug(summary, description);
        }
    }

    public static void createJiraBug(String summary, String description) {
        try {
            String auth = JIRA_EMAIL + ":" + JIRA_API_TOKEN;
            String encodedAuth = Base64.getEncoder().encodeToString(auth.getBytes());

            String environment = null;
            try {
                environment = org.pom.utils.ConfigLoader.get("ENVIRONMENT");
            } catch (Exception e) {
                environment = "PROD";
            }

            String jiraEnvironment = environment;
            if (environment != null) {
                if (environment.equalsIgnoreCase("PROD")) {
                    jiraEnvironment = "Production";
                } else if (environment.equalsIgnoreCase("STAGING")) {
                    jiraEnvironment = "Staging";
                }
            }

            JSONObject project = new JSONObject();
            project.put("key", PROJECT_KEY);

            JSONObject issueType = new JSONObject();
            issueType.put("name", "Bug");

            JSONObject adfDescription = new JSONObject();
            adfDescription.put("type", "doc");
            adfDescription.put("version", 1);
            adfDescription.put("content", new JSONArray()
                .put(new JSONObject()
                    .put("type", "paragraph")
                    .put("content", new JSONArray()
                        .put(new JSONObject()
                            .put("type", "text")
                            .put("text", description)
                        )
                    )
                )
            );

            JSONObject fields = new JSONObject();
            fields.put("project", project);
            fields.put("issuetype", issueType);
            fields.put("summary", summary);
            fields.put("description", adfDescription);
            fields.put("customfield_11494", new JSONObject().put("value", jiraEnvironment));
            fields.put("customfield_11512", new JSONObject().put("value", ATTRIBUTION_CATEGORY));
            JSONArray components = new JSONArray();
            components.put(new JSONObject().put("name", "QA Automation"));
            fields.put("components", components);
            fields.put("priority", new JSONObject().put("name", "Major"));
            // Epic Link is set after creation

            JSONObject issue = new JSONObject();
            issue.put("fields", fields);

            HttpURLConnection conn = (HttpURLConnection) new URL(JIRA_DOMAIN + "/rest/api/3/issue").openConnection();
            conn.setRequestMethod("POST");
            conn.setDoOutput(true);
            conn.setRequestProperty("Authorization", "Basic " + encodedAuth);
            conn.setRequestProperty("Content-Type", "application/json");

            try (OutputStream os = conn.getOutputStream()) {
                os.write(issue.toString().getBytes());
                os.flush();
            }

            int responseCode = conn.getResponseCode();
            BufferedReader br = new BufferedReader(new InputStreamReader(
                responseCode >= 200 && responseCode < 300 ? conn.getInputStream() : conn.getErrorStream()
            ));
            StringBuilder response = new StringBuilder();
            String line;
            while ((line = br.readLine()) != null) {
                response.append(line);
            }
            br.close();

            if (responseCode == 201) {
                JSONObject respJson = new JSONObject(response.toString());
                String issueKey = respJson.optString("key");
                String issueUrl = JIRA_DOMAIN + "/browse/" + issueKey;
                System.out.println("Jira bug created successfully: [" + issueKey + "](" + issueUrl + ")");
                linkIssueToEpic(issueKey, EPIC_LINK);
            } else {
                System.out.println("Failed to create Jira bug. Status: " + responseCode);
                System.out.println("Response: " + response);
            }
        } catch (Exception e) {
            System.err.println("Error creating Jira bug: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public static void linkIssueToEpic(String issueKey, String epicKey) {
        try {
            String auth = JIRA_EMAIL + ":" + JIRA_API_TOKEN;
            String encodedAuth = Base64.getEncoder().encodeToString(auth.getBytes());
            String urlStr = JIRA_DOMAIN + "/rest/api/3/issue/" + issueKey;

            JSONObject parent = new JSONObject();
            parent.put("key", epicKey);

            JSONObject fields = new JSONObject();
            fields.put("parent", parent);

            JSONObject payload = new JSONObject();
            payload.put("fields", fields);

            HttpURLConnection conn = (HttpURLConnection) new URL(urlStr).openConnection();
            conn.setRequestMethod("PUT");
            conn.setDoOutput(true);
            conn.setRequestProperty("Authorization", "Basic " + encodedAuth);
            conn.setRequestProperty("Content-Type", "application/json");

            try (OutputStream os = conn.getOutputStream()) {
                os.write(payload.toString().getBytes());
                os.flush();
            }

            int responseCode = conn.getResponseCode();
            BufferedReader br = new BufferedReader(new InputStreamReader(
                responseCode >= 200 && responseCode < 300 ? conn.getInputStream() : conn.getErrorStream()
            ));
            StringBuilder response = new StringBuilder();
            String line;
            while ((line = br.readLine()) != null) {
                response.append(line);
            }
            br.close();

            if (responseCode == 204) {
                System.out.println("Issue " + issueKey + " successfully linked to Epic " + epicKey + " via parent field");
            } else {
                System.out.println("Failed to link issue to Epic via parent. Status: " + responseCode);
                System.out.println("Response: " + response);
            }
        } catch (Exception e) {
            System.err.println("Error linking issue to Epic via parent: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @Override public void onTestStart(ITestResult result) {}
    @Override public void onTestSuccess(ITestResult result) {}
    @Override public void onTestSkipped(ITestResult result) {}
    @Override public void onTestFailedButWithinSuccessPercentage(ITestResult result) {}
    @Override public void onTestFailedWithTimeout(ITestResult result) {}
    @Override public void onStart(ITestContext context) {}
    @Override public void onFinish(ITestContext context) {}
}
