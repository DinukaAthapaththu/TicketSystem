package ticket.portal.TicketSystem.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ticket.portal.TicketSystem.dto.request.EnvelopedResponse;
import ticket.portal.TicketSystem.service.TicketManagementService;

@RestController
@RequestMapping("/api/v1/system")
public class TicketSystemController {
    private final TicketManagementService ticketManagementService;

    @Autowired
    public TicketSystemController(TicketManagementService ticketManagementService){
        this.ticketManagementService = ticketManagementService;
    }

    @PostMapping("/start")
    public ResponseEntity<EnvelopedResponse<String>> startTicketSystem(){
        return ticketManagementService.startSystem();
    }

    @PostMapping("/stop")
    public ResponseEntity<EnvelopedResponse<String>> stopTicketSystem() {
        return ticketManagementService.stopSystem();
    }

    @GetMapping("/getQueueSize")
    public ResponseEntity<EnvelopedResponse<String>> getQueueSize(){
        return ticketManagementService.getQueueSize();
    }


}
