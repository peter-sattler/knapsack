package com.sattler.knapsack;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Objects;

/**
 * A knapsack capable of holding immutable items
 * 
 * @author Pete Sattler
 */
public interface Knapsack {

    /**
     * Get capacity
     * 
     * @return The maximum weight (in pounds) that the knapsack can hold
     */
    BigDecimal getCapacity();

    /**
     * Add a new item
     * 
     * @param newItem The new item to add. If the knapsack doesn't have enough capacity, then the items will be removed according to implementation specific requirements.
     * @return True if the item was added to the knapsack. Otherwise, returns false.
     */
    boolean add(Item newItem);

    /**
     * Get items
     * 
     * @return An array of all item IDs currently held in the knapsack
     */
    int[] getItems();

    /**
     * An immutable knapsack item
     */
    final class Item implements Serializable {

        private static final long serialVersionUID = 5011062863033824403L;
        private final int id;
        private final BigDecimal weight;
        private final int cost;

        /**
         * Knapsack item with ZERO weight and ZERO cost
         */
        public static final Item ZERO_ITEM = new Item(-1, BigDecimal.ZERO, 0);

        /**
         * Constructs a new knapsack item
         * 
         * @param id A unique identifier
         * @param weight The weight (in pounds)
         * @param cost The cost of the item (in USD)
         */
        public Item(int id, BigDecimal weight, int cost) {
            this.id = id;
            this.weight = weight;
            this.cost = cost;
        }

        public int getId() {
            return id;
        }

        public BigDecimal getWeight() {
            return weight;
        }

        public int getCost() {
            return cost;
        }

        /**
         * Find the most expensive item
         */
        public static Item findMostExpensive(Item item1, Item item2) {
            return (item1.cost > item2.cost) ? item1 : item2;
        }

        @Override
        public int hashCode() {
            return Objects.hash(id);
        }

        @Override
        public boolean equals(Object other) {
            if (this == other)
                return true;
            if (other == null)
                return false;
            if (this.getClass() != other.getClass())
                return false;
            final Item that = (Item) other;
            return this.id == that.id;
        }

        @Override
        public String toString() {
            return String.format("%s [id=%s, weight=%s, cost=%s]", getClass().getSimpleName(), id, weight, cost);
        }
    }
}
