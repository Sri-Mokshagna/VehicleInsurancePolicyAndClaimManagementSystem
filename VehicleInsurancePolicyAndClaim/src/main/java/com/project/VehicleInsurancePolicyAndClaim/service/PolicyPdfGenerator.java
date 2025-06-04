package com.project.VehicleInsurancePolicyAndClaim.service;

import java.io.IOException;

import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.pdf.PdfWriter;
import com.project.VehicleInsurancePolicyAndClaim.model.Policy;

import jakarta.servlet.http.HttpServletResponse;

public class PolicyPdfGenerator {
    public static void generate(HttpServletResponse response, Policy policy) throws IOException, DocumentException {
        Document document = new Document();
        PdfWriter.getInstance(document, response.getOutputStream());
        document.open();
        
        document.add(new Paragraph("Policy Report"));
        document.add(new Paragraph("Policy Number: " + policy.getPolicyNumber()));
        document.add(new Paragraph("Coverage Type: " + policy.getCoverageType()));
        document.add(new Paragraph("Coverage Amount: " + policy.getCoverageAmount()));
        document.add(new Paragraph("Premium Amount: " + policy.getPremiumAmount()));
        document.add(new Paragraph("Status: " + policy.getPolicyStatus()));
        document.add(new Paragraph("Start Date: " + policy.getStartDate()));
        document.add(new Paragraph("End Date: " + policy.getEndDate()));
 
        document.close();
    }
}
 