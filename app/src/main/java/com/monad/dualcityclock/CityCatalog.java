package com.monad.dualcityclock;

import android.content.Context;
import android.content.SharedPreferences;

final class CityCatalog {
    static final String PREFS = "dual_city_clock_prefs";
    static final String FIRST = "first_city";
    static final String SECOND = "second_city";

    static final class City {
        final String label;
        final String zone;
        City(String label, String zone) {
            this.label = label;
            this.zone = zone;
        }
        @Override public String toString() { return label; }
    }

    static final City[] CITIES = new City[] {
        new City("Melbourne", "Australia/Melbourne"),
        new City("Tehran", "Asia/Tehran"),
        new City("Sydney", "Australia/Sydney"),
        new City("Canberra", "Australia/Sydney"),
        new City("Brisbane", "Australia/Brisbane"),
        new City("Perth", "Australia/Perth"),
        new City("Adelaide", "Australia/Adelaide"),
        new City("Hobart", "Australia/Hobart"),
        new City("Darwin", "Australia/Darwin"),
        new City("Auckland", "Pacific/Auckland"),
        new City("Wellington", "Pacific/Auckland"),
        new City("Dubai", "Asia/Dubai"),
        new City("Istanbul", "Europe/Istanbul"),
        new City("London", "Europe/London"),
        new City("Paris", "Europe/Paris"),
        new City("Berlin", "Europe/Berlin"),
        new City("Rome", "Europe/Rome"),
        new City("Madrid", "Europe/Madrid"),
        new City("Amsterdam", "Europe/Amsterdam"),
        new City("Zurich", "Europe/Zurich"),
        new City("Singapore", "Asia/Singapore"),
        new City("Tokyo", "Asia/Tokyo"),
        new City("Seoul", "Asia/Seoul"),
        new City("Hong Kong", "Asia/Hong_Kong"),
        new City("Shanghai", "Asia/Shanghai"),
        new City("Bangkok", "Asia/Bangkok"),
        new City("Mumbai", "Asia/Kolkata"),
        new City("Delhi", "Asia/Kolkata"),
        new City("Karachi", "Asia/Karachi"),
        new City("Riyadh", "Asia/Riyadh"),
        new City("Cairo", "Africa/Cairo"),
        new City("Johannesburg", "Africa/Johannesburg"),
        new City("New York", "America/New_York"),
        new City("Washington DC", "America/New_York"),
        new City("Toronto", "America/Toronto"),
        new City("Chicago", "America/Chicago"),
        new City("Denver", "America/Denver"),
        new City("Los Angeles", "America/Los_Angeles"),
        new City("San Francisco", "America/Los_Angeles"),
        new City("Vancouver", "America/Vancouver"),
        new City("Mexico City", "America/Mexico_City"),
        new City("Sao Paulo", "America/Sao_Paulo")
    };

    static SharedPreferences prefs(Context context) {
        return context.getSharedPreferences(PREFS, Context.MODE_PRIVATE);
    }

    static int firstIndex(Context context) { return validIndex(prefs(context).getInt(FIRST, 0), 0); }
    static int secondIndex(Context context) { return validIndex(prefs(context).getInt(SECOND, 1), 1); }
    private static int validIndex(int index, int defaultIndex) {
        return index >= 0 && index < CITIES.length ? index : defaultIndex;
    }

    static void save(Context context, int first, int second) {
        prefs(context).edit()
            .putInt(FIRST, validIndex(first, 0))
            .putInt(SECOND, validIndex(second, 1))
            .apply();
    }

    private CityCatalog() {}
}
