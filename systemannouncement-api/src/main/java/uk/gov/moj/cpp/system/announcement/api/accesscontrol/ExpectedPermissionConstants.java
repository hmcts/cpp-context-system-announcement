package uk.gov.moj.cpp.system.announcement.api.accesscontrol;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import uk.gov.justice.services.common.converter.jackson.ObjectMapperProducer;
import uk.gov.moj.cpp.accesscontrol.drools.ExpectedPermission;

public class ExpectedPermissionConstants {

    private static final ObjectMapper objectMapper = new ObjectMapperProducer().objectMapper();

    public static final String PUBLISH = "Publish";

    public static final String DOWNTIME_ANNOUNCEMENTS = "DowntimeAnnouncements";

    private ExpectedPermissionConstants() {

    }

    public static String[] expectedPermissionForSystemAnnouncement() throws JsonProcessingException {
        final ExpectedPermission expectedPermission = ExpectedPermission.builder()
                .withAction(PUBLISH)
                .withObject(DOWNTIME_ANNOUNCEMENTS)
                .build();
        return new String[]{objectMapper.writeValueAsString(expectedPermission)};
    }
}
