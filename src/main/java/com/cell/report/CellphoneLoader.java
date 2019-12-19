package com.cell.report;

import com.cell.report.model.Cellphone;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.Scanner;

/*
     Coded by fernando.salazar on 12/18/19
*/

@Slf4j
@Service
public class CellphoneLoader {
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-d");
    private static final int EMPLOYEE_ID_INDEX = 0;
    private static final int EMPLOYEE_NAME_INDEX = 1;
    private static final int PURCHASE_DATE_INDEX = 2;
    private static final int MODEL_INDEX = 3;
    private static final int REQUIRED_DATE_STRING_LENGTH = 8;
    private static final String NOT_AVAILABLE = "N/A";

    private Map<Long, Cellphone> cellPhones;

    Map<Long, Cellphone> loadCellphones(final Scanner scanner) {
        cellPhones = new HashMap<>();
        while (scanner.hasNext()) {
            String line = scanner.nextLine();
            if (StringUtils.isEmpty(line) || line.startsWith("employeeId")) {
                continue;
            }

            addCellphone(line);
        }

        return cellPhones;
    }

    private void addCellphone(final String line) {
        String[] cellphoneData = line.split(",");
        try {
            Cellphone cell = buildCellphone(cellphoneData);
            cellPhones.put(cell.getEmployeeId(), cell);
        } catch (IllegalArgumentException iae) {
            log.error(String.format("Problem while loading CellPhone CSV. Illegal argument was: %s", line), iae);
        }
    }

    private Cellphone buildCellphone(final String[] cell) {
        return Cellphone.builder()
                .employeeId(Long.parseLong(
                        Optional.ofNullable(cell[EMPLOYEE_ID_INDEX])
                                .orElseThrow(() -> new IllegalArgumentException("Employee ID is empty")))
                )
                .employeeName(Optional.ofNullable(cell[EMPLOYEE_NAME_INDEX]).orElse(NOT_AVAILABLE))
                .purchaseDate(parseDate(cell[PURCHASE_DATE_INDEX]))
                .phoneModel(Optional.ofNullable(cell[MODEL_INDEX]).orElse(NOT_AVAILABLE))
                .build();
    }

    private LocalDate parseDate(final String dateString) {
        if (!dateString.matches("\\d+") || dateString.length() != REQUIRED_DATE_STRING_LENGTH) {
            throw new IllegalArgumentException("Unable to parse Cellphone. Date must be formatted like this yyyymmdd, ie: 20171104");
        }
        String formattedDate = new StringBuilder(dateString)
                .insert(4, "-")
                .insert(7, "-")
                .toString();
        return LocalDate.parse(formattedDate, DATE_FORMATTER);
    }
}
