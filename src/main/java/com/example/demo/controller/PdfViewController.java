package com.example.demo.controller;

import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@RestController
@CrossOrigin(origins = "*")
@RequestMapping("/pdf")
public class PdfViewController {

    // Endpoint to view or download a PDF from disk
    @GetMapping("/view")
    public ResponseEntity<byte[]> viewPdf(@RequestParam String path) {
        try {
            // Normalize the provided path by replacing backslashes with forward slashes.
            // This helps in converting the Windows-style path into a consistent format.
            String normalizedPath = path.replace("\\", "/");
            Path pdfPath = Paths.get(normalizedPath);

            // Log the absolute path for debugging purposes.
            System.out.println("Attempting to load file from: " + pdfPath.toAbsolutePath());

            // Check if the file exists. If not, log an error and return HTTP 404.
            if (!Files.exists(pdfPath)) {
                System.err.println("File not found: " + pdfPath.toAbsolutePath());
                return ResponseEntity.notFound().build();
            }

            // Read the PDF file into a byte array.
            byte[] pdfBytes = Files.readAllBytes(pdfPath);

            // Prepare HTTP headers with the correct content type and disposition.
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_PDF);
            headers.setContentDispositionFormData("inline", pdfPath.getFileName().toString());

            return ResponseEntity.ok()
                    .headers(headers)
                    .body(pdfBytes);
        } catch (IOException e) {
            e.printStackTrace();
            return ResponseEntity.notFound().build();
        }
    }

}
