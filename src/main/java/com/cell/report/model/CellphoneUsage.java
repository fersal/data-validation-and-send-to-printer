package com.cell.report.model;

import lombok.Getter;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

/*
     Coded by fernando.salazar on 12/18/19
*/

@Getter
public class CellphoneUsage {
    private long employeeId;
    private double totalMinutes;
    private double totalData;
    private Map<String, Double> minutesDetail;
    private Map<String, Double> dataDetail;

    public CellphoneUsage(final long employeeId) {
        this.employeeId = employeeId;
        this.minutesDetail = new HashMap<>();
        this.dataDetail = new HashMap<>();
    }

    public void addUsage(final LocalDate date, final double minutes, final double data) {
        String key = date.getYear() + "-" + date.getMonth();
        totalMinutes = totalMinutes + minutes;
        totalData = totalData + data;
        if (minutesDetail.containsKey(key)) {
            minutesDetail.computeIfPresent(key, (existingKey, existingVal) -> existingVal + minutes);
        } else {
            minutesDetail.put(key, minutes);
        }

        if (dataDetail.containsKey(key)) {
            dataDetail.computeIfPresent(key, (existingKey, existingVal) -> existingVal + data);
        } else {
            dataDetail.put(key, data);
        }
    }
}
