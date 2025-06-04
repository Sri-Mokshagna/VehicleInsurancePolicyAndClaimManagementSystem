package com.project.VehicleInsurancePolicyAndClaim.service;

import java.io.IOException;

import org.apache.poi.ss.usermodel.BorderStyle;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.FillPatternType;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.apache.poi.xssf.usermodel.XSSFFont;

import com.project.VehicleInsurancePolicyAndClaim.model.Policy;

import jakarta.servlet.http.HttpServletResponse;

public class PolicyExcelGenerator {
    public static void generate(HttpServletResponse response, Policy policy) throws IOException {
        XSSFWorkbook workbook = new XSSFWorkbook();
        XSSFSheet sheet = workbook.createSheet("Policy Report");

        CellStyle sectionHeaderStyle = workbook.createCellStyle();
        XSSFFont sectionHeaderFont = workbook.createFont();
        sectionHeaderFont.setBold(true);
        sectionHeaderFont.setFontHeightInPoints((short) 14);
        sectionHeaderStyle.setFont(sectionHeaderFont);
        sectionHeaderStyle.setFillForegroundColor(IndexedColors.LIGHT_GREEN.getIndex());
        sectionHeaderStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        sectionHeaderStyle.setBorderBottom(BorderStyle.MEDIUM);

        CellStyle fieldLabelStyle = workbook.createCellStyle();
        XSSFFont fieldLabelFont = workbook.createFont();
        fieldLabelFont.setBold(true);
        fieldLabelFont.setFontHeightInPoints((short) 10);
        fieldLabelStyle.setFont(fieldLabelFont);
        fieldLabelStyle.setBorderRight(BorderStyle.THIN);
        fieldLabelStyle.setBorderBottom(BorderStyle.THIN);

        CellStyle valueStyle = workbook.createCellStyle();
        valueStyle.setBorderBottom(BorderStyle.THIN);
        valueStyle.setBorderLeft(BorderStyle.THIN);
        valueStyle.setBorderRight(BorderStyle.THIN);

        int rowNum = 0;
        XSSFRow policyDetailsHeaderRow = sheet.createRow(rowNum++);
        XSSFCell policyDetailsHeaderCell = policyDetailsHeaderRow.createCell(0);
        policyDetailsHeaderCell.setCellValue("Policy Details");
        policyDetailsHeaderCell.setCellStyle(sectionHeaderStyle);

        addRow(sheet, rowNum++, "Policy Number", policy.getPolicyNumber(), fieldLabelStyle, valueStyle);
        addRow(sheet, rowNum++, "Coverage Type", policy.getCoverageType(), fieldLabelStyle, valueStyle);
        addRow(sheet, rowNum++, "Coverage Amount", String.format("%.2f", policy.getCoverageAmount()), fieldLabelStyle, valueStyle);
        addRow(sheet, rowNum++, "Premium Amount", String.format("%.2f", policy.getPremiumAmount()), fieldLabelStyle, valueStyle);
        addRow(sheet, rowNum++, "Status", policy.getPolicyStatus(), fieldLabelStyle, valueStyle);
        addRow(sheet, rowNum++, "Start Date", String.valueOf(policy.getStartDate()), fieldLabelStyle, valueStyle);
        addRow(sheet, rowNum++, "End Date", String.valueOf(policy.getEndDate()), fieldLabelStyle, valueStyle);
        rowNum++;

        sheet.autoSizeColumn(0);
        sheet.autoSizeColumn(1);

        workbook.write(response.getOutputStream());
        workbook.close();
    }

    private static void addRow(XSSFSheet sheet, int rowNum, String fieldName, String fieldValue, CellStyle fieldLabelStyle, CellStyle valueStyle) {
        XSSFRow row = sheet.createRow(rowNum);

        XSSFCell fieldCell = row.createCell(0);
        fieldCell.setCellValue(fieldName);
        fieldCell.setCellStyle(fieldLabelStyle);

        XSSFCell valueCell = row.createCell(1);
        valueCell.setCellValue(fieldValue);
        valueCell.setCellStyle(valueStyle);
    }
}