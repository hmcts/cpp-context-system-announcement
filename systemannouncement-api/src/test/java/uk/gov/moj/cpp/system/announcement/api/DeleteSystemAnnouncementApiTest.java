package uk.gov.moj.cpp.system.announcement.api;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.verify;
import static uk.gov.justice.services.messaging.JsonEnvelope.envelopeFrom;
import static uk.gov.justice.services.test.utils.core.matchers.HandlerMatcher.isCustomHandler;
import static uk.gov.justice.services.test.utils.core.matchers.HandlerMethodMatcher.method;
import static uk.gov.justice.services.test.utils.core.messaging.MetadataBuilderFactory.metadataWithRandomUUID;

import uk.gov.justice.services.common.converter.StringToJsonObjectConverter;
import uk.gov.justice.services.messaging.Envelope;
import uk.gov.justice.services.messaging.JsonEnvelope;
import uk.gov.moj.cpp.system.announcement.api.service.SystemAnnouncementService;

import java.util.UUID;

import javax.json.JsonObject;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class DeleteSystemAnnouncementApiTest {
    private static final String SYSTEM_ANNOUNCEMENT_DELETED = "systemannouncement.system-announcement-deleted";
    private static final String SYSTEMANNOUNCEMENT_API = "SystemAnnouncement.API";
    @InjectMocks
    private DeleteSystemAnnouncementApi deleteSystemAnnouncementApi;
    @Mock
    private SystemAnnouncementService systemAnnouncementService;

    @Test
    public void shouldHandleDeleteSystemAnnouncementByIdCommand() {
        assertThat(deleteSystemAnnouncementApi, isCustomHandler(SYSTEMANNOUNCEMENT_API)
                .with(method("deleteSystemAnnouncementById")
                        .thatHandles("systemannouncement.delete-system-announcement-by-id")));
    }

    @Test
    public void shouldDeleteSystemAnnouncementById() {
        final JsonObject payload = new StringToJsonObjectConverter().convert("{\"systemAnnouncementId\":\"123e4567-e89b-12d3-a456-426614174000\"}");
        final JsonEnvelope command = envelopeFrom(
                metadataWithRandomUUID("systemannouncement.delete-system-announcement-by-id"),
                payload);

        final Envelope result = deleteSystemAnnouncementApi.deleteSystemAnnouncementById(command);
        verify(systemAnnouncementService).deleteSystemAnnouncementById(UUID.fromString("123e4567-e89b-12d3-a456-426614174000"));
        assertThat(result.metadata().name(), is(SYSTEM_ANNOUNCEMENT_DELETED));
    }

    @Test
    public void shouldHandleDeleteExpiredSystemAnnouncementsCommand() {
        assertThat(deleteSystemAnnouncementApi, isCustomHandler(SYSTEMANNOUNCEMENT_API)
                .with(method("deleteExpiredSystemAnnouncements")
                        .thatHandles("systemannouncement.delete-expired-system-announcements")));
    }

    @Test
    public void shouldDeleteExpiredSystemAnnouncements() {
        final JsonObject payload = new StringToJsonObjectConverter().convert("{}");
        final JsonEnvelope command = envelopeFrom(
                metadataWithRandomUUID("systemannouncement.delete-expired-system-announcements"),
                payload);

        final Envelope result = deleteSystemAnnouncementApi.deleteExpiredSystemAnnouncements(command);
        verify(systemAnnouncementService).deleteExpiredSystemAnnouncements();
        assertThat(result.metadata().name(), is(SYSTEM_ANNOUNCEMENT_DELETED));
    }
}