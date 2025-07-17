package org.pom.utils.seleniumutils;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.FileChannel;
import java.nio.channels.FileLock;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.ReentrantLock;

public class XPathCache {

    private static final String FOLDER_PATH = "optimized_xpaths";
    private static final ObjectMapper objectMapper = new ObjectMapper();
    private static final Map<String, Map<String, String>> moduleCaches = new ConcurrentHashMap<>();

    // Private constructor to hide the implicit public one
    private XPathCache() {
        // Prevents instantiation
    }

    // Ensure the folder exists
    static {
        File folder = new File(FOLDER_PATH);
        if (!folder.exists()) {
            folder.mkdirs(); // Create the folder if it doesn't exist
        }
    }

    private static String generateKey(String testMethodName, String elementDescription) {
        return testMethodName + "(" + elementDescription + ")";
    }

    private static final Map<String, ReentrantLock> fileLocks = new ConcurrentHashMap<>();

    private static ReentrantLock getLockForModule(String moduleName) {
        return fileLocks.computeIfAbsent(moduleName, k -> new ReentrantLock());
    }


    public static void storeOptimizedXPath(String moduleName, String testMethodName, String elementDescription, String optimizedXPath) {
        ReentrantLock lock = getLockForModule(moduleName);
        lock.lock();
        try {
            String key = generateKey(testMethodName, elementDescription);
            Map<String, String> moduleCache = getModuleCache(moduleName);
            if (optimizedXPath == null) {
                moduleCache.remove(key);
            } else {
                moduleCache.put(key, optimizedXPath);
            }
            saveCache(moduleName, moduleCache);
        } finally {
            lock.unlock();
        }
    }

    public static String getOptimizedXPath(String moduleName, String testMethodName, String elementDescription) {
        ReentrantLock lock = getLockForModule(moduleName);
        lock.lock();
        try {
            String key = generateKey(testMethodName, elementDescription);
            Map<String, String> moduleCache = getModuleCache(moduleName);
            return moduleCache.get(key);
        } finally {
            lock.unlock();
        }
    }

    private static Map<String, String> getModuleCache(String moduleName) {
        return moduleCaches.computeIfAbsent(moduleName, k -> loadCache(moduleName));
    }

    private static Map<String, String> loadCache(String moduleName) {
        String filePath = FOLDER_PATH + File.separator + moduleName + ".json";
        File file = new File(filePath);
        if (!file.exists()) {
            return new ConcurrentHashMap<>(); // Return empty cache if file doesn't exist
        }
        try (FileInputStream fis = new FileInputStream(filePath)) {
            return objectMapper.readValue(fis, new TypeReference<Map<String, String>>() {});
        } catch (IOException e) {
            e.printStackTrace();
            return new ConcurrentHashMap<>(); // Return empty cache on error
        }
    }

    private static void saveCache(String moduleName, Map<String, String> cache) {
        String filePath = FOLDER_PATH + File.separator + moduleName + ".json";
        File file = new File(filePath);
        try {
            // Ensure the parent directory exists (redundant due to static block, but safe)
            File parentDir = file.getParentFile();
            if (!parentDir.exists()) {
                parentDir.mkdirs();
            }
            try (FileOutputStream fos = new FileOutputStream(filePath);
                 FileChannel channel = fos.getChannel();
                 FileLock lock = channel.lock()) {
                objectMapper.writeValue(fos, cache); // Write or overwrite the JSON file
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}