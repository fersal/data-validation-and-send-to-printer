package com.cell.report.model;

/*
     Coded by fernando.salazar on 12/18/19
*/

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDate;

@Builder
@Getter
public class Cellphone {
    private long employeeId;
    private String employeeName;
    private LocalDate purchaseDate;
    private String phoneModel;
}
