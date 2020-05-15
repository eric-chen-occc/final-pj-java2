import java.text.DecimalFormat;
import java.util.Date;

public class User {
    private String name = "";
    private double accuracy = 0;
    private double speed = 0;
    private long date = 0;

    private static DecimalFormat df = new DecimalFormat("0.00");

    public User(String name, double accuracy, double speed, long date) {
        this.name = name;
        this.accuracy = accuracy;
        this.speed = speed;
        this.date = date;
    }

    public double showAccuracy() {
        return this.accuracy;
    }
    public double showSpeed() {
        return this.speed;
    }
    public long showDate() {
        return this.date;
    }

    public String getName() {
        return name;
    }
    public int getScore() {
        return (int) Math.round(accuracy * accuracy * speed * 100);
    }

    public String getAccuracy() {
        return df.format(accuracy * 100) + "%";
    }
    public String getSpeed() {
        return df.format(speed);
    }
    public String getDate() {
        return new Date(this.date).toString();
    }

}
