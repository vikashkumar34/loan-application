package com.loanapp;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;
import org.springframework.web.multipart.MultipartResolver;
import org.springframework.web.multipart.support.StandardServletMultipartResolver;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
class LoanDisbursementApplicationTests {

    @Autowired
    private ApplicationContext applicationContext;

    @Autowired
    private MultipartResolver multipartResolver;

    @Test
    void contextLoads() {
        // Verifies that the Spring application context starts without errors
        assertNotNull(applicationContext, "The application context should not be null");
    }

    @Test
    void testMainMethod() {
        // This test ensures the main method runs (useful for coverage)
        // It won't start a second server because @SpringBootTest already did
        LoanDisbursementApplication.main(new String[] {});
    }

    @Test
    void testMultipartResolverBeanExists() {
        // Verifies that your custom bean is loaded into the context
        assertNotNull(multipartResolver, "MultipartResolver bean should be present");
        assertTrue(multipartResolver instanceof StandardServletMultipartResolver,
                "Resolver should be an instance of StandardServletMultipartResolver");
    }
}