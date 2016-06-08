package fr.insa.clubinfo.amicale.helpers;

import java.text.SimpleDateFormat;
import java.util.GregorianCalendar;
import java.util.Locale;

/**
 * Created by Proïd on 05/06/2016.
 */

public class Date {
    private static final SimpleDateFormat sdf = new SimpleDateFormat("ccc d LLL yyyy", Locale.getDefault());

    private final GregorianCalendar date;

    public Date(GregorianCalendar date) {
        this.date = date;
    }

    public Date(int dayOfMonth, int month, int year) {
        date = new GregorianCalendar(Locale.getDefault());
        date.set(GregorianCalendar.YEAR, year);
        date.set(GregorianCalendar.MONTH, month-1+GregorianCalendar.JANUARY);
        date.set(GregorianCalendar.DAY_OF_MONTH, dayOfMonth);
    }

    public String toText() {
        return sdf.format(date.getTime());
    }
}
