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
        try {
            // Используем ResourcePatternResolver для поиска ресурсов
            Resource[] resources = resourcePatternResolver.getResources("classpath*:/files/*.pdf");

            if (resources.length == 0) {
                System.out.println("No PDF files found in classpath:/files/");
                return;
            }

            System.out.println("Found " + resources.length + " PDF files to process");

            for (Resource resource : resources) {
                String fileName = resource.getFilename();

                if (vectorStoreRepository.containsDocument(fileName)) {
                    System.out.println("Document " + fileName + " already in db");
                    continue;
                }

                try {
                    TikaDocumentReader reader = new TikaDocumentReader(resource);
                    TextSplitter textSplitter = new TokenTextSplitter(500, 200, 5, 1000, true);
                    List<Document> documents = textSplitter.apply(reader.get());

                    documents.forEach(doc ->
                            doc.getMetadata().put("file_name", fileName)
                    );

                    vectorStore.accept(documents);
                    System.out.println("Document " + fileName + " loaded successfully");
                } catch (Exception e) {
                    System.err.println("Error loading document " + fileName + ": " + e.getMessage());
                    e.printStackTrace();
                }
            }
        } catch (Exception e) {
            System.err.println("Error accessing PDF resources: " + e.getMessage());
            e.printStackTrace();
        }
    }
}