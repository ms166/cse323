package com.example.app;

import android.app.Activity;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;

public class CustomListAdapter extends ArrayAdapter {
    private final Activity context;
    private ArrayList<Row> rows;

    public CustomListAdapter(Activity context, ArrayList<Row> rows){

        super(context, R.layout.listview_row, rows);

        this.context=context;
        this.rows = rows;
    }

    public View getView(int position, View view, ViewGroup parent) {
        LayoutInflater inflater=context.getLayoutInflater();
        View rowView=inflater.inflate(R.layout.listview_row, null,true);

        TextView cell_0 = (TextView) rowView.findViewById(R.id.cell_0);
        TextView cell_1 = (TextView) rowView.findViewById(R.id.cell_1);
        TextView cell_2 = (TextView) rowView.findViewById(R.id.cell_2);
        TextView cell_3 = (TextView) rowView.findViewById(R.id.cell_3);


        cell_0.setText(rows.get(position).getShort_name());
        cell_1.setText("" + rows.get(position).getTime_4_weeks());
        cell_2.setText("" + rows.get(position).getTime_last_week());
        if(rows.get(position).getPercentage_diff() >= 0) {
            cell_3.setText(String.format("+%.2f%%", rows.get(position).getPercentage_diff()));
            cell_3.setTextColor(Color.parseColor("#00bd11"));
        }
        else {
            cell_3.setText(String.format("%.2f%%", rows.get(position).getPercentage_diff()));
            cell_3.setTextColor(Color.parseColor("#ba160c"));
        }

        return rowView;


    };
}
