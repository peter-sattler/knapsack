package net.sattler22.knapsack.model;

import net.jcip.annotations.ThreadSafe;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * A <code>Package</code> holds zero or more purchased items
 *
 * @author Pete Sattler
 * @since December 2018
 * @version November 2025
 */
@ThreadSafe
public final class Package {

    private static final BigDecimal MAX_CAPACITY_LBS = BigDecimal.valueOf(100L);
    private final BigDecimal capacity;
    private final List<Item> items = Collections.synchronizedList(new ArrayList<>());

    /**
     * Constructs a new <code>Package</code>
     *
     * @param capacity The maximum weight (in pounds) that the package can hold
     */
    public Package(BigDecimal capacity) {
        if (capacity.compareTo(MAX_CAPACITY_LBS) > 0)
            throw new IllegalArgumentException("Maximum capacity exceeded");
        this.capacity = capacity;
    }

    /**
     * Get capacity
     *
     * @return The maximum weight (in pounds) that the package can hold
     */
    public BigDecimal capacity() {
        return capacity;
    }

    /**
     * Add a new item
     *
     * @param item The new item to add
     * @return True if the item was added. Otherwise, returns false.
     */
    public boolean add(Item item) {
        if (item == null)
            throw new IllegalArgumentException("Item is required");
        return items.add(item);
    }

    /**
     * Get items
     *
     * @return A copy of all purchased items
     */
    public List<Item> items() {
        return !items.isEmpty() ? List.copyOf(items) : Collections.emptyList();  //Defensive copy
    }

    /**
     * Empty condition check
     *
     * @return True if no items have been added. Otherwise, returns false.
     */
    public boolean isEmpty() {
        return items.isEmpty();
    }

    /**
     * Get item identifiers (IDs)
     *
     * @return A list of all item IDs
     */
    public List<Integer> ids() {
        return items.stream()
                .mapToInt(Item::id)
                .boxed()
                .toList();
    }

    /**
     * Calculate total cost
     *
     * @return The total cost (in USD) of all items
     */
    public BigDecimal totalCost() {
        return items.stream()
                .map(Item::cost)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    /**
     * Calculate total weight
     *
     * @return The total weight (in pounds) of all items
     */
    public BigDecimal totalWeight() {
        return items.stream()
                .map(Item::weight)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    @Override
    public int hashCode() {
        return capacity.hashCode() + items.hashCode();
    }

    @Override
    public boolean equals(Object other) {
        if (this == other)
            return true;
        if (other == null)
            return false;
        if (this.getClass() != other.getClass())
            return false;
        final Package that = (Package) other;
        return this.capacity.equals(that.capacity) && this.items.equals(that.items);
    }

    @Override
    public String toString() {
        return String.format("%s [capacity=%.2f, items=%s]", getClass().getSimpleName(), capacity, items);
    }
}
