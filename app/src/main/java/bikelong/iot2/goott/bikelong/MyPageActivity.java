package bikelong.iot2.goott.bikelong;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

public class MyPageActivity extends AppCompatActivity {

    private Button mUseAppButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mypage);

        mUseAppButton = findViewById(R.id.useAppButton);

    }
}
