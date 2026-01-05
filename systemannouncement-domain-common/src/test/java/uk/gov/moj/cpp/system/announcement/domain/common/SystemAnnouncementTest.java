package uk.gov.moj.cpp.system.announcement.domain.common;

import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.time.LocalTime;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class SystemAnnouncementTest {
    @Test
    public void testConstructorAndGetters() {
        LocalDate startDate = LocalDate.of(2022, 2, 2);
        LocalDate endDate = LocalDate.of(2022, 3, 3);
        LocalTime startTime = LocalTime.of(20, 0);
        LocalTime endTime = LocalTime.of(21, 1, 1);
        SystemAnnouncement announcement = new SystemAnnouncement.Builder()
                .createdBy("test user")
                .category(Category.UNPLANNED)
                .type(Type.CRITICAL)
                .startDate(startDate)
                .endDate(endDate)
                .startTime(startTime)
                .endTime(endTime)
                .title("title")
                .details("details")
                .build();

        assertEquals("test user", announcement.createdBy());
        assertEquals(Category.UNPLANNED, announcement.category());
        assertEquals(Type.CRITICAL, announcement.type());
        assertEquals(startDate, announcement.startDate());
        assertEquals(endDate, announcement.endDate());
        assertEquals(startTime, announcement.startTime());
        assertEquals(endTime, announcement.endTime());
        assertEquals("title", announcement.title());
        assertEquals("details", announcement.details());
    }

}