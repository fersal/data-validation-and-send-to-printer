package com.cell.report;

/*
     Coded by fernando.salazar on 12/18/19
*/

import com.cell.report.model.Cellphone;
import com.cell.report.model.CellphoneUsage;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import javax.print.*;
import javax.print.attribute.HashPrintRequestAttributeSet;
import javax.print.attribute.PrintRequestAttributeSet;
import javax.print.attribute.standard.Copies;
import javax.print.event.PrintJobAdapter;
import javax.print.event.PrintJobEvent;
import java.io.*;
import java.text.DecimalFormat;
import java.time.LocalDate;
import java.util.Map;
import java.util.Optional;
import java.util.Scanner;

@Slf4j
@Service
public class ReportWriter {
    private static final String SECTION_DIVIDER = "-------------------------------------------------------------";
    private static final String REPORT_FILE_NAME = "phoneUsageReport.txt";
    private static final DecimalFormat decimalFormat = new DecimalFormat("#.##");

    private Map<Long, Cellphone> cellPhones;
    private Map<Long, CellphoneUsage> usage;

    private CellphoneLoader cellphoneLoader;
    private CellphoneUsageLoader usageLoader;

    public ReportWriter(CellphoneLoader cellphoneLoader, CellphoneUsageLoader usageLoader) {
        this.cellphoneLoader = cellphoneLoader;
        this.usageLoader = usageLoader;
    }

    @PostConstruct
    public void init() {
        log.info("Reading Cellphone user CSV");
        try {
            cellPhones = cellphoneLoader.loadCellphones(new Scanner(new File("CellPhone.csv")));
        } catch (FileNotFoundException fnfe) {
            log.error("Error while reading CellPhone CSV", fnfe);
        }
        log.info(String.format("Loaded %s cellphones", cellPhones.size()));

        log.info("Reading Cellphone usage CSV.");
        try {
            usage = usageLoader.loadCellPhoneUsage(new Scanner(new File("CellPhoneUsageByMonth.csv")));
        } catch (FileNotFoundException fnfe) {
            log.error("Error while reading usage CSV", fnfe);
        }

        log.info("Creating usage report");
        try {
            buildReport();
        } catch (IOException e) {
            log.error("There was a problem writing report.", e);
        }

        log.info("Printing usage report");
        try {
            print();
        } catch (FileNotFoundException fnfe) {
            log.error("Unable to find report file to print. Was file created successfully?");
        } catch (PrintException pe) {
            log.error("Problem printing Usage report. Report was written to disk with name: " + REPORT_FILE_NAME);
        }
    }

    private void buildReport() throws IOException {
        double totalMinutes = usage.values()
                .stream()
                .mapToDouble(CellphoneUsage::getTotalMinutes)
                .sum();
        double totalData = usage.values()
                .stream()
                .mapToDouble(CellphoneUsage::getTotalData)
                .sum();
        double avgMinutes = totalMinutes / usage.values().size();
        double avgData = totalData / usage.values().size();

        FileWriter writer = new FileWriter(REPORT_FILE_NAME);
        PrintWriter printWriter = new PrintWriter(writer);
        printWriter.println("                   CELLPHONE USAGE REPORT");
        printWriter.println(SECTION_DIVIDER);
        printWriter.println();
        printWriter.println("Report Date: " + LocalDate.now().toString());
        printWriter.printf("Number of phones: %s %s", usage.size(), "\n");
        printWriter.println("Total Minutes: " + decimalFormat.format(totalMinutes));
        printWriter.println("Total Data: " + decimalFormat.format(totalData));
        printWriter.println("Average Minutes: " + decimalFormat.format(avgMinutes));
        printWriter.println("Average Data: " + decimalFormat.format(avgData));
        printWriter.println();
        printWriter.println("                   USAGE DETAILS");

        usage.entrySet()
                .stream()
                .sorted(Map.Entry.comparingByKey())
                .forEachOrdered(usageEntry -> {
                    findCellForEmployeeId(usageEntry.getValue().getEmployeeId())
                            .ifPresent(cellphone -> {
                                printWriter.println(SECTION_DIVIDER);
                                printWriter.println("Employee ID: " + cellphone.getEmployeeId());
                                printWriter.println("Employee Name: " + cellphone.getEmployeeName());
                                printWriter.println("Cellphone Model: " + cellphone.getPhoneModel());
                                printWriter.println("Cellphone Purchase date: " + cellphone.getPurchaseDate().toString());
                                printWriter.println();
                                printWriter.println("Minutes Usage");
                                printWriter.println(buildDetailColumns(usageEntry.getValue().getMinutesDetail()));
                                printWriter.println("Data Usage");
                                printWriter.println(buildDetailColumns(usageEntry.getValue().getDataDetail()));
                                printWriter.flush();
                            });
                });
        printWriter.close();
    }

    private String buildDetailColumns(final Map<String, Double> details) {
        StringBuilder stringBuilder = new StringBuilder();
        details.entrySet()
                .stream()
                .sorted(Map.Entry.comparingByKey())
                .forEachOrdered(data ->
                        stringBuilder
                                .append(String.format("%25s", data.getKey().toLowerCase() + ": " + decimalFormat.format(data.getValue())))
                                .append("\n")
                );
        return stringBuilder.toString();
    }

    private Optional<Cellphone> findCellForEmployeeId(final long employeeId) {
        return Optional.ofNullable(cellPhones.get(employeeId));
    }

    private void print() throws FileNotFoundException, PrintException {
        PrintService printService = PrintServiceLookup.lookupDefaultPrintService();
        DocPrintJob printJob = printService.createPrintJob();
        printJob.addPrintJobListener(new PrintJobAdapter() {
            @Override
            public void printDataTransferCompleted(PrintJobEvent pje) {
                super.printDataTransferCompleted(pje);
                log.info("Print job transfer complete");
            }

            @Override
            public void printJobNoMoreEvents(PrintJobEvent pje) {
                super.printJobNoMoreEvents(pje);
                log.info("No more printing events");
            }
        });

        FileInputStream stream = new FileInputStream(REPORT_FILE_NAME);
        Doc doc = new SimpleDoc(stream, DocFlavor.INPUT_STREAM.AUTOSENSE, null);
        PrintRequestAttributeSet attributes = new HashPrintRequestAttributeSet();
        attributes.add(new Copies(1));
        printJob.print(doc, attributes);
    }
}
