package com.project.VehicleInsurancePolicyAndClaim.service;

import java.io.IOException;

import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.pdf.PdfWriter;
import com.project.VehicleInsurancePolicyAndClaim.model.Claim;

import jakarta.servlet.http.HttpServletResponse;

public class ClaimPdfGenerator {
    public static void generate(HttpServletResponse response, Claim claim) throws IOException, DocumentException {
        Document document = new Document();
        PdfWriter.getInstance(document, response.getOutputStream());
        document.open();
 
        document.add(new Paragraph("Claim Report"));
        document.add(new Paragraph("Claim ID: " + claim.getClaimId()));
        document.add(new Paragraph("Claim Reason: " + claim.getClaimReason()));
        document.add(new Paragraph("Claim Amount: " + claim.getClaimAmount()));
        document.add(new Paragraph("Claim Status: " + claim.getClaimStatus()));
        document.add(new Paragraph("Claim Date: " + claim.getClaimDate()));
        document.add(new Paragraph("Policy Number: " + claim.getPolicy().getPolicyNumber()));
 
        document.close();
    }
}