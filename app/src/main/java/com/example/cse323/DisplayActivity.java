package com.example.cse323;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;

import java.util.ArrayList;

public class DisplayActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display);
        Bundle bundle = this.getIntent().getExtras();
        ArrayList<Row> rows;
        double sum = 0;
        if (bundle != null) {
            rows = (ArrayList<Row>)bundle.getSerializable("rows");
            sum = bundle.getDouble("sum");
        }
        else{
            assert(0 == 1);
            return;
        }
        for(Row row: rows){
            Log.d("_ENTRY_", "" + row);
        }
        Log.d("_ENTRY_", "" + sum);
    }
}
