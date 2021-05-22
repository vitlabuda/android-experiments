package cz.vitlabuda.test.mvvmnotepad.arch;

import androidx.room.TypeConverter;

import java.util.Date;

public class Converters {
    @TypeConverter
    public static Date fromTimestampToDate(long timestamp) {
        return new Date(timestamp);
    }

    @TypeConverter
    public static long fromDateToTimestamp(Date date) {
        return date.getTime();
    }
}
