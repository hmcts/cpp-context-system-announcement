package uk.gov.moj.cpp.system.announcement.util;


import com.google.common.io.Resources;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.charset.Charset;

import static org.junit.jupiter.api.Assertions.fail;

public class TestUtils {

    private static final Logger LOGGER = LoggerFactory.getLogger(TestUtils.class);

    public static String getPayloadForCreatingRequest(final String path) {
        String request = null;
        try {
            request = Resources.toString(
                    Resources.getResource(path),
                    Charset.defaultCharset()
            );
        } catch (final Exception e) {
            LOGGER.error("Error consuming file from location {}", path);
            fail("Error consuming file from location " + path);
        }
        return request;
    }

}
