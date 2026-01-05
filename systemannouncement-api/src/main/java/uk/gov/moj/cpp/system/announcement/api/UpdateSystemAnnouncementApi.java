package uk.gov.moj.cpp.system.announcement.api;

import static uk.gov.justice.services.core.enveloper.Enveloper.envelop;

import uk.gov.justice.services.common.converter.JsonObjectToObjectConverter;
import uk.gov.justice.services.core.annotation.CustomServiceComponent;
import uk.gov.justice.services.core.annotation.Handles;
import uk.gov.justice.services.messaging.Envelope;
import uk.gov.justice.services.messaging.JsonEnvelope;
import uk.gov.moj.cpp.system.announcement.api.service.SystemAnnouncementService;
import uk.gov.moj.cpp.system.announcement.domain.common.UpdateSystemAnnouncement;

import javax.inject.Inject;
import javax.json.JsonObject;

@CustomServiceComponent("SystemAnnouncement.API")
public class UpdateSystemAnnouncementApi {

    private static final String SYSTEM_ANNOUNCEMENT_UPDATED = "systemannouncement.system-announcement-updated";

    @Inject
    private SystemAnnouncementService systemAnnouncementService;

    @Inject
    private JsonObjectToObjectConverter jsonObjectToObjectConverter;

    @Handles("systemannouncement.update-system-announcement")
    public Envelope updateSystemAnnouncement(final JsonEnvelope envelope) {
        final JsonObject payload = envelope.payloadAsJsonObject();

        final UpdateSystemAnnouncement systemAnnouncement = jsonObjectToObjectConverter.convert(envelope.payloadAsJsonObject(), UpdateSystemAnnouncement.class);
        systemAnnouncementService.updateSystemAnnouncement(systemAnnouncement);

        return envelop(payload)
                .withName(SYSTEM_ANNOUNCEMENT_UPDATED)
                .withMetadataFrom(envelope);
    }
}