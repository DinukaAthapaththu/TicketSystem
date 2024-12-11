package ticket.portal.TicketSystem.unit.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.ResponseEntity;
import ticket.portal.TicketSystem.dto.request.EnvelopedResponse;
import ticket.portal.TicketSystem.dto.request.TicketConfigDTO;
import ticket.portal.TicketSystem.exception.ErrorResponse;
import ticket.portal.TicketSystem.model.TicketPool;
import ticket.portal.TicketSystem.service.ConfigService;
import ticket.portal.TicketSystem.service.TicketManagementService;

import java.io.IOException;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

public class TicketManagementUnitTest {
    private TicketManagementService ticketManagementService;

    @Mock
    private ConfigService configService;

    @Mock
    private TicketPool ticketPool;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        ticketManagementService = new TicketManagementService(configService);
    }

    @Test
    void testStartSystemSuccess() throws IOException {
        TicketConfigDTO config = new TicketConfigDTO();
        config.setMaxTicketCapacity(100);
        config.setTicketReleaseRate(5);
        config.setTotalTickets(50);
        config.setCustomerRetrivalRate(2);

        when(configService.loadConfiguration()).thenReturn(config);

        ResponseEntity<EnvelopedResponse<String>> response = ticketManagementService.startSystem();

        assertEquals(200, response.getStatusCodeValue());
        assertEquals("Ticket system started successfully", response.getBody().getData());
        assertNotNull(ticketManagementService.getVendors());
        assertNotNull(ticketManagementService.getCustomers());
    }

    @Test
    void testStartSystemWithNullConfig() throws IOException {
        when(configService.loadConfiguration()).thenReturn(null);

        ResponseEntity<EnvelopedResponse<String>> response = ticketManagementService.startSystem();

        assertEquals(400, response.getStatusCodeValue());
        List<ErrorResponse> errors = response.getBody().getErrors();
        assertEquals("CONFIG_ERROR", errors.get(0).getStatus());
    }

    @Test
    void testStopSystem() {
        TicketPool ticketPool = new TicketPool(100);
        ticketManagementService.startSystem();
        ticketManagementService.stopSystem();

        assertTrue(ticketManagementService.getVendors().isEmpty());
        assertTrue(ticketManagementService.getCustomers().isEmpty());
    }

    @Test
    void testGetQueueSizeWhenSystemNotStarted() {
        ResponseEntity<EnvelopedResponse<String>> response = ticketManagementService.getQueueSize();

        assertEquals(400, response.getStatusCodeValue());
        List<ErrorResponse> errors = response.getBody().getErrors();
        assertEquals("SYSTEM_ERROR", errors.get(0).getStatus());
    }

    @Test
    void testAddVendorSuccessfully() {
        ticketManagementService.startSystem();
        assertDoesNotThrow(() -> ticketManagementService.addVendor(5, 50, 2));
    }

    @Test
    void testAddVendorWithDuplicateId() {
        ticketManagementService.startSystem();
        ticketManagementService.addVendor(5, 50, 1);
        assertThrows(RuntimeException.class, () -> ticketManagementService.addVendor(5, 50, 1));
    }

    @Test
    void testAddCustomerSuccessfully() {
        ticketManagementService.startSystem();
        assertDoesNotThrow(() -> ticketManagementService.addCustomer(2, 3));
    }

    @Test
    void testAddCustomerWithDuplicateId() {
        ticketManagementService.startSystem();
        ticketManagementService.addCustomer(2, 1);
        assertThrows(RuntimeException.class, () -> ticketManagementService.addCustomer(2, 1));
    }

    @Test
    void testRemoveVendorSuccessfully() {
        ticketManagementService.startSystem();
        ticketManagementService.addVendor(5, 50, 1);
        assertDoesNotThrow(() -> ticketManagementService.removeVendor("1"));
    }

    @Test
    void testRemoveVendorThatDoesNotExist() {
        ticketManagementService.startSystem();
        assertThrows(RuntimeException.class, () -> ticketManagementService.removeVendor("99"));
    }

    @Test
    void testRemoveCustomerSuccessfully() {
        ticketManagementService.startSystem();
        ticketManagementService.addCustomer(2, 1);
        assertDoesNotThrow(() -> ticketManagementService.removeCustomer("1"));
    }

    @Test
    void testRemoveCustomerThatDoesNotExist() {
        ticketManagementService.startSystem();
        assertThrows(RuntimeException.class, () -> ticketManagementService.removeCustomer("99"));
    }
}
