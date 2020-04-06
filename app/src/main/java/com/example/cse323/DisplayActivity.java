package com.example.cse323;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AppOpsManager;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

public class DisplayActivity extends AppCompatActivity {
    ListView listview;
    TextView explanation, avg_pd;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display);

        // adds back button to go back to parent activity
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        Bundle bundle = this.getIntent().getExtras();
        ArrayList<Row> rows;
        int score;
        double sum = 0;
        if (bundle != null) {
            rows = (ArrayList<Row>)bundle.getSerializable("rows");
            sum = bundle.getDouble("sum");
            score = bundle.getInt("score");
        }
        else{
            assert(0 == 1);
            return;
        }
        CustomListAdapter whatever = new CustomListAdapter(this, rows);
        listview = (ListView) findViewById(R.id.listviewID);
        listview.setAdapter(whatever);

        double avg = sum/(double)rows.size();
        explanation = findViewById(R.id.explanation);
        avg_pd = findViewById(R.id.avg_pd);
        String average_percentage_diff;
        String explanation_text = getExplanationText(score);
        if(avg >= 0){
            average_percentage_diff = String.format("+%.2f%%", avg);
            avg_pd.setText(average_percentage_diff);
            avg_pd.setTextColor(Color.parseColor("#00bd11"));
        }
        else {
            average_percentage_diff = String.format("%.2f%%", avg);
            avg_pd.setText(average_percentage_diff);
            avg_pd.setTextColor(Color.parseColor("#ba160c"));
        }
        explanation.setText(explanation_text);

    }
    @Override
    public void onResume(){
        super.onResume();
        if(!hasPermission()){
            showToast();
            finish();
        }

    }
    private void showToast(){
        Toast.makeText(getApplicationContext(), "Please enable 'Usage Access' permission in settings", Toast.LENGTH_LONG).show();
    }
    private boolean hasPermission(){
        boolean granted = false;
        AppOpsManager appOps = (AppOpsManager) this.getApplicationContext()
                .getSystemService(Context.APP_OPS_SERVICE);
        assert appOps != null;
        int mode = appOps.checkOpNoThrow(AppOpsManager.OPSTR_GET_USAGE_STATS,
                android.os.Process.myUid(), this.getApplicationContext().getPackageName());

        if (mode == AppOpsManager.MODE_DEFAULT) {
            granted = (this.getApplicationContext().checkCallingOrSelfPermission(android.Manifest.permission.PACKAGE_USAGE_STATS) == PackageManager.PERMISSION_GRANTED);
        } else {
            granted = (mode == AppOpsManager.MODE_ALLOWED);
        }
        return granted;
    }
    private String getExplanationText(int score){
        if(score == 1 || score == 2){
            return "The data shows that your app usage is much less than usual indicating that you are very unhappy.";
        }
        else if(score == 3 || score == 4){
            return "The data shows that your app usage is less than usual indicating that you are quite unhappy.";
        }
        else if(score == 5){
            return "The data shows that your app usage is slightly less than usual indicating that you are somewhat unhappy.";
        }
        else if(score == 6){
            return "The data shows that your app usage is slightly more than usual indicating that you are somewhat happy.";
        }
        else if(score == 7 || score == 8){
            return "The data shows that your app usage is more than usual indicating that you are quite happy.";
        }
        else if(score == 9 || score == 10){
            return "The data shows that your app usage is much more than usual indicating that you are very happy.";
        }
        else {
            throw new RuntimeException("INVALID SCORE");
        }

    }

}
