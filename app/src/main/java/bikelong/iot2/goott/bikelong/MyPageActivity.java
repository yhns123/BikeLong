package bikelong.iot2.goott.bikelong;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;

public class MyPageActivity extends AppCompatActivity {

    private TextView mUseTime;
    private TextView mUseDistance;
    private TextView mUseActive;
    private TextView mUseCarbon;
    private Button mDay;
    private Button mWeek;
    private Button mMonth;

    private int totalDistance=0;
    private int totalCalorie=0;
    private int totalRentalTime=0;
    private double totalCarbon=0;

    Member member= Member.getInstance();
    private List<History> mHistorys;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mypage);

        mUseTime = findViewById(R.id.useTime);
        mUseDistance = findViewById(R.id.useDistance);
        mUseActive = findViewById(R.id.useActive);
        mUseCarbon = findViewById(R.id.useCarbon);
        mDay = findViewById(R.id.day);
        mWeek = findViewById(R.id.week);
        mMonth = findViewById(R.id.month);

        mDay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectActivityThread t = new selectActivityThread(1);
                t.start();
            }
        });

        mWeek.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectActivityThread t = new selectActivityThread(2);
                t.start();
            }
        });

        mMonth.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectActivityThread t = new selectActivityThread(3);
                t.start();
            }
        });

        selectActivityThread t = new selectActivityThread(0);
        t.start();
    }

    class selectActivityThread extends Thread {

        private int requestType;

        public selectActivityThread(int requestType){
            this.requestType=requestType;
        }

        @Override
        public void run() {

            try {
                String id = member.getId();
                String serverUrl = String.format("http://172.16.6.23:8087/bikelong/mypage/mactivity.action?requestType=%d&id=%s", requestType, id);
                URL url = new URL(serverUrl);
                HttpURLConnection con = (HttpURLConnection) url.openConnection();

                final int responseCode = con.getResponseCode();
                if (responseCode == 200) {  //정상 응답인 경우
                    processResult(con.getInputStream());
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

    private boolean processResult(InputStream inputStream) {
        boolean result = true;
        try {
            //JSON 문자열 -> 객체 트리로 변환하는 변환기 만들기
            InputStreamReader reader = new InputStreamReader(inputStream);
            Gson gson = new GsonBuilder().setDateFormat("yyyy-MM-dd").create();
            History[] historyList = gson.fromJson(reader, History[].class);

            totalCalorie=0;
            totalDistance=0;
            totalRentalTime=0;

            for (History history : historyList) {
                totalCalorie += history.getCalorie();
                totalDistance += history.getDistance();
                totalRentalTime += history.getRentalTime();
            }

            totalCarbon = (totalDistance/1000) * 0.232;

            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    mUseTime.setText(totalRentalTime + " 분");
                    mUseDistance.setText(totalDistance + " m");
                    mUseActive.setText(totalCalorie + " Kcal");
                    mUseCarbon.setText(Math.round(totalCarbon*100)/100.0  + " Kg");
                }
            });


        } catch (Exception ex) {
            ex.printStackTrace();
            result = false;
        }
        return result;
    }
}
