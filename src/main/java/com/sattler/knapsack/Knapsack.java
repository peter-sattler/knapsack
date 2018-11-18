package com.sattler.knapsack;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Objects;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

/**
 * Barclay's Programming Challenge 2018
 * <ol>
 * <li>You want to send your friend a package with different things.</li>
 * <li>Each thing you put inside of a package has such parameters as index number, weight and cost.</li>
 * <li>The package has a weight limitation.</li>
 * </ol>
 * <p/>
 * Your goal is to determine which things to put into the package so that the total weight is less than or equal to the package limit and the total cost is as large as possible.
 * <p/>
 * You would prefer to send a package which has less weight if there is more than one package with the same price.
 * <p/>
 * This is a variation of the Knapsack problem.
 * <p/>
 * Input: Use the <code>KnapsackDataParser</code> parse the input data.
 * <p/>
 * Constraints:
 * <ol>
 * <li>Max weight any package can take is <= 100.</li>
 * <li>There might be up to 15 things you need to choose from.</li>
 * <li>Max weight and max cost of any thing is <= 100.</li>
 * </ol>
 * <p/>
 * Output:
 * <p/>
 * For each set of things produce a list of things (their index numbers separated by comma) that you put into the package. If none of the items will fit in the package, print a hyphen (-).
 * <p/>
 * Examples:
 * <ol>
 * <li>81 : (1,53.38,$45) (2,88.62,$98) (3,78.48,$3) (4,72.30,$76) (5,30.18,$9) (6,46.34,$48) ==> 4</li>
 * <li>75 : (1,85.31,$29) (2,14.55,$74) (3,3.98,$16) (4,26.24,$55) (5,63.69,$52) (6,76.25,$75) (7,60.02,$74) (8,93.18,$35) (9,89.95,$78) ==> (2,7)</li>
 * </ol>
 * 
 * @author Pete Sattler
 */
public interface Knapsack {

    /**
     * Get capacity
     * 
     * @return The maximum weight that the knapsack can hold
     */
    BigDecimal getCapacity();

    /**
     * Get possible items
     * 
     * @return The list of possible items that may fit into the knapsack
     */
    Item[] getItems();

    /**
     * Pack the knapsack
     * 
     * @return The most expensive item that will fit into the knapsack
     */
    Item pack();

    /**
     * Represents an immutable item that can be placed into the knapsack
     */
    final class Item implements Serializable {

        private static final long serialVersionUID = -1586177999761882070L;
        private final int number;
        private final BigDecimal weight;
        private final int cost;

        /**
         * Constructs a new knapsack item with ZERO cost
         */
        public Item() {
            this(-1, BigDecimal.ZERO, 0);
        }

        /**
         * Constructs a new knapsack item
         * 
         * @param number The item's index number
         * @param weight The item's weight (in pounds)
         * @param cost The cost of the item
         */
        public Item(int number, BigDecimal weight, int cost) {
            this.number = number;
            this.weight = weight;
            this.cost = cost;
        }

        /**
         * Add the cost of two items
         * 
         * @param additionalItem The item whose additional cost is added to this item
         * @return A new Item with the original's number and weight, and additional cost added to the original cost
         */
        public Item addCost(Item additionalItem) {
            return new Item(this.number, this.weight, this.cost + additionalItem.cost);
        }

        /**
         * Find item with maximum cost
         */
        public static Item max(Item item1, Item item2) {
            return (item1.cost > item2.cost) ? item1 : item2;
        }

        public int getNumber() {
            return number;
        }

        public BigDecimal getWeight() {
            return weight;
        }

        public int getCost() {
            return cost;
        }

        @Override
        public int hashCode() {
            return Objects.hash(this.number, this.weight, this.cost);
        }

        @Override
        public boolean equals(Object other) {
            if (this == other)
                return true;
            if (this.getClass() != other.getClass())
                return false;
            final Item that = (Item) other;
            return new EqualsBuilder().append(this.number, that.number).append(this.weight, that.weight).append(this.cost, that.cost).isEquals();
        }

        @Override
        public String toString() {
            return ToStringBuilder.reflectionToString(this, ToStringStyle.SHORT_PREFIX_STYLE);
        }
    }
}
