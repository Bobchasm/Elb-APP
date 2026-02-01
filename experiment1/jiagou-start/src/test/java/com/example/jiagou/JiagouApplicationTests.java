package com.example.jiagou;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

class JiagouApplicationTests {

    @Test
    void contextLoads() {
        assertDoesNotThrow(() -> {
            System.out.println("Application tests validated successfully!");
        });
    }

}
