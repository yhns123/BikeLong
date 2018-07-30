package bikelong.iot2.goott.bikelong;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class SignInActivity extends AppCompatActivity {

    private Button mSignInButton;
    private Button mSignUpButton;
    private EditText mId;
    private EditText mPassword;
    Member member = Member.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signin);

        mSignInButton = (Button) findViewById(R.id.signInButton);
        mSignUpButton = (Button) findViewById(R.id.signUpButton);
        mId = findViewById(R.id.idText);
        mPassword = findViewById(R.id.passwordText);

        mSignInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SignInRequestThread t = new SignInRequestThread();
                t.start();
            }
        });

        mSignUpButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent signupIntent = new Intent(v.getContext(), SignUpActivity.class);
                startActivity(signupIntent);
            }
        });
    }

/*    Handler handler = new Handler() {

        @Override
        public void handleMessage(Message msg) {

            switch (msg.what) {
                case 1:
                    Intent mainIntent = new Intent(SignInActivity.this, MainActivity.class);
                    startActivity(mainIntent);
            }
        }
    };*/

    class SignInRequestThread extends Thread {
        @Override
        public void run() {

            try {
                String id = mId.getText().toString();
                String password = mPassword.getText().toString();

               //Get 방식
                URL url = new URL(String.format("http://211.197.18.246:8087/bikelong/account/msignin.action?id="+id+"&password="+password));// URL클래스의 생성자로 주소를 넘겨준다.
                HttpURLConnection con = (HttpURLConnection)url.openConnection();// 해당 주소의 페이지로 접속을 하고, 단일 HTTP 접속을 하기위해 캐스트한다.
                con.setRequestMethod("GET");// POST방식으로 요청한다.( 기본값은 GET )
                int responseCode = con.getResponseCode();
                if (responseCode == 200) { // 정상 응답일 경우
                        boolean result = processResult(con);

                        if (result) {
                            //로그인 성공 코드
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    finish();
                                    Intent intent = new Intent(SignInActivity.this, UserMainActivity.class);
                                    startActivity(intent);
                                }
                            });
                        } else {
                            //로그인 실패 코드
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    Toast.makeText(getApplicationContext(), "로그인 실패", Toast.LENGTH_SHORT).show();
                                }
                            });
                    }

                } else {
                    //show error message
                    Toast.makeText(getApplicationContext(),
                            "error " + responseCode, Toast.LENGTH_LONG).show();
                }
            } catch (Exception e) {
                System.out.println(e);
            }

            //handler.sendEmptyMessage(1);
        }
    }

    private boolean processResult(HttpURLConnection conn) {
        boolean result = true;
        try {
            //JSON 문자열 -> 객체 트리로 변환하는 변환기 만들기
            InputStream is = conn.getInputStream();
            InputStreamReader reader = new InputStreamReader(is);
            JsonParser parser = new JsonParser();

            //변환 처리 -> JsonElement 반환
            JsonElement je = parser.parse(reader);
            JsonObject element = je.getAsJsonObject();
            if (element.get("result") == null) {
                Gson gson = new Gson();
                Member.MemberVo memberVo = gson.fromJson(element, Member.MemberVo.class); // JSON 객체 VO 객체로 직접 변환
                member.fillMemberVo(memberVo);
            } else {
                return false;
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            result = false;
        }
    return result;
    }
}