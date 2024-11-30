package com.example.my_budget_tracker.data

import androidx.room.TypeConverter
import java.util.Date

/**
 * Converter class for Room database to handle `Date` objects.
 * Converts between `Date` and `Long` (timestamps) for database storage.
 */
class DateConverter {

    /**
     * Converts a timestamp (Long) to a `Date` object.
     * @param value The timestamp to convert.
     * @return A `Date` object or null if the timestamp is null.
     */
    @TypeConverter
    fun fromTimestamp(value: Long?): Date? {
        return value?.let { Date(it) }
    }

    /**
     * Converts a `Date` object to a timestamp (Long).
     * @param date The `Date` object to convert.
     * @return A timestamp or null if the `Date` is null.
     */
    @TypeConverter
    fun dateToTimestamp(date: Date?): Long? {
        return date?.time
    }
}
