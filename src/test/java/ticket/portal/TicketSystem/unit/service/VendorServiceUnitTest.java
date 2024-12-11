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
import ticket.portal.TicketSystem.model.Vendor;
import ticket.portal.TicketSystem.service.ConfigService;
import ticket.portal.TicketSystem.service.CustomerService;
import ticket.portal.TicketSystem.service.TicketManagementService;
import ticket.portal.TicketSystem.service.VendorService;

import java.io.IOException;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

public class VendorServiceUnitTest {
    @Mock
    private TicketManagementService ticketManagementService;

    @Mock
    private ConfigService configService;

    @Mock
    private TicketPool ticketPool;

    @InjectMocks
    private VendorService vendorService;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testGetVendorList_Success() {
        // Arrange
        List<Vendor> mockVendors = List.of(new Vendor(ticketPool, 1, 10, 4), new Vendor(ticketPool, 2, 10, 2));
        when(ticketManagementService.getVendors()).thenReturn(mockVendors);

        // Act
        ResponseEntity<EnvelopedResponse<List<Vendor>>> response = vendorService.getVendorList();

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(mockVendors, response.getBody().getData());
    }

    @Test
    public void testGetVendorList_Error() {
        // Arrange
        when(ticketManagementService.getVendors()).thenThrow(new RuntimeException("Database error"));

        // Act
        ResponseEntity<EnvelopedResponse<List<Vendor>>> response = vendorService.getVendorList();

        // Assert
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertTrue(response.getBody().getErrors().size() > 0);
    }

    @Test
    public void testAddVendor_Success() throws IOException {
        // Arrange
        TicketConfigDTO mockConfig = new TicketConfigDTO();
        mockConfig.setTotalTickets(10);
        mockConfig.setTicketReleaseRate(1);
        when(configService.loadConfiguration()).thenReturn(mockConfig);
        doNothing().when(ticketManagementService).addVendor(anyInt(), anyInt(), anyInt());

        // Act
        ResponseEntity<EnvelopedResponse<String>> response = vendorService.addVendor("123");

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("Adding vendor: 123 successful", response.getBody().getData());
    }

    @Test
    public void testAddVendor_ConfigNotFound() throws IOException {
        // Arrange
        when(configService.loadConfiguration()).thenReturn(null);

        // Act
        ResponseEntity<EnvelopedResponse<String>> response = vendorService.addVendor("123");

        // Assert
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertTrue(response.getBody().getErrors().size() > 0);
    }

    @Test
    public void testAddVendor_Error() throws IOException {
        // Arrange
        TicketConfigDTO mockConfig = new TicketConfigDTO();
        mockConfig.setTotalTickets(10);
        mockConfig.setTicketReleaseRate(1);
        when(configService.loadConfiguration()).thenReturn(mockConfig);
        doThrow(new RuntimeException("Add customer failed")).when(ticketManagementService).addVendor(anyInt(), anyInt(), anyInt());

        // Act
        ResponseEntity<EnvelopedResponse<String>> response = vendorService.addVendor("123");

        // Assert
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertTrue(response.getBody().getErrors().size() > 0);
    }

    @Test
    public void testRemoveVendor_Success() {
        // Arrange
        doNothing().when(ticketManagementService).removeVendor(anyString());

        // Act
        ResponseEntity<EnvelopedResponse<String>> response = vendorService.removeVendor("123");

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("Removing vendor: 123 successful", response.getBody().getData());
    }

    @Test
    public void testRemoveVendor_Error() {
        // Arrange
        doThrow(new RuntimeException("Remove customer failed")).when(ticketManagementService).removeVendor(anyString());

        // Act
        ResponseEntity<EnvelopedResponse<String>> response = vendorService.removeVendor("123");

        // Assert
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertTrue(response.getBody().getErrors().size() > 0);
    }
}
