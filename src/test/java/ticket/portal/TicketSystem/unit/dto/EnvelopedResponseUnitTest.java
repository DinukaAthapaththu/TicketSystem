package ticket.portal.TicketSystem.unit.dto;

import org.junit.jupiter.api.Test;
import ticket.portal.TicketSystem.dto.request.EnvelopedResponse;
import ticket.portal.TicketSystem.exception.ErrorResponse;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class EnvelopedResponseUnitTest {
    @Test
    void testFromResponse() {
        String testData = "Success";
        EnvelopedResponse<String> response = EnvelopedResponse.fromResponse(testData);

        assertNotNull(response);
        assertEquals(testData, response.getData());
        assertFalse(response.isHasError());
        assertNotNull(response.getErrors());
        assertTrue(response.getErrors().isEmpty());
    }

    @Test
    void testFromErrorResponse() {
        ErrorResponse error = new ErrorResponse(400, "Bad Request", "BAD_REQUEST");
        List<ErrorResponse> errors = List.of(error);

        EnvelopedResponse<String> response = EnvelopedResponse.fromErrorResponse(errors);

        assertNotNull(response);
        assertNull(response.getData());
        assertTrue(response.isHasError());
        assertNotNull(response.getErrors());
        assertEquals(1, response.getErrors().size());
        assertEquals(error, response.getErrors().get(0));
    }

    @Test
    void testConstructorInitialization() {
        ErrorResponse error = new ErrorResponse(404, "Not Found", "NOT_FOUND");
        List<ErrorResponse> errors = List.of(error);

        EnvelopedResponse<String> response = new EnvelopedResponse<>(null, errors, true);

        assertNull(response.getData());
        assertTrue(response.isHasError());
        assertEquals(errors, response.getErrors());
    }

    @Test
    void testEmptyConstructor() {
        EnvelopedResponse<String> response = new EnvelopedResponse<>();

        assertNull(response.getData());
        assertFalse(response.isHasError());
        assertNull(response.getErrors());
    }
}
