package com.example.demo.service;

import com.example.demo.entity.Company;
import com.example.demo.entity.Document;
import com.example.demo.entity.DocumentType;
import com.example.demo.entity.Student;
import com.example.demo.repository.DocumentRepository;
import org.springframework.stereotype.Service;

import java.io.File;
import java.time.LocalDateTime;
import java.util.Optional;

@Service
public class DocumentService {

    private final DocumentRepository documentRepository;
    private final PDFGenerationService pdfGenerationService;

    public DocumentService(DocumentRepository documentRepository, PDFGenerationService pdfGenerationService) {
        this.documentRepository = documentRepository;
        this.pdfGenerationService = pdfGenerationService;
    }

    /**
     * Generates and saves a document record for a given type.
     *
     * @param templateName the template file name (e.g., resume.html)
     * @param documentType type of document (RESUME, REPORT, or AGREEMENT)
     * @param student the student data
     * @param company the company data (can be null for RESUME and REPORT)
     * @param replacements the key-value replacements for the template
     * @return the generated Document entity
     * @throws Exception if PDF generation fails
     */
    public Document generateAndSaveDocument(String templateName, DocumentType documentType,
                                            Student student, Company company,
                                            java.util.Map<String, String> replacements) throws Exception {
        // Check if a document already exists for the given criteria
        Optional<Document> existingDocOpt;
        if (documentType == DocumentType.AGREEMENT) {
            existingDocOpt = documentRepository.findByDocumentTypeAndStudentAndCompany(documentType, student, company);
        } else {
            existingDocOpt = documentRepository.findByDocumentTypeAndStudent(documentType, student);
        }

        existingDocOpt.ifPresent(existingDoc -> {
            // Delete the existing file if it exists
            File oldFile = new File(existingDoc.getFilePath());
            if (oldFile.exists()) {
                oldFile.delete();
            }
            // Remove the record
            documentRepository.delete(existingDoc);
        });

        // Generate new PDF file
        File pdfFile = pdfGenerationService.generatePdf(templateName, replacements);

        // Create and save new document record
        Document newDocument = new Document();
        newDocument.setDocumentType(documentType);
        newDocument.setStudent(student);
        newDocument.setCompany(company);
        newDocument.setFilePath(pdfFile.getAbsolutePath());
        newDocument.setCreatedDate(LocalDateTime.now());

        return documentRepository.save(newDocument);
    }
}
