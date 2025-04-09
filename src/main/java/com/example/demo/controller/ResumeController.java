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
@RequestMapping("/pdf/resume")
public class ResumeController {

    private final StudentRepository studentRepository;
    private final DocumentService documentService;

    public ResumeController(StudentRepository studentRepository, DocumentService documentService) {
        this.studentRepository = studentRepository;
        this.documentService = documentService;
    }

    // Example: POST /pdf/resume?studentId=1
    @PostMapping
    public ResponseEntity<byte[]> generateResume(@RequestParam Long studentId) throws Exception {
        // Retrieve student info
        Student student = studentRepository.findById(studentId)
                .orElseThrow(() -> new RuntimeException("Student not found"));

        // Prepare replacement data
        Map<String, String> replacements = new HashMap<>();
        replacements.put("name", student.getName());
        replacements.put("class", student.getClazz());
        // Add any additional placeholders as needed

        // Generate and save the resume document
        Document document = documentService.generateAndSaveDocument("resume.html", DocumentType.RESUME, student, null, replacements);

        // Read PDF file as byte array
        byte[] pdfBytes = Files.readAllBytes(java.nio.file.Path.of(document.getFilePath()));

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=resume.pdf")
                .contentType(MediaType.APPLICATION_PDF)
                .body(pdfBytes);
    }
}
