package ticket.portal.TicketSystem.service;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import ticket.portal.TicketSystem.dto.request.EnvelopedResponse;
import ticket.portal.TicketSystem.dto.request.TicketConfigDTO;
import ticket.portal.TicketSystem.exception.ErrorResponse;
import ticket.portal.TicketSystem.model.Customer;

import java.util.List;

@Service
public class CustomerService {
    private static final Logger logger = LogManager.getLogger(CustomerService.class);

    private final TicketManagementService ticketManagementService;
    private final ConfigService configService;


    @Autowired
    public CustomerService(TicketManagementService ticketManagementService, ConfigService configService) {
        this.ticketManagementService = ticketManagementService;
        this.configService = configService;
    }

    public ResponseEntity<EnvelopedResponse<List<Customer>>> getCustomerList(){
        try{
            List<Customer> customerList = ticketManagementService.getCustomers();
            return ResponseEntity.ok(EnvelopedResponse.fromResponse(customerList));
        }catch (Exception e){
            logger.error("Error while getting customer List: {}", e.getMessage());
            List<ErrorResponse> errors = List.of(new ErrorResponse(500, "An error occurred while Getting Customer List ", e.getMessage()));
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(EnvelopedResponse.fromErrorResponse(errors));
        }
    }

    public ResponseEntity<EnvelopedResponse<String>> addCustomer(String customerId){
        try {
            TicketConfigDTO config = configService.loadConfiguration();
            if (config == null) {
                logger.error("Configuration not provided");
                List<ErrorResponse> errors = List.of(new ErrorResponse(400, "Configuration not provided", "CONFIG_ERROR"));
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(EnvelopedResponse.fromErrorResponse(errors));
            }
            ticketManagementService.addCustomer(config.getCustomerRetrivalRate(), Integer.parseInt(customerId));
            logger.info("Adding customer: {}", customerId);
            return ResponseEntity.ok(EnvelopedResponse.fromResponse("Adding customer: " + customerId + " successful"));
        } catch (Exception e) {
            logger.error("Error while adding customer: {}", e.getMessage());
            List<ErrorResponse> errors = List.of(new ErrorResponse(500, "An error occurred while Adding customer " + customerId, e.getMessage()));
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(EnvelopedResponse.fromErrorResponse(errors));
        }
    }

    public ResponseEntity<EnvelopedResponse<String>> removeCustomer(String customerId) {
        try {
            ticketManagementService.removeCustomer(customerId);
            logger.info("Removing customer: {}", customerId);
            return ResponseEntity.ok(EnvelopedResponse.fromResponse("Removing customer: " + customerId + " successful"));
        } catch (Exception e) {
            logger.error("Error while removing customer: {}", e.getMessage());
            List<ErrorResponse> errors = List.of(new ErrorResponse(500, "An error occurred while Removing customer " + customerId, e.getMessage()));
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(EnvelopedResponse.fromErrorResponse(errors));
        }
    }
}


