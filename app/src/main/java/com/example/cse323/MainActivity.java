package com.example.cse323;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.AppOpsManager;
import android.app.usage.UsageStats;
import android.app.usage.UsageStatsManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.SortedMap;
import java.util.TreeMap;

public class MainActivity extends AppCompatActivity {
    private final int START_WEEK_COMP = 5;
    private final int END_WEEK_COMP = 1;
    private final int START_WEEK_CUR = 1;
    private final int END_WEEK_CUR = 0;

    private Map<Long, String> stats_4_weeks;
    private Map<String, Long> stats_cur_week;

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


    private void startApp(){
        assignStats();
        ArrayList<Row> rows = new ArrayList<Row>();
        for(Map.Entry<Long, String> entry : stats_4_weeks.entrySet()){
            Long this_week_usage_time = 0L;
            if(this.stats_cur_week.containsKey(entry.getValue())){
                this_week_usage_time = stats_cur_week.get(entry.getValue());
            }
            rows.add(new Row(entry.getValue(), entry.getKey()/(1000 * 60), this_week_usage_time/(1000 * 60)));
            if(rows.size() == 15){
                break;
            }
        }
        double sum = 0;
        for(int i = 0; i < rows.size(); ++i){
            sum += rows.get(i).getPercentage_diff();
        }

        Bundle bundle = new Bundle();
        bundle.putSerializable("rows", rows);
        bundle.putDouble("sum", sum);
        Intent intent = new Intent(this, DisplayActivity.class);
        intent.putExtras(bundle);
        startActivity(intent);
    }
    private void assignStats(){
        final Map<String, UsageStats> stats1 = getStats(START_WEEK_COMP, END_WEEK_COMP);
        this.stats_4_weeks = new TreeMap<Long, String>(Collections.<Long>reverseOrder());
        int time_delta_comp = START_WEEK_COMP - END_WEEK_COMP;
        int time_delta_cur = START_WEEK_CUR - END_WEEK_CUR;
        for(Map.Entry<String, UsageStats> entry: stats1.entrySet()){
            // divided by 4 to get average time per week
            this.stats_4_weeks.put(entry.getValue().getTotalTimeInForeground() / time_delta_comp, entry.getKey());
        }

        final Map<String, UsageStats> stats2 = getStats(START_WEEK_CUR, END_WEEK_CUR);
        this.stats_cur_week = new HashMap<String, Long>();
        for(Map.Entry<String, UsageStats> entry : stats2.entrySet()){
            this.stats_cur_week.put(entry.getKey(), entry.getValue().getTotalTimeInForeground() / time_delta_cur) ;
        }
    }

    private Map<String, UsageStats> getStats(int start_week, int end_week){
        UsageStatsManager mUsageStatsManager = (UsageStatsManager)getSystemService(Context.USAGE_STATS_SERVICE);

        assert mUsageStatsManager != null;
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");

        Calendar beginCal = Calendar.getInstance();
        // (week -1) to (week -5)
        beginCal.add(Calendar.DAY_OF_MONTH, -7 * start_week);
//        Log.d("date_start", dateFormat.format(beginCal.getTime()));

        Calendar endCal = Calendar.getInstance();
        endCal.add(Calendar.DAY_OF_MONTH, -7 * end_week);
//        Log.d("date_end", dateFormat.format(endCal.getTime()));

        final Map<String, UsageStats> stats = mUsageStatsManager.queryAndAggregateUsageStats(beginCal.getTimeInMillis(), endCal.getTimeInMillis());
        return stats;
    }
}
