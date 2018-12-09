package net.sattler22.knapsack;

import java.io.Serializable;
import java.math.BigDecimal;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.sattler22.knapsack.Knapsack.Item;

/**
 * Knapsack whole item packer implemented using simple, naive recursion. It does not allow
 * fractional items to be packed. That is, an item is either packed or it is left out.
 * <p/>
 * <b>WARNING:</b> This algorithm suffers from quadratic performance due to massive
 * recomputation of overlapping subproblems
 * 
 * @see <a href="http://techieme.in/solving-01-knapsack-problem-using-recursion">
 *         Solving 0/1 Knapsack problem using Recursion</a>
 * @author Pete Sattler
 * @version November 2018
 */
public final class KnapsackWholeItemRecursivePackerImpl extends KnapsackBasePackerImpl implements Serializable {

    private static final long serialVersionUID = -4277676325805820861L;
    private static final Logger LOGGER = LoggerFactory.getLogger(KnapsackWholeItemRecursivePackerImpl.class);

    /**
     * Constructs a new whole item recursive knapsack packer
     * 
     * @param knapsack The knapsack to pack
     * @param items The items to pack
     */
    public KnapsackWholeItemRecursivePackerImpl(Knapsack knapsack, Item[] items) {
        super(knapsack, items);
    }

    @Override
    public synchronized void pack() {
        packImpl(0, 0, knapsack.getCapacity(), 0);
    }

    private Item packImpl(int recursionLevel, int itemNbr, BigDecimal remainingCapacity, int totalCost) {
        if (remainingCapacity == BigDecimal.ZERO || itemNbr == items.length) {
            LOGGER.debug("RECURSION-LEVEL={}: End of recursion path, remaining capacity={}, total cost={}", recursionLevel, remainingCapacity, totalCost);
            return Item.ZERO;
        }
        final Item currentItem = items[itemNbr];
        LOGGER.debug("RECURSION-LEVEL={}: Considering {}", recursionLevel, currentItem);
        if (currentItem.getWeight().compareTo(remainingCapacity) > 0) {
            LOGGER.debug("RECURSION-LEVEL={}: {} does not fit into the remaining capacity of {} lbs.", recursionLevel, currentItem, remainingCapacity);
            return packImpl(++recursionLevel, itemNbr + 1, remainingCapacity, totalCost);
        }
        //Either take the item or leave it:
        //NOTE: The knapsack implementation itself handles retention of the most expensive item(s)
        final Item leftItem = packImpl(++recursionLevel, itemNbr + 1, remainingCapacity, totalCost);
        if (knapsack.add(currentItem)) {
            LOGGER.debug("RECURSION-LEVEL={}: Current {} fits into the knapsack", recursionLevel, currentItem);
            return currentItem;
        }
        if (knapsack.add(leftItem)) {
            LOGGER.debug("RECURSION-LEVEL={}: Left {} fits into the knapsack", recursionLevel, leftItem);
            return leftItem;
        }
        return Item.ZERO;
    }
}
