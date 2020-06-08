package in.insideandroid.eyetracker;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class TimerSettingActivity extends Activity {
    private EditText editText;
    private Button button;
    Integer integer;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_timer_setting);
        editText = findViewById(R.id.edit);
        button = findViewById(R.id.button);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                integer = Integer.valueOf(editText.getText().toString());
                Intent intent = new Intent();
                Bundle bundle = new Bundle();
                bundle.putInt("timer",integer);
                intent.putExtras(bundle);
                intent.setClass(getApplicationContext(),MainActivity.class);
                startActivity(intent);
            }
        });

    }
}
