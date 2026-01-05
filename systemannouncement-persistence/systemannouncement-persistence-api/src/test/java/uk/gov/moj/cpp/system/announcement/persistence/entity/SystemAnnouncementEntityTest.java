package uk.gov.moj.cpp.system.announcement.persistence.entity;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import uk.gov.moj.cpp.system.announcement.domain.common.Category;
import uk.gov.moj.cpp.system.announcement.domain.common.Type;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.ZonedDateTime;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;


public class SystemAnnouncementEntityTest {
    private SystemAnnouncementEntity entity;

    @BeforeEach
    public void setUp() {
        entity = new SystemAnnouncementEntity();
    }

    @Test
    public void shouldGetAndSetId() {
        UUID id = UUID.randomUUID();
        entity.setId(id);
        assertEquals(id, entity.getId());
    }

    @Test
    public void shouldGetAndSetCreatedBy() {
        String createdBy = "user";
        entity.setCreatedBy(createdBy);
        assertEquals(createdBy, entity.getCreatedBy());
    }

    @Test
    public void shouldGetAndSetCategory() {
        Category category = Category.UNPLANNED;
        entity.setCategory(category);
        assertEquals(category, entity.getCategory());
    }

    @Test
    public void shouldGetAndSetType() {
        Type type = Type.CRITICAL;
        entity.setType(type);
        assertEquals(type, entity.getType());
    }

    @Test
    public void shouldGetAndSetStartDate() {
        LocalDate startDate = LocalDate.now();
        entity.setStartDate(startDate);
        assertEquals(startDate, entity.getStartDate());
    }

    @Test
    public void shouldGetAndSetEndDate() {
        LocalDate endDate = LocalDate.now();
        entity.setEndDate(endDate);
        assertEquals(endDate, entity.getEndDate());
    }

    @Test
    public void shouldGetAndSetStartTime() {
        LocalTime startTime = LocalTime.now();
        entity.setStartTime(startTime);
        assertEquals(startTime, entity.getStartTime());
    }

    @Test
    public void shouldGetAndSetEndTime() {
        LocalTime endTime = LocalTime.now();
        entity.setEndTime(endTime);
        assertEquals(endTime, entity.getEndTime());
    }

    @Test
    public void shouldGetAndSetTitle() {
        String title = "title";
        entity.setTitle(title);
        assertEquals(title, entity.getTitle());
    }

    @Test
    public void shouldGetAndSetDetails() {
        String details = "details";
        entity.setDetails(details);
        assertEquals(details, entity.getDetails());
    }

    @Test
    public void shouldGetAndSetCreatedAt() {
        ZonedDateTime timestamp = ZonedDateTime.now();
        entity.setCreatedAt(timestamp);
        assertEquals(timestamp, entity.getCreatedAt());
    }
}