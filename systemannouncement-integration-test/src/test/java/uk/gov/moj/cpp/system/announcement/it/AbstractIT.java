package uk.gov.moj.cpp.system.announcement.it;

import org.junit.jupiter.api.extension.ExtendWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uk.gov.justice.services.integrationtest.utils.jms.JmsResourceManagementExtension;

import java.util.UUID;

import static com.github.tomakehurst.wiremock.client.WireMock.configureFor;
import static com.github.tomakehurst.wiremock.client.WireMock.reset;
import static java.util.UUID.randomUUID;
import static uk.gov.moj.cpp.system.announcement.helper.RestHelper.HOST;
import static uk.gov.moj.cpp.system.announcement.helper.StubUtil.setupUsersGroupQueryStub;
import static uk.gov.moj.cpp.system.announcement.util.WireMockStubUtils.setupAsAuthorisedUser;
import static uk.gov.moj.cpp.system.announcement.util.WireMockStubUtils.setupAsSystemUser;
import static uk.gov.moj.cpp.system.announcement.util.WireMockStubUtils.stubGetGroupsForLoggedInQuery;

@ExtendWith(JmsResourceManagementExtension.class)
public class AbstractIT {
    private static final Logger LOGGER = LoggerFactory.getLogger(AbstractIT.class);

    protected static final UUID USER_ID_VALUE = randomUUID();
    protected static final UUID USER_ID_VALUE_AS_ADMIN = randomUUID();

    /**
     * NOTE: this approach is employed to enabled massive savings in test execution test.
     * All tests will need to extend AbstractIT thus ensuring the static initialisation block is fired just once before any test runs
     * Mock reset and stub for all reference data happens once per VM.  If parallel test run is considered, this approach will be tweaked.
     */

    static {
        try {
            configureFor(HOST, 8080);
            reset(); // will need to be removed when things are being run in parallel
            defaultStubs();
        } catch (final Throwable e) {
            LOGGER.error("Failure during set up of integration test", e);
            throw e;
        }
    }

    protected static void defaultStubs() {
        setupAsAuthorisedUser(USER_ID_VALUE);
        setupAsSystemUser(USER_ID_VALUE_AS_ADMIN);
        setupUsersGroupQueryStub();
        stubGetGroupsForLoggedInQuery(USER_ID_VALUE.toString());
    }
}
