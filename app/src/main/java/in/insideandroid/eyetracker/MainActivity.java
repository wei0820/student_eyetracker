package in.insideandroid.eyetracker;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.media.AudioManager;
import android.media.SoundPool;
import android.os.Bundle;
import android.util.Log;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.ankushgrover.hourglass.Hourglass;
import com.google.android.gms.vision.CameraSource;
import com.google.android.gms.vision.MultiProcessor;
import com.google.android.gms.vision.face.FaceDetector;

import java.io.IOException;

public class MainActivity extends AppCompatActivity {

    RelativeLayout background;
    TextView user_message,user_message2;

    boolean flag = false;
    CameraSource cameraSource;
    Hourglass hourglass;
    Integer timer = 0;
    private SoundPool soundPool;
    private int alertId;
    boolean isFinish = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA}, 1);
            Toast.makeText(this, "Permission not granted!\n Grant permission and restart app", Toast.LENGTH_SHORT).show();
        }else{
            getData();
            init();
        }
    }
    private void getData(){
        timer =  getIntent().getIntExtra("timer",0);
    }
    private void init() {
        soundPool = new SoundPool(1, AudioManager.STREAM_MUSIC, 5);
        alertId = soundPool.load(this, R.raw.clock_bell, 1);
        background = findViewById(R.id.background);
        user_message = findViewById(R.id.user_text);
        user_message2 = findViewById(R.id.user_text2);

        flag = true;

        initCameraSource();
        setTimer();

    }
    private void setTimer(){
         hourglass = new Hourglass(timer, 1000) {
            @Override
            public void onTimerTick(long timeRemaining) {
                // Update UI
                user_message2.setText(String.valueOf(timeRemaining/1000)+"秒");
            }

            @Override
            public void onTimerFinish() {
                // Timer finished
                isFinish = true;
                soundPool.play(alertId, 1.0F, 1.0F, 0, 0, 1.0F);
                cameraSource.stop();
                background.setBackgroundColor(getResources().getColor(android.R.color.holo_green_dark));
                user_message.setText("睜開眼");
                setAlert();



            }
        };


    }


    //method to create camera source from faceFactoryDaemon class
    private void initCameraSource() {
        FaceDetector detector = new FaceDetector.Builder(this)
                .setTrackingEnabled(true)
                .setClassificationType(FaceDetector.ALL_CLASSIFICATIONS)
                .setMode(FaceDetector.FAST_MODE)
                .build();
        detector.setProcessor(new MultiProcessor.Builder(new FaceTrackerDaemon(MainActivity.this)).build());

        cameraSource = new CameraSource.Builder(this, detector)
                .setRequestedPreviewSize(1024, 768)
                .setFacing(CameraSource.CAMERA_FACING_FRONT)
                .setRequestedFps(30.0f)
                .build();

        try {
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                // TODO: Consider calling
                //    ActivityCompat#requestPermissions
                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //                                          int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
                // for ActivityCompat#requestPermissions for more details.
                return;
            }
            cameraSource.start();
        }
        catch (IOException e) {
            Toast.makeText(MainActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (cameraSource != null) {
            try {
                if (ActivityCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                    // TODO: Consider calling
                    //    ActivityCompat#requestPermissions
                    // here to request the missing permissions, and then overriding
                    //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                    //                                          int[] grantResults)
                    // to handle the case where the user grants the permission. See the documentation
                    // for ActivityCompat#requestPermissions for more details.
                    return;
                }
                cameraSource.start();
            }
            catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (cameraSource!=null) {
            cameraSource.stop();
        }

        setBackgroundGrey();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (cameraSource!=null) {
            cameraSource.release();
        }
    }

    //update view
    public void updateMainView(Condition condition){
        switch (condition){
            case USER_EYES_OPEN:
                setBackgroundGreen();
                user_message.setText("睜開眼");
                hourglass.pauseTimer();

                break;
            case USER_EYES_CLOSED:
                setBackgroundOrange();
                user_message.setText("閉眼");
                if (!isFinish){
                    hourglass.startTimer();

                }

                if (hourglass.isPaused()){
                    hourglass.resumeTimer();

                }




                break;
            case FACE_NOT_FOUND:
                setBackgroundRed();
                user_message.setText("User not found");
                break;
            default:
                setBackgroundGrey();
                user_message.setText("Hello World");
        }
    }

    //set background Grey
    private void setBackgroundGrey() {
        if(background != null)
            background.setBackgroundColor(getResources().getColor(android.R.color.darker_gray));
    }

    //set background Green
    private void setBackgroundGreen() {
        if(background != null){
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    background.setBackgroundColor(getResources().getColor(android.R.color.holo_green_dark));
                }
            });
        }
    }

    //set background Orange
    private void setBackgroundOrange() {
        if(background != null){
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    background.setBackgroundColor(getResources().getColor(android.R.color.holo_orange_dark));
                }
            });
        }
    }

    //set background Red
    private void setBackgroundRed() {
        if(background != null){
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    background.setBackgroundColor(getResources().getColor(android.R.color.holo_red_dark));
                }
            });
        }
    }
    private void setAlert(){
        new AlertDialog.Builder(this)
                .setTitle("時間提醒通知")
                .setMessage("時間提醒通知")
                .setNegativeButton("我知道了", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                    }
                }).show();

    }
}
