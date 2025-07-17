package org.pom.utils;

import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;

import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.TextStyle;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;

import static org.pom.base.BaseTest.logger;

public class DateTimeUtils {
    String future = "future";
    String past = "past";
    String current = "current";

    public static DateTimeUtils getInstance() {
        return new DateTimeUtils();
    }

    public Date getCurrentTime(long millis) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(millis);
        return calendar.getTime();
    }

    public String getCurrentTime() {
        SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm:ss");
        Calendar time = Calendar.getInstance();
        return dateFormat.format(time.getTime()).replace(":", "-");
    }

    public String getCurrentDate(String format) {
        return getDate(current, format);
    }

    public String getPastDate(String format) {
        return getDate(past, format);
    }

    public String getFutureDate(String format) {
        return getDate(future, format);
    }

    private String getDate(String timePeriod, String format) {
        String date = "";
        try {
            SimpleDateFormat dateFormat = new SimpleDateFormat(format);
            Calendar currentDate = Calendar.getInstance();
            if (timePeriod.equalsIgnoreCase(past))
                currentDate.add(Calendar.DATE, -1);
            if (timePeriod.equalsIgnoreCase(future))
                currentDate.add(Calendar.DATE, 1);
            date = dateFormat.format(currentDate.getTime());
        } catch (Exception e) {
            logger.log(Level.INFO, String.format("Error {%s} occurred due to unacceptable date format.", e));
        }
        return date;
    }

    public String getTimeDifference(Date startDateWithTime, Date endDateWithTime) {
        long difference = endDateWithTime.getTime() - startDateWithTime.getTime();
        long diffMilliSeconds = TimeUnit.MILLISECONDS.toMillis(difference) % 1000;
        long diffSeconds = TimeUnit.MILLISECONDS.toSeconds(difference) % 60;
        long diffMinutes = TimeUnit.MILLISECONDS.toMinutes(difference) % 60;
        long diffHours = TimeUnit.MILLISECONDS.toHours(difference) % 24;

        String hour = (diffHours < 10) ? "0" + diffHours : String.valueOf(diffHours);
        String minutes = (diffMinutes < 10) ? "0" + diffMinutes : String.valueOf(diffMinutes);
        String second = (diffSeconds < 10) ? "0" + diffSeconds : String.valueOf(diffSeconds);
        String milliSeconds = (diffMilliSeconds < 10) ? "00" + diffMilliSeconds
                : (diffMilliSeconds < 100) ? "0" + diffMilliSeconds : String.valueOf(diffMilliSeconds);

        return hour + ":" + minutes + ":" + second + ":" + milliSeconds;
    }

    public String getNextDate() {
        return LocalDate.now().plusDays(1).toString();
    }

    public int getCurrentDay() {
        return LocalDate.now().getDayOfMonth();
    }

    public String getCurrentTimeInHoursAndMinutes(String timeZone) {
        return getHoursFromCurrentTime(timeZone) + ":" + getMinutesFromCurrentTime(timeZone);
    }

    public int getCurrentMonthValue() {
        return LocalDate.now().getMonthValue();
    }

    public String getHoursFromCurrentTime(String timeZone) {
        DateTime dateTime = new DateTime(DateTimeZone.forID(timeZone));
        String hours = String.valueOf(dateTime.getHourOfDay());
        return (hours.length() == 1) ? "0" + hours : hours;
    }

    public String getMinutesFromCurrentTime(String timeZone) {
        DateTime dateTime = new DateTime(DateTimeZone.forID(timeZone));
        String minutes = String.valueOf(dateTime.getMinuteOfHour());
        return (minutes.length() == 1) ? "0" + minutes : minutes;
    }

    private String getDate(String format, String timeZone, String timePeriod) {
        if (format.equals("")) format = "dd-mm-yyyy";
        DateTime dateTime = new DateTime(DateTimeZone.forID(timeZone));

        if (timePeriod.equalsIgnoreCase(past)) dateTime = dateTime.minusDays(1);
        if (timePeriod.equalsIgnoreCase(future)) dateTime = dateTime.plusDays(1);

        String year = String.valueOf(dateTime.getYear());
        String month = String.format("%02d", dateTime.getMonthOfYear());
        String day = String.format("%02d", dateTime.getDayOfMonth());

        format = format.toUpperCase();
        return format.replace("YYYY", year).replace("MM", month).replace("DD", day);
    }

    public String getCurrentDate(String format, String timeZone) {
        return getDate(format, timeZone, current);
    }

    public String getPastDate(String format, String timeZone) {
        return getDate(format, timeZone, past);
    }

    public String getFutureDate(String format, String timeZone) {
        return getDate(format, timeZone, future);
    }

    public String getDayOfMonthCurrentDate(String timeZone) {
        return new DateTime(DateTimeZone.forID(timeZone)).dayOfMonth().getAsText();
    }

    public String getMonthNameAsShortTextFromCurrentDate(String timeZone) {
        return new DateTime(DateTimeZone.forID(timeZone)).monthOfYear().getAsShortText();
    }

    public String getCurrentMonthName(String timeZone) {
        return new DateTime(DateTimeZone.forID(timeZone)).monthOfYear().getAsText();
    }

    public String getCurrentYear(String timeZone) {
        return new DateTime(DateTimeZone.forID(timeZone)).year().getAsText();
    }

    public String getFutureOrPastDate(int numberOfDays, int numberOfMonths, int numberofYears, String format) {
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DATE, numberOfDays);
        calendar.add(Calendar.MONTH, numberOfMonths);
        calendar.add(Calendar.YEAR, numberofYears);
        return new SimpleDateFormat(format).format(calendar.getTime());
    }

    public String getTimeStampInSpecificTimeZone(String timeStampFormat, String timeStamp, String timeZone) {
        String result = "";
        try {
            SimpleDateFormat sdf = new SimpleDateFormat(timeStampFormat);
            sdf.setTimeZone(TimeZone.getTimeZone("GMT"));
            Date date = sdf.parse(timeStamp);
            sdf.setTimeZone(TimeZone.getTimeZone(timeZone));
            result = sdf.format(date);
        } catch (Exception e) {
            logger.log(Level.INFO, String.format("Error {%s} occurred due to unacceptable date format.", e));
        }
        return result;
    }

    public String convertTimeStampIntoSpecificFormat(String givenFormat, String timeStamp, String requiredFormat) {
        String result = "";
        try {
            Date date = new SimpleDateFormat(givenFormat).parse(timeStamp);
            result = new SimpleDateFormat(requiredFormat).format(date);
        } catch (Exception e) {
            logger.log(Level.INFO, "Exception occurred in convertTimeStampIntoSpecificFormat method due to {%s} : ", e);
        }
        return result;
    }

    public String getCurrentWeekDayName() {
        return LocalDate.now().getDayOfWeek().getDisplayName(TextStyle.FULL, Locale.ENGLISH);
    }

    public String[] getNextTimeMultipleOfMinutes(String timeZone, int minuteMultiple, boolean includeCurrent) {
        DateTime now = new DateTime(DateTimeZone.forID(timeZone));
        int minutes = now.getMinuteOfHour();
        int nextMultiple = (minutes % minuteMultiple == 0 && includeCurrent)
                ? minutes
                : ((minutes / minuteMultiple) + 1) * minuteMultiple;

        DateTime nextTime = now.withMinuteOfHour(nextMultiple % 60).withSecondOfMinute(0).withMillisOfSecond(0);
        if (nextMultiple >= 60) nextTime = nextTime.plusHours(1);

        String hour = nextTime.getHourOfDay() < 10 ? "0" + nextTime.getHourOfDay() : String.valueOf(nextTime.getHourOfDay());
        String min = nextTime.getMinuteOfHour() < 10 ? "0" + nextTime.getMinuteOfHour() : String.valueOf(nextTime.getMinuteOfHour());

        System.out.println("Next Multiple of " + minuteMultiple + "min time is: " + hour + ":" + min);
        return new String[]{hour, min};
    }

    public long convertToEpochTime(int hours, int minutes, String timeZone) {
        DateTime now = new DateTime(DateTimeZone.forID(timeZone));
        DateTime dateTime = now.withHourOfDay(hours).withMinuteOfHour(minutes).withSecondOfMinute(0).withMillisOfSecond(0);
        return dateTime.toDateTime(DateTimeZone.UTC).getMillis() / 1000;
    }

    public String convertEpochToISOFormat(long epochTime) {
        String isoFormat = "";
        try {
            Date date = new Date(epochTime);
            SimpleDateFormat isoDateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
            isoDateFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
            isoFormat = isoDateFormat.format(date);
        } catch (Exception e) {
            logger.log(Level.INFO, "Exception occurred in convertEpochToISOFormat method due to {%s} : ", e);
        }
        return isoFormat;
    }

    public String getCurrentTimeIn12HourFormat(String timeZone) {
        return getFutureOrPastTimeIn12HourFormat(timeZone, 0, 0);
    }

    public String getFutureOrPastTimeIn12HourFormat(String timeZone, int minutesToAdd, int hoursToAdd) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("h:mm a", Locale.ENGLISH);
        dateFormat.setTimeZone(TimeZone.getTimeZone(timeZone));
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.MINUTE, minutesToAdd);
        calendar.add(Calendar.HOUR, hoursToAdd);
        return dateFormat.format(calendar.getTime());
    }
}
