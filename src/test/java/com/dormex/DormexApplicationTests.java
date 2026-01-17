package com.dormex;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

/**
 * Basic application context test.
 * Verifies that the Spring application context loads correctly.
 */
@SpringBootTest
@ActiveProfiles("test")
class DormexApplicationTests {

    @Test
    void contextLoads() {
        // Verify that the application context loads without errors
    }

}
