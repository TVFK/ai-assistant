package ru.taf.rag_assistant.services;

import lombok.RequiredArgsConstructor;
import org.springframework.ai.document.Document;
import org.springframework.ai.reader.tika.TikaDocumentReader;
import org.springframework.ai.transformer.splitter.TextSplitter;
import org.springframework.ai.transformer.splitter.TokenTextSplitter;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.ResourcePatternResolver;
import org.springframework.stereotype.Component;
import ru.taf.rag_assistant.repositories.VectorStoreRepository;

import java.util.List;

@Component
@RequiredArgsConstructor
public class DocumentService implements CommandLineRunner {

    private final VectorStore vectorStore;
    private final VectorStoreRepository vectorStoreRepository;
    private final ResourcePatternResolver resourcePatternResolver;

    @Override
    public void run(String... args) {
        System.out.println("=== Starting document loading process ===");

        try {
            Resource[] resources = resourcePatternResolver.getResources("classpath*:/files/*.pdf");
            System.out.println("Found " + resources.length + " PDF files to process");

            for (Resource resource : resources) {
                String fileName = resource.getFilename();
                System.out.println("Processing file: " + fileName);

                if (vectorStoreRepository.containsDocument(fileName)) {
                    System.out.println("Document " + fileName + " already in db");
                    continue;
                }

                try {
                    System.out.println("Reading document: " + fileName);
                    TikaDocumentReader reader = new TikaDocumentReader(resource);
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
            System.out.println("=== Document loading process completed ===");

        } catch (Exception e) {
            System.err.println("✗ Error in document loading process: " + e.getMessage());
            e.printStackTrace();
        }
    }
}