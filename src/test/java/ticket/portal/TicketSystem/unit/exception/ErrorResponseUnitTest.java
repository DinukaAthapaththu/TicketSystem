package ticket.portal.TicketSystem.unit.exception;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ticket.portal.TicketSystem.exception.ErrorResponse;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class ErrorResponseUnitTest {
    private ErrorResponse errorResponse;

    @BeforeEach
    void setUp() {
        errorResponse = new ErrorResponse(400, "An error message", "OFFER_NOT_FOUND");
    }

    @Test
    void testErrorResponseProperties() {
        assertEquals(400, errorResponse.getCode());
        assertEquals("An error message", errorResponse.getMessage());
        assertEquals("OFFER_NOT_FOUND", errorResponse.getStatus());
    }

    @Test
    void testErrorResponseInitialization() {
        ErrorResponse response = new ErrorResponse(500, "Internal Server Error", "SERVER_ERROR");
        assertEquals(500, response.getCode());
        assertEquals("Internal Server Error", response.getMessage());
        assertEquals("SERVER_ERROR", response.getStatus());
    }

    @Test
    void testErrorResponseEquality() {
        ErrorResponse anotherResponse = new ErrorResponse(400, "An error message", "OFFER_NOT_FOUND");
        assertEquals(errorResponse.getCode(), anotherResponse.getCode());
    }
}
