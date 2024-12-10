package ticket.portal.TicketSystem.model;

import lombok.Getter;
import lombok.Setter;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class Customer implements Runnable {
    private static final Logger logger = LogManager.getLogger(Customer.class);

    private final TicketPool ticketPool;
    private final int customerRetrievalRate;


    @Getter
    @Setter
    public int customerId;

    private volatile boolean isRunning = true;

    public Customer(TicketPool ticketPool, int customerRetrievalRate, int customerId) {
        this.ticketPool = ticketPool;
        this.customerRetrievalRate = customerRetrievalRate;
        this.customerId = customerId;
    }

    @Override
    public void run() {
        while (isRunning) {
            logger.info("Customer {} is trying to purchase a ticket.", customerId);
            ticketPool.removeTicket(customerId);
            try {
                Thread.sleep(60000 / customerRetrievalRate);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                logger.error("Customer {} has been interrupted.", customerId);
                break;
            }
        }
        logger.info("Customer {} has finished.", customerId);
    }
    public void stopCustomer(){
        isRunning = false;
    }
    @Override
    public String toString(){
        return "Customer" + customerId;

    }
}

