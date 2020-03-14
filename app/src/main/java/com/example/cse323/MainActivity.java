package com.example.cse323;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.app.usage.UsageStats;
import android.app.usage.UsageStatsManager;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.util.Calendar;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.SortedMap;
import java.util.TreeMap;

public class MainActivity extends AppCompatActivity {

    ListView listview;

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP_MR1)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

//        Intent intent = new Intent(Settings.ACTION_USAGE_ACCESS_SETTINGS);
//        startActivity(intent);

        String topPackageName = "NULL";
        UsageStatsManager mUsageStatsManager = (UsageStatsManager)getSystemService(Context.USAGE_STATS_SERVICE);

        assert mUsageStatsManager != null;

        Calendar beginCal = Calendar.getInstance();
        beginCal.set(Calendar.DATE, 1);
        beginCal.set(Calendar.MONTH, 1);
        beginCal.set(Calendar.YEAR, 2019);

        Calendar endCal = Calendar.getInstance();
        endCal.set(Calendar.DATE, 1);
        endCal.set(Calendar.MONTH, 1);
        endCal.set(Calendar.YEAR, 2020);

        final List<UsageStats> stats=mUsageStatsManager.queryUsageStats(UsageStatsManager.INTERVAL_YEARLY, beginCal.getTimeInMillis(), endCal.getTimeInMillis());

        // Sort the stats by the last time used
        SortedMap<Long,UsageStats> mySortedMap = new TreeMap<Long,UsageStats>(Collections.<Long>reverseOrder());
        if(stats != null) {
            for (UsageStats usageStats : stats) {
                mySortedMap.put(usageStats.getTotalTimeInForeground(), usageStats);
            }
            if(!mySortedMap.isEmpty()) {
//                topPackageName =  Objects.requireNonNull(mySortedMap.get(mySortedMap.lastKey())).getPackageName();

                String[] nameArray = new String[stats.size()];

                String[] infoArray = new String[stats.size()];

                int count = 0;
                for(Map.Entry<Long,UsageStats> entry : mySortedMap.entrySet()) {
                    Long key = entry.getKey();
                    UsageStats us = entry.getValue();
                    Long timeInHours = key/(1000 * 3600);
                    Log.d(us.getPackageName(), "value: " + timeInHours);

                    nameArray[count] = us.getPackageName();
                    infoArray[count] = Long.toString(timeInHours);

                    count++;
                    if(count == 20){
                        break;
                    }
                }

                CustomListAdapter whatever = new CustomListAdapter(this, nameArray, infoArray);
                listview = (ListView) findViewById(R.id.listviewID);
                listview.setAdapter(whatever);
            }
            else {
                Log.d("message3", "stats is empty");
            }
        }
        else {
            Log.d("message3", "stats is null");
        }
    }
}
