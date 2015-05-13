package com.thisisnotajoke.android.groovedriver;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;

import org.joda.time.DateTime;
import org.joda.time.LocalDate;
import org.joda.time.format.ISODateTimeFormat;

import java.lang.reflect.Type;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class DateUtils {

    public static String getDateString(long time) {
        String string;

        Date date = new Date();
        date.setTime(time);
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("MM/dd/yy HH:mm:ss",
                Locale.getDefault());
        string = simpleDateFormat.format(date);

        return string;
    }

    public static Date getDate(String timestampString) {
        Date date = null;

        try {
            String format = "yyyy-MM-ddThh:mm:ssZ"; // 2014-11-22T06:33:03Z
            SimpleDateFormat dateFormat = new SimpleDateFormat(format);
            date = dateFormat.parse(timestampString);
        } catch (Exception e) {
            // this generic but you csan control another types of exception look the origin of exception
        }

        return date;
    }

    public static class DateTimeTypeAdapter implements JsonSerializer<DateTime>, JsonDeserializer<DateTime> {

        @Override
        public JsonElement serialize(DateTime src, Type typeOfSrc, JsonSerializationContext context) {
            return new JsonPrimitive(src.toString(ISODateTimeFormat.dateTimeNoMillis()));
        }

        @Override
        public DateTime deserialize(JsonElement json, Type type, JsonDeserializationContext context)
                throws JsonParseException {
            return new DateTime(json.getAsJsonPrimitive().getAsString());
        }
    }

    public static class LocalDateTypeAdapter implements JsonSerializer<LocalDate>, JsonDeserializer<LocalDate> {

        @Override
        public JsonElement serialize(LocalDate src, Type typeOfSrc, JsonSerializationContext context) {
            return new JsonPrimitive(src.toString(ISODateTimeFormat.date()));
        }

        @Override
        public LocalDate deserialize(JsonElement json, Type type, JsonDeserializationContext context)
                throws JsonParseException {
            DateTime dateTime = new DateTime(json.getAsJsonPrimitive().getAsString());
            return new LocalDate(dateTime);
        }
    }

}