package WeatherApp;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.URL;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.ZoneId;
import java.util.*;

/**
 * Created by Asus on 2018-04-29.
 */
public class GetWeather {

    public static Map <String, Object> getWeather(String cityName) throws IOException, JSONException {

        final String CELSIUS_DEGREE  = "\u00b0C";

        String correctCityName = cityName.replace("ó", "o");
        String site_base = "https://query.yahooapis.com/v1/public/yql";
        String yql = "select * from weather.forecast where woeid in ( select woeid from geo.places where " +
                "text=\"" + correctCityName + "\" limit 1) and u=\"c\"";
        URLParamEncoder encodedYql = new URLParamEncoder();
        yql = encodedYql.encode(yql);

        String site = site_base + "?q=" + yql + "&format=json";

        URL url = new URL(site);
        Scanner jsonFile = new Scanner(url.openStream());
        String str = new String();
        str = "[";
        while (jsonFile.hasNext()){
            str += jsonFile.nextLine();
            str += "\n";
        }
        str += "]";
        jsonFile.close();

        JSONArray array = new JSONArray(str);

        Map <String, String> currentData = new HashMap <String, String>();
        ArrayList <Object> dailyData = new ArrayList <Object>();

        Map <String, Object> weatherData = new HashMap <String, Object>();

        currentData.put("icon", array.getJSONObject(0)
                                    .getJSONObject("query")
                                    .getJSONObject("results")
                                    .getJSONObject("channel")
                                    .getJSONObject("item")
                                    .getJSONObject("condition")
                                    .getString("code")+"");
        int currentTemp = (int) array.getJSONObject(0)
                                    .getJSONObject("query")
                                    .getJSONObject("results")
                                    .getJSONObject("channel")
                                    .getJSONObject("item")
                                    .getJSONObject("condition")
                                    .getLong("temp");
        currentData.put("temp", currentTemp + CELSIUS_DEGREE);
        weatherData.put("current", currentData);

        JSONArray daily = array.getJSONObject(0)
                            .getJSONObject("query")
                            .getJSONObject("results")
                            .getJSONObject("channel")
                            .getJSONObject("item")
                            .getJSONArray("forecast");

        DateFormat dateFormat = new SimpleDateFormat("dd/MM");
        Calendar cal = Calendar.getInstance();

        for (int i = 0 ; i < 7; i++){
            Map <String, String> dailyDataPoint = new HashMap <String, String> ();

            WeekdayName polishName = new WeekdayName();

                if(i == 0)  dailyDataPoint.put("day", "Dzisiaj" + ", " + dateFormat.format(cal.getTime()));
                else dailyDataPoint.put("day", polishName.toPolish(daily
                                                                    .getJSONObject(i)
                                                                    .getString("day"))
                                                                    + ", " + dateFormat.format(cal.getTime()));

                int dailyHigh = (int) daily.getJSONObject(i).getLong("high");
                dailyDataPoint.put("high", dailyHigh + CELSIUS_DEGREE);

                int dailyLow = (int) daily.getJSONObject(i).getLong("low");
                dailyDataPoint.put("low", dailyLow + CELSIUS_DEGREE);

                dailyDataPoint.put("icon", daily.getJSONObject(i).getString("code"));

                dailyData.add(dailyDataPoint);

                cal.add(Calendar.DATE, 1);
    }

        weatherData.put("daily", dailyData);
        return weatherData;
    }
}







