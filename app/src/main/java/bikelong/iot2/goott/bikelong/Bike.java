package bikelong.iot2.goott.bikelong;

import java.io.Serializable;

public class Bike implements Serializable {

    private int bikeNo;
    private int rentalShopNo;
    private int state;
    private String rentalShopName;

    public int getBikeNo() {
        return bikeNo;
    }
    public void setBikeNo(int bikeNo) {
        this.bikeNo = bikeNo;
    }
    public int getRentalShopNo() {
        return rentalShopNo;
    }
    public void setRentalShopNo(int rentalShopNo) {
        this.rentalShopNo = rentalShopNo;
    }
    public int getState() {
        return state;
    }
    public void setState(int state) {
        this.state = state;
    }

    public String getRentalShopName() {
        return rentalShopName;
    }
    public void setRentalShopName(String rentalShopName) {
        this.rentalShopName = rentalShopName;
    }

}
