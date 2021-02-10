package com.example.notesapp.util;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public final class DateTimeUtil {

    private DateTimeUtil() {
    }

    public static String format(long time) {
        Date date = new Date(time);
        SimpleDateFormat sdf = new SimpleDateFormat("dd MMMM yyyy, HH:mm", Locale.getDefault());
        return sdf.format(date);
    }
}
