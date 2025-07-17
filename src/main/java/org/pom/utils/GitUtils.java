package org.pom.utils;

import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.Unirest;
import org.yaml.snakeyaml.Yaml;

import java.util.List;
import java.util.Map;
import java.util.logging.Level;

import static org.pom.base.BaseTest.logger;

public class GitUtils {

    public static GitUtils getInstance() {
        return new GitUtils();
    }

    public String getEmailByGithubHandle(String githubHandle) {
        String mappingDataUrl = "https://raw.githubusercontent.com/DTSL/tf-org-sync/main/data/users/users.yaml";
        Yaml yaml = new Yaml();
        try {
            HttpResponse<String> response = Unirest.get(mappingDataUrl)
                    .header("Authorization", "token " + System.getenv("GITHUB_TOKEN"))
                    .asString();
            if (response.getStatus() == 200) {
                String responseBody = response.getBody();
                Map<String, List<Map<String, String>>> data = yaml.load(responseBody);
                List<Map<String, String>> users = data.get("users");
                if (users != null) {
                    for (Map<String, String> user : users) {
                        if (githubHandle.equals(user.get("github_handle")) && user.get("email") != null) {
                            return user.get("email");
                        }
                    }
                }
            } else {
                logger.log(Level.INFO, "Failed to fetch data. HTTP Status: {0}", response.getStatus());
            }
        } catch (Exception e) {
            logger.log(Level.INFO, "An error occurred while fetching email by GitHub handle: {0}", e.getMessage());
        }
        return "";
    }
}