package com.example.demo.controller;

import com.example.demo.entity.Document;
import com.example.demo.entity.DocumentType;
import com.example.demo.repository.DocumentRepository;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/pdf/documents")
public class DocumentsController {

    private final DocumentRepository documentRepository;

    public DocumentsController(DocumentRepository documentRepository) {
        this.documentRepository = documentRepository;
    }

    // Example: GET /pdf/documents?type=RESUME
    @GetMapping
    public List<Document> getDocuments(@RequestParam(required = false) DocumentType type) {
        if (type != null) {
            // Return documents of a specific type sorted by createdDate (ascending or descending as needed)
            return documentRepository.findByDocumentType(type);
        } else {
            return documentRepository.findAll(); // Optionally sort by createdDate here
        }
    }
}
