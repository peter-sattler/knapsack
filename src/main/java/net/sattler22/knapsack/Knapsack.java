package net.sattler22.knapsack;

import java.io.Serializable;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Objects;

/**
 * A knapsack capable of holding immutable items
 *
 * @author Pete Sattler
 * @version December 2018
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
     * @param newItem The new item to add. If the knapsack doesn't have enough capacity, then one or more items will be removed
     *        according to the specific implementation.
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
     * Get total weight
     *
     * @return The total weight (in pounds) of all items currently held in the knapsack
     */
    BigDecimal getTotalWeight();

    /**
     * Get total cost
     *
     * @return The total cost (in USD) of all items currently held in the knapsack
     */
    int getTotalCost();

    /**
     * Empty the knapsack of all items
     */
    void empty();

    /**
     * An immutable knapsack item
     */
    final class Item implements Serializable {

        private static final long serialVersionUID = 5011062863033824403L;
        private static final int COST_WEIGHT_RATIO_SCALE = 9;
        private final int id;
        private final BigDecimal weight;
        private final int cost;

        /**
         * Knapsack item with ZERO weight and ZERO cost
         */
        public static final Item ZERO = new Item(0, BigDecimal.ZERO, 0);

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
         * Get cost/weight ratio
         *
         * @return The cost per unit of weight (9 digits of scale, rounded half up)
         */
        public BigDecimal getCostWeightRatio() {
            if (weight.compareTo(BigDecimal.ZERO) == 0)
                return BigDecimal.ZERO;
            return new BigDecimal(cost).divide(weight, COST_WEIGHT_RATIO_SCALE, RoundingMode.HALF_UP);
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
            return String.format("%s [id=%s, weight=%s, cost=%s, cost/weight=%s]", getClass().getSimpleName(), id, weight, cost, getCostWeightRatio());
        }
    }
}
