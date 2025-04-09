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
            // Create a Path object from the provided string path.
            Path pdfPath = Paths.get(path);

            // Read PDF file into byte array.
            byte[] pdfBytes = Files.readAllBytes(pdfPath);

            // Set headers so that the browser treats the response as a PDF.
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_PDF);
            // Optionally, to show the PDF in the browser:
            headers.setContentDispositionFormData("inline", pdfPath.getFileName().toString());
            // If you prefer to force download replace 'inline' with 'attachment'
            // headers.setContentDispositionFormData("attachment", pdfPath.getFileName().toString());

            return ResponseEntity
                    .ok()
                    .headers(headers)
                    .body(pdfBytes);
        } catch (IOException e) {
            // Log the exception and return a 404 (Not Found) error response.
            e.printStackTrace();
            return ResponseEntity.notFound().build();
        }
    }
}
