package org.pom.utils;

import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.pom.enums.LogLevelEnum;

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
        String currentTime = "";
        SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm:ss");
        Calendar time = Calendar.getInstance();
        currentTime = dateFormat.format(time.getTime()).replace(":", "-");
        return currentTime;
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
        String hour = "";
        String minutes = "";
        String second = "";
        String milliSeconds = "";
        if (diffHours < 10) hour = "0" + diffHours;
        else hour = String.valueOf(diffHours);
        if (diffMinutes < 10) minutes = "0" + diffMinutes;
        else minutes = String.valueOf(diffMinutes);
        if (diffSeconds < 10) second = "0" + diffSeconds;
        else second = String.valueOf(diffSeconds);
        if (diffMilliSeconds < 10) milliSeconds = "00" + diffMilliSeconds;
        else if (diffMilliSeconds < 100) milliSeconds = "0" + diffMilliSeconds;
        else milliSeconds = String.valueOf(diffMilliSeconds);
        return hour + ":" + minutes + ":" + second + ":" + milliSeconds;
    }

    public String getNextDate() {
        LocalDate date = LocalDate.now();
        date = date.plusDays(1);
        return date.toString();
    }

    public int getCurrentDay() {
        LocalDate date = LocalDate.now();
        return date.getDayOfMonth();
    }

    public String getCurrentTimeInHoursAndMinutes(String timeZone) {
        String hours = getHoursFromCurrentTime(timeZone);
        String minutes = getMinutesFromCurrentTime(timeZone);
        return hours + ":" + minutes;
    }

    public int getCurrentMonthValue() {
        LocalDate date = LocalDate.now();
        return date.getMonthValue();
    }

    public String getHoursFromCurrentTime(String timeZone) {
        DateTimeZone zone = DateTimeZone.forID(timeZone);
        DateTime dateTime = new DateTime(zone);
        String hours = String.valueOf(dateTime.getHourOfDay());
        if (hours.length() == 1) hours = "0" + hours;
        return hours;
    }

    public String getMinutesFromCurrentTime(String timeZone) {
        DateTimeZone zone = DateTimeZone.forID(timeZone);
        DateTime dateTime = new DateTime(zone);
        String minutes = String.valueOf(dateTime.getMinuteOfHour());
        if (minutes.length() == 1) minutes = "0" + minutes;
        return minutes;
    }

    private String getDate(String format, String timeZone, String timePeriod) {
        if (format.equals("")) format = "dd-mm-yyyy";
        DateTimeZone zone = DateTimeZone.forID(timeZone);
        DateTime dateTime = new DateTime(zone);
        if (timePeriod.equalsIgnoreCase(past)) dateTime = dateTime.minusDays(1);
        if (timePeriod.equalsIgnoreCase(future)) dateTime = dateTime.plusDays(1);
        String year = String.valueOf(dateTime.getYear());
        String month = String.valueOf(dateTime.getMonthOfYear());
        String day = String.valueOf(dateTime.getDayOfMonth());
        if (day.length() == 1) day = "0" + day;
        if (month.length() == 1) month = "0" + month;
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
        DateTimeZone zone = DateTimeZone.forID(timeZone);
        DateTime dateTime = new DateTime(zone);
        return dateTime.dayOfMonth().getAsText();
    }

    public String getMonthNameAsShortTextFromCurrentDate(String timeZone) {
        DateTimeZone zone = DateTimeZone.forID(timeZone);
        DateTime dateTime = new DateTime(zone);
        return dateTime.monthOfYear().getAsShortText();
    }

    public String getCurrentMonthName(String timeZone) {
        DateTimeZone zone = DateTimeZone.forID(timeZone);
        DateTime dateTime = new DateTime(zone);
        return dateTime.monthOfYear().getAsText();
    }

    public String getCurrentYear(String timeZone) {
        DateTimeZone zone = DateTimeZone.forID(timeZone);
        DateTime dateTime = new DateTime(zone);
        return dateTime.year().getAsText();
    }

    public String getFutureOrPastDate(int numberOfDays, int numberOfMonths, int numberofYears, String format) {
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DATE, numberOfDays);
        calendar.add(Calendar.MONTH, numberOfMonths);
        calendar.add(Calendar.YEAR, numberofYears);
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(format);
        return simpleDateFormat.format(calendar.getTime());
    }

    public String getTimeStampInSpecificTimeZone(String timeStampFormat, String timeStamp, String timeZone) {
        String timeStampWithRequiredTimezone = "";
        try {
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat(timeStampFormat);
            simpleDateFormat.setTimeZone(java.util.TimeZone.getTimeZone("GMT"));
            Date date = simpleDateFormat.parse(timeStamp);
            simpleDateFormat.setTimeZone(java.util.TimeZone.getTimeZone(timeZone));
            timeStampWithRequiredTimezone = simpleDateFormat.format(date);
        } catch (Exception e) {
            logger.log(Level.INFO, String.format("Error {%s} occurred due to unacceptable date format.", e));
        }
        return timeStampWithRequiredTimezone;
    }

    public String convertTimeStampIntoSpecificFormat(String givenFormat, String timeStamp, String requiredFormat) {
        Date date = null;
        String convertTimeStamp = "";
        try {
            date = new SimpleDateFormat(givenFormat).parse(timeStamp);
            convertTimeStamp = new SimpleDateFormat(requiredFormat).format(date);
        } catch (Exception e) {
            logger.log(Level.INFO, "Exception occurred in convertTimeStampIntoSpecificFormat method due to {%s} : ", e);
        }
        return convertTimeStamp;
    }

    public String getCurrentWeekDayName() {
        LocalDate date = LocalDate.now();
        return date.getDayOfWeek().getDisplayName(TextStyle.FULL, Locale.ENGLISH);
    }

    public String[] getNextTimeMultipleOfMinutes(String timeZone, int minuteMultiple, boolean includeCurrent) {
        DateTimeZone zone = DateTimeZone.forID(timeZone);
        DateTime now = new DateTime(zone);
        int minutes = now.getMinuteOfHour();
        int nextMultiple = (minutes % minuteMultiple == 0 && includeCurrent) ? minutes : ((minutes / minuteMultiple) + 1) * minuteMultiple;
        DateTime nextTime = now.withMinuteOfHour(nextMultiple % 60).withSecondOfMinute(0).withMillisOfSecond(0);
        if (nextMultiple >= 60) {
            nextTime = nextTime.plusHours(1);
        }
        String hour = nextTime.getHourOfDay() < 10 ? "0" + nextTime.getHourOfDay() : String.valueOf(nextTime.getHourOfDay());
        String min = nextTime.getMinuteOfHour() < 10 ? "0" + nextTime.getMinuteOfHour() : String.valueOf(nextTime.getMinuteOfHour());
        ReportUtils.getInstance().reportStepWithoutScreenshot("Next Multiple of 15min time is: " + hour + ":" + min, LogLevelEnum.INFO);
        return new String[]{hour, min};
    }

    public long convertToEpochTime(int hours, int minutes, String timeZone) {
        DateTimeZone zone = DateTimeZone.forID(timeZone);
        DateTime now = new DateTime(zone);
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