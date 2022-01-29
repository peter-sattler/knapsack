package net.sattler22.knapsack;

import java.math.BigDecimal;
import java.math.RoundingMode;

/**
 * A knapsack capable of holding immutable items
 *
 * @author Pete Sattler
 * @version December 2018
 */
public sealed interface Knapsack permits KnapsackBaseImpl {

    /**
     * Get capacity
     *
     * @return The maximum weight (in pounds) that the knapsack can hold
     */
    BigDecimal capacity();

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
    int[] items();

    /**
     * Get total weight
     *
     * @return The total weight (in pounds) of all items currently held in the knapsack
     */
    BigDecimal totalWeight();

    /**
     * Get total cost
     *
     * @return The total cost (in USD) of all items currently held in the knapsack
     */
    int totalCost();

    /**
     * Empty the knapsack of all items
     */
    void empty();

    /**
     * Knapsack item
     */
    static record Item(int id, BigDecimal weight, int cost) {

        private static final int COST_WEIGHT_RATIO_SCALE = 9;

        /**
         * Knapsack item with ZERO weight and ZERO cost
         */
        public static final Item ZERO = new Item(0, BigDecimal.ZERO, 0);

        /**
         * Calculate the cost/weight ratio
         *
         * @return The cost per unit of weight (9 digits of scale, rounded half up)
         */
        public BigDecimal calculateCostWeightRatio() {
            if (weight.compareTo(BigDecimal.ZERO) == 0)
                return BigDecimal.ZERO;
            return new BigDecimal(cost).divide(weight, COST_WEIGHT_RATIO_SCALE, RoundingMode.HALF_UP);
        }
    }
}
