package ru.taf.rag_assistant.services;

import lombok.RequiredArgsConstructor;
import org.springframework.ai.document.Document;
import org.springframework.ai.reader.tika.TikaDocumentReader;
import org.springframework.ai.transformer.splitter.TextSplitter;
import org.springframework.ai.transformer.splitter.TokenTextSplitter;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;
import ru.taf.rag_assistant.repositories.VectorStoreRepository;

import java.util.List;

@Component
@RequiredArgsConstructor
public class DocumentService implements CommandLineRunner {

    private final VectorStore vectorStore;
    private final VectorStoreRepository vectorStoreRepository;

    @Value("classpath:/files/*.pdf")
    private Resource[] resources;

    @Override
    public void run(String... args) {
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
            }
        }
    }
}