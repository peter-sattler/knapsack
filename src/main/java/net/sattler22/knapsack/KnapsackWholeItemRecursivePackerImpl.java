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
 * @version December 2018
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
        if (remainingCapacity.compareTo(BigDecimal.ZERO) == 0 || itemNbr == items.length) {
            LOGGER.info("RECURSION-LEVEL-{}: End of recursion path, remaining capacity={}, total cost={}", recursionLevel, remainingCapacity, totalCost);
            //return Item.ZERO;
            return items[itemNbr - 1];
        }
        final Item currentItem = items[itemNbr];
        LOGGER.info("RECURSION-LEVEL-{}: Considering {}, remaining capacity={}, total cost={}", recursionLevel, currentItem, remainingCapacity, totalCost);
        if (currentItem.getWeight().compareTo(remainingCapacity) > 0) {
            //LOGGER.info("RECURSION-LEVEL-{}: {} does not fit into the remaining capacity of {} lbs.", recursionLevel, currentItem, remainingCapacity);
            return packImpl(++recursionLevel, itemNbr + 1, remainingCapacity, totalCost);
        }

        //Take the item:
        final Item tookItem = packImpl(++recursionLevel, itemNbr + 1, remainingCapacity.subtract(currentItem.getWeight()), totalCost + currentItem.getCost());
        //LOGGER.info("RECURSION-LEVEL-{}: TOOK {}", recursionLevel, tookItem);

        //Leave the item:
        final Item leftItem = packImpl(++recursionLevel, itemNbr + 1, remainingCapacity, totalCost);
        //LOGGER.info("RECURSION-LEVEL-{}: LEFT {}", recursionLevel, leftItem);

        //final Item maxItem = max(tookItem, leftItem);
        final Item maxItem = tookItem.getCost() > leftItem.getCost() ? tookItem : leftItem;

        //LOGGER.info("RECURSION-LEVEL-{}: MAX {}", recursionLevel, maxItem);

        knapsack.add(maxItem);
        return maxItem;
    }

    private static Item max(Item item1, Item item2) {
        return item1.getCost() > item2.getCost() ? item1 : item2;
    }
}
