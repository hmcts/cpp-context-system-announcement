package uk.gov.moj.cpp.system.announcement.api;

import static uk.gov.justice.services.core.enveloper.Enveloper.envelop;

import uk.gov.justice.services.common.converter.JsonObjectToObjectConverter;
import uk.gov.justice.services.core.annotation.CustomServiceComponent;
import uk.gov.justice.services.core.annotation.Handles;
import uk.gov.justice.services.messaging.Envelope;
import uk.gov.justice.services.messaging.JsonEnvelope;
import uk.gov.moj.cpp.system.announcement.api.service.SystemAnnouncementService;
import uk.gov.moj.cpp.system.announcement.domain.common.SystemAnnouncement;

import javax.inject.Inject;
import javax.json.JsonObject;

@CustomServiceComponent("SystemAnnouncement.API")
public class CreateSystemAnnouncementApi {

    private static final String SYSTEM_ANNOUNCEMENT_CREATED = "systemannouncement.system-announcement-created";

    @Inject
    private SystemAnnouncementService systemAnnouncementService;

    @Inject
    private JsonObjectToObjectConverter jsonObjectToObjectConverter;

    @Handles("systemannouncement.create-system-announcement")
    public Envelope createSystemAnnouncement(final JsonEnvelope envelope) {
        final JsonObject payload = envelope.payloadAsJsonObject();
        final SystemAnnouncement systemAnnouncement = jsonObjectToObjectConverter.convert(envelope.payloadAsJsonObject(), SystemAnnouncement.class);
        systemAnnouncementService.createSystemAnnouncement(systemAnnouncement);

        return envelop(payload)
                .withName(SYSTEM_ANNOUNCEMENT_CREATED)
                .withMetadataFrom(envelope);
    }

}