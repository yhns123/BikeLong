package bikelong.iot2.goott.bikelong;

import android.content.Intent;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class RentActivity extends AppCompatActivity {

    private LocationManager mLocationManager; // 디바이스의 위치 정보를 관리하는 서비스
    private GoogleMap mMap;
    private ArrayList<Marker> mMarkers = new ArrayList<>(); // 지도에 표시할 표시 요소 (여러 위치를 표시하려면 List 로 구현)
    private ArrayList<Marker> mRentalshopMakers = new ArrayList<>();
    private List<RentalShop> mRentalShop = new ArrayList<>();
    private TextView displayDistance;
    private int bikeNo;
    private int rentalShopNo;

    private double distance=0;
    private double prevLat=0;
    private double prevLng=0;
    Member member = Member.getInstance();
    private SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    private String startTime;
    private String endTime;

    //뒤로가기 막기
    @Override public void onBackPressed() { }

    //2개의 마커 사이의 거리계산 메소드
    public double getDistance(double prevLat, double prevLng, double lat, double lng) {
        double currentdistance = 0;

        if(prevLat != 0 && prevLng != 0){
            Location locationA = new Location("A");
            locationA.setLatitude(prevLat);
            locationA.setLongitude(prevLng);
            Location locationB = new Location("B");
            locationB.setLatitude(lat);
            locationB.setLongitude(lng);
            currentdistance = locationA.distanceTo(locationB);
        }

        return currentdistance;
    }

    // LocationManager 로부터 위치 정보를 수신하기 위한 인터페이스 구현 객체
    private LocationListener mListener = new LocationListener() {
        @Override
        public void onLocationChanged(Location location) { // 위치 정보가 수신되면 호출되는 메서드
            //위도와 경도
            final double lat = location.getLatitude();
            final double lng = location.getLongitude();

            insertGpsThread t = new insertGpsThread(lat,lng,2);
            t.start();

            distance = distance + getDistance(prevLat,prevLng,lat,lng);
            prevLat = lat;
            prevLng = lng;

            //지도에 표시
            LatLng position = new LatLng(lat,lng);
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(position,18));

            displayDistance.setText("총 이동거리\n" + Math.round(distance*100)/100.0 + " M");
            showMarker(position, R.drawable.position);
        }

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) { }
        @Override
        public void onProviderEnabled(String provider) { }
        @Override
        public void onProviderDisabled(String provider) { }
    };

    private void showMarker(LatLng position, int image) {

        MarkerOptions options = new MarkerOptions();
        options.icon(BitmapDescriptorFactory.fromResource(image));
        options.position(position);

        mMarkers.add(mMap.addMarker(options)); //지도에서 마커(표시)를 추가
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rent);

        Intent intent = getIntent();
        bikeNo = intent.getIntExtra("bikeNo",0);
        rentalShopNo = intent.getIntExtra("rentalShopNo",0);

        displayDistance = findViewById(R.id.displayDistance);

        Date startDate = new Date();
        startTime = format.format(startDate);

        getRentalShopList();

        mLocationManager = (LocationManager)getSystemService(LOCATION_SERVICE);
        // LBS 에 수신기를 등록하는 명령
        mLocationManager.requestLocationUpdates( LocationManager.GPS_PROVIDER,5000, 10, mListener);
        FragmentManager manager = getSupportFragmentManager();
        SupportMapFragment fragment = (SupportMapFragment)manager.findFragmentById(R.id.map);

        fragment.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(GoogleMap googleMap) {

                mMap = googleMap;

                //특정 프로바이더(위치 정보 수신기)를 통해 수신한 마지막 위치 정보 반환
                Location location = null;

                LatLng position = null;
                if(location==null){
                    position = new LatLng(37.484995,126.901238);
                } else {
                    position = new LatLng(location.getLatitude(),location.getLatitude());
                }

                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(position,18));

                showMarker(position, R.drawable.position);
            }
        });
    }

    class insertGpsThread extends Thread {

        private Double lat;
        private Double lng;
        private int bikeNo;

        public insertGpsThread(Double lat, Double lng, int bikeNo){
            this.lat=lat;
            this.lng=lng;
            this.bikeNo=bikeNo;
        }

        @Override
        public void run() {

            try {
                String id = member.getId();
                String serverUrl = String.format("http://211.197.18.246:8087/bikelong/gps/minsertgps.action?id=%s&bikeNo=%d&latitude=%s&longitude=%s", id, bikeNo,(lat).toString(),(lng).toString());
                URL url = new URL(serverUrl);
                HttpURLConnection con = (HttpURLConnection) url.openConnection();

                final int responseCode = con.getResponseCode();
                if (responseCode == 200) {  //정상 응답인 경우
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            //show error message
                            Toast.makeText(getApplicationContext(),
                                    "위치 저장", Toast.LENGTH_SHORT).show();
                        }
                    });
                    //processResult(con.getInputStream());
                    //processResult1(con.getInputStream());
                } else {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            //show error message
                            Toast.makeText(getApplicationContext(),
                                    "error " + responseCode, Toast.LENGTH_SHORT).show();
                        }
                    });

                }
            } catch (Exception e) {
                System.out.println(e);
            }
        }
    }

    private void getRentalShopList() {
        Thread t = new Thread() {
            public void run() {

                try {
                    String serverUrl = String.format("http://211.197.18.246:8087/bikelong/mobile_rentalShop.action");

                    URL url = new URL(serverUrl);
                    HttpURLConnection con = (HttpURLConnection) url.openConnection();

                    final int responseCode = con.getResponseCode();

                    if (responseCode == 200) {
                        processResult(con.getInputStream());
                    } else {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(getApplicationContext(),
                                        "error " + responseCode, Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                    if(mRentalShop.size() != 0){
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                int count = 0;
                                for(RentalShop rentalShop : mRentalShop) {
                                    showMarkerRentalShop(mRentalShop, count);
                                    count++;
                                }
                            }
                        });
                    }
                } catch (Exception e){
                    System.out.println(e);
                }

            }
        };
        t.start();
    }

    private void processResult(InputStream inputStream) {

        mRentalShop.clear();

        try {
            InputStreamReader reader = new InputStreamReader(inputStream);
            Gson gson = new GsonBuilder().setDateFormat("yyyy-MM-dd").create();
            RentalShop[] rentalShops = gson.fromJson(reader, RentalShop[].class);
            int count=0;
            for(RentalShop rentalShop : rentalShops) {
                mRentalShop.add(rentalShop);
                count++;
            }

        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private void showMarkerRentalShop(final List<RentalShop> mRentalShop, int i) {

        LatLng rentalShopLocation = new LatLng(mRentalShop.get(i).getLat(), mRentalShop.get(i).getLng());

        MarkerOptions options = new MarkerOptions();

        if(mRentalShop.get(i).getCount() == 0) {
            options.icon(BitmapDescriptorFactory.fromResource(R.drawable.icon0));
        } else if(mRentalShop.get(i).getCount() > 0 && mRentalShop.get(i).getCount() < 4) {
            options.icon(BitmapDescriptorFactory.fromResource(R.drawable.icon3));
        } else if(mRentalShop.get(i).getCount() > 3 && mRentalShop.get(i).getCount() < 7 ) {
            options.icon(BitmapDescriptorFactory.fromResource(R.drawable.icon2));
        } else if(mRentalShop.get(i).getCount() > 6) {
            options.icon(BitmapDescriptorFactory.fromResource(R.drawable.icon1));
        } else {
            options.icon(BitmapDescriptorFactory.fromResource(R.drawable.icon4));
        }

        options.title(mRentalShop.get(i).getRentalShopNo() + ". " + mRentalShop.get(i).getRentalShopName());
        options.snippet("이 대여소에 반납을 원하시면 클릭하세요.");
        options.position(rentalShopLocation);

        mMarkers.add(mMap.addMarker(options)); //지도에 마커(표시)를 추가    관리용(띄우기)

        mMap.setOnInfoWindowClickListener(infoWindowClickListener);
    }

    GoogleMap.OnInfoWindowClickListener infoWindowClickListener = new GoogleMap.OnInfoWindowClickListener() {
        @Override
        public void onInfoWindowClick(Marker marker) {

            Date endDate = new Date();
            endTime = format.format(endDate);
            Toast.makeText(getApplicationContext(), endTime, Toast.LENGTH_SHORT).show();

            // LBS 에서 수신기를 제거하는 명령
            mLocationManager.removeUpdates(mListener);

            int finalDistance = (int) distance;
            insertHistoryThread t = new insertHistoryThread(finalDistance, startTime,endTime,2);
            t.start();

            updateBikeThread t2 = new updateBikeThread(bikeNo,Integer.parseInt(marker.getTitle().substring(0,1)));
            t2.start();

            finish();
            Intent intent = new Intent(RentActivity.this, UserMainActivity.class);
            intent.setFlags(intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);

        }
    };

    class updateBikeThread extends Thread {

        private int bikeNo;
        private int rentalShopNo;


        public updateBikeThread(int bikeNo, int rentalShopNo){
            this.bikeNo=bikeNo;
            this.rentalShopNo=rentalShopNo;
        }

        @Override
        public void run() {

            try {
                String serverUrl = String.format("http://211.197.18.246:8087/bikelong/mobile_updateBikeAndRentalShop.action?bikeNo=%d&rentalShopNo=%d&request=1",bikeNo,rentalShopNo);
                URL url = new URL(serverUrl);
                HttpURLConnection con = (HttpURLConnection) url.openConnection();

                final int responseCode = con.getResponseCode();
                if (responseCode == 200) {  //정상 응답인 경우
                    Toast.makeText(getApplicationContext(),
                            "반납되었습니다.", Toast.LENGTH_SHORT).show();
                } else {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            //show error message
                            Toast.makeText(getApplicationContext(),
                                    "error " + responseCode, Toast.LENGTH_SHORT).show();
                        }
                    });

                }
            } catch (Exception e) {
                System.out.println(e);
            }
        }
    }

    class insertHistoryThread extends Thread {

        private String startTime;
        private String endTime;
        private int bikeNo;
        private int distance;

        public insertHistoryThread(int distance, String startTime, String endTime, int bikeNo){
            this.startTime=startTime;
            this.endTime=endTime;
            this.bikeNo=bikeNo;
            this.distance=distance;
        }

        @Override
        public void run() {

            try {
                String id = member.getId();
                int weight = member.getWeight();
                String serverUrl = String.format("http://211.197.18.246:8087/bikelong/history/minserthistory.action?id=%s&bikeNo=%d&startTime=%s&endTime=%s&distance=%d&weight=%d", id, bikeNo,startTime,endTime,distance,weight);
                URL url = new URL(serverUrl);
                HttpURLConnection con = (HttpURLConnection) url.openConnection();

                final int responseCode = con.getResponseCode();
                if (responseCode == 200) {  //정상 응답인 경우
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            //show error message
                            Toast.makeText(getApplicationContext(),
                                    "사용기록 저장 성공", Toast.LENGTH_SHORT).show();
                        }
                    });
                } else {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            //show error message
                            Toast.makeText(getApplicationContext(),
                                    "error " + responseCode, Toast.LENGTH_SHORT).show();
                        }
                    });

                }
            } catch (Exception e) {
                System.out.println(e);
            }
        }
    }
}
