package de.cubiclabs.mensax.models;

import android.os.Parcel;
import android.os.Parcelable;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class Meal {

    private static final DateFormat FORMAT_ORIGINAL = new SimpleDateFormat("yyyy-MM-dd", Locale.GERMAN);
    private static final DateFormat FORMAT_READABLE = new SimpleDateFormat("EE, d.MM.", Locale.GERMAN);

	public String name = "";
	public String category = "";
	public String price = "";
    public String datum = "";
    public int like = 0;
    public int dislike = 0;
    public int cafeteria = 0;
    public int id = 0;

	public Meal() {

	}

    public String getFormattedDate() {
        try {
            Date dateObj = FORMAT_ORIGINAL.parse(datum);
            return FORMAT_READABLE.format(dateObj);
        } catch (ParseException e) {
            return "";
        }
    }

}
