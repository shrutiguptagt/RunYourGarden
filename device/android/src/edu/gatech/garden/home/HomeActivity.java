package edu.gatech.garden.home;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.GridView;
import android.widget.TextView;
import edu.gatech.garden.Garden;
import edu.gatech.garden.Garden.Plant;
import edu.gatech.garden.R;
import edu.gatech.garden.Util;
import edu.gatech.garden.googlemaps.GoogleMapsActivity;

public class HomeActivity extends Activity {
    public static TextView tvUsername = null;

    public static Button checkOutBtn = null;
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
    public static TextView tvSummary = null;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.home);
        tvUsername = (TextView) findViewById(R.id.username);

        Garden.initGarden();
        Garden.initInventory();
        GridView gridview = (GridView) findViewById(R.id.gridview);
        gridview.setAdapter(new GardenAdapter(this));

        checkOutBtn = (Button) findViewById(R.id.setupGoal);
        tvSummary = (TextView) findViewById(R.id.summary);
        gridview.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View v,
                    int position, long id) {
                View grid = v;

                Plant plant = Garden.plantsList.get(position);
                plant.increaseAmount();
                TextView amountTv = (TextView) grid
                        .findViewById(R.id.amountText);
                amountTv.setText(" Tot: " + plant.amount);

                if (!plant.isInInventory) {
                    Garden.Inventory.add(plant);
                    plant.isInInventory = true;
                }

                int size = Garden.Inventory.size();
                int totalCal = 0;
                for (int i = 0; i < size; ++i) {
                    Plant p = Garden.Inventory.get(i);
                    totalCal += p.amount * p.calorie;
                }
                double mile = Util.getDistanceByCaloriesMilePound(totalCal,
                        180.0);
                tvSummary.setText("Total " + totalCal + " cal; "
                        + Util.DoubleFormat.format(mile) + " miles");
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu optionMenu) {
        super.onCreateOptionsMenu(optionMenu);

        optionMenu.add(0, 5, 1, R.string.Exit);

        return true;
    }

    public void handleCheckout(View view) {
        Intent myIntent = new Intent(view.getContext(),
                GoogleMapsActivity.class);
        startActivityForResult(myIntent, 0);
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
    }

    @Override
    public void onResume() {
        super.onResume();
    }

}
