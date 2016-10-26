package com.poovarasan.miu.activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.databinding.DataBindingUtil;
import android.graphics.Color;
import android.graphics.Point;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.view.Display;
import android.view.View;
import android.view.Window;
import android.widget.Toast;

import com.flipkart.chatheads.ui.ChatHead;
import com.flipkart.chatheads.ui.ChatHeadArrangement;
import com.flipkart.chatheads.ui.ChatHeadContainer;
import com.flipkart.chatheads.ui.ChatHeadListener;
import com.flipkart.chatheads.ui.ChatHeadViewAdapter;
import com.flipkart.chatheads.ui.MinimizedArrangement;
import com.poovarasan.miu.MainActivity;
import com.poovarasan.miu.R;
import com.poovarasan.miu.application.App;
import com.poovarasan.miu.config.CharHeadConfig;
import com.poovarasan.miu.databinding.ActivityChatHeadBinding;
import com.poovarasan.miu.fragments.widget.CharHeadContainer;
import com.poovarasan.miu.widget.CircularDrawable;
import com.poovarasan.miu.widget.TextDrawer;
import com.poovarasan.miu.widget.notification.CircularNotificationDrawer;

public class ChatHeadActivity extends AppCompatActivity {

    ActivityChatHeadBinding activityChatHeadBinding;
    private SharedPreferences chatHeadPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activityChatHeadBinding = DataBindingUtil.setContentView(this, R.layout.activity_chat_head);
        chatHeadPreferences = getSharedPreferences("chat", MODE_PRIVATE);

        Window window = this.getWindow();
        //window.addFlags(WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD);
        //window.addFlags(WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED);
        //window.addFlags(WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().setStatusBarColor(Color.TRANSPARENT);
            getWindow().setNavigationBarColor(Color.TRANSPARENT);
        }
        activityChatHeadBinding.chatHeadContainer.setViewAdapter(new ChatHeadViewAdapter() {
            @Override
            public FragmentManager getFragmentManager() {
                return getSupportFragmentManager();
            }

            @Override
            public Fragment instantiateFragment(Object key, ChatHead chatHead) {
                // return the fragment which should be shown when the arrangment switches to maximized (on clicking a chat head)
                // you can use the key parameter to get back the object you passed in the addChatHead method.
                // this key should be used to decide which fragment to show.
                return CharHeadContainer.newInstance();
            }

            @Override
            public Drawable getChatHeadDrawable(Object key) {


                CircularDrawable circularDrawable = new CircularDrawable();
                TextDrawer textDrawer = new TextDrawer().setText("A").setTextColor(Color.WHITE).setBackgroundColor(App.generateRandomColor());
                circularDrawable.setBitmapOrTextOrIcon(textDrawer);


                circularDrawable
                        .setNotificationDrawer(new CircularNotificationDrawer().setNotificationText("1").setNotificationAngle(135).setNotificationColor(Color.WHITE, Color.RED));

                // this is where you return a drawable for the chat head itself based on the key. Typically you return a circular shape
                // you may want to checkout circular image library https://github.com/flipkart-incubator/circular-image
                return circularDrawable;
            }

            @Override
            public Drawable getPointerDrawable() {
                return getResources().getDrawable(R.drawable.default_image);
            }

            @Override
            public View getTitleView(Object key, ChatHead chatHead) {
                return null;
            }
        });

        activityChatHeadBinding.chatHeadContainer.setOnItemSelectedListener(new ChatHeadContainer.OnItemSelectedListener() {
            @Override
            public boolean onChatHeadSelected(Object key, ChatHead chatHead) {
                return false;
            }

            @Override
            public void onChatHeadRollOver(Object key, final ChatHead chatHead) {

                Toast.makeText(getApplicationContext(),"Roll Over",Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onChatHeadRollOut(Object key, ChatHead chatHead) {
                Toast.makeText(getApplicationContext(),"Roll Out",Toast.LENGTH_SHORT).show();
            }
        });
        activityChatHeadBinding.chatHeadContainer.setListener(new ChatHeadListener() {
            @Override
            public void onChatHeadAdded(Object key) {
            }

            @Override
            public void onChatHeadRemoved(Object key, boolean userTriggered) {
                quit();
            }

            @Override
            public void onChatHeadArrangementChanged(ChatHeadArrangement oldArrangement, ChatHeadArrangement newArrangement) {
                setTitle(newArrangement.getClass().getSimpleName());
            }

            @Override
            public void onChatHeadAnimateStart(ChatHead chatHead) {

            }

            @Override
            public void onChatHeadAnimateEnd(ChatHead chatHead) {

            }
        });
        if (savedInstanceState == null) {
            activityChatHeadBinding.chatHeadContainer.setArrangement(MinimizedArrangement.class, null);

        }

        activityChatHeadBinding.chatHeadContainer.setConfig(new CharHeadConfig(this, getInitialX(), getInitialY()));
        activityChatHeadBinding.chatHeadContainer.addChatHead("head" + Math.random(), false, true);
        activityChatHeadBinding.chatHeadContainer.setArrangement(MinimizedArrangement.class, null);
    }

    private int getInitialX() {
        Display display = getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        int width = size.x;
        int height = size.y;
        float defaultChatHeadXPosition = width;
        return chatHeadPreferences.getInt("initialX", (int) defaultChatHeadXPosition);
    }

    public void quit() {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            finishAndRemoveTask();
        } else {

            Intent intent  = new Intent(this, MainActivity.class);
            intent.putExtra("EXTRA_FINISH", true);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
            finish();
        }
    }

    private int getInitialY() {
        Display display = getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        int width = size.x;
        int height = size.y;
        float defaultChatHeadYPosition = height * 0.50f;
        return chatHeadPreferences.getInt("initialY", (int) defaultChatHeadYPosition);
    }
}
