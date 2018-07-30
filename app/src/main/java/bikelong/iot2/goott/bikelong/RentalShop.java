package bikelong.iot2.goott.bikelong;

import java.io.Serializable;

public class RentalShop implements Serializable {

    private int rentalShopNo;
    private String rentalShopName;
    private String location;
    private double lat;
    private double lng;
    private int count;

    public int getRentalShopNo() {
        return rentalShopNo;
    }
    public void setRentalShopNo(int rentalShopNo) {
        this.rentalShopNo = rentalShopNo;
    }
    public String getRentalShopName() {
        return rentalShopName;
    }
    public void setRentalShopName(String rentalShopName) {
        this.rentalShopName = rentalShopName;
    }
    public String getLocation() {
        return location;
    }
    public void setLocation(String location) {
        this.location = location;
    }
    public double getLat() {
        return lat;
    }
    public void setLat(double lat) {
        this.lat = lat;
    }
    public double getLng() {
        return lng;
    }
    public void setLng(double lng) {
        this.lng = lng;
    }
    public int getCount() {
        return count;
    }
    public void setCount(int count) {
        this.count = count;
    }

}
