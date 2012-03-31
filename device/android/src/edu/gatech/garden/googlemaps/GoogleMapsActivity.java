package edu.gatech.garden.googlemaps;

import java.util.ArrayList;

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

    public class Coordinate {
        public double latitude;
        public double longitude;
        public double altitude;
        public float speed;
        public long timestamp;

        Coordinate(double latitude, double longitude, double altitude,
                long timestamp, float speed) {
            this.latitude = latitude;
            this.longitude = longitude;
            this.altitude = altitude;
            this.timestamp = timestamp;
            this.speed = speed;
        }
    }

    public static double latitude;
    public static double longitude;
    public static double altitude;
    public static long timestamp;
    public static float speed;
    public static double accDistance;
    public static TextView tvCurrentSpeed = null;
    public static TextView debugText = null;
    public static Button sessionBtn = null;

    public static MapController mapController;
    public static MapView mapView;
    public static LocationManager locationManager;
    public static MyOverlays itemizedoverlay;
    public static MyLocationOverlay myLocationOverlay;

    // TODO: synchronization
    public static ArrayList<Coordinate> coordinates = null;

    public static long startTime = 0;
    public static long currentTime = 0;
    public static boolean isSessionRunning = false;
    public final static long SLEEP_INTERVAL = 5000;
    public static String locationProvider = null;
    private static SessionTask sessionTask = null;
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

        debugText = (TextView) findViewById(R.id.debug);

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
            timestamp = location.getTime();
            GeoPoint point = new GeoPoint((int) (latitude * 1E6),
                    (int) (longitude * 1E6));
            // createMarker();
            mapController.animateTo(point);
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
            sessionBtn.setText(R.string.strBtnSessionStart);
        } else {
            sessionBtn.setText(R.string.strBtnSessionStop);
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
            coordinates = new ArrayList<Coordinate>();
            Coordinate initCoord = new Coordinate(latitude, longitude,
                    altitude, timestamp, speed);
            coordinates.add(initCoord);
            accDistance = 0.0;
            Bundle bundle = new Bundle();
            bundle.putString("user_id", HomeActivity.userid);
            bundle.putString("session_state", "start");
            bundle.putString("gps_update", formalizeGpsUpdate(initCoord));

            String resp = Communicator.communicate(bundle,
                    Communicator.SESSION_URL);
            // debugText.setText(resp);
            try {
                JSONObject json = new JSONObject(resp);
                sessionid = json.getString("session_id");
            } catch (JSONException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            Toast.makeText(this, sessionid, Toast.LENGTH_LONG).show();

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
                    if (coordinates == null) {
                        throw new Exception();
                    }
                    Coordinate coord = new Coordinate(latitude, longitude,
                            altitude, timestamp, speed);
                    coordinates.add(coord);
                    int index = coordinates.size() - 1;
                    accDistance += convertGpsToMeters(coordinates.get(index),
                            coordinates.get(index - 1));
                    String gpsUpdate = formalizeGpsUpdate(coord);
                    Bundle bundle = new Bundle();
                    bundle.putString("user_id", HomeActivity.userid);
                    bundle.putString("session_id", sessionid);
                    bundle.putString("session_state", "ongoing");
                    bundle.putString("gps_update", gpsUpdate);

                    Communicator.communicate(bundle, Communicator.SESSION_URL);
                    publishProgress(gpsUpdate + ";" + accDistance);

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
                Toast.makeText(GoogleMapsActivity.this, updates[0],
                        Toast.LENGTH_SHORT).show();

            }
        }
    }

    public static double convertGpsToMeters(Coordinate from, Coordinate to) {
        double dlon, dlat, a, c;
        dlon = to.longitude - from.longitude;
        dlat = to.latitude - to.latitude;
        a = Math.pow(Math.sin(dlat / 2), 2) + Math.cos(from.latitude)
                * Math.cos(to.latitude) * Math.pow(Math.sin(dlon / 2), 2);
        c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        return 6378140 * c; /* 6378140 is the radius of the Earth in meters */
    }

    public static String formalizeGpsUpdate(Coordinate coord) {
        return coord.timestamp + ";" + coord.latitude + ";" + coord.longitude
                + ";" + coord.altitude + ";" + coord.speed;
    }
}
