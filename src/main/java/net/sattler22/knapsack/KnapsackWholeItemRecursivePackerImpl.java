package net.sattler22.knapsack;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Stack;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.sattler22.knapsack.Knapsack.Item;

/**
 * Knapsack whole item packer implemented using simple, naive recursion. It does not allow fractional items to be packed. That is,
 * an item is either packed or it is left out.
 * <p/>
 * <b>WARNING:</b> This algorithm suffers from quadratic performance due to massive recomputation of overlapping subproblems.
 *
 * @see <a href="http://techieme.in/solving-01-knapsack-problem-using-recursion"> Solving 0/1 Knapsack problem using Recursion</a>
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
        packImpl(0, 0, knapsack.getCapacity(), 0, new Stack<>());
    }

    private Item packImpl(int recursionLevel, int itemNbr, BigDecimal remainingCapacity, int totalCost, Stack<Item> visited) {
        if (remainingCapacity.compareTo(BigDecimal.ZERO) == 0 || itemNbr == items.length) {
            LOGGER.info("RECURSION-LEVEL-{}: End of recursion path, remaining capacity={}, total cost={}, visited: {}", recursionLevel, remainingCapacity, totalCost, visited);
            if (totalCost > knapsack.getTotalCost()) {
                LOGGER.info("Repacking knapsack with visited items of higher cost");
                knapsack.empty();
                for (Item visitedItem : visited)
                    knapsack.add(visitedItem);
            }
            if (!visited.isEmpty()) {
                final Item removedItem = visited.pop();
                LOGGER.info("Removed {}", removedItem);
            }
            return Item.ZERO;
        }
        final Item currentItem = items[itemNbr];
        visited.push(currentItem);
        LOGGER.info("RECURSION-LEVEL-{}: *** Considering {}, remaining capacity={}, total cost={}", recursionLevel, currentItem, remainingCapacity, totalCost);
        if (currentItem.getWeight().compareTo(remainingCapacity) > 0) {
            LOGGER.info("RECURSION-LEVEL-{}: {} does not fit into the remaining capacity of {} lbs.", recursionLevel, currentItem, remainingCapacity);
            LOGGER.info("Removed {}", visited.pop());
            return packImpl(++recursionLevel, itemNbr + 1, remainingCapacity, totalCost, visited);
        }
        final Item takeTheItem = packImpl(++recursionLevel, itemNbr + 1, remainingCapacity.subtract(currentItem.getWeight()), totalCost + currentItem.getCost(), visited);
        final Item leaveTheItem = packImpl(++recursionLevel, itemNbr + 1, remainingCapacity, totalCost, visited);
        if (takeTheItem.getCost() >= leaveTheItem.getCost()) {
            LOGGER.info("RECURSION-LEVEL-{}: TOOK: {}", recursionLevel, takeTheItem);
            return takeTheItem;
        }
        LOGGER.info("RECURSION-LEVEL-{}: LEFT: {}", recursionLevel, leaveTheItem);
        return leaveTheItem;
    }
}
