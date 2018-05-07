package WeatherApp;

/**
 * Created by Asus on 2018-04-29.
 */
public class StatusImage {

    public static String getImage(String iconNumber) {

        switch(iconNumber){
            case "32":
            case "34":
            case "36":
                iconNumber = "01d";
                break;
            case "31":
            case "33":
                iconNumber = "01n";
                break;
            case "30":
            case "44":
                iconNumber = "02d";
                break;
            case "29":
                iconNumber = "02n";
                break;
            case "25":
                iconNumber = "03d";
                break;
            case "26":
            case "27":
            case "28":
                iconNumber = "04d";
                break;
            case "8":
            case "9":
            case "10":
            case "11":
            case "12":
            case "35":
            case "40":
                iconNumber = "09d";
                break;
            case "3":
            case "4":
            case "37":
            case "38":
            case "39":
            case "45":
            case "47":
                iconNumber = "11d";
                break;
            case "5":
            case "6":
            case "7":
            case "13":
            case "14":
            case "15":
            case "16":
            case "17":
            case "18":
            case "41":
            case "42":
            case "43":
            case "46":
                iconNumber = "13d";
                break;
            case "0":
            case "1":
            case "2":
            case "19":
            case "20":
            case "21":
            case "22":
            case "23":
            case "24":
                iconNumber = "50d";
                break;
        }

        return "http://openweathermap.org/img/w/" + iconNumber + ".png";
        }
    }
