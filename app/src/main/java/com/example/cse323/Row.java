package com.example.cse323;

public class Row {
    private String name;
    private Long time_4_weeks;
    private Long time_last_week;
    private Double percentage_diff;
    public Row(String name, Long time_4_weeks, Long time_last_week){
        this.name = name;
        this.time_4_weeks = time_4_weeks;
        this.time_last_week = time_last_week;
        Long time_diff = this.time_last_week - this.time_4_weeks;
        this.percentage_diff = 100 * time_diff / (double) this.time_4_weeks;
    }
    public String getName(){
        return this.name;
    }
    public Long getTime_4_weeks(){
        return this.time_4_weeks;
    }
    public Long getTime_last_week(){
        return this.time_last_week;
    }
    public Double getPercentage_diff(){
        return this.percentage_diff;
    }
    public String toString(){
        return String.format("name: %s, time_4_weeks: %d, time_last_week %d, percentage_diff: %.2f\n", this.name, this.time_4_weeks, this.time_last_week, this.percentage_diff);
    }

}

