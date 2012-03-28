package edu.gatech.garden;

import android.app.TabActivity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TabHost;
import android.widget.TabHost.TabSpec;
import android.widget.TextView;
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
        setRequestedOrientation (ActivityInfo.SCREEN_ORIENTATION_PORTRAIT); 
        
        // Set title "Run Your Garden"
        this.setTitle(R.string.strAppTitle);
                
        mTab = getTabHost();
        
        setupTab(new TextView(this), "Home", new Intent(this, HomeActivity.class));
        setupTab(new TextView(this), "Map", new Intent(this, GoogleMapsActivity.class));
        setupTab(new TextView(this), "History", new Intent(this, HistoryActivity.class));
    }
    
    private static void setupTab(View view, String tag, Intent intent) {
        View tabView = createTabView(mTab.getContext(), tag);
        TabSpec setContent = mTab.newTabSpec(tag)
                             .setIndicator(tabView)
                             .setContent(intent);
        mTab.addTab(setContent);
    }
    
    private static View createTabView(Context context, String text) {
        View view = LayoutInflater.from(context).inflate(R.layout.tabcontent, null);
        TextView tv = (TextView)view.findViewById(R.id.tabsText);
        tv.setText(text);
        return view;
    }
    
}