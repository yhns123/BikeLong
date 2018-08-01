package bikelong.iot2.goott.bikelong;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class BikeActivity extends AppCompatActivity {

    private String rentalShopNo;
    private List<Bike> mBikes = new ArrayList<>();
    private ListView mBikeListView;
    private BikeListAdapter mBikeListAdapter;
    private TextView mLogText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bike);

        mLogText = findViewById(R.id.log);
        mBikeListAdapter = new BikeListAdapter(mBikes, this, R.layout.bike_list_view);
        mBikeListView = findViewById(R.id.bike_list);
        mBikeListView.setAdapter(mBikeListAdapter);

        Intent intent = getIntent();
        rentalShopNo = intent.getStringExtra("rentalShopNo");
                //Integer.parseInt(intent.getStringExtra("rentalShopNo"));

        loadBikes();
    }

    private void loadBikes() {
        Thread t = new Thread() {
            public void run() {
                try {
                    int mRentalShopNo = Integer.parseInt(rentalShopNo);
                    String serverUrl = String.format("http://211.197.18.246:8087/bikelong/mobile_bike.action?rentalShopNo=%d", mRentalShopNo);

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

                    //click test
                    mBikeListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                        @Override
                        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                            updateBikeThread t = new updateBikeThread(mBikes.get(position).getBikeNo(),mBikes.get(position).getRentalShopNo());
                            t.start();

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
        mBikes.clear();

        try {
            InputStreamReader reader = new InputStreamReader(inputStream);
            Gson gson = new GsonBuilder().setDateFormat("yyyy-MM-dd").create();
            Bike[] bikes = gson.fromJson(reader, Bike[].class);

            for(Bike bike : bikes) {
                mBikes.add(bike);
            }

            if(mBikes.size() == 0 || mBikes == null) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        AlertDialog.Builder builder = new AlertDialog.Builder(BikeActivity.this, R.style.MyAlertDialogStyle);

                        builder.setIcon(R.mipmap.ic_launcher)
                                .setTitle("BIKE")
                                .setMessage("대여할 수 있는 자전거가 없습니다.")
                                .setPositiveButton("확인", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        finish();
                                    }
                                });
                        builder.show();
                    }
                });
            }

            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    mBikeListAdapter.notifyDataSetChanged();
                }
            });

        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

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
                String serverUrl = String.format("http://211.197.18.246:8087/bikelong/mobile_updateBikeAndRentalShop.action?bikeNo=%d&rentalShopNo=%d&request=0",bikeNo,rentalShopNo);
                URL url = new URL(serverUrl);
                HttpURLConnection con = (HttpURLConnection) url.openConnection();

                final int responseCode = con.getResponseCode();
                if (responseCode == 200) {  //정상 응답인 경우
                    finish();
                    Intent intent = new Intent(BikeActivity.this, RentActivity.class);
                    intent.putExtra("bikeNo",bikeNo);
                    intent.putExtra("rentalShopNo",rentalShopNo);
                    startActivity(intent);
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
