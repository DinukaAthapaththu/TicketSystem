package ticket.portal.TicketSystem.model;


import lombok.Getter;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.concurrent.locks.Condition;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class TicketPool {
    private static final Logger logger = LogManager.getLogger(TicketPool.class);

    private final int maxTicketCapacity;

    @Getter
    private int currentTickets;
    private final List<Ticket> buffer;

    private final Lock lock;
    private final Condition empty;
    private final Condition full;

    private int nextTicketId = 1;

    public TicketPool(int maxTicketCapacity) {
        this.maxTicketCapacity = maxTicketCapacity;
        int Ticket = 0;
        this.buffer = Collections.synchronizedList(new ArrayList<>(Ticket));


        lock = new ReentrantLock();
        empty = lock.newCondition();
        full = lock.newCondition();

        currentTickets = 0;
    }

    public void addTicket(int vendorId) {
        lock.lock();
        try {
            while (currentTickets >= maxTicketCapacity) {
                logger.warn("Full. Waiting for customers to buy ticket");
                full.await();
            }

            Ticket ticket = new Ticket(nextTicketId++, vendorId);
            buffer.add(ticket);
            currentTickets++;

            logger.info("Ticket{} added by vendor{}. Current pool size: {}", ticket, vendorId, currentTickets);

            empty.signalAll();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        } finally {
            lock.unlock();
        }
    }

    public void removeTicket(int customerId) {
        lock.lock();
        try {
            while (currentTickets == 0) {
                logger.warn("Buffer empty. Waiting for Vendors to release tickets");
                empty.await();
            }
            Ticket ticket = buffer.remove(0);
            currentTickets--;
            logger.info("Ticket {} purchased by Customer {}. Remaining pool size: {}", ticket, customerId, currentTickets);

            full.signalAll();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        } finally {
            lock.unlock();
        }
    }

}
