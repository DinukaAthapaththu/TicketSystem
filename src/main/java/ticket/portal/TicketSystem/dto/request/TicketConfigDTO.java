package ticket.portal.TicketSystem.dto.request;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class TicketConfigDTO {
    private int totalTickets;
    private int ticketReleaseRate;
    private int customerRetrivalRate;
    private int maxTicketCapacity;
}
