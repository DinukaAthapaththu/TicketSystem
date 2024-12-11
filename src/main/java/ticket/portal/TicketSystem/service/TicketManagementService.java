package ticket.portal.TicketSystem.service;

import lombok.Getter;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import ticket.portal.TicketSystem.dto.request.EnvelopedResponse;
import ticket.portal.TicketSystem.dto.request.TicketConfigDTO;
import ticket.portal.TicketSystem.exception.ErrorResponse;
import ticket.portal.TicketSystem.model.Customer;
import ticket.portal.TicketSystem.model.TicketPool;
import ticket.portal.TicketSystem.model.Vendor;

import java.util.ArrayList;
import java.util.List;

@Service
public class TicketManagementService {
    private static final Logger logger = LogManager.getLogger(TicketManagementService.class);

    private final ConfigService configService;
    private TicketPool ticketPool;

    @Getter
    private final List<Vendor> vendors = new ArrayList<>();

    @Getter
    private final List<Customer> customers = new ArrayList<>();
    private final List<Thread> vendorThreads = new ArrayList<>();
    private final List<Thread> customerThreads = new ArrayList<>();


    public TicketManagementService(ConfigService configService) {
        this.configService = configService;
    }

    public ResponseEntity<EnvelopedResponse<String>> getQueueSize() {
        try {
            if (this.ticketPool == null) {
                logger.error("System is not Started");
                List<ErrorResponse> errors = List.of(new ErrorResponse(400, "System is not Started", "SYSTEM_ERROR"));
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(EnvelopedResponse.fromErrorResponse(errors));
            }
            int currentTickets = this.ticketPool.getCurrentTickets();
            return ResponseEntity.ok(EnvelopedResponse.fromResponse(String.valueOf(currentTickets)));
        } catch (Exception e){
            logger.error("An error occurred while starting the system", e);
            List<ErrorResponse> errors = List.of(new ErrorResponse(500, "An error occurred while starting the system", e.getMessage()));
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(EnvelopedResponse.fromErrorResponse(errors));
        }
    }

    public ResponseEntity<EnvelopedResponse<String>> startSystem() {
        try {
            TicketConfigDTO config = configService.loadConfiguration();
            if (config == null) {
                logger.error("Configuration not suported");
                List<ErrorResponse> errors = List.of(new ErrorResponse(400, "Configuration not provided", "CONFIG_ERROR"));
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(EnvelopedResponse.fromErrorResponse(errors));
            } else {
                this.ticketPool = new TicketPool(config.getMaxTicketCapacity());
                addVendor(config.getTicketReleaseRate(), config.getTotalTickets(), 1);
                addCustomer(config.getCustomerRetrivalRate(), 1);
                logger.info("Ticket system started successfully");
                return ResponseEntity.ok(EnvelopedResponse.fromResponse("Ticket system started successfully"));
            }

        } catch (Exception e) {
            logger.error("An error occurred while starting the system", e);
            List<ErrorResponse> errors = List.of(new ErrorResponse(500, "An error occurred while starting the system", e.getMessage()));
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(EnvelopedResponse.fromErrorResponse(errors));
        }

    }

    public ResponseEntity<EnvelopedResponse<String>> stopSystem() {
        try {
            for (Thread vendorThread : vendorThreads) {
                if (vendorThread != null && vendorThread.isAlive()) {
                    vendorThread.interrupt();
                }
            }

            for (Thread customerThread : customerThreads) {
                if (customerThread != null && customerThread.isAlive()) {
                    customerThread.interrupt();
                }
            }

            vendors.clear();
            customers.clear();
            vendorThreads.clear();
            customerThreads.clear();
            logger.info("Ticket system stopped successfully");
            return ResponseEntity.ok(EnvelopedResponse.fromResponse("Ticket system stopped successfully"));
        } catch (Exception e) {
            logger.error("An error occurred while stopping the system", e);
            return null;
        }
    }

    public void addVendor(int ticketReleaseRate, int totalTickets, int vendorId) {
        try {
            boolean isVendorExist = false;
            for (Vendor vendor : vendors) {
                if (vendor.getVendorId() == vendorId) {
                    isVendorExist = true;
                    break;
                }
            }

            if (isVendorExist) {
                throw new RuntimeException();
            }
            Vendor vendor = new Vendor(ticketPool, ticketReleaseRate, totalTickets, vendorId);
            vendors.add(vendor);
            Thread vendorThread = new Thread(vendor);
            vendorThreads.add(vendorThread);
            vendorThread.start();
            logger.info("Added vendor thread: {}", vendor.vendorId);
        } catch (Exception e) {
            throw new RuntimeException("Failed to add vendor.", e);
        }
    }

    public void addCustomer(int customerRetrievalRate, int customerId) {
        try {
            boolean isCustomerExist = false;
            for (Customer customer : customers) {
                if (customer.getCustomerId() == customerId) {
                    isCustomerExist = true;
                    break;
                }
            }
            if (isCustomerExist) {
                throw new RuntimeException();
            }
            Customer customer = new Customer(ticketPool, customerRetrievalRate, customerId);
            customers.add(customer);
            Thread customerThread = new Thread(customer);
            customerThreads.add(customerThread);
            customerThread.start();
            logger.info("Added customer thread: {}", customer);
        } catch (Exception e) {
            throw new RuntimeException("Failed to add customer.", e);
        }
    }

    public void removeVendor(String vendorId) {
        try {
            int vendorIdInt = Integer.parseInt(vendorId);
            Vendor vendorToRemove = null;
            for (Vendor v : vendors) {
                if (v.vendorId == vendorIdInt) {
                    vendorToRemove = v;
                    break;
                }
            }
            if (vendorToRemove != null) {
                vendorToRemove.stopVendor();
                vendors.remove(vendorToRemove);
                logger.info("Vendor {} has been removed.", vendorId);
            } else {
                logger.error("vendor {} does not exist.", vendorId);
                throw new RuntimeException("Vendor " + vendorId + " does not exist.");
            }
        } catch (Exception e) {
            logger.error("Error removing vendor: {}", vendorId);
            throw new RuntimeException("Failed to remove vendor.", e);
        }
    }

    public void removeCustomer(String customerId) {
        try {
            int customerIdInt = Integer.parseInt(customerId);
            Customer customerToRemove = null;
            for (Customer c : customers) {
                if (c.customerId == customerIdInt) {
                    customerToRemove = c;
                    break;
                }
            }
            if (customerToRemove != null) {
                customerToRemove.stopCustomer();  // Stop the customer's thread
                customers.remove(customerToRemove);
                logger.info("Customer {} has been removed.", customerId);
            } else {
                logger.error("Customer {} does not exist.", customerId);
                throw new RuntimeException("Customer " + customerId + " does not exist.");
            }
        } catch (Exception e) {
            logger.error("Error removing customer: {}", customerId);
            throw new RuntimeException("Failed to remove customer.", e);
        }
    }
}
