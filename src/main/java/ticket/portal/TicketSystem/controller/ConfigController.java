package ticket.portal.TicketSystem.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ticket.portal.TicketSystem.dto.request.EnvelopedResponse;
import ticket.portal.TicketSystem.dto.request.TicketConfigDTO;
import ticket.portal.TicketSystem.exception.ErrorResponse;
import ticket.portal.TicketSystem.service.ConfigService;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/api/v1/config")
public class ConfigController {
    private final ConfigService configService;

    @Autowired
    public ConfigController(ConfigService configService) {
        this.configService = configService;
    }

    @PostMapping("/save")
    public ResponseEntity<EnvelopedResponse<String>> saveConfig(@RequestBody TicketConfigDTO config) throws IOException {
        return configService.saveConfiguration(config);
    }

    @GetMapping("/load")
    public ResponseEntity<EnvelopedResponse<TicketConfigDTO>> loadConfig(){
        try {
            return ResponseEntity.ok(EnvelopedResponse.fromResponse(configService.loadConfiguration()));
        } catch (Exception e){
            List<ErrorResponse> errors = List.of(new ErrorResponse(500, "An error occurred while Getting config", e.getMessage()));
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(EnvelopedResponse.fromErrorResponse(errors));
        }
    }
}
