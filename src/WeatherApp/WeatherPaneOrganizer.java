package WeatherApp;

import com.maxmind.geoip2.WebServiceClient;
import com.maxmind.geoip2.exception.GeoIp2Exception;
import com.maxmind.geoip2.model.CityResponse;
import com.maxmind.geoip2.record.City;
import com.maxmind.geoip2.record.Country;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.text.TextAlignment;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.InetAddress;
import java.net.URL;
import java.util.ArrayList;
import java.util.Map;
import java.util.Scanner;

/**
 * Created by Asus on 2018-04-29.
 */

public class WeatherPaneOrganizer {

    private static final int WIDTH = 450, HEIGHT = 300;
    private static final String FX_FONT_SIZE = "-fx-font-size: ",
                                FX_TEXT_COLOR = "-fx-text-fill: ";
    private static final String INVALID_CITY_NAME = "Miasto nie zostało znalezione!";
    BorderPane root;
    String cityName;
    Map<String, Object> weatherData;

    public WeatherPaneOrganizer(String cityName) throws IOException, JSONException{

        root = new BorderPane();
        root.setPrefSize(WIDTH, HEIGHT);
        this.cityName = cityName;

       /*
        ****************************************************************************************
        * Geolocalization - working properly only with correct IP passed in a getByName function
        ****************************************************************************************

        String countryDefault = new String();
        String cityDefault = new String();

        try (WebServiceClient client = new WebServiceClient.Builder(132942, "o3dfDh32R6x9").build()) {

            InetAddress ipAddress = InetAddress.getByName("188.40.110.12");
            //InetAddress ipAddress = InetAddress.getLocalHost();

            CityResponse response = client.city(ipAddress);

            Country country = response.getCountry();
            countryDefault = country.getName();

            City city = response.getCity();
            cityDefault = city.getName();

        } catch (GeoIp2Exception e) {
            e.printStackTrace();
        }

        String receivedCityName = cityDefault + " , " + countryDefault;
        if(receivedCityName.matches(".*null.*"))
        */

        weatherData = GetWeather.getWeather(cityName);

        setTop();
        setCurrent(); // in left pane
        setDaily(); // in right pane
    }

    public Pane getRoot() {
        return root;
    }

    private void setTop() {
        // Main top is a Horizontal box with 3 items
        HBox top = new HBox();
        root.setTop(top);
        HBox.setMargin(top, new Insets(5)); // Sets overall margins for box
        top.setAlignment(Pos.CENTER);

        // City label
        Label city = new Label(cityName);
        city.setStyle(FX_TEXT_COLOR + "white;" + FX_FONT_SIZE + "30;");

        // Exit button - option
        /*Button exitButton = new Button();
        exitButton.setText("Wyjście");
        HBox.setMargin(exitButton, new Insets(0, 0, 0, 10));
        exitButton.setOnAction(e-> Platform.exit());
        */

        Button changeButton = new Button();
        HBox.setMargin(changeButton, new Insets(0, 0, 0, 10));
        changeButton.setText("Zmiana miasta");
        changeButton.setOnAction(e-> {
            try {
                changeCity();
            } catch (IOException ex) {
                ex.printStackTrace();
            } catch (JSONException ex) {
                ex.printStackTrace();
            }
        });

        top.getChildren().addAll(city, changeButton); // exitButton optionally
    }

    private void changeCity() throws IOException, JSONException {

        MessageBox newCity = new MessageBox();
        newCity.show();
        String nameOfCity = newCity.getCityName();
        String encodedCityName;
        URLParamEncoder encoder = new URLParamEncoder();
        encodedCityName = encoder.encode(nameOfCity);
        boolean result = encodedCityName.matches(".*F3*.");
        if(result) {
            encodedCityName = encodedCityName.replace("F3", "3F");
        }
        String site = "https://maps.googleapis.com/maps/api/geocode/json?address=" + encodedCityName +
                "&language=pl-PL&key=AIzaSyD5GAa5nlD4oPrhTM6t6ZAqurx2jhvVjfI";

        URL url = new URL(site);

        Scanner jsonFile = new Scanner(url.openStream());
        String str = new String();
        while (jsonFile.hasNext())
            str += jsonFile.nextLine();
        jsonFile.close();
        JSONObject obj = new JSONObject(str);

        if(obj.getString("status").equals("OK")){
            String city;
            String country;
            int addressComponentsLength;
            String addressType;

            addressComponentsLength = obj.getJSONArray("results")
                    .getJSONObject(0)
                    .getJSONArray("address_components").length();

            addressType = obj.getJSONArray("results")
                    .getJSONObject(0)
                    .getJSONArray("address_components")
                    .getJSONObject(addressComponentsLength-1)
                    .getJSONArray("types")
                    .getString(0);

            if(addressType.equals("country")){
                country = obj.getJSONArray("results")
                        .getJSONObject(0)
                        .getJSONArray("address_components")
                        .getJSONObject(addressComponentsLength-1)
                        .getString("long_name");
            } else {
                country = obj.getJSONArray("results")
                        .getJSONObject(0)
                        .getJSONArray("address_components")
                        .getJSONObject(addressComponentsLength-2)
                        .getString("long_name");
            }

            if(country.length() > 16){
                switch(country){
                    case "Zjednoczone Emiraty Arabskie":
                        country = "ZEA";
                        break;
                    case "Wybrzeże Kości Słoniowej":
                        country = "WKS";
                        break;
                    case "Stany Zjednoczone":
                        country = "USA";
                        break;
                    case "Republika Południowej Afryki":
                        country = "RPA";
                        break;
                    case "Demokratyczna Republika Konga":
                        country = "DR Konga";
                        break;
                    case "Bośnia i Hercegowina":
                        country = "Bośnia i Herc.";
                        break;
                    default:
                        country = obj.getJSONArray("results")
                                .getJSONObject(0)
                                .getJSONArray("address_components")
                                .getJSONObject(addressComponentsLength-1)
                                .getString("short_name");
                }
            }

            city = obj.getJSONArray("results")
                    .getJSONObject(0)
                    .getJSONArray("address_components")
                    .getJSONObject(0)
                    .getString("long_name");

            cityName = city + ", " + country;

            weatherData = GetWeather.getWeather(cityName);

        } else {
            cityName = INVALID_CITY_NAME;
        }

        setTop();
        setCurrent();
        setDaily();
    }

    private void setCurrent() {

        // Main layout is a VBox
        VBox currentPane = new VBox();
        currentPane.setPrefWidth(WIDTH * 0.3);
        currentPane.setAlignment(Pos.CENTER);
        currentPane.setPadding(new Insets(5));

        VBox temperatureBox = new VBox();
        temperatureBox.setAlignment(Pos.CENTER);

        Map<String, String> currentWeather = (Map<String, String>) weatherData.get("current");

        // Top box is an HBox with the label and temperature
        Label currently = new Label("Aktualnie:");
        currently.setStyle(FX_TEXT_COLOR + "white;" + FX_FONT_SIZE + "26;");
        currently.setMaxHeight(Double.MAX_VALUE);

        Label currentTemperature = new Label(currentWeather.get("temp"));
        currentTemperature.setStyle(FX_TEXT_COLOR + "white;" + FX_FONT_SIZE + "30;");
        temperatureBox.getChildren().addAll(currently, currentTemperature);
        HBox.setMargin(temperatureBox, new Insets(10));

        // Bottom box is the status image. The image location comes from the StatusImage class.
        Image currentImageIcon = new Image(StatusImage.getImage(currentWeather.get("icon")));
        ImageView currentImage = new ImageView(currentImageIcon);
        Node summaryImage = currentImage;
        VBox.setMargin(summaryImage, new Insets(0, 0, 10, 0));

        currentPane.getChildren().addAll(temperatureBox, summaryImage);
        root.setLeft(currentPane);
    }

    private void setDaily(){

        // Main layout is a VBox
        VBox dailyPane = new VBox();
        dailyPane.setPrefWidth(WIDTH * 0.7);
        dailyPane.setAlignment(Pos.CENTER);
        dailyPane.setPadding(new Insets(5));

        Label forecast = new Label("Prognoza tygodniowa");
        forecast.setStyle(FX_TEXT_COLOR + "white;" + FX_FONT_SIZE + "18;");
        forecast.setPrefWidth(WIDTH);
        forecast.setAlignment(Pos.TOP_CENTER);
        dailyPane.getChildren().add(forecast);

        ArrayList< Map <String, String> > dailyForecast = (ArrayList <Map <String, String> >) weatherData.get("daily");

        for (int i = 0; i < dailyForecast.size(); i++){

            HBox daily = new HBox();

            Label day = new Label(dailyForecast.get(i).get("day"));
            day.setStyle(FX_TEXT_COLOR + "white;" + FX_FONT_SIZE + "15;");
            day.setMaxHeight(Double.MAX_VALUE);
            day.setPrefWidth(WIDTH * 5 / 16);

            Label high = new Label(dailyForecast.get(i).get("high"));
            high.setStyle(FX_TEXT_COLOR + "red;" + FX_FONT_SIZE + "15;");
            high.setMaxHeight(Double.MAX_VALUE);
            high.setPrefWidth(WIDTH * 2 / 16);

            Label low = new Label(dailyForecast.get(i).get("low"));
            low.setStyle(FX_TEXT_COLOR + "blue;" + FX_FONT_SIZE + "15;");
            low.setMaxHeight(Double.MAX_VALUE);
            low.setPrefWidth(WIDTH * 2 / 16);

            Image dailyImageIcon = new Image(StatusImage.getImage(dailyForecast.get(i).get("icon")));
            ImageView currentdailyImage = new ImageView(dailyImageIcon);
            Node dailyImage = currentdailyImage;
            VBox.setMargin(dailyImage, new Insets(5, 5, 10, 5));

            daily.getChildren().addAll(day, high, low, dailyImage);
            dailyPane.getChildren().add(daily);
        }

        root.setCenter(dailyPane);
    }
}



































