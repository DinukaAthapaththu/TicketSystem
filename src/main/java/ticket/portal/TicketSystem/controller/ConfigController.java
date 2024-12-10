package ticket.portal.TicketSystem.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ticket.portal.TicketSystem.dto.request.EnvelopedResponse;
import ticket.portal.TicketSystem.dto.request.TicketConfigDTO;
import ticket.portal.TicketSystem.service.ConfigService;

import java.io.IOException;

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
    public ResponseEntity<TicketConfigDTO> loadConfig() throws IOException {
        return ResponseEntity.ok(configService.loadConfiguration());
    }
}
