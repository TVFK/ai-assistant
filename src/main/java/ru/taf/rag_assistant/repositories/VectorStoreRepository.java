package ru.taf.rag_assistant.repositories;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class VectorStoreRepository {
    private final JdbcTemplate jdbcTemplate;

    public boolean containsDocument(String fileName) {
        try {
            String sql = "SELECT COUNT(*) FROM vector_store WHERE metadata ->> 'file_name' = ?";
            Integer count = jdbcTemplate.queryForObject(sql, Integer.class, fileName);
            return count != null && count > 0;
        } catch (Exception e) {
            System.err.println("Error checking document existence: " + e.getMessage());
            return false;
        }
    }
}