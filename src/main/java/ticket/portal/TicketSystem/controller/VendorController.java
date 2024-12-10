package ticket.portal.TicketSystem.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ticket.portal.TicketSystem.dto.request.EnvelopedResponse;
import ticket.portal.TicketSystem.model.Vendor;
import ticket.portal.TicketSystem.service.VendorService;

import java.util.List;

@RestController
@RequestMapping("/api/v1/vendor")
public class VendorController {
    private final VendorService vendorService;

    @Autowired
    public VendorController(VendorService vendorService) {
        this.vendorService = vendorService;
    }

    @PostMapping("/add")
    public ResponseEntity<EnvelopedResponse<String>> addVendor(@RequestParam String vendorId) {
        return vendorService.addVendor(vendorId);
    }

    @PostMapping("/remove")
    public ResponseEntity<EnvelopedResponse<String>> removeVendor(@RequestParam String vendorId) {
        return vendorService.removeVendor(vendorId);
    }

    @GetMapping("/list")
    public ResponseEntity<EnvelopedResponse<List<Vendor>>> getVendorList() {
        return vendorService.getVendorList();
    }
}