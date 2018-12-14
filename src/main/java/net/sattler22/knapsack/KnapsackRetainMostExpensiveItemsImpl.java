package net.sattler22.knapsack;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Comparator;
import java.util.concurrent.PriorityBlockingQueue;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * An immutable knapsack which retains the most expensive items possible within the available capacity
 * 
 * @author Pete Sattler
 * @version December 2018
 */
public final class KnapsackRetainMostExpensiveItemsImpl extends KnapsackBaseImpl implements Serializable {

    //TODO: BRUTE-FORCE + THIS KNAPSACK
    //NOTE: The knapsack implementation itself handles retention of the most expensive item(s)

    private static final long serialVersionUID = 497637156362609201L;
    private static final Logger LOGGER = LoggerFactory.getLogger(KnapsackRetainMostExpensiveItemsImpl.class);

    /**
     * Constructs a new knapsack
     * 
     * @param capacity The maximum weight (in pounds) that the knapsack can hold
     */
    public KnapsackRetainMostExpensiveItemsImpl(BigDecimal capacity) {
        super(capacity, new PriorityBlockingQueue<>(11, Comparator.comparingInt(Item::getCost)));
    }

    @Override
    protected boolean addImpl(Item newItem) {
        while (!hasEnoughCapacity(newItem)) {
            //Free up capacity by removing items with a lower cost than the new item:
            final Item peekItem = ((PriorityBlockingQueue<Item>) items).peek();
            if (peekItem != null && peekItem.getCost() <= newItem.getCost()) {
                final Item removedItem = ((PriorityBlockingQueue<Item>) items).remove();
                LOGGER.info("Removed existing {}", removedItem);
                continue;
            }
            LOGGER.info("No more items with a lower cost than {}", newItem);
            break;
        }
        if (hasEnoughCapacity(newItem) && items.add(newItem)) {
            LOGGER.info("Added new {}", newItem);
            return true;
        }
        return false;
    }
}
