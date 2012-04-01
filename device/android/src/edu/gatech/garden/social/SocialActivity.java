package edu.gatech.garden.social;

import java.io.IOException;
import java.net.MalformedURLException;
import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONObject;

import android.app.ListActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;
import edu.gatech.garden.R;
import edu.gatech.garden.RunYourGardenActivity;

public class SocialActivity extends ListActivity {

    public static ArrayList<String> friendLists;

    @Override
    public void onCreate(Bundle icicle) {
        super.onCreate(icicle);
        friendLists = new ArrayList<String>();

        String response;
        try {
            response = RunYourGardenActivity.facebook.request("/me/friends");
            JSONObject json = new JSONObject(response);
            JSONArray data = json.getJSONArray("data");
            for (int i = 0; i < data.length(); ++i) {
                JSONObject friend = (JSONObject) data.get(i);
                String name = friend.getString("name");
                // String id = friend.getString("id");
                friendLists.add(name);
            }
            // Use your own layout
            ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
                    R.layout.social, R.id.label, friendLists);
            setListAdapter(adapter);
        } catch (MalformedURLException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (Exception e) {

        }
    }

    @Override
    protected void onListItemClick(ListView l, View v, int position, long id) {
        String item = (String) getListAdapter().getItem(position);
        Toast.makeText(this, item + " selected", Toast.LENGTH_LONG).show();
    }
}
