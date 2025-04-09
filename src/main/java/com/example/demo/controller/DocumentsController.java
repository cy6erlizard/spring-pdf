package com.example.demo.controller;

import com.example.demo.entity.Document;
import com.example.demo.entity.DocumentType;
import com.example.demo.repository.DocumentRepository;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@CrossOrigin(origins = "*")
@RequestMapping("/pdf/documents")
public class DocumentsController {

    private final DocumentRepository documentRepository;

    public DocumentsController(DocumentRepository documentRepository) {
        this.documentRepository = documentRepository;
    }

    // Example: GET /pdf/documents?type=RESUME
    // Example: GET /pdf/documents?type=RESUME&id=1
    @GetMapping
    public List<Document> getDocuments(
            @RequestParam(required = false) DocumentType type,
            @RequestParam(required = false) Long id
    ) {
        if (type != null && id != null) {
            // Return documents of a specific type and id
            return documentRepository.findByIdAndDocumentType(id, type).stream()
                    .collect(Collectors.toList());
        } else if (type != null) {
            // Return documents of a specific type
            return documentRepository.findByDocumentType(type);
        } else if (id != null) {
            // Return documents by id
            return documentRepository.findById(id).stream()
                    .collect(Collectors.toList());
        } else {
            // Return all documents
            return documentRepository.findAll();
        }
    }
}