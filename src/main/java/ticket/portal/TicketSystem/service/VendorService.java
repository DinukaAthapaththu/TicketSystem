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
import ticket.portal.TicketSystem.model.Vendor;

import java.util.List;

@Service
public class VendorService {
    private static final Logger logger = LogManager.getLogger(VendorService.class);

    private final TicketManagementService ticketManagementService;
    private final ConfigService configService;


    @Autowired
    public VendorService(TicketManagementService ticketManagementService, ConfigService configService) {
        this.ticketManagementService = ticketManagementService;
        this.configService = configService;

    }

    //
    public ResponseEntity<EnvelopedResponse<List<Vendor>>> getVendorList() {
        try {
            List<Vendor> vendorList = ticketManagementService.getVendors();
            return ResponseEntity.ok(EnvelopedResponse.fromResponse(vendorList));
        } catch (Exception e) {
            logger.error("Error while getting vendor List: {}", e.getMessage());
            List<ErrorResponse> errors = List.of(new ErrorResponse(500, "An error occurred while Getting Vendor List ", e.getMessage()));
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(EnvelopedResponse.fromErrorResponse(errors));
        }
    }

    public ResponseEntity<EnvelopedResponse<String>> addVendor(String vendorId) {
        try {
            TicketConfigDTO config = configService.loadConfiguration();
            if (config == null) {
                logger.error("Configuration not suported");
                List<ErrorResponse> errors = List.of(new ErrorResponse(400, "Configuration not provided", "CONFIG_ERROR"));
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(EnvelopedResponse.fromErrorResponse(errors));
            }
            ticketManagementService.addVendor(config.getTicketReleaseRate(), config.getTotalTickets(), Integer.parseInt(vendorId));
            logger.info("Adding vendor: {}", vendorId);
            return ResponseEntity.ok(EnvelopedResponse.fromResponse("Adding vendor: " + vendorId + " successful"));
        } catch (Exception e) {
            logger.error("Error while adding vendor: {}", e.getMessage());
            List<ErrorResponse> errors = List.of(new ErrorResponse(500, "An error occurred while Adding vendor " + vendorId, e.getMessage()));
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(EnvelopedResponse.fromErrorResponse(errors));
        }
    }


    public ResponseEntity<EnvelopedResponse<String>> removeVendor(String vendorId) {
        try {
            ticketManagementService.removeVendor(vendorId);
            logger.info("Remove vendor: {}", vendorId);
            return ResponseEntity.ok(EnvelopedResponse.fromResponse("Removing vendor: " + vendorId + " successful"));
        } catch (Exception e) {
            logger.error("Error while removing vendor: {}", e.getMessage());
            List<ErrorResponse> errors = List.of(new ErrorResponse(500, "An error occurred while Removing vendor " + vendorId, e.getMessage()));
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(EnvelopedResponse.fromErrorResponse(errors));
        }
    }
}
