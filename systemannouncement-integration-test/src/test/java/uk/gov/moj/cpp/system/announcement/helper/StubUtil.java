package uk.gov.moj.cpp.system.announcement.helper;

import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.get;
import static com.github.tomakehurst.wiremock.client.WireMock.stubFor;
import static com.github.tomakehurst.wiremock.client.WireMock.urlMatching;
import static java.util.UUID.randomUUID;
import static uk.gov.moj.cpp.system.announcement.util.FileUtil.getPayload;

/**
 * Class to set up stub.
 */
public class StubUtil {
    private static final int HTTP_STATUS_OK = 200;

    public static void setupUsersGroupQueryStub() {
        stubFor(get(urlMatching("/usersgroups-service/query/api/rest/usersgroups/users/.*"))
                .willReturn(aResponse().withStatus(HTTP_STATUS_OK)
                        .withHeader("CPPID", randomUUID().toString())
                        .withHeader("Content-Type", "application/json")
                        .withBody(getPayload("stub-data/usersgroups.get-groups-by-user.json"))));

    }
}