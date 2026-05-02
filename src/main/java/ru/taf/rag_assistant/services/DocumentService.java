package ru.taf.rag_assistant.services;

import lombok.RequiredArgsConstructor;
import org.springframework.ai.document.Document;
import org.springframework.ai.reader.tika.TikaDocumentReader;
import org.springframework.ai.transformer.splitter.TextSplitter;
import org.springframework.ai.transformer.splitter.TokenTextSplitter;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.ResourcePatternResolver;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;
import ru.taf.rag_assistant.repositories.VectorStoreRepository;

import java.util.List;

@Component
@RequiredArgsConstructor
public class DocumentService {

    private final VectorStore vectorStore;
    private final VectorStoreRepository vectorStoreRepository;

    public void loadFile(MultipartFile file) {
        String fileName = file.getOriginalFilename();

        if (vectorStoreRepository.containsDocument(fileName)) {
            System.out.println("Document " + fileName + " already in db");
            return;
        }

        try {
            System.out.println("Processing file: " + fileName);
            TikaDocumentReader reader = new TikaDocumentReader(new InputStreamResource(file.getInputStream()));
            TextSplitter textSplitter = new TokenTextSplitter(500, 200, 5, 1000, true);
            List<Document> documents = textSplitter.apply(reader.get());

            System.out.println("Created " + documents.size() + " chunks for document: " + fileName);

            documents.forEach(doc ->
                    doc.getMetadata().put("file_name", fileName)
            );

            System.out.println("Starting vector store upload for " + documents.size() + " chunks...");
            vectorStore.accept(documents);
            System.out.println("✓ Document " + fileName + " loaded successfully with " + documents.size() + " chunks");

        } catch (Exception e) {
            System.err.println("✗ Error loading document " + fileName + ": " + e.getMessage());
            e.printStackTrace();
        }
    }
}