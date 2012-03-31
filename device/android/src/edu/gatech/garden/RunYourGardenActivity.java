package edu.gatech.garden;

import android.app.TabActivity;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.res.Resources;
import android.os.Bundle;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.LinearLayout;
import android.widget.TabHost;
import android.widget.Toast;
import edu.gatech.garden.googlemaps.GoogleMapsActivity;
import edu.gatech.garden.history.HistoryActivity;
import edu.gatech.garden.home.HomeActivity;

public class RunYourGardenActivity extends TabActivity {

    public static TabHost mTab = null;

    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.maintab);

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
                Toast.makeText(RunYourGardenActivity.this, "Ani gone",
                        Toast.LENGTH_LONG).show();
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

        intent = new Intent().setClass(this, HistoryActivity.class);
        spec = tabHost
                .newTabSpec("")
                .setIndicator("Friends",
                        res.getDrawable(R.drawable.ic_tab_social))
                .setContent(intent);
        tabHost.addTab(spec);

        tabHost.setCurrentTab(0);
    }

}