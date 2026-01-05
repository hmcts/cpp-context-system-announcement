package uk.gov.moj.cpp.system.announcement.api;


import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import uk.gov.justice.services.common.converter.ObjectToJsonObjectConverter;
import uk.gov.justice.services.messaging.JsonEnvelope;
import uk.gov.justice.services.messaging.Metadata;
import uk.gov.moj.cpp.system.announcement.domain.common.SystemAnnouncement;
import uk.gov.moj.cpp.system.announcement.persistence.entity.SystemAnnouncementEntity;
import uk.gov.moj.cpp.system.announcement.persistence.repository.SystemAnnouncementRepository;

import java.util.Collections;
import java.util.List;

import javax.json.JsonObject;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class QueryAllAnnouncementsApiTest {
    @Mock
    private SystemAnnouncementRepository systemAnnouncementRepository;
    @Mock
    private ObjectToJsonObjectConverter objectToJsonObjectConverter;
    @Mock
    private JsonEnvelope envelope;
    @Mock
    private JsonObject jsonObject;
    @Mock
    private Metadata metadata;
    @InjectMocks
    private QueryAllAnnouncementsApi queryAllAnnouncementsApi;

    @Test
    void shouldReturnSystemAnnouncements() {

        SystemAnnouncementEntity entity = new SystemAnnouncementEntity();
        SystemAnnouncement systemAnnouncement = new SystemAnnouncement.Builder().build();
        List<SystemAnnouncementEntity> entities = Collections.singletonList(entity);
        when(systemAnnouncementRepository.findSystemAnnouncements()).thenReturn(entities);
        when(systemAnnouncementRepository.sortSystemAnnouncements(any())).thenReturn(List.of(systemAnnouncement));
        when(objectToJsonObjectConverter.convert(systemAnnouncement)).thenReturn(jsonObject);
        when(envelope.metadata()).thenReturn(metadata);

        JsonEnvelope result = queryAllAnnouncementsApi.getAllSystemAnnouncements(envelope);

        assertNotNull(result);
        verify(systemAnnouncementRepository, times(1)).findSystemAnnouncements();
        verify(objectToJsonObjectConverter, times(1)).convert(systemAnnouncement);
    }
}