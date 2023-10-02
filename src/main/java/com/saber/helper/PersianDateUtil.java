package com.saber.helper;


import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.joda.time.Days;

public class PersianDateUtil {
    public PersianDateUtil() {
    }

    public static Integer getYearOfDate(Date date) {
        return date.getYear();
    }

    public static Boolean isLeapCurrentYear() {
        Timestamp currentTime = new Timestamp(System.currentTimeMillis());
        return JalaliCalendar.isLeepYear(getYearMonthDate(currentTime).getYear());
    }

    public static Boolean isLeapYear(Date date) {
        new Timestamp(System.currentTimeMillis());
        return JalaliCalendar.isLeepYear(getYearMonthDate(date).getYear());
    }

    public static String getTimeFromDate(Date currentDate) {
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");
        return sdf.format(currentDate);
    }

    public static String getTimeFromDateWithoutSeperator(Date currentDate) {
        SimpleDateFormat sdf = new SimpleDateFormat("HHmmss");
        return sdf.format(currentDate);
    }

    @PersianDateMapper.julianToPersian
    public static String convertJulianToYearMounthDayPersian(Date julianDate) {
        return convertJulianToYearMounthDayPersian(julianDate, "/");
    }

    public static String convertJulianToYearMounthDayPersian(Date julianDate, String dateSeperator) {
        if (julianDate == null) {
            return null;
        } else {
            Calendar calendar = Calendar.getInstance(TimeZone.getTimeZone("Iran"), new Locale("fa_IR"));
            calendar.setTime(julianDate);
            int year = calendar.get(1);
            int month = calendar.get(2);
            int day = calendar.get(5);
            JalaliCalendar.YearMonthDate yearMonthDate = new JalaliCalendar.YearMonthDate(year, month, day);
            new JalaliCalendar();
            yearMonthDate = JalaliCalendar.gregorianToJalali(yearMonthDate);
            return yearMonthDate.getYearMonthDayBySeprator(dateSeperator);
        }
    }

    public static String convertJulianToYearDayMounth24E(Date julianDate) {
        if (julianDate == null) {
            return "000000";
        } else {
            Calendar calendar = Calendar.getInstance(TimeZone.getTimeZone("Iran"), new Locale("fa_IR"));
            calendar.setTime(julianDate);
            int year = calendar.get(1);
            int month = calendar.get(2);
            int day = calendar.get(5);
            JalaliCalendar.YearMonthDate yearMonthDate = new JalaliCalendar.YearMonthDate(year, month, day);
            new JalaliCalendar();
            yearMonthDate = JalaliCalendar.gregorianToJalali(yearMonthDate);
            return yearMonthDate.getYearDayMonthBySeprator("");
        }
    }

    public static String convertJulianToYearMounthDayPersianWithoutSeparator(Date julianDate) {
        if (julianDate == null) {
            return "00000000";
        } else {
            Calendar calendar = Calendar.getInstance(TimeZone.getTimeZone("Iran"), new Locale("fa_IR"));
            calendar.setTime(julianDate);
            int year = calendar.get(1);
            int month = calendar.get(2);
            int day = calendar.get(5);
            JalaliCalendar.YearMonthDate yearMonthDate = new JalaliCalendar.YearMonthDate(year, month, day);
            new JalaliCalendar();
            yearMonthDate = JalaliCalendar.gregorianToJalali(yearMonthDate);
            return yearMonthDate.getYearMonthDayBySeprator("");
        }
    }

    public static String convetJulianToDateTimePersianWithSeparator(Date julianDate, String dateSeprator, String dateTimeSeperator, String timeSeperator) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(julianDate);
        int year = calendar.get(1);
        int month = calendar.get(2);
        int day = calendar.get(5);
        int hour = calendar.get(11);
        int minute = calendar.get(12);
        int second = calendar.get(13);
        JalaliCalendar.DateTime dateTime = new JalaliCalendar.DateTime(year, month, day, hour, minute, second);
        dateTime = JalaliCalendar.gregorianToJalali(dateTime);
        return dateTime.getDateTimeBySeprator(dateSeprator, dateTimeSeperator, timeSeperator);
    }

    /** @deprecated */
    @Deprecated
    public static Timestamp getTimeStampFromIntString(String persianDateString) throws ParseException {
        JalaliCalendar.YearMonthDate yearMonthDate = new JalaliCalendar.YearMonthDate(Integer.parseInt(persianDateString.substring(0, 4)), Integer.parseInt(persianDateString.substring(4, 6)) - 1, Integer.parseInt(persianDateString.substring(6, 8)));
        yearMonthDate = JalaliCalendar.jalaliToGregorian(yearMonthDate);
        DateFormat formatter = new SimpleDateFormat("yyyy/MM/dd");
        Date date = formatter.parse(yearMonthDate.getYearMonthDayBySeprator("/"));
        return new Timestamp(date.getTime());
    }

    @PersianDateMapper.julianToPersian
    public static Timestamp getTimeStampFromString(String persianDateString) throws ParseException {
        return getTimeStampFromStringWithSeparator(persianDateString, "/", " ", ":");
    }

    public static Timestamp getTimeStampFromStringWithSeparator(String persianDateString, String dateSeprator, String dateTimeSeperator, String timeSeperator) throws ParseException {
        if (persianDateString != null && !persianDateString.isEmpty()) {
            String persianDateRegx = String.format("(?<year>\\d{4})%1$s(?<month>\\d{2})%1$s(?<day>\\d{2})(?<time>%2$s(?<hour>\\d{2})%3$s(?<minute>\\d{2})%3$s(?<second>\\d{2}))?", dateSeprator, dateTimeSeperator, timeSeperator);
            Matcher matcher = Pattern.compile(persianDateRegx).matcher(persianDateString);
            if (!matcher.find()) {
                throw new ParseException("persian date must be in " + String.format("yyyy%1$sMM%1$sdd%2$s", dateSeprator, dateTimeSeperator, timeSeperator), 0);
            } else {
                String format = "";
                Integer year = null;
                Integer month = null;
                Integer day = null;
                Integer hour = 0;
                Integer minute = 0;
                Integer second = 0;
                if (matcher.group("year") != null) {
                    format = format + "yyyy";
                    year = Integer.valueOf(matcher.group("year"));
                }

                if (matcher.group("month") != null) {
                    format = format + dateSeprator + "MM";
                    month = Integer.valueOf(matcher.group("month"));
                }

                if (matcher.group("day") != null) {
                    format = format + dateSeprator + "dd";
                    day = Integer.valueOf(matcher.group("day"));
                }

                if (matcher.group("hour") != null) {
                    format = format + dateTimeSeperator + "HH";
                    hour = Integer.valueOf(matcher.group("hour"));
                }

                if (matcher.group("minute") != null) {
                    format = format + timeSeperator + "mm";
                    minute = Integer.valueOf(matcher.group("minute"));
                }

                if (matcher.group("second") != null) {
                    format = format + timeSeperator + "ss";
                    second = Integer.valueOf(matcher.group("second"));
                }

                JalaliCalendar.DateTime dateTime = JalaliCalendar.jalaliToGregorian(new JalaliCalendar.DateTime(year, month - 1, day, hour, minute, second));
                Date date = (new SimpleDateFormat(format)).parse(dateTime.getDateTimeBySeprator(dateSeprator, dateTimeSeperator, timeSeperator));
                return new Timestamp(date.getTime());
            }
        } else {
            return null;
        }
    }

    public static Timestamp startOfDay(Date dateTime) {
        Calendar calendar = Calendar.getInstance(TimeZone.getTimeZone("Iran"), new Locale("fa_IR"));
        calendar.setTime(dateTime);
        calendar.set(11, 0);
        calendar.set(12, 0);
        calendar.set(13, 0);
        calendar.set(14, 0);
        return new Timestamp(calendar.getTimeInMillis());
    }

    public static Timestamp endOfDay(Date dateTime) {
        Calendar calendar = Calendar.getInstance(TimeZone.getTimeZone("Iran"), new Locale("fa_IR"));
        calendar.setTime(dateTime);
        int day = calendar.get(5);
        calendar.set(5, day + 1);
        calendar.set(11, 0);
        calendar.set(12, 0);
        calendar.set(13, 0);
        calendar.set(14, 0);
        return new Timestamp(calendar.getTimeInMillis());
    }

    public static JalaliCalendar.YearMonthDate getJulianYearMonthDateFromPersianDateString(String persianDateString) throws ParseException {
        Calendar calendar = Calendar.getInstance(TimeZone.getTimeZone("GMT"));
        Timestamp timestamp = getTimeStampFromString(persianDateString);
        calendar.setTimeInMillis(timestamp.getTime());
        return new JalaliCalendar.YearMonthDate(calendar.get(1), calendar.get(2) + 1, calendar.get(5) + 1);
    }

    public static JalaliCalendar.YearMonthDate convertJulianToYearMounthDayJalali(Date julianDate) {
        if (julianDate == null) {
            return null;
        } else {
            Calendar calendar = Calendar.getInstance(TimeZone.getTimeZone("Iran"), new Locale("fa_IR"));
            calendar.setTime(julianDate);
            int year = calendar.get(1);
            int month = calendar.get(2);
            int day = calendar.get(5);
            JalaliCalendar.YearMonthDate yearMonthDate = new JalaliCalendar.YearMonthDate(year, month, day);
            yearMonthDate = JalaliCalendar.gregorianToJalali(yearMonthDate);
            return yearMonthDate;
        }
    }

    public static JalaliCalendar.YearMonthDate getYearMonthDate(Date date) {
        Calendar calendar = Calendar.getInstance(TimeZone.getTimeZone("Iran"), new Locale("fa_IR"));
        calendar.setTime(date);
        int year = calendar.get(1);
        int month = calendar.get(2);
        int day = calendar.get(5);
        JalaliCalendar.YearMonthDate yearMonthDate = new JalaliCalendar.YearMonthDate(year, month, day);
        yearMonthDate = JalaliCalendar.gregorianToJalali(yearMonthDate);
        return yearMonthDate;
    }

    public static Date addMonths(Date date, int amount) {
        JalaliCalendar jalaliCalendar = new JalaliCalendar(TimeZone.getTimeZone("Iran"), new Locale("fa_IR"), date);
        jalaliCalendar.add(2, amount);
        return jalaliCalendar.getTime();
    }

    public static Date addDays(Date date, int amount) {
        JalaliCalendar jalaliCalendar = new JalaliCalendar(TimeZone.getTimeZone("Iran"), new Locale("fa_IR"), date);
        jalaliCalendar.add(5, amount);
        return jalaliCalendar.getTime();
    }

    public static int compare(Date start, Date end) {
        start = startOfDay(start);
        end = startOfDay(end);
        org.joda.time.DateTime start1 = new org.joda.time.DateTime(start);
        org.joda.time.DateTime end1 = new org.joda.time.DateTime(end);
        int dif = Days.daysBetween(start1.toLocalDate(), end1.toLocalDate()).getDays();
        return dif;
    }

    public static int getMonthsBetween(Date startDate, Date endDate) {
        if (startDate.after(endDate)) {
            Date tempDate = startDate;
            startDate = endDate;
            endDate = tempDate;
        }

        JalaliCalendar.YearMonthDate startYearMonthDate = getYearMonthDate(startDate);
        JalaliCalendar.YearMonthDate endYearMonthDate = getYearMonthDate(endDate);
        if (startYearMonthDate.getYear() < endYearMonthDate.getYear()) {
            if (startYearMonthDate.getMonth() < endYearMonthDate.getMonth()) {
                return startYearMonthDate.getDate() < endYearMonthDate.getDate() ? (endYearMonthDate.getYear() - startYearMonthDate.getYear()) * 12 + endYearMonthDate.getMonth() - startYearMonthDate.getMonth() + 1 : (endYearMonthDate.getYear() - startYearMonthDate.getYear()) * 12 + endYearMonthDate.getMonth() - startYearMonthDate.getMonth();
            } else if (startYearMonthDate.getMonth() == endYearMonthDate.getMonth()) {
                return startYearMonthDate.getDate() < endYearMonthDate.getDate() ? (endYearMonthDate.getYear() - startYearMonthDate.getYear()) * 12 + 1 : (endYearMonthDate.getYear() - startYearMonthDate.getYear()) * 12;
            } else {
                return startYearMonthDate.getDate() < endYearMonthDate.getDate() ? 12 - startYearMonthDate.getMonth() + endYearMonthDate.getMonth() + 1 : 12 - startYearMonthDate.getMonth() + endYearMonthDate.getMonth();
            }
        } else if (startYearMonthDate.getMonth() < endYearMonthDate.getMonth()) {
            return startYearMonthDate.getDate() < endYearMonthDate.getDate() ? endYearMonthDate.getMonth() - startYearMonthDate.getMonth() + 1 : endYearMonthDate.getMonth() - startYearMonthDate.getMonth();
        } else {
            return startYearMonthDate.getDate() < endYearMonthDate.getDate() ? 1 : 0;
        }
    }

    public static int diffDates(Date start, Date end) {
         start = startOfDay(start);
         end = startOfDay(end);
        org.joda.time.DateTime start1 = new org.joda.time.DateTime(start);
        org.joda.time.DateTime end1 = new org.joda.time.DateTime(end);
        int dif = Math.abs(Days.daysBetween(start1.toLocalDate(), end1.toLocalDate()).getDays());
        return dif;
    }

    public static void main(String[] args) {
        Timestamp timestamp = new Timestamp(System.currentTimeMillis());
        System.out.println(timestamp);
        System.out.println(addDays(timestamp, 30));
    }
}
