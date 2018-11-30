package net.sattler22.knapsack;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Objects;
import java.util.concurrent.PriorityBlockingQueue;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * An immutable knapsack to retain the most expensive items possible
 * 
 * @author Pete Sattler
 * @version November 2018
 */
public final class KnapsackRetainMostExpensiveImpl implements Knapsack, Serializable {

    private static final long serialVersionUID = 1219933700555470834L;
    private static final Logger LOGGER = LoggerFactory.getLogger(KnapsackRetainMostExpensiveImpl.class);
    private static final Comparator<Item> LOWEST_COST_ITEM = Comparator.comparingInt(Item::getCost);
    private final BigDecimal capacity;
    private final PriorityBlockingQueue<Item> items = new PriorityBlockingQueue<>(11, LOWEST_COST_ITEM);

    /**
     * Constructs a new knapsack
     * 
     * @param capacity The maximum weight (in pounds) that the knapsack can hold
     */
    public KnapsackRetainMostExpensiveImpl(BigDecimal capacity) {
        this.capacity = capacity;
    }

    @Override
    public BigDecimal getCapacity() {
        return capacity;
    }

    @Override
    public boolean add(Item newItem) {
        if (newItem.getWeight().compareTo(BigDecimal.ZERO) > 0 && newItem.getWeight().compareTo(capacity) <= 0) {
            if (!items.contains(newItem)) {
                //Free up capacity by removing items with a lower cost than the new item:
                while (!hasEnoughCapacity(newItem)) {
                    final Item peekItem = items.peek();
                    if (peekItem != null && peekItem.getCost() <= newItem.getCost()) {
                        final Item removedItem = items.remove();
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
            }
        }
        return false;
    }

    /**
     * New item capacity check
     * 
     * @param newItem The new item to add
     * @return True if there is enough available capacity to fit the new item. Otherwise, returns false.
     */
    private boolean hasEnoughCapacity(Item newItem) {
        final BigDecimal usedCapacity = newItem.getWeight().add(items.stream().map(Item::getWeight).reduce(BigDecimal.ZERO, BigDecimal::add));
        return usedCapacity.compareTo(capacity) <= 0;
    }

    @Override
    public int[] getItems() {
        return items.stream().mapToInt(Item::getId).toArray();
    }

    @Override
    public int hashCode() {
        return Objects.hash(capacity, getItems());
    }

    @Override
    public boolean equals(Object other) {
        if (this == other)
            return true;
        if (other == null)
            return false;
        if (this.getClass() != other.getClass())
            return false;
        final KnapsackRetainMostExpensiveImpl that = (KnapsackRetainMostExpensiveImpl) other;
        return Objects.equals(this.capacity, that.capacity) && Arrays.equals(this.getItems(), that.getItems());
    }

    @Override
    public String toString() {
        return String.format("%s [capacity=%s, items=%s]", getClass().getSimpleName(), capacity, items);
    }
}
