package com.project.VehicleInsurancePolicyAndClaim.service;

import java.io.IOException;

import org.apache.poi.sl.usermodel.Sheet;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import com.project.VehicleInsurancePolicyAndClaim.model.Policy;

import jakarta.servlet.http.HttpServletResponse;

public class PolicyExcelGenerator {
    public static void generate(HttpServletResponse response, Policy policy) throws IOException {
        XSSFWorkbook workbook = new XSSFWorkbook();
        XSSFSheet sheet = workbook.createSheet("Policy Report");
 
        XSSFRow header = sheet.createRow(0);
        header.createCell(0).setCellValue("Field");
        header.createCell(1).setCellValue("Value");
 
        String[][] data = {
            {"Policy Number", policy.getPolicyNumber()},
            {"Coverage Type", policy.getCoverageType()},
            {"Coverage Amount", String.valueOf(policy.getCoverageAmount())},
            {"Premium Amount", String.valueOf(policy.getPremiumAmount())},
            {"Status", policy.getPolicyStatus()},
            {"Start Date", String.valueOf(policy.getStartDate())},
            {"End Date", String.valueOf(policy.getEndDate())}
        };
 
        for (int i = 0; i < data.length; i++) {
            XSSFRow row = sheet.createRow(i + 1);
            row.createCell(0).setCellValue(data[i][0]);
            row.createCell(1).setCellValue(data[i][1]);
        }
 
        workbook.write(response.getOutputStream());
        workbook.close();
    }
}