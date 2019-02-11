package xyz.lebalex.mysongs;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.opengl.Visibility;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.constraint.ConstraintLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.util.TypedValue;
import android.view.Display;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.View;
import android.webkit.WebView;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.Timer;
import java.util.TimerTask;

import static java.lang.Math.abs;

public class SongActivity extends AppCompatActivity {


    private Timer mTimer;
    private int STEP = 100;
    private int sdvig = 0;
    private int scrollB = -1;
    private boolean startScroll = false;


    private float sizeTextConst=42.0f;
    private float sizeText=sizeTextConst;
    private int perionScrollConst;
    private int perionScroll;

    private ScaleGestureDetector scaleGestureDetector;
    private GestureDetector gestureDetector;
    private TextView songTextView;
    private ScrollView scrollPanel;
    private FloatingActionButton fab;
    private String songFileName;




    public class simpleOnScaleGestureListener extends
            ScaleGestureDetector.SimpleOnScaleGestureListener {

        @Override
        public boolean onScale(ScaleGestureDetector detector) {
            float factor = detector.getScaleFactor();
            /*if(factor<1) {
                if (sizeText > 14)
                    sizeText = --sizeText;
            }else
                sizeText = ++sizeText;*/
            sizeText = Math.round(songTextView.getTextSize()*factor);
            if (sizeText < sizeTextConst)
                sizeText = sizeTextConst;

                //songTextView.setTextSize(TypedValue.COMPLEX_UNIT_SP, sizeText);
            songTextView.setTextSize(TypedValue.COMPLEX_UNIT_PX, sizeText);

            scrollB = scrollPanel.getChildAt(0).getHeight() - scrollPanel.getHeight()+100;
            //Log.d("scale", factor+"");
            if (scrollB < 0)
            {
                scrollB = STEP;
                fab.setVisibility(View.GONE);
            }else
            {
                STEP = Math.round(STEP+((factor-1.0f)*1000));
                //perionScroll = Math.round(perionScroll/factor);
                perionScroll = perionScrollConst-Math.round((sizeText-sizeTextConst)*2);
                if(perionScroll<=0) perionScroll=1;
                fab.setVisibility(View.VISIBLE);
            }
            //Log.d("scalemy", STEP+"");

            return true;
        }
    }

    public class simpleOnGestureListener extends GestureDetector.SimpleOnGestureListener {

                @Override
                public boolean onDown(MotionEvent e) {
                    return true;
                }

                @Override
                public boolean onDoubleTap(MotionEvent e) {
                    Intent mIntent = new Intent(getApplicationContext(), SongEditActivity.class);
                    mIntent.putExtra("filename", songFileName);
                    startActivityForResult(mIntent, 111);
                    return super.onSingleTapUp(e);
                }

                @Override
                public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX,
                                       float velocityY) {
                    return super.onFling(e1, e2, velocityX, velocityY);
                }

            }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 111) {
            if (resultCode == RESULT_OK)
                songTextView.setText(FileHelper.getFileContext(songFileName));
        }
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(this);
        if(sp.getBoolean("black_theme", false))
            setTheme(R.style.AppThemeDarkNoActionBar);
        else
            setTheme(R.style.AppThemeLightNoActionBar);
        perionScrollConst=Integer.parseInt(sp.getString("perionScrollConst", getResources().getString(R.string.pref_default_perionScrollConst)));
        setContentView(R.layout.activity_song);
        songFileName = getIntent().getStringExtra("filename");
        songTextView = (TextView) findViewById(R.id.songText);
        sizeText = sp.getFloat("fontSize", sizeText);
        songTextView.setTextSize(TypedValue.COMPLEX_UNIT_PX, sizeText);

        float factor = sizeText/sizeTextConst;

        STEP = Math.round(STEP+((factor-1.0f)*1000));
        perionScroll = perionScrollConst-Math.round((sizeText-sizeTextConst)*2);
        if(perionScroll<=0) perionScroll=1;



        songTextView.setText(FileHelper.getFileContext(songFileName));

        scaleGestureDetector = new ScaleGestureDetector(this, new simpleOnScaleGestureListener());
        gestureDetector = new GestureDetector(this, new simpleOnGestureListener());

        songTextView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {

                boolean retVal = scaleGestureDetector.onTouchEvent(event);
                retVal = gestureDetector.onTouchEvent(event) || retVal;
                return retVal;

                //return scaleGestureDetector.onTouchEvent(event);
            }
        });



        scrollPanel = (ScrollView) findViewById(R.id.scrollPanel);
        fab = (FloatingActionButton) findViewById(R.id.fab);

        scrollPanel.post(new Runnable() {
            @Override
            public void run() {
                scrollB = scrollPanel.getChildAt(0).getHeight() - scrollPanel.getHeight()+100;
                if (scrollB < 0)
                {
                    scrollB = sdvig;
                    fab.setVisibility(View.GONE);
                }
            }
        });





        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startScroll = !startScroll;
                if (startScroll)
                    startScrollTimer(scrollPanel, fab);
                else
                    stopScrollTimer(scrollPanel, fab);
            }
        });

    }

    private void stopScrollTimer(final ScrollView scrollPanel, final FloatingActionButton fab) {
        fab.setImageDrawable(ContextCompat.getDrawable(getApplicationContext(), android.R.drawable.ic_media_play));
        if (mTimer != null) {
            mTimer.cancel();
            mTimer = null;
        }
    }
    private void startScrollTimer(final ScrollView scrollPanel, final FloatingActionButton fab) {
        fab.setImageDrawable(ContextCompat.getDrawable(getApplicationContext(), android.R.drawable.ic_media_pause));
        if (mTimer != null) {
            mTimer.cancel();
        }
        mTimer = new Timer();
        mTimer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        //sdvig += STEP;
                        sdvig += 2;
                        scrollPanel.smoothScrollTo(0, sdvig);
                        if (sdvig > scrollB) {
                            //scrollPanel.smoothScrollTo(0, scrollPanel.getBottom());
                            mTimer.cancel();
                            mTimer = null;
                            fab.setImageDrawable(ContextCompat.getDrawable(getApplicationContext(), android.R.drawable.ic_media_play));
                            sdvig = 0;
                        }

                        //Log.d("scroll", sdvig+" - "+scrollB);
                    }
                });
            }
        }, 0, perionScroll);
    }



    @Override
    protected void onDestroy() {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(this);
        SharedPreferences.Editor editor = sp.edit();
        editor.putFloat("fontSize", songTextView.getTextSize());
        editor.apply();
        editor.commit();



        super.onDestroy();
        if (mTimer != null) {
            mTimer.cancel();
            mTimer = null;
        }
    }
}
