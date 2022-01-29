package net.sattler22.knapsack;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Objects;

/**
 * Knapsack base implementation
 *
 * @implSpec All sub-classes are required to be thread-safe
 * @author Pete Sattler
 * @version December 2018
 */
public abstract sealed class KnapsackBaseImpl implements Knapsack permits KnapsackDefaultImpl {

    protected final BigDecimal capacity;
    protected final Collection<Item> items;

    /**
     * Constructs a new knapsack
     *
     * @param capacity The maximum weight (in pounds) that the knapsack can hold
     * @param items The currently held items
     */
    protected KnapsackBaseImpl(BigDecimal capacity, Collection<Item> items) {
        this.capacity = capacity;
        this.items = Collections.synchronizedCollection(items);
    }

    @Override
    public BigDecimal capacity() {
        return capacity;
    }

    @Override
    public boolean add(Item newItem) {
        if (newItem.weight().compareTo(BigDecimal.ZERO) <= 0)
            return false;
        if (items.contains(newItem))
            return false;
        return addImpl(newItem);
    }

    protected abstract boolean addImpl(Item newItem);

    /**
     * New item capacity check
     *
     * @param newItem The new item to add
     * @return True if there is enough available capacity to fit the new item. Otherwise, returns false.
     */
    protected boolean hasEnoughCapacity(Item newItem) {
        final var usedCapacity = newItem.weight().add(totalWeight());
        return usedCapacity.compareTo(capacity) <= 0;
    }

    @Override
    public int[] items() {
        return items.stream().mapToInt(Item::id).toArray();
    }

    @Override
    public BigDecimal totalWeight() {
        return items.stream().map(Item::weight).reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    @Override
    public int totalCost() {
        return items.stream().mapToInt(Item::cost).sum();
    }

    @Override
    public void empty() {
        items.clear();
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(capacity) + Arrays.hashCode(items());
    }

    @Override
    public boolean equals(Object other) {
        if (this == other)
            return true;
        if (other == null)
            return false;
        if (this.getClass() != other.getClass())
            return false;
        final var that = (KnapsackBaseImpl) other;
        return Objects.equals(this.capacity, that.capacity) && Arrays.equals(this.items(), that.items());
    }

    @Override
    public String toString() {
        return String.format("%s [capacity=%s, items=%s]", getClass().getSimpleName(), capacity, items);
    }
}
