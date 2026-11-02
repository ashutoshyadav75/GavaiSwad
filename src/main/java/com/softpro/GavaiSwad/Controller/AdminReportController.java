
package com.softpro.GavaiSwad.Controller;

import com.softpro.GavaiSwad.Service.AdminReportService;
import com.softpro.GavaiSwad.Service.PdfReportService;

import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.io.IOException;

@Controller
@RequestMapping("/admin/reports")
public class AdminReportController {

    private final AdminReportService reportService;
    private final PdfReportService pdfReportService;

    public AdminReportController(
            AdminReportService reportService,
            PdfReportService pdfReportService) {

        this.reportService = reportService;
        this.pdfReportService = pdfReportService;
    }

    @GetMapping
    public String showReports(Model model) {

        model.addAttribute("totalOrders",
                reportService.getTotalOrders());

        model.addAttribute("pendingOrders",
                reportService.getPendingOrders());

        model.addAttribute("deliveredOrders",
                reportService.getDeliveredOrders());

        model.addAttribute("totalRevenue",
                reportService.getTotalRevenue());

        model.addAttribute("monthlySales",
                reportService.getMonthlySales());

        return "admin/reports";
    }

    // Download PDF Report
    @GetMapping("/download-pdf")
    public ResponseEntity<byte[]> downloadPdfReport()
            throws IOException {

        byte[] pdf = pdfReportService.generateReport(
                reportService.getTotalOrders(),
                reportService.getPendingOrders(),
                reportService.getDeliveredOrders(),
                reportService.getTotalRevenue(),
                reportService.getMonthlySales()
        );

        return ResponseEntity.ok()
                .header(
                        HttpHeaders.CONTENT_DISPOSITION,
                        "attachment; filename=GavaiSwad-Report.pdf"
                )
                .contentType(MediaType.APPLICATION_PDF)
                .contentLength(pdf.length)
                .body(pdf);
    }
}
