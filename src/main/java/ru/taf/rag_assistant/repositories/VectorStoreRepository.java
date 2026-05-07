package ru.taf.rag_assistant.repositories;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;

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

    public List<String> findDistinctFileNames() {
        String sql = "SELECT DISTINCT metadata ->> 'file_name' FROM vector_store";
        return jdbcTemplate.queryForList(sql, String.class);
    }

    public void deleteByFileName(String fileName) {
        String sql = "DELETE FROM vector_store WHERE metadata ->> 'file_name' = ?";
        jdbcTemplate.update(sql, fileName);
    }
}