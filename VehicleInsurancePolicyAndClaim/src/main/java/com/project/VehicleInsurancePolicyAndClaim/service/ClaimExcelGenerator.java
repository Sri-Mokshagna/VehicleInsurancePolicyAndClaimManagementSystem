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

import com.project.VehicleInsurancePolicyAndClaim.model.Claim;

import jakarta.servlet.http.HttpServletResponse;

public class ClaimExcelGenerator {
    public static void generate(HttpServletResponse response, Claim claim) throws IOException {
        XSSFWorkbook workbook = new XSSFWorkbook();
        XSSFSheet sheet = workbook.createSheet("Claim Report");

        CellStyle sectionHeaderStyle = workbook.createCellStyle();
        XSSFFont sectionHeaderFont = workbook.createFont();
        sectionHeaderFont.setBold(true);
        sectionHeaderFont.setFontHeightInPoints((short) 14);
        sectionHeaderStyle.setFont(sectionHeaderFont);
        sectionHeaderStyle.setFillForegroundColor(IndexedColors.LIGHT_CORNFLOWER_BLUE.getIndex());
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

        XSSFRow claimDetailsHeaderRow = sheet.createRow(rowNum++);
        XSSFCell claimDetailsHeaderCell = claimDetailsHeaderRow.createCell(0);
        claimDetailsHeaderCell.setCellValue("Claim Details");
        claimDetailsHeaderCell.setCellStyle(sectionHeaderStyle);

        addRow(sheet, rowNum++, "Claim ID", String.valueOf(claim.getClaimId()), fieldLabelStyle, valueStyle);
        addRow(sheet, rowNum++, "Claim Reason", claim.getClaimReason(), fieldLabelStyle, valueStyle);
        addRow(sheet, rowNum++, "Claim Amount", String.format("%.2f", claim.getClaimAmount()), fieldLabelStyle, valueStyle);
        addRow(sheet, rowNum++, "Claim Status", claim.getClaimStatus(), fieldLabelStyle, valueStyle);
        addRow(sheet, rowNum++, "Claim Date", String.valueOf(claim.getClaimDate()), fieldLabelStyle, valueStyle);
        rowNum++;

        XSSFRow policyInfoHeaderRow = sheet.createRow(rowNum++);
        XSSFCell policyInfoHeaderCell = policyInfoHeaderRow.createCell(0);
        policyInfoHeaderCell.setCellValue("Policy Information");
        policyInfoHeaderCell.setCellStyle(sectionHeaderStyle);
        addRow(sheet, rowNum++, "Policy Number", claim.getPolicy().getPolicyNumber(), fieldLabelStyle, valueStyle);
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

