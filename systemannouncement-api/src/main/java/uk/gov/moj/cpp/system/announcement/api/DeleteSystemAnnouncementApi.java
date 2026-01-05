package uk.gov.moj.cpp.system.announcement.api;

import uk.gov.justice.services.core.annotation.CustomServiceComponent;
import uk.gov.justice.services.core.annotation.Handles;
import uk.gov.justice.services.messaging.Envelope;
import uk.gov.justice.services.messaging.JsonEnvelope;
import uk.gov.moj.cpp.system.announcement.api.service.SystemAnnouncementService;

import javax.inject.Inject;
import javax.json.JsonObject;
import java.util.UUID;

import static java.util.UUID.fromString;
import static uk.gov.justice.services.core.enveloper.Enveloper.envelop;

@CustomServiceComponent("SystemAnnouncement.API")
public class DeleteSystemAnnouncementApi {

    private static final String SYSTEM_ANNOUNCEMENT_DELETED = "systemannouncement.system-announcement-deleted";
    private static final String SYSTEM_ANNOUNCEMENT_ID = "systemAnnouncementId";

    @Inject
    private SystemAnnouncementService systemAnnouncementService;

    @Handles("systemannouncement.delete-system-announcement-by-id")
    public Envelope deleteSystemAnnouncementById(final JsonEnvelope envelope) {
        final JsonObject payload = envelope.payloadAsJsonObject();
        final UUID systemAnnouncementId = fromString(payload.getString(SYSTEM_ANNOUNCEMENT_ID));
        systemAnnouncementService.deleteSystemAnnouncementById(systemAnnouncementId);

        return envelop(payload)
                .withName(SYSTEM_ANNOUNCEMENT_DELETED)
                .withMetadataFrom(envelope);
    }

    @Handles("systemannouncement.delete-expired-system-announcements")
    public Envelope deleteExpiredSystemAnnouncements(final JsonEnvelope envelope) {
        final JsonObject payload = envelope.payloadAsJsonObject();
        systemAnnouncementService.deleteExpiredSystemAnnouncements();

        return envelop(payload)
                .withName(SYSTEM_ANNOUNCEMENT_DELETED)
                .withMetadataFrom(envelope);
    }
}