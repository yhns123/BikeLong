package bikelong.iot2.goott.bikelong;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
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
    private Intent intent;

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

                intent = new Intent(v.getContext(), MapWithLBSActivity.class);
                startActivity(intent);
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.my_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.menu_rentalshop:
                intent = new Intent(this, MapWithLBSActivity.class);
                startActivity(intent);
                break;
            case R.id.menu_activity:
                intent = new Intent(this, MyPageActivity.class);
                startActivity(intent);
                break;
            case R.id.menu_goal:
                AlertDialog.Builder builder = new AlertDialog.Builder(this, R.style.MyAlertDialogStyle);
                builder.setTitle("알림");
                builder.setMessage("아직 구현중입니다. 빠른시일 내에 찾아뵙겠습니다. ㅠㅠ");
                builder.setPositiveButton("확인", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {}
                });
                builder.show();
                break;
        }
        return super.onOptionsItemSelected(item);
    }
}
