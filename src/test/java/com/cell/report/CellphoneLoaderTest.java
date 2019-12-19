package com.cell.report;

import com.cell.report.model.Cellphone;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.MockitoAnnotations;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Map;
import java.util.Scanner;

import static org.junit.jupiter.api.Assertions.*;

class CellphoneLoaderTest {

    private static final long EXPECTED_EMPLOYEE_ID = 191011L;
    @InjectMocks
    private CellphoneLoader sut;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    void cellphoneIsParsedCorrectlyTest() throws FileNotFoundException {
        Map<Long, Cellphone> result = sut.loadCellphones(new Scanner(new File("src/test/resources/cellphoneTest.csv")));

        assertNotNull(result);
        Cellphone cell = result.get(EXPECTED_EMPLOYEE_ID);
        assertEquals(EXPECTED_EMPLOYEE_ID, cell.getEmployeeId());
        assertEquals("Fernando", cell.getEmployeeName());
        assertEquals("2016-01-01", cell.getPurchaseDate().toString());
        assertEquals("Pixel 4", cell.getPhoneModel());
    }

    @Test
    void cellphoneShouldNotBeLoadedIfEmpIDIsMissingTest() throws FileNotFoundException {
        Map<Long, Cellphone> result = sut.loadCellphones(new Scanner(new File("src/test/resources/cellphoneMissingEmpID.csv")));

        assertNotNull(result);
        assertEquals(1, result.size());
        Cellphone cell = result.get(EXPECTED_EMPLOYEE_ID);
        assertEquals(EXPECTED_EMPLOYEE_ID, cell.getEmployeeId());
        assertEquals("Fernando", cell.getEmployeeName());
        assertEquals("2016-01-01", cell.getPurchaseDate().toString());
        assertEquals("Pixel 4", cell.getPhoneModel());
    }

    @Test
    void invalidDateCellphoneIsNotLoadedTest() throws FileNotFoundException {
        Map<Long, Cellphone> result = sut.loadCellphones(new Scanner(new File("src/test/resources/cellphoneWithWrongDateFormat.csv")));

        assertNotNull(result);
        assertEquals(2, result.size());
        Cellphone cell = result.get(EXPECTED_EMPLOYEE_ID);
        assertEquals(EXPECTED_EMPLOYEE_ID, cell.getEmployeeId());
        assertEquals("Fernando", cell.getEmployeeName());
        assertEquals("2016-01-01", cell.getPurchaseDate().toString());
        assertEquals("Pixel 4", cell.getPhoneModel());

        Cellphone secondCell = result.get(484848L);
        assertEquals("Jon Land", secondCell.getEmployeeName());
        assertEquals(484848L, secondCell.getEmployeeId());
        assertEquals("2016-01-01", secondCell.getPurchaseDate().toString());
        assertEquals("Motorola Razr", secondCell.getPhoneModel());
    }
}