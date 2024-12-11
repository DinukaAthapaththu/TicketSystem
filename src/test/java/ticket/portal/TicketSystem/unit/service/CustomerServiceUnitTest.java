package ticket.portal.TicketSystem.unit.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import ticket.portal.TicketSystem.dto.request.EnvelopedResponse;
import ticket.portal.TicketSystem.dto.request.TicketConfigDTO;
import ticket.portal.TicketSystem.model.Customer;
import ticket.portal.TicketSystem.model.TicketPool;
import ticket.portal.TicketSystem.service.ConfigService;
import ticket.portal.TicketSystem.service.CustomerService;
import ticket.portal.TicketSystem.service.TicketManagementService;

import java.io.IOException;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.*;

public class CustomerServiceUnitTest {
    @Mock
    private TicketManagementService ticketManagementService;

    @Mock
    private ConfigService configService;

    @Mock
    private TicketPool ticketPool;

    @InjectMocks
    private CustomerService customerService;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testGetCustomerList_Success() {
        // Arrange
        List<Customer> mockCustomers = List.of(new Customer(ticketPool, 1, 1), new Customer(ticketPool, 2, 2));
        when(ticketManagementService.getCustomers()).thenReturn(mockCustomers);

        // Act
        ResponseEntity<EnvelopedResponse<List<Customer>>> response = customerService.getCustomerList();

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(mockCustomers, response.getBody().getData());
    }

    @Test
    public void testGetCustomerList_Error() {
        // Arrange
        when(ticketManagementService.getCustomers()).thenThrow(new RuntimeException("Database error"));

        // Act
        ResponseEntity<EnvelopedResponse<List<Customer>>> response = customerService.getCustomerList();

        // Assert
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertTrue(response.getBody().getErrors().size() > 0);
    }

    @Test
    public void testAddCustomer_Success() throws IOException {
        // Arrange
        TicketConfigDTO mockConfig = new TicketConfigDTO();
        mockConfig.setCustomerRetrivalRate(5);
        when(configService.loadConfiguration()).thenReturn(mockConfig);
        doNothing().when(ticketManagementService).addCustomer(anyInt(), anyInt());

        // Act
        ResponseEntity<EnvelopedResponse<String>> response = customerService.addCustomer("123");

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("Adding customer: 123 successful", response.getBody().getData());
    }

    @Test
    public void testAddCustomer_ConfigNotFound() throws IOException {
        // Arrange
        when(configService.loadConfiguration()).thenReturn(null);

        // Act
        ResponseEntity<EnvelopedResponse<String>> response = customerService.addCustomer("123");

        // Assert
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertTrue(response.getBody().getErrors().size() > 0);
    }

    @Test
    public void testAddCustomer_Error() throws IOException {
        // Arrange
        TicketConfigDTO mockConfig = new TicketConfigDTO();
        mockConfig.setCustomerRetrivalRate(5);
        when(configService.loadConfiguration()).thenReturn(mockConfig);
        doThrow(new RuntimeException("Add customer failed")).when(ticketManagementService).addCustomer(anyInt(), anyInt());

        // Act
        ResponseEntity<EnvelopedResponse<String>> response = customerService.addCustomer("123");

        // Assert
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertTrue(response.getBody().getErrors().size() > 0);
    }

    @Test
    public void testRemoveCustomer_Success() {
        // Arrange
        doNothing().when(ticketManagementService).removeCustomer(anyString());

        // Act
        ResponseEntity<EnvelopedResponse<String>> response = customerService.removeCustomer("123");

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("Removing customer: 123 successful", response.getBody().getData());
    }

    @Test
    public void testRemoveCustomer_Error() {
        // Arrange
        doThrow(new RuntimeException("Remove customer failed")).when(ticketManagementService).removeCustomer(anyString());

        // Act
        ResponseEntity<EnvelopedResponse<String>> response = customerService.removeCustomer("123");

        // Assert
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertTrue(response.getBody().getErrors().size() > 0);
    }
}
