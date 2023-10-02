package com.saber.helper;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;

import java.io.IOException;
import java.sql.Timestamp;
import java.text.ParseException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Date;


//@JsonComponent
public class PersianDateJackson {

    public static class SerializerLocalDate extends JsonSerializer<LocalDate> {
        @Override
        public void serialize(LocalDate value, JsonGenerator gen, SerializerProvider arg2) throws IOException {
            Date valueDate = Date.from(value.atStartOfDay()
                    .atZone(ZoneId.systemDefault())
                    .toInstant());
            gen.writeString(PersianDateUtil.convertJulianToYearMounthDayPersian(valueDate, "/"));
        }
    }

    public static class SerializerLocalDateDash extends JsonSerializer<LocalDate> {
        @Override
        public void serialize(LocalDate value, JsonGenerator gen, SerializerProvider arg2) throws IOException {
            Date valueDate = Date.from(value.atStartOfDay()
                    .atZone(ZoneId.systemDefault())
                    .toInstant());
            gen.writeString(PersianDateUtil.convertJulianToYearMounthDayPersian(valueDate, "-"));
        }
    }

    public static class SerializerLocalDateTime extends JsonSerializer<LocalDateTime> {
        @Override
        public void serialize(LocalDateTime value, JsonGenerator gen, SerializerProvider arg2) throws IOException {
            ZonedDateTime zdt = value.atZone(ZoneId.systemDefault());
            Date output = Date.from(zdt.toInstant());
            gen.writeString(PersianDateUtil.convetJulianToDateTimePersianWithSeparator(output, "/", " ", ":"));
        }

    }

    public static class SerializerLocalDateTimeDash extends JsonSerializer<LocalDateTime> {
        @Override
        public void serialize(LocalDateTime value, JsonGenerator gen, SerializerProvider arg2) throws IOException {
            ZonedDateTime zdt = value.atZone(ZoneId.systemDefault());
            Date output = Date.from(zdt.toInstant());
            gen.writeString(PersianDateUtil.convetJulianToDateTimePersianWithSeparator(output, "-", " ", ":"));
        }

    }


    public static class DeserializerDate extends JsonDeserializer<Date> {
        @Override
        public Date deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
            if (p.getText() == null || p.getText().isBlank()) return null;
            try {
                return PersianDateUtil.getTimeStampFromStringWithSeparator(p.getText(), "-", " ", ":");
            } catch (ParseException e) {
                throw new RuntimeException(e);
            }
        }
    }

    public static class SerializerDate extends JsonSerializer<Date> {
        @Override
        public void serialize(Date value, JsonGenerator gen, SerializerProvider arg2) throws IOException {
            gen.writeString(PersianDateUtil.convetJulianToDateTimePersianWithSeparator(value, "-", " ", ":"));
        }

    }


    public static class DeserializerLocalDateSlash extends JsonDeserializer<LocalDate> {
        @Override
        public LocalDate deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
            if (p.getText() == null || p.getText().isBlank()) return null;
            try {
                Timestamp timestamp = PersianDateUtil.getTimeStampFromStringWithSeparator(p.getText(), "/", " ", ":");
                return timestamp.toLocalDateTime().toLocalDate();
            } catch (ParseException e) {
                throw new RuntimeException(e);
            }
        }
    }

    public static class DeserializerLocalDateTimeSlash extends JsonDeserializer<LocalDateTime> {
        @Override
        public LocalDateTime deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
            if (p.getText() == null || p.getText().isBlank()) return null;
            try {
                Timestamp timestamp = PersianDateUtil.getTimeStampFromStringWithSeparator(p.getText(), "/", " ", ":");
                return timestamp.toLocalDateTime();
            } catch (ParseException e) {
                throw new RuntimeException(e);
            }
        }
    }

    public static class DeserializerLocalDateDash extends JsonDeserializer<LocalDate> {
        @Override
        public LocalDate deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
            if (p.getText() == null || p.getText().isBlank()) return null;
            try {
                Timestamp timestamp = PersianDateUtil.getTimeStampFromStringWithSeparator(p.getText(), "-", " ", ":");
                return timestamp.toLocalDateTime().toLocalDate();
            } catch (ParseException e) {
                throw new RuntimeException(e);
            }
        }
    }


    public static class DeserializerLocalDateTimeDash extends JsonDeserializer<LocalDateTime> {
        @Override
        public LocalDateTime deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
            if (p.getText() == null || p.getText().isBlank()) return null;
            try {
                Timestamp timestamp = PersianDateUtil.getTimeStampFromStringWithSeparator(p.getText(), "-", " ", ":");
                return timestamp.toLocalDateTime();
            } catch (ParseException e) {
                throw new RuntimeException(e);
            }
        }
    }
}