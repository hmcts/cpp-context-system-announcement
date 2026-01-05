package uk.gov.moj.cpp.system.announcement.helper;

import com.google.common.base.Joiner;
import org.junit.jupiter.api.extension.ExtendWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uk.gov.justice.services.common.http.HeaderConstants;
import uk.gov.justice.services.integrationtest.utils.jms.JmsResourceManagementExtension;
import uk.gov.justice.services.test.utils.core.rest.RestClient;

import javax.ws.rs.core.MultivaluedHashMap;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.Response;
import java.util.UUID;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

@SuppressWarnings("WeakerAccess")
@ExtendWith(JmsResourceManagementExtension.class)
public abstract class AbstractTestHelper {

    private static final Logger LOGGER = LoggerFactory.getLogger(AbstractTestHelper.class);

    public static final String USER_ID = UUID.randomUUID().toString();
    private static final String HOST = System.getProperty("INTEGRATION_HOST_KEY", "localhost");
    protected static final String BASE_URI = System.getProperty("baseUri", "http://" + HOST + ":8080");
    private static final String WRITE_BASE_URL = "/progression-service/command/api/rest/progression";
    private static final String READ_BASE_URL = "/progression-service/query/api/rest/progression";

    protected final RestClient restClient = new RestClient();

    public static String getWriteUrl(final String resource) {
        return Joiner.on("").join(BASE_URI, WRITE_BASE_URL, resource);
    }

    public static String getReadUrl(final String resource) {
        return Joiner.on("").join(BASE_URI, READ_BASE_URL, resource);
    }

    static {
        doAllStubbing();
    }

    public static void doAllStubbing() {
    }

    protected void makePostCall(final String url, final String mediaType, final String payload) {
        makePostCall(url, mediaType, payload, Response.Status.ACCEPTED.getStatusCode());
    }

    protected void makePostCall(final String url, final String mediaType, final String payload, final int statusCode) {
        makePostCall(UUID.fromString(USER_ID), url, mediaType, payload, statusCode);
    }

    protected void makePostCall(final UUID userId, final String url, final String mediaType, final String payload, final int statusCode) {
        LOGGER.info("Post call made: \n\n\tURL = {} \n\tMedia type = {} \n\tPayload = {}\n\nUser ID = {}", url, mediaType, payload, USER_ID);
        final MultivaluedMap<String, Object> map = new MultivaluedHashMap<>();
        map.add(HeaderConstants.USER_ID, userId.toString());
        final Response response = restClient.postCommand(url, mediaType, payload, map);
        assertThat(response.getStatus(), is(statusCode));
    }
}
