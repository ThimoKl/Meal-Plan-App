package de.cubiclabs.mensax.models;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

public class Day extends ArrayList<Meal>{

	private static final DateFormat FORMAT_ORIGINAL = new SimpleDateFormat("yyyy-MM-dd", Locale.GERMAN);
	private static final DateFormat FORMAT_READABLE = new SimpleDateFormat("EEEE, d.MM.", Locale.GERMAN);

	public String mDatum = "";

	public Day() {
	}


	public String getFormattedDate() {
		try {
			Date dateObj = FORMAT_ORIGINAL.parse(mDatum);
			return FORMAT_READABLE.format(dateObj);
		} catch (ParseException e) {
			return "";
		}
	}
	
}
