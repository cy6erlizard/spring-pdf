package com.example.demo.controller;

import com.example.demo.entity.Company;
import com.example.demo.entity.Document;
import com.example.demo.entity.DocumentType;
import com.example.demo.entity.Student;
import com.example.demo.repository.CompanyRepository;
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
@RequestMapping("/pdf/agreement")
public class AgreementController {

    private final StudentRepository studentRepository;
    private final CompanyRepository companyRepository;
    private final DocumentService documentService;

    public AgreementController(StudentRepository studentRepository, CompanyRepository companyRepository, DocumentService documentService) {
        this.studentRepository = studentRepository;
        this.companyRepository = companyRepository;
        this.documentService = documentService;
    }

    // Example: POST /pdf/agreement?studentId=1&companyName=ABC&duration=6months
    @PostMapping
    public ResponseEntity<byte[]> generateAgreement(@RequestParam Long studentId,
                                                    @RequestParam String companyName,
                                                    @RequestParam String duration) throws Exception {
        Student student = studentRepository.findById(studentId)
                .orElseThrow(() -> new RuntimeException("Student not found"));

        // Find or create the company. Here we assume company names are unique.
        Company company = companyRepository.findByName(companyName);
        if (company == null) {
            company = new Company(companyName, "Unknown address");
            company = companyRepository.save(company);
        }

        Map<String, String> replacements = new HashMap<>();
        replacements.put("studentName", student.getName());
        replacements.put("class", student.getClazz());
        replacements.put("companyName", company.getName());
        replacements.put("duration", duration);
        // Populate other placeholders if the agreement template needs them

        Document document = documentService.generateAndSaveDocument("agreement.html", DocumentType.AGREEMENT, student, company, replacements);
        byte[] pdfBytes = Files.readAllBytes(java.nio.file.Path.of(document.getFilePath()));

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=agreement.pdf")
                .contentType(MediaType.APPLICATION_PDF)
                .body(pdfBytes);
    }
}
