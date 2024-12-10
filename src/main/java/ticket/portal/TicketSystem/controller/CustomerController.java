package ticket.portal.TicketSystem.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ticket.portal.TicketSystem.dto.request.EnvelopedResponse;
import ticket.portal.TicketSystem.model.Customer;
import ticket.portal.TicketSystem.service.CustomerService;

import java.util.List;

@RestController
@RequestMapping("/api/v1/customer")
public class CustomerController {
    private final CustomerService customerService;

    @Autowired
    private CustomerController(CustomerService customerService) {
        this.customerService = customerService;
    }

    @GetMapping("/list")
    public ResponseEntity<EnvelopedResponse<List<Customer>>> getCustomerList() {
        return customerService.getCustomerList();
    }

    @PostMapping("/add")
    public ResponseEntity<EnvelopedResponse<String>> addCustomer(@RequestParam String customerId) {
        return customerService.addCustomer(customerId);
    }

    @PostMapping("/remove")
    public ResponseEntity<EnvelopedResponse<String>> removeCustomer(@RequestParam String customerId) {
        return customerService.removeCustomer(customerId);
    }
}
