package edu.gatech.garden.home;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import edu.gatech.garden.Garden;
import edu.gatech.garden.Garden.Plant;
import edu.gatech.garden.R;

public class GardenAdapter extends BaseAdapter {
    private Context mContext;
    private LayoutInflater layoutInflater;

    public GardenAdapter(Context c) {
        mContext = c;
        layoutInflater = LayoutInflater.from(c);
    }

    @Override
    public int getCount() {
        return Garden.mThumbIds.length;
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    // create a new ImageView for each item referenced by the Adapter
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View grid;
        if (convertView == null) {
            grid = new View(mContext);
            grid = layoutInflater.inflate(R.layout.grid_item, null);
        } else {
            grid = convertView;
        }

        Plant plant = Garden.plantsList.get(position);
        ImageView imageView = (ImageView) grid.findViewById(R.id.plantImage);
        imageView.setImageResource(plant.drawableId);
        TextView calorieTv = (TextView) grid.findViewById(R.id.calorieText);
        calorieTv.setText(plant.calorie + " cal");
        TextView amountTv = (TextView) grid.findViewById(R.id.amountText);
        amountTv.setText("Tot: " + plant.amount);

        return grid;
    }
}
