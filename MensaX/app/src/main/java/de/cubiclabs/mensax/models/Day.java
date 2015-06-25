package de.cubiclabs.mensax.models;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class Day{

	private static final DateFormat FORMAT_ORIGINAL = new SimpleDateFormat("yyyy-MM-dd", Locale.GERMAN);
	private static final DateFormat FORMAT_READABLE = new SimpleDateFormat("EEEE, d.MM.", Locale.GERMAN);

	public String mDatum = "";
    public List<Meal> mMeals;

	public Day() {
        mMeals = new ArrayList<Meal>();
	}


	public String getFormattedDate() {
		try {
			Date dateObj = FORMAT_ORIGINAL.parse(mDatum);
			return FORMAT_READABLE.format(dateObj);
		} catch (ParseException e) {
			return "";
		}
	}

	@Override
	public String toString() {
		return "Day: " + mDatum + ", Size: " + mMeals.size();
	}
}
