package WeatherApp;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;


public class CheckWeatherApp extends Application{

    public static void main(String[] args) {
    launch(args);
    }

    @Override
    public void start(Stage stage) throws Exception {

        WeatherPaneOrganizer main = new WeatherPaneOrganizer();
        stage.setTitle("Check weather");
        stage.setResizable(false);

        Scene root = new Scene(main.getRoot());
        stage.setScene(root);
        stage.show();
    }
}
