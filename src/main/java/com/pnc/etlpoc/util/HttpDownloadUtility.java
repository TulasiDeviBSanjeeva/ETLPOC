package com.pnc.etlpoc.util;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.RandomStringUtils;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.Optional;

/**
 * A utility that downloads a file from a URL.
 */
@Slf4j
public class HttpDownloadUtility {

    public static final String RESOURCE_DIR = "/data/";
    public static final String PREFIX = "speaker-data";
    public static final String EXT = ".csv";

    /**
     * Downloads a file from a URL
     *
     * @param resourceUrl HTTP URL of the file to be downloaded
     * @return downloaded resource name
     */
    public static Optional<String> getResourceAtUrl(String resourceUrl) {
        File resourceFile = createResourceFileName();
        try {
            URL url = new URL(resourceUrl);
            // Copy bytes from the URL to the destination file.
            FileUtils.copyURLToFile(url, resourceFile);
        } catch (IOException e) {
            log.error("URL Resource : " + resourceUrl + " NOT found or corrupted." + e);
            return Optional.empty();
        }
        return Optional.of(resourceFile.getName());
    }

    private static File createResourceFileName() {
        String resourcePath = HttpDownloadUtility.class.getClassLoader().getResource(".").getFile();
        String pathname = resourcePath + RESOURCE_DIR + PREFIX + RandomStringUtils.randomAlphanumeric(16) + EXT;
        return new File(pathname);
    }

}
