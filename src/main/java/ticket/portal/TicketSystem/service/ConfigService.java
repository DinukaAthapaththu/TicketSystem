package ticket.portal.TicketSystem.service;

import com.google.gson.Gson;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import ticket.portal.TicketSystem.dto.request.EnvelopedResponse;
import ticket.portal.TicketSystem.dto.request.TicketConfigDTO;
import ticket.portal.TicketSystem.exception.ErrorResponse;

import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;


@Service
public class ConfigService {
    private static final Logger logger = LogManager.getLogger(ConfigService.class);

    @Value("${ticket.portal.configFilePath}")
    private String configFilePath;

    public ResponseEntity<EnvelopedResponse<String>> saveConfiguration(TicketConfigDTO config) throws IOException {
        try (FileWriter writer = new FileWriter(configFilePath)) {
            Gson gson = new Gson();
            gson.toJson(config, writer);
            logger.info("Saving config successfully");
            return ResponseEntity.ok(EnvelopedResponse.fromResponse("Saving config successfully"));
        } catch (IOException e) {
            logger.error("An error occurred while Saving Config");
            List<ErrorResponse> errors = List.of(new ErrorResponse(500, "An error occurred while Saving Config ", e.getMessage()));
            throw e;
        }

    }

    public TicketConfigDTO loadConfiguration() throws IOException {
        try (FileReader reader = new FileReader(configFilePath)) {
            Gson gson = new Gson();
            logger.info("Loading configs successful");
            return gson.fromJson(reader, TicketConfigDTO.class);
        } catch (IOException e) {
            logger.error("An error occurred while Loading Config");
            throw e;
        }
    }
}
