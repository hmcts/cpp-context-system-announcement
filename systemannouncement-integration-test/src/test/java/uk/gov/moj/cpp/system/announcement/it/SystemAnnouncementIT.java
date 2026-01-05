package uk.gov.moj.cpp.system.announcement.it;

import io.restassured.response.Response;
import org.apache.http.HttpStatus;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalTime;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.MatcherAssert.assertThat;
import static uk.gov.moj.cpp.system.announcement.util.SystemAnnouncementHelper.createSystemAnnouncement;
import static uk.gov.moj.cpp.system.announcement.util.SystemAnnouncementHelper.deleteExpiredSystemAnnouncement;
import static uk.gov.moj.cpp.system.announcement.util.SystemAnnouncementHelper.deleteSystemAnnouncementById;
import static uk.gov.moj.cpp.system.announcement.util.SystemAnnouncementHelper.pollGetAnnouncementResultAsString;
import static uk.gov.moj.cpp.system.announcement.util.SystemAnnouncementHelper.updateSystemAnnouncement;

public class SystemAnnouncementIT extends AbstractIT {

    private static final String SYSTEM_ANNOUNCEMENT_COMMAND_PATH = "/systemannouncement-service/rest/systemannouncement/announcement";
    private static final String QUERY_SYSTEM_ANNOUNCEMENT_BASE_PATH = "/systemannouncement-service/rest/systemannouncement/announcements";
    private static final String UPDATE_SYSTEM_ANNOUNCEMENT_COMMAND_PATH = "/systemannouncement-service/rest/systemannouncement/announcement";
    private static final String QUERY_ALL_ANNOUNCEMENT_TYPE = "application/vnd.systemannouncement.get-all-announcements+json";
    private static final String QUERY_BANNER_ANNOUNCEMENT_TYPE = "application/vnd.systemannouncement.get-banner-announcements+json";
    private static final String CREATE_SYSTEM_ANNOUNCEMENT_COMMAND_MEDIA_TYPE = "application/vnd.systemannouncement.create-system-announcement+json";
    private static final String UPDATE_SYSTEM_ANNOUNCEMENT_COMMAND_MEDIA_TYPE = "application/vnd.systemannouncement.update-system-announcement+json";
    private static final String DELETE_EXPIRED_SYSTEM_ANNOUNCEMENT_COMMAND_MEDIA_TYPE = "application/vnd.systemannouncement.delete-expired-system-announcements+json";
    private static final String DELETE_SYSTEM_ANNOUNCEMENT_BY_ID_COMMAND_MEDIA_TYPE = "application/vnd.systemannouncement.delete-system-announcement-by-id+json";

    @Test
    public void shouldVerifyCreateAndGetSystemAnnouncement() throws IOException {
        final LocalDate startDate = LocalDate.now().minusDays(10);
        final LocalDate endDate = LocalDate.now().plusDays(20);
        final LocalTime startTime = LocalTime.of(20, 00, 00);
        final LocalTime endTime = LocalTime.of(20, 00, 00);

        createAndVerifyAnnouncement(USER_ID_VALUE.toString(), startDate, endDate, startTime, endTime);
        getAndVerifyAnnouncement(QUERY_ALL_ANNOUNCEMENT_TYPE);
    }

    @Test
    public void shouldVerifyCreateAndGetBannerAnnouncement() throws IOException {
        final LocalDate startDate = LocalDate.now().minusDays(10);
        final LocalDate endDate = LocalDate.now().plusDays(20);
        final LocalTime startTime = LocalTime.of(20, 00, 00);
        final LocalTime endTime = LocalTime.of(20, 00, 00);

        createAndVerifyAnnouncement(USER_ID_VALUE.toString(), startDate, endDate, startTime, endTime);
        getAndVerifyAnnouncement(QUERY_BANNER_ANNOUNCEMENT_TYPE);
    }

    @Test
    public void shouldVerifyCreateAndDeleteExpiredAnnouncement() throws IOException {
        final LocalDate startDate = LocalDate.now().minusDays(10);
        final LocalDate endDate = LocalDate.now().plusDays(20);
        final LocalTime startTime = LocalTime.of(20, 00, 00);
        final LocalTime endTime = LocalTime.of(20, 00, 00);

        createAndVerifyAnnouncement(USER_ID_VALUE.toString(), startDate, endDate, startTime, endTime);
        Response response = deleteExpiredSystemAnnouncement(USER_ID_VALUE.toString(), SYSTEM_ANNOUNCEMENT_COMMAND_PATH, DELETE_EXPIRED_SYSTEM_ANNOUNCEMENT_COMMAND_MEDIA_TYPE);
        assertThat(response.getStatusCode(), equalTo(HttpStatus.SC_ACCEPTED));
    }

    @Test
    public void shouldVerifyCreateUpdateAndDeleteAnnouncement() throws IOException {
        final LocalDate startDate = LocalDate.now().minusDays(10);
        final LocalDate endDate = LocalDate.now().plusDays(20);
        final LocalTime startTime = LocalTime.of(20, 00, 00);
        final LocalTime endTime = LocalTime.of(20, 00, 00);

        createAndVerifyAnnouncement(USER_ID_VALUE.toString(), startDate, endDate, startTime, endTime);
        Response response = updateSystemAnnouncement(USER_ID_VALUE.toString(), UPDATE_SYSTEM_ANNOUNCEMENT_COMMAND_PATH, UPDATE_SYSTEM_ANNOUNCEMENT_COMMAND_MEDIA_TYPE, startDate, endDate, startTime, endTime);
        assertThat(response.getStatusCode(), equalTo(HttpStatus.SC_ACCEPTED));
        response = deleteSystemAnnouncementById(USER_ID_VALUE.toString(), UPDATE_SYSTEM_ANNOUNCEMENT_COMMAND_PATH, DELETE_SYSTEM_ANNOUNCEMENT_BY_ID_COMMAND_MEDIA_TYPE);
        assertThat(response.getStatusCode(), equalTo(HttpStatus.SC_ACCEPTED));
    }

    private static void createAndVerifyAnnouncement(String user, LocalDate startDate, LocalDate endDate, LocalTime startTime, LocalTime endTime) throws IOException {
        Response response = createSystemAnnouncement(user, SYSTEM_ANNOUNCEMENT_COMMAND_PATH, CREATE_SYSTEM_ANNOUNCEMENT_COMMAND_MEDIA_TYPE, startDate, endDate, startTime, endTime);
        assertThat(response.getStatusCode(), equalTo(HttpStatus.SC_ACCEPTED));
    }

    private static void getAndVerifyAnnouncement(String queryAllAnnouncementType) {
        String resultAsString = pollGetAnnouncementResultAsString(QUERY_SYSTEM_ANNOUNCEMENT_BASE_PATH, queryAllAnnouncementType);
        assertThat(resultAsString, notNullValue());
    }
}
