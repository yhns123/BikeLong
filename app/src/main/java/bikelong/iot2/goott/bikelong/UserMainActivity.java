package bikelong.iot2.goott.bikelong;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.net.HttpURLConnection;
import java.net.URL;

public class UserMainActivity extends AppCompatActivity {

    private Button mRentBikeButton;
    private TextView userIdTextView;
    Member member = Member.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_usermain);

        mRentBikeButton = (Button) findViewById(R.id.RentBikeButton);
        userIdTextView = (TextView) findViewById(R.id.userIdTextView);

        userIdTextView.setText(member.getName() + " 님");

        mRentBikeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(v.getContext(), RentActivity.class);
                startActivity(intent);
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        menu.add(Menu.NONE, 1, Menu.NONE, "대여 및 반납");
        menu.add(Menu.NONE, 2, Menu.NONE, "대여소 보기");
        menu.add(Menu.NONE, 3, Menu.NONE, "활동량 보기");
        menu.add(Menu.NONE, 4, Menu.NONE, "목표량 설정");
        return super.onCreateOptionsMenu(menu);
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Intent intent;
        switch (item.getItemId()) {
            case 4 :
                intent = new Intent(this, UserMainActivity.class);
                startActivity(intent);
                break;
            case 3 :
                intent = new Intent(this, UserMainActivity.class);
                startActivity(intent);
                break;
            case 2 :
                intent = new Intent(this, UserMainActivity.class);
                startActivity(intent);
                break;
            case 1 :
                intent = new Intent(this, UserMainActivity.class);
                startActivity(intent);
                break;
        }
        return super.onOptionsItemSelected(item);
    }
}
