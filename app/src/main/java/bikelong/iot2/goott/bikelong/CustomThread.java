package bikelong.iot2.goott.bikelong;

public class CustomThread extends Thread {

    private Double latitude;
    private Double longitude;

    public CustomThread(Double latitude, Double longitude) {
                 this.latitude = latitude;
                 this.longitude = longitude;
             }
}
