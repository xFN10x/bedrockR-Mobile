package fn10.bedrockrmobile.activity.components;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;

import java.util.ArrayList;

import fn10.bedrockrmobile.R;

public class RArrayAdapter extends ArrayAdapter<String> {
    public RArrayAdapter(Context context, ArrayList<String> list) {
        super(context, 0, list);
    }

    public RArrayAdapter(Context context, String[] list) {
        super(context, 0, list);
    }

    @NonNull
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        return initView(position, convertView, parent);
    }

    @Override
    public View getDropDownView(int position, View convertView, ViewGroup parent) {
        return initView(position, convertView, parent);
    }

    private View initView(int position, View convertView, ViewGroup parent) {
        // Inflate the view only if it's null to optimize performance
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.rspinneritem, parent, false);
        }

        // Find TextView and set the item name
        TextView textViewName = convertView.findViewById(R.id.itemText);
        String currentItem = getItem(position);

        if (currentItem != null) {
            textViewName.setText(currentItem);
        }

        return convertView;
    }
}
