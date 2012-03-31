package edu.gatech.garden.home;

import java.io.IOException;
import java.net.MalformedURLException;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.GridView;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.android.DialogError;
import com.facebook.android.Facebook;
import com.facebook.android.Facebook.DialogListener;
import com.facebook.android.FacebookError;

import edu.gatech.garden.R;
import edu.gatech.garden.communicator.Communicator;

public class HomeActivity extends Activity {

    public static TextView tvUsername = null;

    public static Facebook facebook = new Facebook("168610229927148");
    public static final String FILENAME = "AndroidSSO_data";
    private SharedPreferences mPrefs;

    public static String userid = null;
    public static String username = null;
    public static int weight;
    public static int age;
    public static int height;
    public static double accDistance = 0.0;
    public static long accTime = 0;
    public static long accCalories = 0;
    public static double remainDistance = 0.0;
    public static long remainTime = 0;
    public static long remainCalories = 0;

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
            facebook.authorize(this, new String[] {}, new DialogListener() {
                @Override
                public void onComplete(Bundle values) {
                    SharedPreferences.Editor editor = mPrefs.edit();
                    editor.putString("access_token", facebook.getAccessToken());
                    editor.putLong("access_expires",
                            facebook.getAccessExpires());
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
            });
        }
        try {
            String response = facebook.request("/me");
            JSONObject json = new JSONObject(response);
            userid = json.getString("id");
            username = json.getString("name");
            tvUsername.setText("Welcome " + username);

            Bundle bundle = new Bundle();
            bundle.putString("user_id", userid);

            // Login to RunYourGarden Server
            String resp = Communicator.communicate(bundle,
                    Communicator.LOGIN_URL);
            JSONObject resJson = new JSONObject(resp);
            accDistance = Double.parseDouble(resJson.getString("acc_distance"));
            accTime = Long.parseLong(resJson.getString("acc_time"));
            accCalories = Long.parseLong(resJson.getString("acc_calories"));
            remainDistance = Double.parseDouble(resJson
                    .getString("remain_distance"));
            remainTime = Long.parseLong(resJson.getString("remain_time"));
            remainCalories = Long.parseLong(resJson
                    .getString("remain_calories"));

        } catch (MalformedURLException e1) {
            e1.printStackTrace();
        } catch (IOException e1) {
            e1.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.home);
        tvUsername = (TextView) findViewById(R.id.username);

        if (userid == null) {
            facebookLogin();
        }

        GridView gridview = (GridView) findViewById(R.id.gridview);
        gridview.setAdapter(new GardenAdapter(this));

        gridview.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View v,
                    int position, long id) {
                Toast.makeText(HomeActivity.this, "" + position,
                        Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu optionMenu) {
        super.onCreateOptionsMenu(optionMenu);

        optionMenu.add(0, 5, 1, R.string.Exit);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
        case 5:
            android.os.Process.killProcess(android.os.Process.myPid());
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        facebook.authorizeCallback(requestCode, resultCode, data);
    }

    @Override
    public void onResume() {
        super.onResume();
        if (username != null) {
            tvUsername.setText(username);
        }
    }
}
