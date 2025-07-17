package org.pom.utils;

import com.jayway.jsonpath.JsonPath;
import org.apache.commons.io.FileUtils;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import org.json.simple.JSONObject;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.logging.Level;

import static org.pom.base.BaseTest.logger;

public class IOUtils {

    private static IOUtils ioUtils;

    public static IOUtils getInstance() {
        if (ioUtils == null) {
            ioUtils = new IOUtils();
        }
        return ioUtils;
    }

    public String readAllTextFromFile(String filePath) {
        String text = "";
        try {
            text = new String(Files.readAllBytes(Paths.get(filePath)));
        } catch (Exception e) {
            logger.log(Level.INFO, "Exception occurred:-" + e);
        }
        return text;
    }

    public void cleanDirectory(String directoryPath) {
        try {
            FileUtils.cleanDirectory(new File(directoryPath));
        } catch (Exception e) {
            System.out.println("Unable to delete directory -> " + directoryPath + " due to exception occurred: " + e);
        }
    }

    public void writingInXmlFile(String xmlFilePath, String text) {
        FileOutputStream fileOutputStream = null;
        BufferedWriter file = null;
        try {
            File xmlfile = new File(xmlFilePath);
            fileOutputStream = new FileOutputStream(xmlfile);
            file = new BufferedWriter(new OutputStreamWriter(fileOutputStream));
            file.write(text);
        } catch (Exception e) {
            logger.log(Level.INFO, "Exception occurred:-" + e);
        } finally {
            try {
                if (file != null)
                    file.close();
                if (fileOutputStream != null)
                    fileOutputStream.close();
            } catch (Exception e) {
                logger.log(Level.INFO, "Exception occurred:-" + e);
            }
        }
    }

    public String readDataFromPdfFile(String filePath) {
        String text = "";
        PDDocument document = null;
        try {
            document = PDDocument.load(new File(filePath));
            PDFTextStripper pdfStripper = new PDFTextStripper();
            text = pdfStripper.getText(document);
        } catch (Exception e) {
            System.out.println("Exception occurred while reading PDF: " + e);
        } finally {
            if (document != null) {
                try {
                    document.close();
                } catch (Exception e) {
                    System.out.println("Exception occurred while closing PDF document: " + e);
                }
            }
        }
        return text;
    }

    public void writingDataIntoCsvFile(String fileDirectory, String[] finalData) {
        StringBuilder line = new StringBuilder();
        for (int i = 0; i < finalData.length; i++) {
            line.append("\"");
            line.append(finalData[i].replace("\"", "\"\""));
            line.append("\"");
            if (i != finalData.length - 1) {
                line.append(',');
            }
        }
        line.append("\n");
        File csvFile = new File(fileDirectory);
        try (FileWriter fileWriter = new FileWriter(csvFile, true)) {
            fileWriter.write(line.toString());
        } catch (Exception e) {
            logger.log(Level.WARNING, "Exception occurred:-" + e);
        }
    }

    public void writeJsonDataToJsonFile(String fileDirectory, JSONObject jsonObject) {
        File jsonFile = new File(fileDirectory);
        try (FileWriter file = new FileWriter(jsonFile)) {
            file.write(jsonObject.toJSONString());
        } catch (IOException e) {
            logger.log(Level.WARNING, "Exception occurred:-" + e);
        }
    }

    public void createDirectory(String directoryName) {
        Path path = Paths.get(directoryName);
        if (!Files.exists(path)) {
            try {
                Files.createDirectories(path);
            } catch (IOException e) {
                logger.log(Level.INFO, String.format("Error occurred while creating report directory: %s", e));
            }
        }
    }

    public Map<String, String> readDataFromJson(String jsonFilePath) {
        Map<String, String> linkedHashMap = new LinkedHashMap<>();
        try {
            Path path = Paths.get(jsonFilePath);
            if (path.toFile().exists()) {
                String json = readAllTextFromFile(jsonFilePath);
                linkedHashMap = JsonPath.read(json, "$");
            } else {
                String message = "File '" + jsonFilePath + "' does not exist.";
                logger.log(Level.WARNING, message);
            }
        } catch (Exception e) {
            String message = "Exception occurred: " + e + " Please check the file '" + jsonFilePath + "' exists with valid details.";
            logger.log(Level.WARNING, message);
        }
        return linkedHashMap;
    }

    public String readPdfText(String path) {
        String text = "";
        try {
            PDDocument document = PDDocument.load(new File(path));
            PDFTextStripper textStripper = new PDFTextStripper();
            textStripper.setStartPage(1);
            textStripper.setEndPage(document.getNumberOfPages());
            text = textStripper.getText(document);
            document.close();
        } catch (Exception e) {
            logger.log(Level.INFO, "Exception occurred:-" + e);
        }
        return text;
    }

    public void deleteFile(String filePath) {
        try {
            FileUtils.delete(new File(filePath));
        } catch (Exception e) {
            System.out.println("Unable to delete file -> " + filePath + " due to exception occurred: " + e);
        }
    }

    public boolean isFileExist(String filePath) {
        return Files.exists(Paths.get(filePath));
    }
}
