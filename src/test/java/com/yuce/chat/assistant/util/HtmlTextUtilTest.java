package com.yuce.chat.assistant.util;


import com.yuce.chat.assistant.persistence.entity.Drug;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.sql.Date;
import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertTrue;

public class HtmlTextUtilTest {

    private HtmlTextUtil htmlTextUtil;

    @BeforeEach
    void setUp() {
        // Override the default instance by setting up test-specific configuration if needed
        htmlTextUtil = new HtmlTextUtil();
    }

    @Test
    void testFormatDrugResponse_generatesCorrectHtml() {
        // Given
        Drug drug = new Drug(1, "Ibuprofen", "Pain relief", Date.valueOf(LocalDate.of(2025, 12, 31)));

        // When
        String html = htmlTextUtil.formatDrugResponse(drug);

        // Then
        assertTrue(html.contains("Ibuprofen"));
        assertTrue(html.contains("Pain relief"));
        assertTrue(html.contains("2025-12-31"));
    }
}