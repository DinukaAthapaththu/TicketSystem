package ticket.portal.TicketSystem.dto.request;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import lombok.Getter;
import ticket.portal.TicketSystem.exception.ErrorResponse;

import java.util.List;

@Builder
@Getter
@JsonInclude(JsonInclude.Include.NON_NULL)
public class EnvelopedResponse<T> {
    private T data;
    private List<ErrorResponse> errors;
    private boolean hasError;

    public EnvelopedResponse() {
    }

    public EnvelopedResponse(T data, List<ErrorResponse> errors, boolean hasError) {
        this.data = data;
        this.errors = errors;
        this.hasError = hasError;
    }

    public static <T> EnvelopedResponse<T> fromResponse(T data) {
        EnvelopedResponse.EnvelopedResponseBuilder<T> response = EnvelopedResponse.builder();

        return response.errors(List.of())
                .hasError(false)
                .data(data)
                .build();
    }

    public static <T> EnvelopedResponse<T> fromErrorResponse(List<ErrorResponse> errors) {
        EnvelopedResponse.EnvelopedResponseBuilder<T> response = EnvelopedResponse.builder();
        return response.errors(errors)
                .hasError(true)
                .build();
    }
}