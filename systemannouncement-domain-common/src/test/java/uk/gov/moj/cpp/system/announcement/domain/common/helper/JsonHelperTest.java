package uk.gov.moj.cpp.system.announcement.domain.common.helper;

import static org.junit.jupiter.api.Assertions.assertEquals;

import uk.gov.justice.services.common.converter.LocalDates;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import uk.gov.justice.services.messaging.JsonObjects;
import javax.json.JsonObject;
import javax.json.JsonValue;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class JsonHelperTest {

    private static final String EMPTY = "";

    private static JsonObject createPayload(final String key, final String value) {
        return JsonObjects.createObjectBuilder()
                .add(key, value)
                .build();
    }

    private static JsonObject createEmptyPayload() {
        return JsonObjects.createObjectBuilder()
                .build();
    }

    @Test
    public void shouldGetList() throws Exception {
        final String id = "EWAY,SNONIMP";
        final JsonObject jsonObject = createPayload("modeOfTrail", id);

        final List<String> result = JsonHelper.getList(jsonObject, "modeOfTrail");
        assertEquals("EWAY", result.get(0));
        assertEquals("SNONIMP", result.get(1));
    }

    @Test
    public void shouldGetString() throws Exception {
        final String id = "7e2f843e-d639-40b3-8611-8015f3a18958";
        final JsonObject jsonObject = createPayload("id", id);

        final String result = JsonHelper.getString(jsonObject, "id");

        assertEquals(id, result);
    }

    @Test
    public void shouldReturnEmptyStringIfNotFound() throws Exception {
        final JsonObject jsonObject = createEmptyPayload();

        final String result = JsonHelper.getString(jsonObject, "id");

        assertEquals(EMPTY, result);
    }

    @Test
    public void shouldReturnEmptyStringForJsonNull() throws Exception {
        final JsonObject jsonNull = JsonObjects.createObjectBuilder().add("id", JsonValue.NULL).build();

        final String result = JsonHelper.getString(jsonNull, "id");

        assertEquals(EMPTY, result);
    }

    @Test
    public void shouldGetLocalDate() throws Exception {
        final LocalDate expected = LocalDates.from("2016-02-15");
        final JsonObject jsonObject = createPayload("postingDate", expected.toString());

        final Optional<LocalDate> result = JsonHelper.getLocalDate(jsonObject, "postingDate");

        assertEquals(Optional.of(expected), result);
    }

    @Test
    public void shouldReturnEmptyStringIfLocalDateNotFound() throws Exception {
        final JsonObject jsonObject = createEmptyPayload();

        final Optional<LocalDate> result = JsonHelper.getLocalDate(jsonObject, "postingDate");

        assertEquals(Optional.empty(), result);
    }
}