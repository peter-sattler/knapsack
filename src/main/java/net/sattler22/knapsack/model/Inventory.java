package net.sattler22.knapsack.model;

import java.util.Collections;
import java.util.List;

/**
 * The <code>Inventory</code> contains all available items for purchase
 *
 * @author Pete Sattler
 * @version November 2025
 */
public record Inventory(List<Item> items) {

    private static final int MAX_ITEMS = 15;

    /**
     * Constructs a new <code>Inventory</</code>
     *
     * @param items One or more items to add
     */
    public Inventory(List<Item> items) {
        if (items == null || items.isEmpty())
            throw new IllegalArgumentException("At least one items is required");
        if (items.size() > MAX_ITEMS)
            throw new IllegalStateException("Too many items");
        this.items = List.copyOf(items);  //Defensive copy
    }

    /**
     * Get items
     *
     * @return A copy of all inventory items
     */
    @Override
    public List<Item> items() {
        return !items.isEmpty() ? List.copyOf(items) : Collections.emptyList();  //Defensive copy
    }
}
