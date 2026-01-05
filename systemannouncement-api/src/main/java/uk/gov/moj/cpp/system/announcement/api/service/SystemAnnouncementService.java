package uk.gov.moj.cpp.system.announcement.api.service;

import static java.util.Objects.nonNull;
import static java.util.Optional.ofNullable;

import uk.gov.justice.services.common.configuration.GlobalValue;
import uk.gov.moj.cpp.system.announcement.domain.common.Category;
import uk.gov.moj.cpp.system.announcement.domain.common.SystemAnnouncement;
import uk.gov.moj.cpp.system.announcement.domain.common.Type;
import uk.gov.moj.cpp.system.announcement.domain.common.UpdateSystemAnnouncement;
import uk.gov.moj.cpp.system.announcement.persistence.entity.SystemAnnouncementEntity;
import uk.gov.moj.cpp.system.announcement.persistence.repository.SystemAnnouncementRepository;

import java.sql.Date;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.ZonedDateTime;
import java.util.Collections;
import java.util.Map;
import java.util.UUID;

import javax.inject.Inject;
import javax.transaction.Transactional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Transactional
public class SystemAnnouncementService {
    private static final Logger LOGGER = LoggerFactory.getLogger(SystemAnnouncementService.class.getCanonicalName());

    @Inject
    @GlobalValue(key = "system.announcement.daysToKeep", defaultValue = "7")
    private String daysToKeep;

    private static final Map<Category, Map<Type, Short>> categoryTypeCache = Map.of(
            Category.UNPLANNED, Map.of(
                    Type.CRITICAL, (short) 1,
                    Type.WARNING, (short) 3,
                    Type.INFORMATION, (short) 5),
            Category.PLANNED, Map.of(
                    Type.CRITICAL, (short) 2,
                    Type.WARNING, (short) 4,
                    Type.INFORMATION, (short) 6)
    );
    @Inject
    private SystemAnnouncementRepository systemAnnouncementRepository;

    public void createSystemAnnouncement(final SystemAnnouncement systemAnnouncement) {
        SystemAnnouncementEntity systemAnnouncementEntity = new SystemAnnouncementEntity();
        systemAnnouncementEntity.setId(UUID.randomUUID());
        populateSystemAnnouncementEntity(systemAnnouncement, systemAnnouncementEntity);
        systemAnnouncementRepository.save(systemAnnouncementEntity);
    }

    public void updateSystemAnnouncement(final UpdateSystemAnnouncement systemAnnouncement) {
        SystemAnnouncementEntity systemAnnouncementEntity = systemAnnouncementRepository.findById(systemAnnouncement.systemAnnouncementId());
        if (nonNull(systemAnnouncementEntity)) {
            ofNullable(systemAnnouncement.createdBy()).ifPresent(systemAnnouncementEntity::setCreatedBy);
            ofNullable(systemAnnouncement.category()).ifPresent(systemAnnouncementEntity::setCategory);
            ofNullable(systemAnnouncement.type()).ifPresent(systemAnnouncementEntity::setType);
            ofNullable(systemAnnouncement.endDate()).ifPresent(systemAnnouncementEntity::setEndDate);
            ofNullable(systemAnnouncement.endTime()).ifPresent(systemAnnouncementEntity::setEndTime);
            systemAnnouncementEntity.setStartDate(systemAnnouncement.startDate() != null ? systemAnnouncement.startDate() : LocalDate.now());
            systemAnnouncementEntity.setStartTime(systemAnnouncement.startTime() != null ? systemAnnouncement.startTime() : LocalTime.now());
            ofNullable(systemAnnouncement.title()).ifPresent(systemAnnouncementEntity::setTitle);
            ofNullable(systemAnnouncement.details()).ifPresent(systemAnnouncementEntity::setDetails);
            systemAnnouncementEntity.setCreatedAt(ZonedDateTime.now());
            systemAnnouncementEntity.setOrderIndex(getOrderIndex(systemAnnouncementEntity.getCategory(), systemAnnouncementEntity.getType()));
            systemAnnouncementRepository.update(systemAnnouncementEntity);
        } else {
            LOGGER.warn("System Announcement record not present for id {}", systemAnnouncement.systemAnnouncementId());
        }
    }

    private SystemAnnouncementEntity populateSystemAnnouncementEntity(final SystemAnnouncement systemAnnouncement, final SystemAnnouncementEntity systemAnnouncementEntity) {
        systemAnnouncementEntity.setCreatedBy(systemAnnouncement.createdBy());
        systemAnnouncementEntity.setCategory(systemAnnouncement.category());
        systemAnnouncementEntity.setType(systemAnnouncement.type());
        systemAnnouncementEntity.setEndDate(systemAnnouncement.endDate());
        systemAnnouncementEntity.setEndTime(systemAnnouncement.endTime());
        systemAnnouncementEntity.setStartDate(systemAnnouncement.startDate() != null ? systemAnnouncement.startDate() : LocalDate.now());
        systemAnnouncementEntity.setStartTime(systemAnnouncement.startTime() != null ? systemAnnouncement.startTime() : LocalTime.now());
        systemAnnouncementEntity.setTitle(systemAnnouncement.title());
        systemAnnouncementEntity.setDetails(systemAnnouncement.details());
        systemAnnouncementEntity.setCreatedAt(ZonedDateTime.now());
        systemAnnouncementEntity.setOrderIndex(getOrderIndex(systemAnnouncement.category(), systemAnnouncement.type()));
        return systemAnnouncementEntity;
    }

    public void deleteSystemAnnouncementById(final UUID systemAnnouncementId) {
        systemAnnouncementRepository.deleteById(systemAnnouncementId);
    }

    public void deleteExpiredSystemAnnouncements() {
        final Date endDate = Date.valueOf(LocalDate.now().minusDays(Long.parseLong(daysToKeep)));
        systemAnnouncementRepository.deleteByEndDate(endDate);
    }

    public short getOrderIndex(final Category category, final Type type) {
        Map<Type, Short> typeMap = categoryTypeCache.getOrDefault(category, Collections.emptyMap());
        return typeMap.get(type) != null ? typeMap.get(type) : 0;
    }
}
