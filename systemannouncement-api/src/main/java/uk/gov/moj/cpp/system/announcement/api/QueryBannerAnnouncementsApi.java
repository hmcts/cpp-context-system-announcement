package uk.gov.moj.cpp.system.announcement.api;

import uk.gov.justice.services.common.converter.ObjectToJsonObjectConverter;
import uk.gov.justice.services.core.annotation.CustomServiceComponent;
import uk.gov.justice.services.core.annotation.Handles;
import uk.gov.justice.services.messaging.JsonEnvelope;
import uk.gov.moj.cpp.system.announcement.domain.common.SystemBannerAnnouncement;
import uk.gov.moj.cpp.system.announcement.persistence.entity.SystemAnnouncementEntity;
import uk.gov.moj.cpp.system.announcement.persistence.repository.SystemAnnouncementRepository;

import javax.inject.Inject;
import javax.json.Json;
import javax.json.JsonArrayBuilder;
import javax.json.JsonObjectBuilder;
import java.util.List;

import static uk.gov.justice.services.messaging.JsonEnvelope.envelopeFrom;

@CustomServiceComponent("SystemAnnouncement.API")
public class QueryBannerAnnouncementsApi {
    @Inject
    private SystemAnnouncementRepository systemAnnouncementRepository;
    @Inject
    private ObjectToJsonObjectConverter objectToJsonObjectConverter;

    @Handles("systemannouncement.get-banner-announcements")
    public JsonEnvelope getSystemBannerAnnouncements(final JsonEnvelope envelope) {
        List<SystemAnnouncementEntity> result = systemAnnouncementRepository.findActiveSystemAnnouncements();

        List<SystemBannerAnnouncement> systemBannerAnnouncementList = systemAnnouncementRepository.sort(result);
        JsonArrayBuilder jsonArrayBuilder = Json.createArrayBuilder();
        systemBannerAnnouncementList.stream().forEach(a -> jsonArrayBuilder.add(objectToJsonObjectConverter.convert(a)));

        final JsonObjectBuilder jsonObjectBuilder = Json.createObjectBuilder();
        jsonObjectBuilder.add("systemBannerAnnouncements", jsonArrayBuilder);

        return envelopeFrom(envelope.metadata(), jsonObjectBuilder.build());
    }
}
