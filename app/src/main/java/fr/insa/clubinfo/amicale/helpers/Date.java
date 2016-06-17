package fr.insa.clubinfo.amicale.helpers;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.Locale;

/**
 * Created by Proïd on 05/06/2016.
 */

@SuppressWarnings("CloneDoesntCallSuperClone")
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
    public static Date today(int hour, int minute) {
        Date d = new Date(new GregorianCalendar());
        d.date.set(GregorianCalendar.HOUR_OF_DAY, hour);
        d.date.set(GregorianCalendar.MINUTE, minute);
        return d;
    }
    public static Date todayAfter(int hour, int minute, Date dateBefore) {
        Date d = today(hour, minute);
        if(dateBefore.date.after(d)) {
            d.date.add(Calendar.DAY_OF_MONTH, 1);
        }
        return d;
    }

    public String toText() {
        return sdf.format(date.getTime());
    }

    public GregorianCalendar getDate() {
        return date;
    }

    @Override
    public Object clone() {
        GregorianCalendar gc = new GregorianCalendar(date.get(GregorianCalendar.YEAR),
                date.get(GregorianCalendar.MONTH),
                date.get(GregorianCalendar.DAY_OF_MONTH),
                date.get(GregorianCalendar.HOUR_OF_DAY),
                date.get(GregorianCalendar.MINUTE));
        return new Date(gc);
    }

    public void subtract(int minutes) {
        date.add(GregorianCalendar.MINUTE, -minutes);
    }

    public boolean isBeforeNow() {
        return !date.after(new GregorianCalendar());
    }

    public long getTimeInMillis() {
        return date.getTimeInMillis();
    }
}
