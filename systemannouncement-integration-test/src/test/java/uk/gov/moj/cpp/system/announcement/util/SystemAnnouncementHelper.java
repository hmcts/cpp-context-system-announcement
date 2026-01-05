package uk.gov.moj.cpp.system.announcement.util;


import com.jayway.jsonpath.ReadContext;
import io.restassured.response.Response;
import org.hamcrest.CoreMatchers;
import org.hamcrest.Matcher;
import uk.gov.justice.services.test.utils.core.matchers.ResponseStatusMatcher;

import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.concurrent.TimeUnit;

import static com.google.common.collect.Lists.newArrayList;
import static com.jayway.jsonpath.matchers.JsonPathMatchers.withJsonPath;
import static java.lang.String.format;
import static java.util.UUID.randomUUID;
import static javax.ws.rs.core.Response.Status.OK;
import static org.hamcrest.CoreMatchers.is;
import static uk.gov.justice.services.common.http.HeaderConstants.USER_ID;
import static uk.gov.justice.services.test.utils.core.http.BaseUriProvider.getBaseUri;
import static uk.gov.justice.services.test.utils.core.http.RequestParamsBuilder.requestParams;
import static uk.gov.justice.services.test.utils.core.http.RestPoller.poll;
import static uk.gov.justice.services.test.utils.core.matchers.ResponsePayloadMatcher.payload;
import static uk.gov.justice.services.test.utils.core.matchers.ResponseStatusMatcher.status;
import static uk.gov.moj.cpp.system.announcement.helper.RestHelper.postCommand;
import static uk.gov.moj.cpp.system.announcement.util.TestUtils.getPayloadForCreatingRequest;

public class SystemAnnouncementHelper {
    private static final int DEFAULT_POLL_INTERVAL_MSECS = 300;


    public static Response createSystemAnnouncement(final String user, final String queryPath, final String queryType, final LocalDate startDate, final LocalDate endDate, final LocalTime startTime, final LocalTime endTime) throws IOException {
        final String createBannerPayload = createBannerPayload(startDate, endDate, startTime, endTime);
        return postCommand(user, getBaseUri() + queryPath, queryType, createBannerPayload);
    }

    public static Response updateSystemAnnouncement(final String user,final String queryPath, final String queryType, final LocalDate startDate, final LocalDate endDate, final LocalTime startTime, final LocalTime endTime) throws IOException {
        final String createBannerPayload = createBannerPayload(startDate, endDate, startTime, endTime);
        return postCommand(user, getBaseUri() + queryPath + format("/%s", randomUUID().toString()), queryType, createBannerPayload);
    }

    public static Response deleteSystemAnnouncementById(final String user, final String queryPath, final String queryType) throws IOException {
        return postCommand(user, getBaseUri() + queryPath + format("/%s", randomUUID().toString()), queryType, "{}");
    }
    public static Response deleteExpiredSystemAnnouncement(final String user, final String queryPath, final String queryType) throws IOException {
        return postCommand(user, getBaseUri() + queryPath, queryType, "{}");
    }

    public static String createBannerPayload(final LocalDate startDate, final LocalDate endDate, final LocalTime startTime, final LocalTime endTime) {
        final String template = getPayloadForCreatingRequest("payloads/create-system-announcement.json");
        return template.replace("START_DATE", startDate.toString())
                .replace("END_DATE", endDate.toString())
                .replace("START_TIME", startTime.toString())
                .replace("END_TIME", endTime.toString());
    }

    public static String pollGetAnnouncementResultAsString(final String queryPath, final String queryType) {
        List<Matcher<? super ReadContext>> matchers = newArrayList(
                withJsonPath("$.createdBy", is("user123"))
        );
        return pollForResponse(getBaseUri() + queryPath, queryType);
    }

    public static String pollForResponse(final String path, final String mediaType, final Matcher... payloadMatchers) {
        return pollForResponse(path, mediaType, randomUUID().toString(), payloadMatchers);
    }

    public static String pollForResponse(final String path, final String mediaType, final String userId, final Matcher... payloadMatchers) {
        return pollForResponse(path, mediaType, userId, status().is(OK), payloadMatchers);
    }

    public static String pollForResponse(final String path, final String mediaType, final String userId, final ResponseStatusMatcher responseStatusMatcher, final Matcher... payloadMatchers) {
        return poll(requestParams(path, mediaType)
                .withHeader(USER_ID, userId).build())
                .pollInterval(DEFAULT_POLL_INTERVAL_MSECS, TimeUnit.MILLISECONDS)
                .until(
                        responseStatusMatcher
                )
                .getPayload();
    }
}
