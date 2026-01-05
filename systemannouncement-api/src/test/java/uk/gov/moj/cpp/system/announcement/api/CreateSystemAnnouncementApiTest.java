package uk.gov.moj.cpp.system.announcement.api;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static uk.gov.justice.services.messaging.JsonEnvelope.envelopeFrom;
import static uk.gov.justice.services.test.utils.core.matchers.HandlerMatcher.isCustomHandler;
import static uk.gov.justice.services.test.utils.core.matchers.HandlerMethodMatcher.method;
import static uk.gov.justice.services.test.utils.core.messaging.MetadataBuilderFactory.metadataWithRandomUUID;

import uk.gov.justice.services.common.converter.JsonObjectToObjectConverter;
import uk.gov.justice.services.common.converter.StringToJsonObjectConverter;
import uk.gov.justice.services.messaging.Envelope;
import uk.gov.justice.services.messaging.JsonEnvelope;
import uk.gov.moj.cpp.system.announcement.api.service.SystemAnnouncementService;
import uk.gov.moj.cpp.system.announcement.api.util.FileUtil;
import uk.gov.moj.cpp.system.announcement.domain.common.Category;
import uk.gov.moj.cpp.system.announcement.domain.common.SystemAnnouncement;
import uk.gov.moj.cpp.system.announcement.domain.common.Type;

import java.time.LocalDate;
import java.time.LocalTime;

import javax.json.JsonObject;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class CreateSystemAnnouncementApiTest {
    private static final String SYSTEM_ANNOUNCEMENT_CREATE_SYSTEM_ANNOUNCEMENT = "systemannouncement.create-system-announcement";
    private static final String SYSTEM_ANNOUNCEMENT_REQUEST_CREATED = "systemannouncement.system-announcement-created";
    private static final String SYSTEMANNOUNCEMENT_API = "SystemAnnouncement.API";
    @InjectMocks
    private CreateSystemAnnouncementApi createSystemAnnouncementApi;
    @Mock
    private SystemAnnouncementService systemAnnouncementService;
    @Mock
    private JsonObjectToObjectConverter jsonObjectToObjectConverter;

    @Test
    public void shouldHaveCorrectHandlerMethods() throws Exception {
        assertThat(createSystemAnnouncementApi, isCustomHandler(SYSTEMANNOUNCEMENT_API)
                .with(method("createSystemAnnouncement").thatHandles("systemannouncement.create-system-announcement")));
    }

    @Test
    public void shouldCreateSystemAnnouncementRequest() {
        final String systemAnnouncementRequest = FileUtil.getFileContentsAsString("systemannouncement.create-system-announcement.json");
        final JsonObject payload = new StringToJsonObjectConverter().convert(systemAnnouncementRequest);

        SystemAnnouncement systemAnnouncement = new SystemAnnouncement.Builder()
                .createdBy("test user")
                .category(Category.UNPLANNED)
                .type(Type.CRITICAL)
                .startDate(LocalDate.parse("2022-02-02"))
                .endDate(LocalDate.parse("2022-02-03"))
                .startTime(LocalTime.parse("20:00:00"))
                .endTime(LocalTime.parse("21:00:00"))
                .title("title")
                .details("details")
                .build();

        final JsonEnvelope command = envelopeFrom(
                metadataWithRandomUUID(SYSTEM_ANNOUNCEMENT_CREATE_SYSTEM_ANNOUNCEMENT),
                payload);

        when(jsonObjectToObjectConverter.convert(payload, SystemAnnouncement.class)).thenReturn(systemAnnouncement);

        final Envelope result = createSystemAnnouncementApi.createSystemAnnouncement(command);
        verify(systemAnnouncementService).createSystemAnnouncement(any(SystemAnnouncement.class));
        assertThat(result.metadata().name(), is(SYSTEM_ANNOUNCEMENT_REQUEST_CREATED));
    }
}