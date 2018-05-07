package WeatherApp;

/**
 * Created by Asus on 2018-05-06.
 */
public class WeekdayName {

    public static String toPolish(String input) {

        switch (input) {
            case "Sun":
                return "Niedziela";
            case "Mon":
                return "Poniedziałek";
            case "Tue":
                return "Wtorek";
            case "Wed":
                return "Środa";
            case "Thu":
                return "Czwartek";
            case "Fri":
                return "Piątek";
            case "Sat":
                return "Sobota";
            default:
                return input;
        }

    }
}
