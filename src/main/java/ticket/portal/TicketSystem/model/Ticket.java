package ticket.portal.TicketSystem.model;

public class Ticket {
    private final int ticketId;
    private final int vendorId;

    public Ticket(int vendorId, int ticketId) {
        this.ticketId = ticketId;
        this.vendorId = vendorId;
    }
    @Override
    public String toString(){
        return ticketId + "-" + vendorId;
    }
}
