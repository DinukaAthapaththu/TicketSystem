package ticket.portal.TicketSystem.model;

import lombok.Getter;
import lombok.Setter;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;

public class Vendor implements Runnable {
    private static final Logger logger = LogManager.getLogger(Vendor.class);

    private final TicketPool ticketPool;
    private final int ticketReleaseRate;
    private int totalTicketsToRelease;

    @Getter
    @Setter
    public int vendorId;

    private volatile boolean isRunning = true;

    public Vendor(TicketPool ticketPool, int ticketReleaseRate, int totalTicketsToRelease, int vendorId) {
        this.ticketPool = ticketPool;
        this.ticketReleaseRate = ticketReleaseRate;
        this.totalTicketsToRelease = totalTicketsToRelease;
        this.vendorId = vendorId;

    }

    @Override
    public void run() {
        while (isRunning && totalTicketsToRelease > 0) {
            logger.info("Vendor {} is trying to release a ticket.", vendorId);
            ticketPool.addTicket(vendorId);
            totalTicketsToRelease--;
            try {
                Thread.sleep(60000 / ticketReleaseRate); // 60000/10 -> 6000ms wait after each iteration
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                logger.error("Vendor {} has been interrupted.", vendorId);
                break;
            }

        }
        logger.info("Vendor {} has finished releasing all tickets.", vendorId);
    }

    public void stopVendor() {
        isRunning = false;
    }
}
