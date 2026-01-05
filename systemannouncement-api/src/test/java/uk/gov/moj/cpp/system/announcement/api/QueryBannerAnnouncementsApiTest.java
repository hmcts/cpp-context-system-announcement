package uk.gov.moj.cpp.system.announcement.api;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.when;
import static uk.gov.justice.services.messaging.JsonEnvelope.envelopeFrom;
import static uk.gov.justice.services.test.utils.core.messaging.MetadataBuilderFactory.metadataWithRandomUUID;

import uk.gov.justice.services.common.converter.ObjectToJsonObjectConverter;
import uk.gov.justice.services.messaging.JsonEnvelope;
import uk.gov.justice.services.messaging.Metadata;
import uk.gov.moj.cpp.system.announcement.domain.common.SystemBannerAnnouncement;
import uk.gov.moj.cpp.system.announcement.persistence.entity.SystemAnnouncementEntity;
import uk.gov.moj.cpp.system.announcement.persistence.repository.SystemAnnouncementRepository;

import java.util.Collections;
import java.util.List;

import javax.json.Json;
import javax.json.JsonArrayBuilder;
import javax.json.JsonObject;
import javax.json.JsonObjectBuilder;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class QueryBannerAnnouncementsApiTest {

    @InjectMocks
    private QueryBannerAnnouncementsApi queryBannerAnnouncementsApi;

    @Mock
    private SystemAnnouncementRepository systemAnnouncementRepository;

    @Mock
    private ObjectToJsonObjectConverter objectToJsonObjectConverter;

    @Test
    public void shouldReturnSystemBannerAnnouncements() {

        SystemAnnouncementEntity entity = new SystemAnnouncementEntity();
        List<SystemAnnouncementEntity> entities = Collections.singletonList(entity);
        SystemBannerAnnouncement bannerAnnouncement = new SystemBannerAnnouncement.Builder().build();
        List<SystemBannerAnnouncement> bannerAnnouncements = Collections.singletonList(bannerAnnouncement);

        when(systemAnnouncementRepository.findActiveSystemAnnouncements()).thenReturn(entities);
        when(systemAnnouncementRepository.sort(entities)).thenReturn(bannerAnnouncements);
        when(objectToJsonObjectConverter.convert(bannerAnnouncement)).thenReturn(Json.createObjectBuilder().build());

        JsonEnvelope envelope = envelopeFrom(metadataWithRandomUUID("systemannouncement.get-banner-announcements"), Json.createObjectBuilder().build());
        JsonEnvelope result = queryBannerAnnouncementsApi.getSystemBannerAnnouncements(envelope);

        JsonArrayBuilder jsonArrayBuilder = Json.createArrayBuilder();
        jsonArrayBuilder.add(Json.createObjectBuilder().build());
        JsonObjectBuilder jsonObjectBuilder = Json.createObjectBuilder();
        jsonObjectBuilder.add("systemBannerAnnouncements", jsonArrayBuilder);

        assertThat(result.payloadAsJsonObject(), is(jsonObjectBuilder.build()));
    }

}