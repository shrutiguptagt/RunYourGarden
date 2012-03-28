package edu.gatech.garden.googlemaps;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.maps.GeoPoint;
import com.google.android.maps.MapActivity;
import com.google.android.maps.MapController;
import com.google.android.maps.MapView;
import com.google.android.maps.MyLocationOverlay;
import com.google.android.maps.OverlayItem;

import edu.gatech.garden.R;
import edu.gatech.garden.communicator.Communicator;
import edu.gatech.garden.home.HomeActivity;

public class GoogleMapsActivity extends MapActivity {

    public static TextView tvCurrentSpeed = null;
    public static Button sessionBtn = null;

    public static MapController mapController;
    public static MapView mapView;
    public static LocationManager locationManager;
    public static MyOverlays itemizedoverlay;
    public static MyLocationOverlay myLocationOverlay;
    public static double latitude = 0.0;
    public static double longitude = 0.0;
    public static double altitude = 0.0;
    public static float speed = 0.0f;
    public static long startTime = 0;
    public static long currentTime = 0;
    public static boolean isSessionRunning = false;
    public final static long SLEEP_INTERVAL = 2000;
    public static String locationProvider = null;
    private SessionTask sessionTask = null;
    private static GeoUpdateHandler geoUpdateHandler;
    public static String sessionid = null;

    @Override
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        setContentView(R.layout.mapview); // bind the layout to the activity

        // Configure the Map
        mapView = (MapView) findViewById(R.id.mapview);
        mapView.setBuiltInZoomControls(true);
        mapView.setStreetView(true);
        mapController = mapView.getController();
        mapController.setZoom(14); // Zoon 1 is world view
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        Criteria criteria = new Criteria();
        locationProvider = locationManager.getBestProvider(criteria, false);
        geoUpdateHandler = new GeoUpdateHandler();
        locationManager.requestLocationUpdates(locationProvider, 0, 0,
                geoUpdateHandler);

        myLocationOverlay = new MyLocationOverlay(this, mapView);
        mapView.getOverlays().add(myLocationOverlay);

        sessionBtn = (Button) findViewById(R.id.sessionBtn);

        myLocationOverlay.runOnFirstFix(new Runnable() {
            @Override
            public void run() {
                mapView.getController().animateTo(
                        myLocationOverlay.getMyLocation());
            }
        });
        tvCurrentSpeed = (TextView) findViewById(R.id.currentSpeed);
        Drawable drawable = this.getResources().getDrawable(R.drawable.point);
        itemizedoverlay = new MyOverlays(this, drawable);
        // createMarker();
    }

    @Override
    protected boolean isRouteDisplayed() {
        return false;
    }

    public class GeoUpdateHandler implements LocationListener {

        @Override
        public void onLocationChanged(Location location) {
            latitude = location.getLatitude();
            longitude = location.getLongitude();
            altitude = location.getAltitude();
            speed = location.getSpeed();
            GeoPoint point = new GeoPoint((int) (latitude * 1E6),
                    (int) (longitude * 1E6));
            // createMarker();
            mapController.animateTo(point); // mapController.setCenter(point);
        }

        @Override
        public void onProviderDisabled(String provider) {
        }

        @Override
        public void onProviderEnabled(String provider) {
        }

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {
        }
    }

    private void createMarker() {
        GeoPoint p = mapView.getMapCenter();
        OverlayItem overlayitem = new OverlayItem(p, "", "");
        itemizedoverlay.addOverlay(overlayitem);
        if (itemizedoverlay.size() > 0) {
            mapView.getOverlays().add(itemizedoverlay);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (!isSessionRunning) {
            myLocationOverlay.enableMyLocation();
            myLocationOverlay.enableCompass();
            locationManager.requestLocationUpdates(locationProvider, 0, 0,
                    new GeoUpdateHandler());
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (!isSessionRunning) {
            myLocationOverlay.disableMyLocation();
            myLocationOverlay.disableCompass();
            locationManager.removeUpdates(geoUpdateHandler);
        }
    }

    public void handleSessionChange(View view) {
        isSessionRunning = !isSessionRunning;
        if (isSessionRunning) {
            Bundle bundle = new Bundle();
            bundle.putString("user_id", HomeActivity.userid);
            bundle.putString("session_state", "start");
            String resp = Communicator.communicate(bundle,
                    Communicator.SESSION_URL);
            try {
                JSONObject json = new JSONObject(resp);
                sessionid = json.getString("session_id");
            } catch (JSONException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }

            sessionTask = new SessionTask();
            sessionTask.execute();
            sessionBtn.setText(R.string.strBtnSessionStop);
        } else {
            sessionTask.cancel(true);
            Toast.makeText(this, "Session stopped", Toast.LENGTH_SHORT).show();
            sessionBtn.setText(R.string.strBtnSessionStart);
        }
    }

    public class SessionTask extends AsyncTask<Void, String, Void> {

        @Override
        protected Void doInBackground(Void... params) {
            try {
                while (true) {
                    String gpsUpdate = System.currentTimeMillis() + ";"
                            + latitude + ";" + longitude + ";" + altitude;
                    Bundle bundle = new Bundle();
                    bundle.putString("user_id", HomeActivity.userid);
                    bundle.putString("session_id", sessionid);
                    bundle.putString("session_state", "ongoing");
                    bundle.putString("gps_update", "" + speed);

                    Communicator.communicate(bundle, Communicator.SESSION_URL);

                    publishProgress(gpsUpdate);

                    Thread.sleep(SLEEP_INTERVAL);
                }
            } catch (Exception e) {

            }
            return null;
        }

        @Override
        protected void onCancelled() {
            Bundle bundle = new Bundle();
            bundle.putString("user_id", HomeActivity.userid);
            bundle.putString("session_id", sessionid);
            bundle.putString("session_state", "stop");
        }

        @Override
        protected void onProgressUpdate(String... updates) {
            if (updates != null && updates.length > 0) {
                tvCurrentSpeed.setText(updates[0]);
            }
        }
    }

}
