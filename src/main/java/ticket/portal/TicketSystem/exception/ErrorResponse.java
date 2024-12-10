package ticket.portal.TicketSystem.exception;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;

@Getter
public class ErrorResponse {

    @JsonProperty("code")
    @Schema(example = "400", description = "Error code")
    private final int code;

    @JsonProperty("message")
    @Schema(example = "An error message", description = "Error message")
    private final String message;

    @JsonProperty("status")
    @Schema(example = "OFFER_NOT_FOUND", description = "Error status code")
    private final String status;

    public ErrorResponse(int code, String message, String status) {
        this.code = code;
        this.message = message;
        this.status = status;
    }

}