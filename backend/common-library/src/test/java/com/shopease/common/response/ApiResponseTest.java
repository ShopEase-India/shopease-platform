package com.shopease.common.response;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ApiResponseTest {

    @Test
    void shouldCreateSuccessfulResponse() {

        ApiResponse<String> response =
                ApiResponse.success("Hello", "Request successful");

        assertTrue(response.getSuccess());
        assertEquals("Request successful", response.getMessage());
        assertEquals("Hello", response.getData());
        assertNotNull(response.getTimestamp());
    }

    @Test
    void shouldCreateFailureResponse() {

        ApiResponse<String> response =
                ApiResponse.failure("Something went wrong");

        assertFalse(response.getSuccess());
        assertEquals("Something went wrong", response.getMessage());
        assertNull(response.getData());
        assertNotNull(response.getTimestamp());
    }
}