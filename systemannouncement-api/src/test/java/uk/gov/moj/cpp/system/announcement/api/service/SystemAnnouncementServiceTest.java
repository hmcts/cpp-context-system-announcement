package uk.gov.moj.cpp.system.announcement.api.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static uk.gov.justice.services.test.utils.core.reflection.ReflectionUtil.setField;

import uk.gov.moj.cpp.system.announcement.domain.common.Category;
import uk.gov.moj.cpp.system.announcement.domain.common.SystemAnnouncement;
import uk.gov.moj.cpp.system.announcement.domain.common.Type;
import uk.gov.moj.cpp.system.announcement.domain.common.UpdateSystemAnnouncement;
import uk.gov.moj.cpp.system.announcement.persistence.entity.SystemAnnouncementEntity;
import uk.gov.moj.cpp.system.announcement.persistence.repository.SystemAnnouncementRepository;

import java.sql.Date;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.UUID;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class SystemAnnouncementServiceTest {

    @Mock
    private SystemAnnouncementRepository systemAnnouncementRepository;

    @InjectMocks
    private SystemAnnouncementService systemAnnouncementService;

    @Test
    public void shouldCreateSystemAnnouncement() throws Exception {
        LocalDate startDate = LocalDate.of(2022, 2, 2);
        LocalDate endDate = LocalDate.of(2022, 3, 3);
        LocalTime startTime = LocalTime.of(20, 0);
        LocalTime endTime = LocalTime.of(21, 1, 1);

        SystemAnnouncement systemAnnouncement = new SystemAnnouncement.Builder()
                .createdBy("test user")
                .category(Category.PLANNED)
                .type(Type.CRITICAL)
                .startDate(startDate)
                .endDate(endDate)
                .startTime(startTime)
                .endTime(endTime)
                .title("title")
                .details("details")
                .build();

        systemAnnouncementService.createSystemAnnouncement(systemAnnouncement);
        verify(systemAnnouncementRepository, times(1)).save(any(SystemAnnouncementEntity.class));
    }

    @Test
    public void shouldUpdateSystemAnnouncement() {
        UUID systemAnnouncementId = UUID.randomUUID();
        UpdateSystemAnnouncement systemAnnouncement = new UpdateSystemAnnouncement.Builder()
                .systemAnnouncementId(systemAnnouncementId)
                .createdBy("test user")
                .category(Category.PLANNED)
                .type(Type.CRITICAL)
                .startDate(LocalDate.of(2022, 2, 2))
                .endDate(LocalDate.of(2022, 3, 3))
                .startTime(LocalTime.of(20, 0))
                .endTime(LocalTime.of(21, 0))
                .title("title")
                .details("details")
                .build();

        SystemAnnouncementEntity systemAnnouncementEntity = new SystemAnnouncementEntity();
        when(systemAnnouncementRepository.findById(systemAnnouncementId)).thenReturn(systemAnnouncementEntity);

        systemAnnouncementService.updateSystemAnnouncement(systemAnnouncement);
        verify(systemAnnouncementRepository, times(1)).update(any(SystemAnnouncementEntity.class));
    }

    @Test
    public void shouldNotUpdateNonExistentSystemAnnouncement() {
        UUID systemAnnouncementId = UUID.randomUUID();
        UpdateSystemAnnouncement systemAnnouncement = new UpdateSystemAnnouncement.Builder()
                .systemAnnouncementId(systemAnnouncementId)
                .createdBy("test user")
                .category(Category.PLANNED)
                .type(Type.CRITICAL)
                .startDate(LocalDate.of(2022, 2, 2))
                .endDate(LocalDate.of(2022, 3, 3))
                .startTime(LocalTime.of(20, 0))
                .endTime(LocalTime.of(21, 0))
                .title("title")
                .details("details")
                .build();

        when(systemAnnouncementRepository.findById(systemAnnouncementId)).thenReturn(null);

        systemAnnouncementService.updateSystemAnnouncement(systemAnnouncement);
        verify(systemAnnouncementRepository, times(0)).update(any(SystemAnnouncementEntity.class));
    }

    @Test
    public void shouldDeleteSystemAnnouncementById() {
        UUID id = UUID.randomUUID();
        systemAnnouncementService.deleteSystemAnnouncementById(id);
        verify(systemAnnouncementRepository, times(1)).deleteById(id);
    }

    @Test
    public void shouldDeleteExpiredSystemAnnouncements() {
        setField(systemAnnouncementService, "daysToKeep", "7");
        systemAnnouncementService.deleteExpiredSystemAnnouncements();
        final Date endDate = Date.valueOf(LocalDate.now().minusDays(Long.parseLong("7")));
        verify(systemAnnouncementRepository, times(1)).deleteByEndDate(endDate);
    }

    @Test
    public void shouldReturnCorrectOrderIndexForValidCategoryAndType() {
        short orderIndex = systemAnnouncementService.getOrderIndex(Category.UNPLANNED, Type.CRITICAL);
        assertEquals(1, orderIndex);
    }

    @Test
    public void shouldHandleUnplannedAnnouncementNullDatesAndTimes() {
        SystemAnnouncement systemAnnouncement = new SystemAnnouncement.Builder()
                .createdBy("test user")
                .category(Category.UNPLANNED)
                .type(Type.CRITICAL)
                .startDate(null)
                .endDate(null)
                .startTime(null)
                .endTime(null)
                .title("title")
                .details("details")
                .build();

        systemAnnouncementService.createSystemAnnouncement(systemAnnouncement);
        verify(systemAnnouncementRepository, times(1)).save(any(SystemAnnouncementEntity.class));
    }

}