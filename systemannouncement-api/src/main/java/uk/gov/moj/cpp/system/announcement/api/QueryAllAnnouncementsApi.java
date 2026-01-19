package uk.gov.moj.cpp.system.announcement.api;

import uk.gov.justice.services.common.converter.ObjectToJsonObjectConverter;
import uk.gov.justice.services.core.annotation.CustomServiceComponent;
import uk.gov.justice.services.core.annotation.Handles;
import uk.gov.justice.services.messaging.JsonEnvelope;
import uk.gov.moj.cpp.system.announcement.domain.common.SystemAnnouncement;
import uk.gov.moj.cpp.system.announcement.persistence.entity.SystemAnnouncementEntity;
import uk.gov.moj.cpp.system.announcement.persistence.repository.SystemAnnouncementRepository;

import javax.inject.Inject;
import uk.gov.justice.services.messaging.JsonObjects;
import javax.json.JsonArrayBuilder;
import javax.json.JsonObjectBuilder;
import java.util.List;

import static uk.gov.justice.services.messaging.JsonEnvelope.envelopeFrom;

@CustomServiceComponent("SystemAnnouncement.API")
public class QueryAllAnnouncementsApi {
    @Inject
    private SystemAnnouncementRepository systemAnnouncementRepository;
    @Inject
    private ObjectToJsonObjectConverter objectToJsonObjectConverter;

    @Handles("systemannouncement.get-all-announcements")
    public JsonEnvelope getAllSystemAnnouncements(final JsonEnvelope envelope) {
        List<SystemAnnouncementEntity> result = systemAnnouncementRepository.findSystemAnnouncements();

        JsonArrayBuilder jsonArrayBuilder = JsonObjects.createArrayBuilder();
        List<SystemAnnouncement> systemAnnouncementList = systemAnnouncementRepository.sortSystemAnnouncements(result);
        systemAnnouncementList.stream().forEach(a -> jsonArrayBuilder.add(objectToJsonObjectConverter.convert(a)));

        final JsonObjectBuilder jsonObjectBuilder = JsonObjects.createObjectBuilder();
        jsonObjectBuilder.add("systemAnnouncements", jsonArrayBuilder);

        return envelopeFrom(envelope.metadata(), jsonObjectBuilder.build());
    }
}
