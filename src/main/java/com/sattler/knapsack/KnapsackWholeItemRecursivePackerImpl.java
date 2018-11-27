package com.sattler.knapsack;

import static com.sattler.knapsack.Knapsack.Item.ZERO_ITEM;

import java.io.Serializable;
import java.math.BigDecimal;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sattler.knapsack.Knapsack.Item;

/**
 * Knapsack whole item packer implemented using simple, naive recursion. It does not allow 
 * fractional items to be packed. That is, an item is either packed or it is left out.
 * 
 * @see <a href="http://techieme.in/solving-01-knapsack-problem-using-recursion">Solving 0/1 Knapsack problem using Recursion</a>
 * @author Pete Sattler
 * @version November 2018
 */
public final class KnapsackWholeItemRecursivePackerImpl implements KnapsackPacker, Serializable {

    private static final long serialVersionUID = -4277676325805820861L;
    private static final Logger LOGGER = LoggerFactory.getLogger(KnapsackWholeItemRecursivePackerImpl.class);
    private final Knapsack knapsack;

    /**
     * Constructs a new knapsack packer
     * 
     * @param knapsack The knapsack to pack
     */
    public KnapsackWholeItemRecursivePackerImpl(Knapsack knapsack) {
        this.knapsack = knapsack;
    }

    @Override
    public Knapsack pack(Item[] items) {
        packImpl(0, items, knapsack.getCapacity(), 0, 0);
        return knapsack;
    }

    private Item packImpl(int recursionLevel, Item[] items, BigDecimal remainingCapacity, int totalCost, int itemNbr) {
        if (remainingCapacity == BigDecimal.ZERO || itemNbr == items.length) {
            LOGGER.debug("RECURSION-LEVEL={}: End of recursion path, remaining capacity={}, total cost={}", recursionLevel, remainingCapacity, totalCost);
            return ZERO_ITEM;
        }
        final Item currentItem = items[itemNbr];
        LOGGER.debug("RECURSION-LEVEL={}: Considering {}", recursionLevel, currentItem);
        if (currentItem.getWeight().compareTo(remainingCapacity) > 0) {
            LOGGER.debug("RECURSION-LEVEL={}: {} does not fit into the remaining capacity of {} lbs.", recursionLevel, currentItem, remainingCapacity);
            return packImpl(++recursionLevel, items, remainingCapacity, totalCost, itemNbr + 1);
        }
        //Let the specific knapsack implementation handle the retention of the most expensive items:
        final Item rightBranch = currentItem.add(packImpl(++recursionLevel, items, remainingCapacity.subtract(currentItem.getWeight()), totalCost, itemNbr + 1));
        final Item leftBranch = packImpl(++recursionLevel, items, remainingCapacity, totalCost, itemNbr + 1);
        //The RIGHT branch is the solution where the parent node is chosen:
        if (knapsack.add(rightBranch)) {
            LOGGER.debug("RECURSION-LEVEL={}: {} from RIGHT branch fits into the knapsack", recursionLevel, rightBranch);
            return rightBranch;
        }
        //The LEFT branch is the solution where the parent node is not chosen:
        if (knapsack.add(leftBranch)) {
            LOGGER.debug("RECURSION-LEVEL={}: {} from LEFT branch fits into the knapsack", recursionLevel, leftBranch);
            return leftBranch;
        }
        return ZERO_ITEM;
    }

    @Override
    public String toString() {
        return String.format("%s [knapsack=%s]", getClass().getSimpleName(), knapsack);
    }
}
