package uk.gov.moj.cpp.system.announcement.domain.common.helper;

import static com.google.common.collect.Lists.newArrayList;
import static java.util.Collections.emptyList;
import static java.util.stream.Collectors.toList;

import uk.gov.justice.services.common.converter.LocalDates;

import java.time.LocalDate;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

import javax.json.JsonObject;
import javax.json.JsonValue;

import com.google.common.base.Splitter;

public class JsonHelper {

    private static final String EMPTY = "";

    private JsonHelper() {
    }

    public static String getString(final JsonObject jsonObject, final String key) {
        return valueExists(jsonObject, key) ? jsonObject.getString(key) : EMPTY;
    }

    public static List<String> getList(final JsonObject jsonObject, final String key) {
        return valueExists(jsonObject, key) ? splitList(jsonObject.getString(key)) : emptyList();
    }

    private static List<String> splitList(final String listOfValues) {
        return newArrayList(Splitter.on(",").omitEmptyStrings()
                .split(listOfValues)).stream().collect(toList());
    }

    public static Optional<LocalDate> getLocalDate(final JsonObject jsonObject, final String key) {
        final String dateString = getString(jsonObject, key);
        return dateString.isEmpty() ? Optional.empty() : Optional.ofNullable(LocalDates.from(dateString));
    }

    public static boolean valueExists(final JsonObject jsonObject, final String key) {
        return jsonObject.containsKey(key) && !Objects.equals(jsonObject.get(key), JsonValue.NULL);
    }
}
