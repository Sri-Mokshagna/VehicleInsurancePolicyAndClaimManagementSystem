package com.project.VehicleInsurancePolicyAndClaim.service;
import java.io.IOException;
import java.util.Date;
import com.itextpdf.text.*;
import com.itextpdf.text.pdf.*;
import com.itextpdf.text.pdf.draw.LineSeparator;
import com.project.VehicleInsurancePolicyAndClaim.model.Claim;
import com.project.VehicleInsurancePolicyAndClaim.model.Policy;
import com.project.VehicleInsurancePolicyAndClaim.model.Vehicle;
import com.project.VehicleInsurancePolicyAndClaim.model.Customer;
import jakarta.servlet.http.HttpServletResponse;

public class ClaimPdfGenerator {
    public static void generate(HttpServletResponse response, Claim claim) throws IOException, DocumentException {

        Document document = new Document(PageSize.A4);
        PdfWriter.getInstance(document, response.getOutputStream());
        document.open();

        Font titleFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 20, BaseColor.BLACK);
        Paragraph title = new Paragraph("Vehicle Insurance Claim Report", titleFont);
        title.setAlignment(Element.ALIGN_CENTER);
        title.setSpacingAfter(10f);
        document.add(title);

        LineSeparator separator = new LineSeparator();
        separator.setLineColor(BaseColor.GRAY);
        document.add(new Chunk(separator));

        Font sectionFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 14);
        Paragraph claimHeader = new Paragraph("Claim Details", sectionFont);
        claimHeader.setSpacingBefore(15f);
        claimHeader.setSpacingAfter(10f);
        claimHeader.setAlignment(Element.ALIGN_CENTER);
        document.add(claimHeader);

        Font keyFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD);
        Font valueFont = FontFactory.getFont(FontFactory.HELVETICA);

        PdfPTable claimTable = new PdfPTable(2);
        claimTable.setWidthPercentage(80);
        claimTable.setSpacingBefore(10f);
        claimTable.setWidths(new float[]{3f, 5f});
        claimTable.setHorizontalAlignment(Element.ALIGN_CENTER);

        addTableRow(claimTable, "Claim ID", String.valueOf(claim.getClaimId()), keyFont, valueFont);
        addTableRow(claimTable, "Claim Reason", claim.getClaimReason(), keyFont, valueFont);
        addTableRow(claimTable, "Claim Amount", String.valueOf(claim.getClaimAmount()), keyFont, valueFont);
        addTableRow(claimTable, "Claim Status", claim.getClaimStatus(), keyFont, valueFont);
        addTableRow(claimTable, "Claim Date", String.valueOf(claim.getClaimDate()), keyFont, valueFont);

        document.add(claimTable);

        if (claim.getPolicy() != null) {
            Policy policy = claim.getPolicy();

            document.add(new Paragraph("\n"));
            Paragraph policyHeader = new Paragraph("Associated Policy Details", sectionFont);
            policyHeader.setSpacingAfter(10f);
            policyHeader.setAlignment(Element.ALIGN_CENTER);
            document.add(policyHeader);

            PdfPTable policyTable = new PdfPTable(2);
            policyTable.setWidthPercentage(80);
            policyTable.setHorizontalAlignment(Element.ALIGN_CENTER);
            policyTable.setSpacingBefore(10f);
            policyTable.setWidths(new float[]{3f, 5f});

            addTableRow(policyTable, "Policy Number", policy.getPolicyNumber(), keyFont, valueFont);
            addTableRow(policyTable, "Coverage Type", policy.getCoverageType(), keyFont, valueFont);
            addTableRow(policyTable, "Coverage Amount", String.valueOf(policy.getCoverageAmount()), keyFont, valueFont);
            addTableRow(policyTable, "Premium Amount", String.valueOf(policy.getPremiumAmount()), keyFont, valueFont);
            addTableRow(policyTable, "Status", policy.getPolicyStatus(), keyFont, valueFont);
            addTableRow(policyTable, "Start Date", String.valueOf(policy.getStartDate()), keyFont, valueFont);
            addTableRow(policyTable, "End Date", String.valueOf(policy.getEndDate()), keyFont, valueFont);

            document.add(policyTable);

            if (policy.getVehicle() != null) {
                Vehicle vehicle = policy.getVehicle();

                document.add(new Paragraph("\n"));
                Paragraph vehicleHeader = new Paragraph("Associated Vehicle Details", sectionFont);
                vehicleHeader.setSpacingAfter(10f);
                vehicleHeader.setAlignment(Element.ALIGN_CENTER);
                document.add(vehicleHeader);

                PdfPTable vehicleTable = new PdfPTable(2);
                vehicleTable.setWidthPercentage(80);
                vehicleTable.setHorizontalAlignment(Element.ALIGN_CENTER);
                vehicleTable.setSpacingBefore(10f);
                vehicleTable.setWidths(new float[]{3f, 5f});

                addTableRow(vehicleTable, "Make", vehicle.getMake(), keyFont, valueFont);
                addTableRow(vehicleTable, "Model", vehicle.getModel(), keyFont, valueFont);

                addTableRow(vehicleTable, "Year", String.valueOf(vehicle.getYearOfManufacture() ), keyFont, valueFont);
                addTableRow(vehicleTable, "Registration No.", vehicle.getRegistrationNumber(), keyFont, valueFont);

                document.add(vehicleTable);

                if (vehicle.getCustomer() != null) {
                    Customer customer = vehicle.getCustomer();

                    document.add(new Paragraph("\n"));
                    Paragraph customerHeader = new Paragraph("Policy Holder Details", sectionFont);
                    customerHeader.setSpacingAfter(10f);
                    customerHeader.setAlignment(Element.ALIGN_CENTER);
                    document.add(customerHeader);

                    PdfPTable customerTable = new PdfPTable(2);
                    customerTable.setWidthPercentage(80);
                    customerTable.setHorizontalAlignment(Element.ALIGN_CENTER);
                    customerTable.setSpacingBefore(10f);
                    customerTable.setWidths(new float[]{3f, 5f});

                    addTableRow(customerTable, "Customer Name", customer.getName(), keyFont, valueFont);
                    addTableRow(customerTable, "Customer ID", String.valueOf(customer.getCustomerId()), keyFont, valueFont);
                    addTableRow(customerTable, "Email", customer.getEmail(), keyFont, valueFont);
                    addTableRow(customerTable, "Phone", customer.getPhone(), keyFont, valueFont);

                    document.add(customerTable);
                }
            }
        }

        Paragraph footer = new Paragraph("Generated by Vehicle Insurance System | Contact: support@vehicleinsure.com",
                FontFactory.getFont(FontFactory.HELVETICA_OBLIQUE, 10));
        footer.setAlignment(Element.ALIGN_CENTER);
        footer.setSpacingBefore(30f);
        document.add(footer);

        Paragraph timestamp = new Paragraph("Generated on: " + new Date().toString(),
                FontFactory.getFont(FontFactory.HELVETICA, 9));
        timestamp.setAlignment(Element.ALIGN_CENTER);
        document.add(timestamp);
        document.close();
    }

    private static void addTableRow(PdfPTable table, String key, String value, Font keyFont, Font valueFont) {
        PdfPCell cellKey = new PdfPCell(new Phrase(key, keyFont));
        PdfPCell cellValue = new PdfPCell(new Phrase(value, valueFont));
        float cellPadding = 5f;
        cellKey.setPadding(cellPadding);
        cellValue.setPadding(cellPadding);
        cellKey.setHorizontalAlignment(Element.ALIGN_CENTER);
        cellValue.setHorizontalAlignment(Element.ALIGN_CENTER);
        cellKey.setVerticalAlignment(Element.ALIGN_MIDDLE);
        cellValue.setVerticalAlignment(Element.ALIGN_MIDDLE);
        table.addCell(cellKey);
        table.addCell(cellValue);
    }
}
