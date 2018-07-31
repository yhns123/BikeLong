package bikelong.iot2.goott.bikelong;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

public class MapWithLBSActivity extends AppCompatActivity {

    private LocationManager mLocationManager; //디바이스의 위치 정보를 관리하는 서비스
    private GoogleMap mMap;
    private Marker mMarker; //지도에 표시할 표시 요소 (여러 위치를 표시하려면 List로 만들기)
    private List<Marker> mMarkers = new ArrayList<>();

    private List<RentalShop> mRentalShop = new ArrayList<>();
    private List<RentalShop> mRentalShopSearch = new ArrayList<>();
    private EditText mSearch;
    private Button mSearchBtn;


    //LocationManager로부터 위치 정보를 수신하기 위한 인터페이스 구현 객체
    private LocationListener mListener = new LocationListener() {
        @Override
        public void onLocationChanged(Location l) { //위치 정보가 수신되면 호출되는 메서드
            double lat = l.getLatitude();
            double lng = l.getLongitude();

            //지도에 표시
            LatLng pos = new LatLng(lat,lng);
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(pos, 17));

        }
        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {}
        @Override
        public void onProviderEnabled(String provider) {}
        @Override
        public void onProviderDisabled(String provider) {}
    };

    private void loadMaps() {
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

            for(RentalShop rentalShop : rentalShops) {

                mRentalShop.add(rentalShop);
            }

        } catch (Exception ex) {
            ex.printStackTrace();
        }

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);

        loadMaps();

        FragmentManager manager = getSupportFragmentManager();
        SupportMapFragment fragment = (SupportMapFragment)manager.findFragmentById(R.id.map);

        fragment.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(GoogleMap googleMap) {
                mMap = googleMap;

                //특정 프로바이더(위치 정보 수신기)를 통해 수신한 마지막 위치 정보 반환
                Location location =  mLocationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);

                LatLng pos = null;
                if (location == null) {
                    pos = new LatLng(37.4831784, 126.8971856);
                } else {
                    pos = new LatLng(location.getLatitude(), location.getLongitude());
                }

                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(pos, 17));

            }
        });

        mLocationManager = (LocationManager)getSystemService(LOCATION_SERVICE);

        mSearch = findViewById(R.id.search);
        mSearchBtn = findViewById(R.id.searchBtn);

        mSearchBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loadSearch();
            }
        });
    }

    private void loadSearch() {
        Thread t = new Thread() {
            public void run() {
                try {
                    String text = mSearch.getText().toString();
                    text = URLEncoder.encode(text, "UTF-8");
                    Log.e("테스트", text);
                    String serverUrl = String.format("http://211.197.18.246:8087/bikelong/mobile_search.action?mSearch=%s", text);

                    URL url = new URL(serverUrl);
                    HttpURLConnection con = (HttpURLConnection) url.openConnection();

                    final int responseCode = con.getResponseCode();

                    if (responseCode == 200) {
                        processResult2(con.getInputStream());
                    } else {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(getApplicationContext(),
                                        "error " + responseCode, Toast.LENGTH_SHORT).show();
                            }
                        });
                    }

                    int count = 0;
                    for (RentalShop re : mRentalShopSearch) {
                        count++;
                    }

                    if(count > 1) {

                        String temp[] = new String[count];

                        for(int i=0; i<count; i++) {
                            temp[i] = mRentalShopSearch.get(i).getRentalShopName();
                        }

                        final String rentalShopSearch[] = temp;

                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                AlertDialog.Builder builder = new AlertDialog.Builder(MapWithLBSActivity.this);
                                builder.setIcon(R.mipmap.ic_launcher)
                                        .setTitle("원하시는 대여소를 선택해 주세요")
                                        .setItems(rentalShopSearch, new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                                LatLng searchLatLng = new LatLng(mRentalShopSearch.get(which).getLat(), mRentalShopSearch.get(which).getLng());
                                                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(searchLatLng, 17));
                                            }
                                        });
                                builder.show();
                            }
                        });
                    } else if(count == 1){
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                final LatLng searchLatLng = new LatLng(mRentalShopSearch.get(0).getLat(), mRentalShopSearch.get(0).getLng());
                                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(searchLatLng, 17));
                                Log.e("이동완료", "./");
                            }
                        });
                    } else {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                AlertDialog.Builder builder = new AlertDialog.Builder(MapWithLBSActivity.this);
                                builder.setIcon(R.mipmap.ic_launcher)
                                        .setTitle("search")
                                        .setMessage("검색결과가 없습니다.");

                                builder.show();
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

    private void processResult2(InputStream inputStream) {

        mRentalShopSearch.clear();

        try {
            InputStreamReader reader = new InputStreamReader(inputStream);
            Gson gson = new GsonBuilder().setDateFormat("yyyy-MM-dd").create();
            RentalShop[] rentalShops = gson.fromJson(reader, RentalShop[].class);


            for(RentalShop rentalShop : rentalShops) {
                mRentalShopSearch.add(rentalShop);
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
        options.snippet("대여가능 자전거 : " + mRentalShop.get(i).getCount());
        options.position(rentalShopLocation);

        mMarkers.add(mMap.addMarker(options)); //지도에 마커(표시)를 추가    관리용(띄우기)

        mMap.setOnInfoWindowClickListener(infoWindowClickListener);
    }

    GoogleMap.OnInfoWindowClickListener infoWindowClickListener = new GoogleMap.OnInfoWindowClickListener() {
        @Override
        public void onInfoWindowClick(Marker marker) {
            Intent intent = new Intent(MapWithLBSActivity.this, BikeActivity.class);
            Log.e("렌탈샵 번호",marker.getTitle().substring(0,1));
            intent.putExtra("rentalShopNo", marker.getTitle().substring(0,1));
            startActivity(intent);
        }
    };

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        menu.add(Menu.NONE, 1, Menu.NONE, "서비스 시작");
        menu.add(Menu.NONE, 2, Menu.NONE, "서비스 중지");
        return super.onCreateOptionsMenu(menu);
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Intent intent = null;
        switch (item.getItemId()) {
            case 1:

                //LBS에 수신기를 등록하는 명령
                mLocationManager.requestLocationUpdates(
                        LocationManager.GPS_PROVIDER,       //GPS 정보를 수신해서
                        5000,       //최소 5초 이상의 시간이 경과하고
                        10,      //최소 10M 이상의 위치 변경이 발생하면
                        mListener           //이 수신기에게 알려주세요
                );
                break;
            case 2:
                //LBS에서 수신기를 제거하는 명령
                mLocationManager.removeUpdates(mListener);

                break;
        }
        return super.onOptionsItemSelected(item);
    }

}
