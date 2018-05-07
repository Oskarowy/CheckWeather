package WeatherApp;

import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;

/**
 * Created by Asus on 2018-04-29.
 */
public class MessageBox {

    private String cityName;

    public MessageBox(){
        cityName = "Warszawa, Polska";
    }

    public void show(){
        Stage stage = new Stage();
        stage.initModality(Modality.APPLICATION_MODAL); // Requires the message box to be completed before continuing
        stage.setTitle("Nowe miasto");
        stage.setMinWidth(250);

        /*
         * The main layout is a VBox and two HBoxes in it
         * The first HBox holds the label and text field
         * and the second holds the two buttons.
         */

        TextField city = new TextField();
        city.setPromptText("Podaj miasto, w którym chcesz sprawdzić pogodę");
        city.setMaxWidth(50);

        Label cityLabel = new Label();
        cityLabel.setText("Nazwa miasta: ");

        HBox cityNamePane = new HBox(10);
        cityNamePane.setAlignment(Pos.CENTER);
        cityNamePane.getChildren().addAll(cityLabel, city);

        Button okButton = new Button();
        okButton.setText("OK");
        okButton.setOnAction(e -> {
            cityName = city.getText();
            stage.close();
        });

        Button cancelButton = new Button();
        cancelButton.setText("Anuluj");
        cancelButton.setOnAction(e-> stage.close());

        HBox buttons = new HBox(10);
        buttons.setAlignment(Pos.CENTER);
        buttons.getChildren().addAll(okButton, cancelButton);

        VBox pane = new VBox(20);
        pane.setPrefSize(250, 100);
        pane.setStyle("-fx-background-color: skyblue;");
        pane.getChildren().addAll(cityNamePane, buttons);
        pane.setAlignment(Pos.CENTER);

        Scene scene = new Scene(pane);
        stage.setScene(scene);
        stage.showAndWait();
    }

    public String getCityName(){
        return cityName;
    }
}












