package de.cubiclabs.mensax.parser;

import android.app.Application;
import android.content.Context;

import com.google.android.gms.analytics.HitBuilders;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.StringWriter;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import de.cubiclabs.mensax.MyApplication;
import de.cubiclabs.mensax.R;
import de.cubiclabs.mensax.models.Day;
import de.cubiclabs.mensax.models.Meal;
import de.cubiclabs.mensax.util.CryptoUtils;

public class CafeteriaDOMParser {

    private Application mApplication;
    private int mCafeteriaId;

    public CafeteriaDOMParser(Application app, int cafeteriaId) {
        mApplication = app;
        mCafeteriaId = cafeteriaId;
    }

	public List<Day> parse(InputStream stream) {
        List<Day> days;

		Document document = null;
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        try {
            //InputStream stream = new ByteArrayInputStream(content.getBytes("ISO-8859-1"));
            DocumentBuilder db = factory.newDocumentBuilder();
            //InputSource inputSource = new InputSource(stream);
            document = db.parse(stream);
        } catch (Exception e) {
            return null;
        }
        
        NodeList dayNodeList = document.getElementsByTagName("day");
        
        int amountNodes = dayNodeList.getLength();
        days = new ArrayList<Day>(amountNodes);
        for(int i=0; i<amountNodes; i++) {
        	Element dayNode = (Element) dayNodeList.item(i);
            Day day = new Day();


            if(dayNode.hasAttribute("date")) {
                day.mDatum = dayNode.getAttribute("date");
            }

            NodeList mealNodeList = dayNode.getElementsByTagName("meal");
            for(int j=0; j<mealNodeList.getLength(); j++) {
                Element mealNode = (Element) mealNodeList.item(j);

                try {
                    Meal meal = new Meal();
                    meal.name = getValue(mealNode, "name");
                    meal.category = getValue(mealNode, "category");
                    meal.price = getValue(mealNode, "price");
                    meal.datum = getValue(mealNode, "datum");
                    meal.id = Integer.parseInt(getValue(mealNode, "id"));
                    meal.cafeteria = Integer.parseInt(getValue(mealNode, "cafeteria"));
                    meal.uid = CryptoUtils.md5(meal.category.toLowerCase(Locale.GERMAN) + meal.name.toLowerCase(Locale.GERMAN));
                    if(meal.name.length() > 0) day.mMeals.add(meal);
                } catch (Exception e) {}
            }
            days.add(day);
        }

        // Report parser errors: Only 1 meal (Hinweis: Keine Gerichte verfügbar) oder 0 meals per day
        boolean noMeals = true;
        for(Day day : days) {
            if(day.mMeals.size() > 1) {
                noMeals = false;
                break;
            }
        }
        if(noMeals) {
            ((MyApplication)mApplication).mTracker.send(new HitBuilders.EventBuilder()
                    .setCategory("NoMeals")
                    .setAction(mApplication.getString(R.string.app_name))
                    .setLabel("CafeteriaId: " + mCafeteriaId)
                    .setValue(1)
                    .build());
        }

        return days;
	}

	private static String getValue(Element item, String name) {
		try {
	        NodeList nodes = item.getElementsByTagName(name);
	        return CafeteriaDOMParser.getTextNodeValue(nodes.item(0));
		} catch(Exception e) {
			return "";
		}
    }
 
    private static final String getTextNodeValue(Node node) {
        Node child;
        if (node != null) {
            if (node.hasChildNodes()) {
                child = node.getFirstChild();
                while(child != null) {
                    if (child.getNodeType() == Node.TEXT_NODE) {
                        return child.getNodeValue();
                    }
                    child = child.getNextSibling();
                }
            }
        }
        return "";
    }

}
