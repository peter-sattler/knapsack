package net.sattler22.knapsack;

import java.math.BigDecimal;
import java.util.ArrayDeque;
import java.util.Deque;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.jcip.annotations.Immutable;
import net.sattler22.knapsack.Knapsack.Item;

/**
 * Knapsack whole item packer implemented using simple, naive recursion. It does not allow fractional items to be packed. That is,
 * an item is either packed or it is left out.
 * <p/>
 * <b>WARNING:</b> This algorithm suffers from quadratic performance due to massive recomputation of overlapping sub-problems.
 *
 * @see <a href="http://techieme.in/solving-01-knapsack-problem-using-recursion"> Solving 0/1 Knapsack problem using Recursion</a>
 * @author Pete Sattler
 * @version December 2018
 */
@Immutable
public final class KnapsackPackerWholeItemRecursiveImpl extends KnapsackPackerBaseImpl {

    private static final Logger logger = LoggerFactory.getLogger(KnapsackPackerWholeItemRecursiveImpl.class);

    /**
     * Constructs a new whole item recursive knapsack packer
     *
     * @param knapsack The knapsack to pack
     * @param items The items to pack
     */
    public KnapsackPackerWholeItemRecursiveImpl(Knapsack knapsack, Item[] items) {
        super(knapsack, items);
    }

    @Override
    public synchronized void pack() {
        packImpl(0, 0, knapsack.capacity(), 0, new ArrayDeque<>());
    }

    private Item packImpl(int recursionLevel, int itemNbr, BigDecimal remainingCapacity, int totalCost, Deque<Item> visited) {
        if (remainingCapacity.compareTo(BigDecimal.ZERO) == 0 || itemNbr == items.length) {
            logger.info("RECURSION-LEVEL-{}: End of recursion path, remaining capacity={}, total cost={}, visited: {}", recursionLevel, remainingCapacity, totalCost, visited);
            if (totalCost > knapsack.totalCost()) {
                logger.info("Repacking knapsack with visited items of higher cost");
                knapsack.empty();
                for (final var visitedItem : visited)
                    knapsack.add(visitedItem);
            }
            if (!visited.isEmpty())
                logger.info("Removed {}", visited.pop());
            return Item.ZERO;
        }
        final var currentItem = items[itemNbr];
        visited.push(currentItem);
        logger.info("RECURSION-LEVEL-{}: *** Considering {}, remaining capacity={}, total cost={}", recursionLevel, currentItem, remainingCapacity, totalCost);
        if (currentItem.weight().compareTo(remainingCapacity) > 0) {
            logger.info("RECURSION-LEVEL-{}: {} does not fit into the remaining capacity of {} lbs.", recursionLevel, currentItem, remainingCapacity);
            logger.info("Removed {}", visited.pop());
            return packImpl(++recursionLevel, itemNbr + 1, remainingCapacity, totalCost, visited);
        }
        final var takeTheItem = packImpl(++recursionLevel, itemNbr + 1, remainingCapacity.subtract(currentItem.weight()), totalCost + currentItem.cost(), visited);
        final var leaveTheItem = packImpl(++recursionLevel, itemNbr + 1, remainingCapacity, totalCost, visited);
        if (takeTheItem.cost() >= leaveTheItem.cost()) {
            logger.info("RECURSION-LEVEL-{}: TOOK: {}", recursionLevel, takeTheItem);
            return takeTheItem;
        }
        logger.info("RECURSION-LEVEL-{}: LEFT: {}", recursionLevel, leaveTheItem);
        return leaveTheItem;
    }
}
