package com.example.demo.repository;

import com.example.demo.entity.Document;
import com.example.demo.entity.DocumentType;
import com.example.demo.entity.Student;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface DocumentRepository extends JpaRepository<Document, Long> {

    // For resume and report: uniqueness by document type and student.
    Optional<Document> findByDocumentTypeAndStudent(DocumentType documentType, Student student);

    // For agreements: uniqueness by document type, student, and company.
    Optional<Document> findByDocumentTypeAndStudentAndCompany(DocumentType documentType, Student student, com.example.demo.entity.Company company);
    Optional<Document> findByIdAndDocumentType(Long id, DocumentType documentType);

    // For the fourth controller: find all documents optionally filtered by type
    List<Document> findByDocumentType(DocumentType documentType);
}
