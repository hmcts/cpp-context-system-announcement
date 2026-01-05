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
import uk.gov.moj.cpp.system.announcement.domain.common.Category;
import uk.gov.moj.cpp.system.announcement.domain.common.Type;
import uk.gov.moj.cpp.system.announcement.domain.common.UpdateSystemAnnouncement;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.UUID;

import javax.json.JsonObject;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class UpdateSystemAnnouncementApiTest {
    private static final String SYSTEM_ANNOUNCEMENT_UPDATED = "systemannouncement.system-announcement-updated";
    private static final String SYSTEMANNOUNCEMENT_API = "SystemAnnouncement.API";
    @InjectMocks
    private UpdateSystemAnnouncementApi updateSystemAnnouncementApi;
    @Mock
    private SystemAnnouncementService systemAnnouncementService;
    @Mock
    private JsonObjectToObjectConverter jsonObjectToObjectConverter;

    @Test
    public void shouldHandleUpdateSystemAnnouncementCommand() {
        assertThat(updateSystemAnnouncementApi, isCustomHandler(SYSTEMANNOUNCEMENT_API)
                .with(method("updateSystemAnnouncement")
                        .thatHandles("systemannouncement.update-system-announcement")));
    }

    @Test
    public void shouldUpdateSystemAnnouncement() {
        final String systemAnnouncementRequest = "{\"systemAnnouncementId\":\"123e4567-e89b-12d3-a456-426614174000\",\"createdBy\":\"test user\",\"category\":\"test category\",\"type\":\"test type\",\"startDate\":\"2022-02-02\",\"endDate\":\"2022-02-03\",\"startTime\":\"20:00:00\",\"endTime\":\"21:00:00\",\"title\":\"title\",\"details\":\"details\"}";
        final JsonObject payload = new StringToJsonObjectConverter().convert(systemAnnouncementRequest);

        UpdateSystemAnnouncement systemAnnouncement = new UpdateSystemAnnouncement.Builder()
                .systemAnnouncementId(UUID.fromString("123e4567-e89b-12d3-a456-426614174000"))
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
                metadataWithRandomUUID("systemannouncement.update-system-announcement"),
                payload);

        when(jsonObjectToObjectConverter.convert(payload, UpdateSystemAnnouncement.class)).thenReturn(systemAnnouncement);

        final Envelope result = updateSystemAnnouncementApi.updateSystemAnnouncement(command);
        verify(systemAnnouncementService).updateSystemAnnouncement(any(UpdateSystemAnnouncement.class));
        assertThat(result.metadata().name(), is(SYSTEM_ANNOUNCEMENT_UPDATED));
    }

    @Test
    public void shouldHandleInvalidPayload() {
        final JsonObject payload = new StringToJsonObjectConverter().convert("{\"invalidField\":\"invalidValue\"}");
        final JsonEnvelope command = envelopeFrom(
                metadataWithRandomUUID("systemannouncement.update-system-announcement"),
                payload);

        when(jsonObjectToObjectConverter.convert(payload, UpdateSystemAnnouncement.class)).thenThrow(new IllegalArgumentException("Invalid payload"));

        try {
            updateSystemAnnouncementApi.updateSystemAnnouncement(command);
        } catch (IllegalArgumentException e) {
            assertThat(e.getMessage(), is("Invalid payload"));
        }
    }
}