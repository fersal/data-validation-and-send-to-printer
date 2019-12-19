package com.cell.report;

import com.cell.report.model.CellphoneUsage;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.MockitoAnnotations;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Map;
import java.util.Scanner;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class CellphoneUsageLoaderTest {

    @InjectMocks
    private CellphoneUsageLoader sut;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    void loadCellPhoneUsageTest() throws FileNotFoundException {
        Map<Long, CellphoneUsage> result =
                sut.loadCellPhoneUsage(new Scanner(new File("src/test/resources/usageTest.csv")));

        assertNotNull(result);
        CellphoneUsage usage = result.get(1910L);
        assertEquals(1.69, usage.getDataDetail().get("2017-OCTOBER"));
        assertEquals(13, usage.getMinutesDetail().get("2017-OCTOBER"));
    }

    @Test
    void usageWithWrongDateIsNotLoaded() throws FileNotFoundException {
        Map<Long, CellphoneUsage> result =
                sut.loadCellPhoneUsage(new Scanner(new File("src/test/resources/usageWithWrongDateFormat.csv")));

        assertNotNull(result);
        assertEquals(2, result.size());
        CellphoneUsage usage = result.get(1910L);
        assertEquals(1.69, usage.getDataDetail().get("2017-OCTOBER"));
        assertEquals(13, usage.getMinutesDetail().get("2017-OCTOBER"));

        CellphoneUsage secondUsage = result.get(2345L);
        assertEquals(45, secondUsage.getMinutesDetail().get("2019-DECEMBER"));
        assertEquals(.5, secondUsage.getDataDetail().get("2019-DECEMBER"));
    }
}