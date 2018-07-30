package bikelong.iot2.goott.bikelong;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ScrollView;

public class MainActivity extends AppCompatActivity {

    private Button mUseAppButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Intent intent = getIntent();
        Member member = (Member) intent.getSerializableExtra("member");

        mUseAppButton = findViewById(R.id.useAppButton);

        mUseAppButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent signinIntent = new Intent(v.getContext(), SignInActivity.class);
                startActivity(signinIntent);
            }
        });
    }
}
