package com.example.demo.controller;

import com.example.demo.entity.Document;
import com.example.demo.entity.DocumentType;
import com.example.demo.entity.Student;
import com.example.demo.repository.StudentRepository;
import com.example.demo.service.DocumentService;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.nio.file.Files;
import java.util.HashMap;
import java.util.Map;

@RestController
@CrossOrigin(origins = "*")
@RequestMapping("/pdf/report")
public class ReportController {

    private final StudentRepository studentRepository;
    private final DocumentService documentService;

    public ReportController(StudentRepository studentRepository, DocumentService documentService) {
        this.studentRepository = studentRepository;
        this.documentService = documentService;
    }

    // Example: POST /pdf/report?studentId=1
    @PostMapping
    public ResponseEntity<byte[]> generateReport(@RequestParam Long studentId) throws Exception {
        Student student = studentRepository.findById(studentId)
                .orElseThrow(() -> new RuntimeException("Student not found"));

        Map<String, String> replacements = new HashMap<>();
        replacements.put("name", student.getName());
        replacements.put("class", student.getClazz());
        // Add more parameters as required by your report template

        Document document = documentService.generateAndSaveDocument("report.html", DocumentType.REPORT, student, null, replacements);
        byte[] pdfBytes = Files.readAllBytes(java.nio.file.Path.of(document.getFilePath()));

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=report.pdf")
                .contentType(MediaType.APPLICATION_PDF)
                .body(pdfBytes);
    }
}
