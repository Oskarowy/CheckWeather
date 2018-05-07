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
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
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

    private static final int WIDTH = 1000, HEIGHT = 600;
    private static final String FX_BACKGROUND = "-fx-background-color: ",
                                FX_FONT_SIZE = "-fx-font-size: ",
                                FX_TEXT_COLOR = "-fx-text-fill: ";
    private static final String INVALID_CITY_NAME = "Miasto nie zostało znalezione!";
    BorderPane root;
    String cityName;
    Map<String, Object> weatherData;

    public WeatherPaneOrganizer() throws IOException, JSONException{

        root = new BorderPane();
        root.setStyle(FX_BACKGROUND + "blue;");
        root.setPrefSize(WIDTH, HEIGHT);

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
        if(receivedCityName.matches(".*null.*")) cityName = "Warszawa, Polska";

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
        HBox.setMargin(top, new Insets(15, 12, 15, 12)); // Sets overall margins for box
        top.setAlignment(Pos.CENTER);
        top.setStyle(FX_BACKGROUND + "#016367;");

        // City label
        Label city = new Label(cityName);
        city.setStyle(FX_TEXT_COLOR + "white;" + FX_FONT_SIZE + "40;");

        // Exit button
        Button exitButton = new Button();
        exitButton.setText("Wyjście");
        HBox.setMargin(exitButton, new Insets(0, 0, 0, 20));
        exitButton.setOnAction(e-> Platform.exit());

        Button changeButton = new Button();
        HBox.setMargin(changeButton, new Insets(0, 0, 0, 20));
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

        top.getChildren().addAll(city, changeButton, exitButton);
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
            cityName = obj.getJSONArray("results")
                    .getJSONObject(0)
                    .getString("formatted_address");

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
        currentPane.setPrefWidth(WIDTH * 0.25);
        currentPane.setStyle(FX_BACKGROUND + "#44af92;");
        currentPane.setAlignment(Pos.CENTER);
        currentPane.setPadding(new Insets(15));

        HBox temperatureBox = new HBox();
        temperatureBox.setAlignment(Pos.CENTER);

        Map<String, String> currentWeather = (Map<String, String>) weatherData.get("current");

        // Top box is the status image. The image location comes from the StatusImage class.

        Image currentImageIcon = new Image(StatusImage.getImage(currentWeather.get("icon")));
        ImageView currentImage = new ImageView(currentImageIcon);
        Node summaryImage = currentImage;
        VBox.setMargin(summaryImage, new Insets(0, 0, 20, 0));

        // Bottom box is an HBox with the label and temperature
        Label currently = new Label("Aktualnie:");
        currently.setStyle(FX_TEXT_COLOR + "white;" + FX_FONT_SIZE + "30;");
        currently.setMaxHeight(Double.MAX_VALUE);

        Label currentTemperature = new Label(currentWeather.get("temp"));
        currentTemperature.setStyle(FX_TEXT_COLOR + "white;" + FX_FONT_SIZE + "40;");
        temperatureBox.getChildren().addAll(currently, currentTemperature);
        HBox.setMargin(temperatureBox, new Insets(10));

        currentPane.getChildren().addAll(summaryImage, temperatureBox);
        root.setLeft(currentPane);
    }

    private void setDaily(){

        // Main layout is a VBox
        VBox dailyPane = new VBox();
        dailyPane.setPrefWidth(WIDTH * 0.625);
        dailyPane.setStyle(FX_BACKGROUND + "#9fedd4;");
        dailyPane.setAlignment(Pos.CENTER);
        dailyPane.setPadding(new Insets(15));

        Label forecast = new Label("Prognoza tygodniowa");
        forecast.setStyle(FX_TEXT_COLOR + "black;" + FX_FONT_SIZE + "30;");
        forecast.setPrefWidth(WIDTH);
        forecast.setAlignment(Pos.TOP_LEFT);
        dailyPane.getChildren().add(forecast);

        ArrayList< Map <String, String> > dailyForecast = (ArrayList <Map <String, String> >) weatherData.get("daily");

        for (int i = 0; i < dailyForecast.size(); i++){

            HBox daily = new HBox();

            Label day = new Label(dailyForecast.get(i).get("day"));
            day.setStyle(FX_TEXT_COLOR + "black;" + FX_FONT_SIZE + "20;");
            day.setMaxHeight(Double.MAX_VALUE);
            day.setPrefWidth(WIDTH * 3 / 16);

            Label high = new Label(dailyForecast.get(i).get("high"));
            high.setStyle(FX_TEXT_COLOR + "red;" + FX_FONT_SIZE + "20;");
            high.setMaxHeight(Double.MAX_VALUE);
            high.setPrefWidth(WIDTH * 2 / 16);

            Label low = new Label(dailyForecast.get(i).get("low"));
            low.setStyle(FX_TEXT_COLOR + "blue;" + FX_FONT_SIZE + "20;");
            low.setMaxHeight(Double.MAX_VALUE);
            low.setPrefWidth(WIDTH * 2 / 16);

            Image dailyImageIcon = new Image(StatusImage.getImage(dailyForecast.get(i).get("icon")));
            ImageView currentdailyImage = new ImageView(dailyImageIcon);
            Node dailyImage = currentdailyImage;
            VBox.setMargin(dailyImage, new Insets(0, 0, 20, 0));

            daily.getChildren().addAll(day, high, low, dailyImage);
            dailyPane.getChildren().add(daily);
        }

        root.setCenter(dailyPane);
    }
}



































