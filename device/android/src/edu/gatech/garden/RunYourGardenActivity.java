package edu.gatech.garden;

import java.io.IOException;
import java.net.MalformedURLException;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.TabActivity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.content.res.Resources;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.LinearLayout;
import android.widget.TabHost;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.android.DialogError;
import com.facebook.android.Facebook;
import com.facebook.android.Facebook.DialogListener;
import com.facebook.android.FacebookError;

import edu.gatech.garden.communicator.Communicator;
import edu.gatech.garden.googlemaps.GoogleMapsActivity;
import edu.gatech.garden.history.HistoryActivity;
import edu.gatech.garden.home.HomeActivity;
import edu.gatech.garden.social.SocialActivity;

public class RunYourGardenActivity extends TabActivity {

    public static TabHost mTab = null;

    public static Facebook facebook = new Facebook("168610229927148");
    public static final String FILENAME = "AndroidSSO_data";
    public static SharedPreferences mPrefs;

    public static DialogListener dialogListener = new DialogListener() {
        @Override
        public void onComplete(Bundle values) {
            SharedPreferences.Editor editor = mPrefs.edit();
            editor.putString("access_token", facebook.getAccessToken());
            editor.putLong("access_expires", facebook.getAccessExpires());
            editor.commit();
        }

        @Override
        public void onFacebookError(FacebookError error) {
        }

        @Override
        public void onError(DialogError e) {
        }

        @Override
        public void onCancel() {
        }
    };

    private void facebookLogin() {
        // Get existing access_token if any
        mPrefs = getPreferences(MODE_PRIVATE);
        String access_token = mPrefs.getString("access_token", null);
        long expires = mPrefs.getLong("access_expires", 0);
        if (access_token != null) {
            facebook.setAccessToken(access_token);
        }
        if (expires != 0) {
            facebook.setAccessExpires(expires);
        }

        // Only call authorize if the access_token has expired.
        if (!facebook.isSessionValid()) {
            facebook.authorize(this, new String[] { "read_friendlists", },
                    dialogListener);
        }
        try {
            String response = facebook.request("/me");
            JSONObject json = new JSONObject(response);
            HomeActivity.userid = json.getString("id");
            HomeActivity.username = json.getString("name");
            tvTitleLoginUser.setText(HomeActivity.username);
            Toast.makeText(this, HomeActivity.username, Toast.LENGTH_LONG);
            Bundle bundle = new Bundle();
            bundle.putString("user_id", HomeActivity.userid);

            // Login to RunYourGarden Server
            String resp = Communicator.communicate(bundle,
                    Communicator.LOGIN_URL);
            JSONObject resJson = new JSONObject(resp);
            HomeActivity.accDistance = Double.parseDouble(resJson
                    .getString("acc_distance"));
            HomeActivity.accTime = Long
                    .parseLong(resJson.getString("acc_time"));
            HomeActivity.accCalories = Long.parseLong(resJson
                    .getString("acc_calories"));
            HomeActivity.remainDistance = Double.parseDouble(resJson
                    .getString("remain_distance"));
            HomeActivity.remainTime = Long.parseLong(resJson
                    .getString("remain_time"));
            HomeActivity.remainCalories = Long.parseLong(resJson
                    .getString("remain_calories"));

        } catch (MalformedURLException e1) {
            e1.printStackTrace();
        } catch (IOException e1) {
            e1.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    boolean customTitleSupported = false;
    public static TextView tvAppTitle = null;
    public static TextView tvTitleLoginUser = null;

    public void customTitleBar(String left, String right) {
        if (right.length() > 20) {
            right = right.substring(0, 20);
        }
        // set up custom title
        if (customTitleSupported) {
            getWindow().setFeatureInt(Window.FEATURE_CUSTOM_TITLE,
                    R.layout.title_bar);
            tvAppTitle = (TextView) findViewById(R.id.appTitle);
            tvTitleLoginUser = (TextView) findViewById(R.id.loginUser);

            tvAppTitle.setText(left);
            tvTitleLoginUser.setText(right);
        }
    }

    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        customTitleSupported = requestWindowFeature(Window.FEATURE_CUSTOM_TITLE);
        setContentView(R.layout.maintab);

        // set custom titlebar
        customTitleBar(getText(R.string.app_name).toString(),
                getText(R.string.strEmpty).toString());

        // Fix orientation
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        // Set title "Run Your Garden"
        this.setTitle(R.string.strAppTitle);

        // Launch welcome animation
        LinearLayout mainMenu = (LinearLayout) findViewById(R.id.mainLayout);
        LinearLayout linear = (LinearLayout) findViewById(R.id.welcomeLayout);
        mainMenu.bringChildToFront(linear);

        final Animation toDisappearance = AnimationUtils.loadAnimation(this,
                R.anim.alpha);
        toDisappearance.reset();
        LinearLayout splashScreen = (LinearLayout) findViewById(R.id.welcomeLayout);
        splashScreen.startAnimation(toDisappearance);

        toDisappearance.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
                // To change body of implemented methods use File | Settings |
                // File Templates.
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                LinearLayout splashScreen = (LinearLayout) findViewById(R.id.welcomeLayout);
                splashScreen.setVisibility(View.GONE);
                // Toast.makeText(RunYourGardenActivity.this, "Ani gone",
                // Toast.LENGTH_LONG).show();
            }

            @Override
            public void onAnimationRepeat(Animation animation) {
                // To change body of implemented methods use File | Settings |
                // File Templates.
            }
        });

        mTab = getTabHost();

        Resources res = getResources(); // Resource object to get Drawables
        TabHost tabHost = getTabHost(); // The activity TabHost
        TabHost.TabSpec spec; // Resusable TabSpec for each tab
        Intent intent; // Reusable Intent for each tab

        // Create an Intent to launch an Activity for the tab (to be reused)
        intent = new Intent().setClass(this, HomeActivity.class);

        // Initialize a TabSpec for each tab and add it to the TabHost
        spec = tabHost.newTabSpec("")
                .setIndicator("Home", res.getDrawable(R.drawable.ic_tab_home))
                .setContent(intent);
        tabHost.addTab(spec);

        // Do the same for the other tabs
        intent = new Intent().setClass(this, GoogleMapsActivity.class);
        spec = tabHost.newTabSpec("")
                .setIndicator("Map", res.getDrawable(R.drawable.ic_tab_map))
                .setContent(intent);
        tabHost.addTab(spec);

        intent = new Intent().setClass(this, HistoryActivity.class);
        spec = tabHost
                .newTabSpec("")
                .setIndicator("History",
                        res.getDrawable(R.drawable.ic_tab_stat))
                .setContent(intent);
        tabHost.addTab(spec);

        intent = new Intent().setClass(this, SocialActivity.class);
        spec = tabHost
                .newTabSpec("")
                .setIndicator("Friends",
                        res.getDrawable(R.drawable.ic_tab_social))
                .setContent(intent);
        tabHost.addTab(spec);

        tabHost.setCurrentTab(0);

        if (HomeActivity.userid == null) {
            facebookLogin();
        }
    }
}