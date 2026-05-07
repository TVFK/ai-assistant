package ru.taf.rag_assistant.controllers;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import ru.taf.rag_assistant.services.DocumentService;

import java.util.List;

@RestController
@RequestMapping("/api/documents")
@RequiredArgsConstructor
public class DocumentsController {

    private final DocumentService documentService;

    @PostMapping("/upload")
    public ResponseEntity<?> upload(@RequestParam("file") MultipartFile file) {
        documentService.loadFile(file);
        return ResponseEntity.ok("uploaded");
    }

    @GetMapping
    public ResponseEntity<List<String>> listDocuments() {
        return ResponseEntity.ok(documentService.getAllDocumentNames());
    }

    @DeleteMapping("/{fileName}")
    public ResponseEntity<?> deleteDocument(@PathVariable String fileName) {
        documentService.deleteDocument(fileName);
        return ResponseEntity.ok("Document " + fileName + " deleted");
    }
}
