package com.project.VehicleInsurancePolicyAndClaim.service;

import java.io.IOException;

import org.apache.poi.sl.usermodel.Sheet;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import com.project.VehicleInsurancePolicyAndClaim.model.Claim;

import jakarta.servlet.http.HttpServletResponse;

public class ClaimExcelGenerator {
    public static void generate(HttpServletResponse response, Claim claim) throws IOException {
        XSSFWorkbook workbook = new XSSFWorkbook();
        XSSFSheet sheet = workbook.createSheet("Claim Report");
 
        XSSFRow header = sheet.createRow(0);
        header.createCell(0).setCellValue("Field");
        header.createCell(1).setCellValue("Value");
 
        String[][] data = {
            {"Claim ID", String.valueOf(claim.getClaimId())},
            {"Claim Reason", claim.getClaimReason()},
            {"Claim Amount", String.valueOf(claim.getClaimAmount())},
            {"Claim Status", claim.getClaimStatus()},
            {"Claim Date", String.valueOf(claim.getClaimDate())},
            {"Policy Number", claim.getPolicy().getPolicyNumber()}
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