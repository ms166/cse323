package com.example.cse323;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AppOpsManager;
import android.app.usage.UsageStats;
import android.app.usage.UsageStatsManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

public class MainActivity extends AppCompatActivity {
    private final int START_WEEK_COMP = 5;
    private final int END_WEEK_COMP = 1;
    private final int START_WEEK_CUR = 1;
    private final int END_WEEK_CUR = 0;

    private Bundle bundle_for_display_activity;

    private Map<Long, String> stats_4_weeks;
    private Map<String, Long> stats_cur_week;
    private String colours[] = {"#FF0000", "#FF0000", "#FFA500", "#FFA500", "#FFD700", "#FFD700", "#a4c639", "#a4c639", "#00FF00", "#00FF00"};

    ListView listview;

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP_MR1)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if(hasPermission()){
            Log.d("permission", "true");
            startApp();
        }
        else {
            Log.d("permission", "false");
            startActivity(new Intent(Settings.ACTION_USAGE_ACCESS_SETTINGS));
        }
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
    private int getScore(double avg){
        if(avg <= -80){
            return 1;
        }
        if(-80 < avg && avg <= -60){
            return 2;
        }
        if(-60 < avg && avg <= -40){
            return 3;
        }
        if(-40 < avg && avg <= -20){
            return 4;
        }
        if(-20 < avg && avg < 0){
            return 5;
        }
        if(0 <= avg && avg <= 20){
            return 6;
        }
        if(20 < avg && avg <= 40){
            return 7;
        }
        if(40 < avg && avg <= 60){
            return 8;
        }
        if(60 < avg && avg <= 80){
            return 9;
        }
        else {
            assert(avg > 80);
            return 10;
        }
    }

    private void startApp(){
        assignStats();
        ArrayList<Row> rows = new ArrayList<Row>();
        for(Map.Entry<Long, String> entry : stats_4_weeks.entrySet()){
            Long this_week_usage_time = 0L;
            if(this.stats_cur_week.containsKey(entry.getValue())){
                this_week_usage_time = stats_cur_week.get(entry.getValue());
            }
            rows.add(new Row(entry.getValue(), entry.getKey(), this_week_usage_time));
            if(rows.size() == 10 || entry.getKey() == 0){
                break;
            }
        }
        double sum = 0;
        for(int i = 0; i < rows.size(); ++i){
            sum += rows.get(i).getPercentage_diff();
        }
        int score = getScore(sum/(double)rows.size());
        this.bundle_for_display_activity = new Bundle();
        bundle_for_display_activity.putSerializable("rows", rows);
        bundle_for_display_activity.putDouble("sum", sum);
        bundle_for_display_activity.putInt("score", score);

        TextView score_text_view = findViewById(R.id.score_text_view);
        score_text_view.setText("" + score + " / " + 10);
        score_text_view.setTextColor(Color.parseColor(this.colours[score - 1]));

    }
    private void assignStats(){
        final Map<String, UsageStats> stats1 = getStats(START_WEEK_COMP, END_WEEK_COMP);
        this.stats_4_weeks = new TreeMap<Long, String>(Collections.<Long>reverseOrder());
        int time_delta_comp = START_WEEK_COMP - END_WEEK_COMP;
        int time_delta_cur = START_WEEK_CUR - END_WEEK_CUR;
//        Log.d("time_delta", "" + time_delta_comp);
//        Log.d("time_delta", "" + time_delta_cur);
        for(Map.Entry<String, UsageStats> entry: stats1.entrySet()){
            // divided by 4 to get average time per week
            Long time = entry.getValue().getTotalTimeInForeground() / time_delta_comp;
            time = fixTime(time);
            this.stats_4_weeks.put(time, entry.getKey());
        }

        final Map<String, UsageStats> stats2 = getStats(START_WEEK_CUR, END_WEEK_CUR);
        this.stats_cur_week = new HashMap<String, Long>();
        for(Map.Entry<String, UsageStats> entry : stats2.entrySet()){
            Long time = entry.getValue().getTotalTimeInForeground() / time_delta_cur;
            time = fixTime(time);
            this.stats_cur_week.put(entry.getKey(), time) ;
        }
    }

    private Map<String, UsageStats> getStats(int start_week, int end_week){
        UsageStatsManager mUsageStatsManager = (UsageStatsManager)getSystemService(Context.USAGE_STATS_SERVICE);

        assert mUsageStatsManager != null;
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");

        Calendar beginCal = Calendar.getInstance();
        beginCal.add(Calendar.DAY_OF_MONTH, -7 * start_week);
//        Log.d("date_start", dateFormat.format(beginCal.getTime()));

        Calendar endCal = Calendar.getInstance();
        endCal.add(Calendar.DAY_OF_MONTH, -7 * end_week);
//        Log.d("date_end", dateFormat.format(endCal.getTime()));
//        Log.d("time_diff","" + (endCal.getTimeInMillis() - beginCal.getTimeInMillis()) / (double)(1000 * 3600));

        Long time_delta = 1000L * 3600 * 10;
        final Map<String, UsageStats> stats = mUsageStatsManager.queryAndAggregateUsageStats(beginCal.getTimeInMillis() + time_delta, endCal.getTimeInMillis());
        return stats;
    }

    public void launchDisplayActivity(View view) {
        Intent intent = new Intent(this, DisplayActivity.class);
        intent.putExtras(this.bundle_for_display_activity);
        startActivity(intent);
    }
    private Long fixTime(Long time){
        time /= (1000 * 60);
        while (time >= (24L * 7 * 60)) {
            time /= 8;
            time *= 7;
        }
        return time;
    }
}
