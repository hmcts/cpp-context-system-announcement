package uk.gov.moj.cpp.system.announcement.util;

import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;

import static java.nio.charset.Charset.defaultCharset;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.IsNull.notNullValue;
import static org.junit.jupiter.api.Assertions.fail;

public final class FileUtil {
    private static final Logger LOGGER = LoggerFactory.getLogger(FileUtil.class);

    private FileUtil() {
    }

    public static String getPayload(final String path) {
        String fileContents = null;
        try (final InputStream inputStream = FileUtil.class.getClassLoader().getResourceAsStream(path)) {
            assertThat(inputStream, notNullValue());
            fileContents = IOUtils.toString(inputStream, defaultCharset());
        } catch (final Exception e) {
            LOGGER.error("Error consuming file from location {}", path, e);
            fail("Error consuming file from location " + path);
        }
        return fileContents;
    }


    public static BufferedReader getBufferedReader(final InputStreamReader inputStreamReader) {
        return new BufferedReader(inputStreamReader);
    }

    public static InputStreamReader getReader(final InputStream fileStream) {
        return new InputStreamReader(fileStream);
    }

    public static InputStream getResourceAsStream(String fileName) {
        return FileUtil.class.getClassLoader().getResourceAsStream(fileName);
    }
}
