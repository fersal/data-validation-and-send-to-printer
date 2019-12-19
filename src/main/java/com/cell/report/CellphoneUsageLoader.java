package com.cell.report;

import com.cell.report.model.CellphoneUsage;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.Scanner;
import java.util.function.Supplier;

/*
     Coded by fernando.salazar on 12/18/19
*/

@Slf4j
@Service
public class CellphoneUsageLoader {
    private static final String ZERO = "0";
    private static final int DATA_USAGE_INDEX = 3;
    private static final int MINUTES_USAGE_INDEX = 2;
    private static final int EMPLOYEE_ID_INDEX = 0;
    private static final int USAGE_DATE_INDEX = 1;
    private static final int MONTH_INDEX = 0;
    private static final int DAY_INDEX = 1;
    private static final int YEAR_INDEX = 2;

    private Map<Long, CellphoneUsage> usage;

    Map<Long, CellphoneUsage> loadCellPhoneUsage(final Scanner scanner) {
        usage = new HashMap<>();
        while (scanner.hasNext()) {
            String usageLine = scanner.nextLine();
            if (StringUtils.isEmpty(usageLine) || usageLine.startsWith("emplyeeId")) {
                continue;
            }

            addUsageRecord(usageLine);
        }

        return usage;
    }

    private void addUsageRecord(final String usageLine) {
        String[] data = usageLine.split(",");
        try {
            Long employeeId = parseEmployeeId(data);
            if (usage.containsKey(employeeId)) {
                CellphoneUsage individual = usage.get(employeeId);
                individual.addUsage(parseDate(data),
                        parseMinutes(data),
                        parseData(data));
            } else {
                CellphoneUsage newUsage = new CellphoneUsage(employeeId);
                newUsage.addUsage(parseDate(data),
                        parseMinutes(data),
                        parseData(data));
                usage.put(employeeId, newUsage);
            }
        } catch (IllegalArgumentException | DateTimeParseException e) {
            log.error(String.format("Error while loading Usage data. Illegal argument was: %s", usageLine), e);
        }
    }

    private Double parseData(final String[] data) {
        return Double.parseDouble(Optional.ofNullable(data[DATA_USAGE_INDEX]).orElse(ZERO));
    }

    private Double parseMinutes(final String[] data) {
        return Double.parseDouble(Optional.ofNullable(data[MINUTES_USAGE_INDEX]).orElse(ZERO));
    }

    private Long parseEmployeeId(final String[] data) {
        return Long.parseLong(
                Optional.ofNullable(data[EMPLOYEE_ID_INDEX])
                        .orElseThrow(() -> new IllegalArgumentException("Illegal employeeId in Usage CSV"))
        );
    }

    private LocalDate parseDate(final String[] data) {
        Supplier<IllegalArgumentException> illegalArgumentExceptionSupplier =
                () -> new IllegalArgumentException(
                        "Illegal usage date format. Legal format is 10/23/2019. Illegal argument was: " + data[1]);

        String date = data[USAGE_DATE_INDEX];
        if (!date.matches("\\d+/\\d+/\\d+")) {
            throw illegalArgumentExceptionSupplier.get();
        }

        String[] dateString = date.split("/");
        String month = dateString[MONTH_INDEX].length() == 1 ? "0" + dateString[0] : dateString[0];
        String day = dateString[DAY_INDEX].length() == 1 ? "0" + dateString[1] : dateString[1];
        String year = dateString[YEAR_INDEX];
        try {
            return LocalDate.parse(month + "/" + day + "/" + year, DateTimeFormatter.ofPattern("MM/dd/yyyy"));
        } catch (DateTimeParseException dtpe) {
            throw illegalArgumentExceptionSupplier.get();
        }
    }
}
