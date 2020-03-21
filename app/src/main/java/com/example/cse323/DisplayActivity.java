package com.example.cse323;

import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;

public class DisplayActivity extends AppCompatActivity {
    ListView listview;
    TextView explanation, avg_pd;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display);
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
        for(Row row: rows){
            Log.d("_ENTRY_", "" + row);
        }
        Log.d("_ENTRY_", "" + sum);
        CustomListAdapter whatever = new CustomListAdapter(this, rows);
        listview = (ListView) findViewById(R.id.listviewID);
        listview.setAdapter(whatever);

        double avg = sum/(double)15;
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
    private String getExplanationText(int score){
        if(score == 1 || score == 2){
            return "The data shows that your app usage is quite a bit less than usual indicating that you are very unhappy.";
        }
        else if(score == 3 || score == 4){
            return "The data shows that your app usage is less than usual indicating that you are moderately unhappy.";
        }
        else if(score == 5){
            return "The data shows that your app usage is slightly less than usual indicating that you are somewhat unhappy.";
        }
        else if(score == 6){
            return "The data shows that your app usage is slightly more than usual indicating that you are somewhat happy.";
        }
        else if(score == 7 || score == 8){
            return "The data shows that your app usage is more than usual indicating that you are moderately happy.";
        }
        else if(score == 9 || score == 10){
            return "The data shows that your app usage is quite a bit more than usual indicating that you are very happy.";
        }
        else {
            throw new RuntimeException("INVALID SCORE");
        }

    }

}
