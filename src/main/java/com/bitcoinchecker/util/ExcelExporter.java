package com.bitcoinchecker.util;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import javax.swing.*;
import java.io.File;
import java.io.FileOutputStream;
import com.bitcoinchecker.model.BitcoinAddress;
import java.util.List;

public class ExcelExporter {
    public static void exportToExcel(List<BitcoinAddress> addresses, JFrame parentFrame) {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Save Excel File");
        fileChooser.setSelectedFile(new File("scan_results.xlsx"));

        if (fileChooser.showSaveDialog(parentFrame) == JFileChooser.APPROVE_OPTION) {
            File file = fileChooser.getSelectedFile();
            if (!file.getName().endsWith(".xlsx")) {
                file = new File(file.getAbsolutePath() + ".xlsx");
            }

            try (Workbook workbook = new XSSFWorkbook()) {
                Sheet sheet = workbook.createSheet("Scan Results");

                // Create header row
                Row headerRow = sheet.createRow(0);
                headerRow.createCell(0).setCellValue("Address");
                headerRow.createCell(1).setCellValue("Number of Abuses");
                headerRow.createCell(2).setCellValue("Report URL");

                // Create data rows
                for (int i = 0; i < addresses.size(); i++) {
                    BitcoinAddress address = addresses.get(i);
                    Row row = sheet.createRow(i + 1);
                    row.createCell(0).setCellValue(address.getAddress());
                    row.createCell(1).setCellValue(address.getAbuseCount());
                    row.createCell(2).setCellValue(address.getReportUrl());
                }

                // Auto-size columns
                for (int i = 0; i < 3; i++) {
                    sheet.autoSizeColumn(i);
                }

                // Save workbook
                try (FileOutputStream outputStream = new FileOutputStream(file)) {
                    workbook.write(outputStream);
                }

                JOptionPane.showMessageDialog(parentFrame,
                        "Results exported successfully to:\n" + file.getAbsolutePath(),
                        "Export Complete",
                        JOptionPane.INFORMATION_MESSAGE);

            } catch (Exception e) {
                JOptionPane.showMessageDialog(parentFrame,
                        "Error exporting file: " + e.getMessage(),
                        "Export Error",
                        JOptionPane.ERROR_MESSAGE);
            }
        }
    }
}