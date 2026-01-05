package uk.gov.moj.cpp.system.announcement.api.accesscontrol;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.justice.services.common.converter.jackson.ObjectMapperProducer;

import javax.json.JsonObject;
import java.io.IOException;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

@ExtendWith(MockitoExtension.class)
public class ExpectedPermissionConstantsTest {
    @InjectMocks
    private ExpectedPermissionConstants expectedPermissionConstants;

    @Spy
    private ObjectMapper objectMapper = new ObjectMapperProducer().objectMapper();



    @Test
    public void shouldReturnexpectedPermissionForSystemAnnouncement() throws IOException {
        final String[] result = ExpectedPermissionConstants.expectedPermissionForSystemAnnouncement();
        final JsonObject jsonObject = objectMapper.readValue(result[0], JsonObject.class);
        assertThat(jsonObject.getString("object"), is("DowntimeAnnouncements"));
        assertThat(jsonObject.getString("action"), is("Publish"));
        assertThat(jsonObject.getString("keyWithOutSource"), is("DowntimeAnnouncements_Publish"));
        assertThat(jsonObject.getString("key"), is("DowntimeAnnouncements_Publish"));
    }



}