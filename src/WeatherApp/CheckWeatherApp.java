package WeatherApp;

import javafx.application.Application;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.stage.Stage;


public class CheckWeatherApp extends Application{

    public static void main(String[] args) {
    launch(args);
    }

    @Override
    public void start(Stage stage) throws Exception {

        HBox mainbox = new HBox(3);

        WeatherPaneOrganizer city1 = new WeatherPaneOrganizer("Warszawa, Polska");
        WeatherPaneOrganizer city2 = new WeatherPaneOrganizer("Rzym, Włochy");

        String url = "https://c.pxhere.com/photos/16/3f/bokeh_blur_blue_wallpaper_background-649344.jpg!d";
        mainbox.getChildren().add(city1.getRoot());
        mainbox.getChildren().add(city2.getRoot());
        BackgroundImage myBI = new BackgroundImage(new Image(url),BackgroundRepeat.NO_REPEAT,
                BackgroundRepeat.NO_REPEAT, BackgroundPosition.CENTER, BackgroundSize.DEFAULT);
        mainbox.setBackground(new Background(myBI));

        Scene scene = new Scene(new Group(mainbox));
        stage.setScene(scene);
        stage.setTitle("Check weather");
        stage.setResizable(false);
        stage.show();
    }
}
