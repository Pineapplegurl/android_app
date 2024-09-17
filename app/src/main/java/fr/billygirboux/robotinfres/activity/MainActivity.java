package fr.billygirboux.robotinfres.activity;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GestureDetectorCompat;

import fr.billygirboux.robotinfres.R;
import fr.billygirboux.robotinfres.service.RobotService;

public class MainActivity extends AppCompatActivity {

    private ImageView imgTop;
    private ImageView imgBottom;
    private ImageView imgLeft;
    private ImageView imgRight;
    private SeekBar seekSpeed;
    private ImageView imgVideo;
    private ImageView imgAVG;
    private ImageView imgAVD;
    private ImageView imgARD;
    private ImageView imgARG;


    private boolean videoStarted;


    private RobotService robotService;


    private String currentDirection = null;
    private int speed = 50;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        this.imgTop = findViewById(R.id.imgTop);
        this.imgBottom = findViewById(R.id.imgBottom);
        this.imgLeft = findViewById(R.id.imgLeft);
        this.imgRight = findViewById(R.id.imgRight);
        this.seekSpeed = findViewById(R.id.seekSpeed);
        this.imgVideo = findViewById(R.id.imgVideo);
        this.imgAVG = findViewById(R.id.imgAVG);
        this.imgAVD = findViewById(R.id.imgAVD);
        this.imgARD = findViewById(R.id.imgARD);
        this.imgARG = findViewById(R.id.imgARG);

        String ip = getIntent().getStringExtra("ip");
        int port = getIntent().getIntExtra("port", 0);

        Handler handler = initHandler();
        this.robotService = new RobotService(handler, getApplicationContext());
        this.robotService.connect(ip, port);

        initAllArrow();
        initVideo();

    }

    private Handler initHandler() {
        Handler handler = new Handler(Looper.getMainLooper()) {

            @Override
            public void handleMessage(@NonNull Message msg) {
                switch (msg.what) {
                    case 1:
                        Toast.makeText(MainActivity.this, R.string.connect_ok, Toast.LENGTH_SHORT).show();
                        robotService.retrieveDetectorsProx();
                        break;
                    case -1:
                        Toast.makeText(MainActivity.this, R.string.connect_ko, Toast.LENGTH_SHORT).show();
                        break;
                    case -2:
                        Toast.makeText(MainActivity.this, "TRAME_NOK", Toast.LENGTH_SHORT).show();
                        break;
                    case 2: // VIDEO RETRIEVE
                        imgVideo.setImageBitmap((Bitmap) msg.obj);
                        break;
                    case 3: // DETECTORS PROX
                        imgAVG.setImageResource(android.R.drawable.presence_online);
                        imgAVD.setImageResource(android.R.drawable.presence_online);
                        imgARD.setImageResource(android.R.drawable.presence_online);
                        imgARG.setImageResource(android.R.drawable.presence_online);

                        if (msg.obj.toString().contains("AVG")) {
                            imgAVG.setImageResource(android.R.drawable.presence_busy);
                        }
                        if (msg.obj.toString().contains("AVD")) {
                            imgAVD.setImageResource(android.R.drawable.presence_busy);
                        }
                        if (msg.obj.toString().contains("ARG")) {
                            imgARG.setImageResource(android.R.drawable.presence_busy);
                        }
                        if (msg.obj.toString().contains("ARD")) {
                            imgARD.setImageResource(android.R.drawable.presence_busy);
                        }
                        break;

                }
            }
        };

        return handler;

    }


    private void initAllArrow() {
        initArrow(this.imgTop);
        initArrow(this.imgBottom);
        initArrow(this.imgLeft);
        initArrow(this.imgRight);
    }


    private void initArrow(ImageView arrowView) {

        arrowView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                switch (motionEvent.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        if (currentDirection == null) {
                            arrowView.setImageResource(R.drawable.arrow_green);
                            currentDirection = arrowView.getTag().toString();
                            String command = currentDirection + " " + seekSpeed.getProgress() * 10;
                            robotService.sendCommand(command);

                        }
                        break;
                    case MotionEvent.ACTION_UP:
                        if (currentDirection.equals(arrowView.getTag().toString())) {
                            arrowView.setImageResource(R.drawable.arrow_red);
                            currentDirection = null;
                            robotService.sendCommand("ARRET");
                        }
                        break;
                }
                return true;
            }
        });
    }


    private void initVideo() {
        GestureDetector.OnGestureListener gestureListener = new GestureDetector.SimpleOnGestureListener() {
            @Override
            public boolean onDoubleTap(@NonNull MotionEvent e) {
                videoStarted = !videoStarted;
                robotService.sendCommand(videoStarted ? "VIDEO_START" : "VIDEO_STOP");
                if (videoStarted) {
                    robotService.retrieveVideo();
                } else {
                    robotService.stopVideo();
                    imgVideo.setImageResource(android.R.drawable.ic_menu_camera);
                }
                return true;
            }
        };

        GestureDetectorCompat gestureScanner = new GestureDetectorCompat(getApplicationContext(),
                gestureListener);

        this.imgVideo.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                gestureScanner.onTouchEvent(motionEvent);
                return true;
            }
        });
    }

    private void initDetectors() {

    }
}