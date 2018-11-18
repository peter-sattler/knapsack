package com.sattler.knapsack;

import java.math.BigDecimal;
import java.util.Arrays;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sattler.knapsack.KnapsackDataParser.KnapsackParameter;

/**
 * Knapsack 0/1 algorithm implemented using simple, naive recursion
 * <p/>
 * NOTE: This implementation restricts the number of copies of each kind of item to zero or one.
 * 
 * @author Pete Sattler
 */
public final class KnapsackZeroOneRecursiveImpl implements Knapsack {

    private static final Logger LOGGER = LoggerFactory.getLogger(KnapsackZeroOneRecursiveImpl.class);
    private final BigDecimal capacity;
    private final Item[] items;

    /**
     * Constructs a new knapsack 0/1 algorithm recursion implementation
     */
    public KnapsackZeroOneRecursiveImpl(String unparsedData) {
        KnapsackDataParser parser = new KnapsackDataParser(unparsedData);
        KnapsackParameter dataParm = parser.parse();
        this.capacity = dataParm.getCapacity();
        this.items = dataParm.getItems();
    }

    @Override
    public BigDecimal getCapacity() {
        return capacity;
    }

    @Override
    public Item[] getItems() {
        return Arrays.copyOf(items, items.length);
    }

    @Override
    public Item pack() {
        LOGGER.info("Knapsack capacity: {} lbs.", capacity);
        for (Item item : items)
            LOGGER.info("Item #{} weighs {} lbs. and costs ${}", item.getNumber(), item.getWeight(), item.getCost());
        return pack(-1, capacity, items.length, items);
    }

    /**
     * Pack the knapsack using recursion
     * 
     * @param level The recursion level
     * @param remainingCapacity The knapsack's remaining capacity
     * @param itemNbr The item under consideration
     * @param items The list of possible items to place into the knapsack
     * @return The item with the highest cost that will fit into the knapsack
     */
    private static Item pack(int level, BigDecimal remainingCapacity, int itemNbr, Item[] items) {
        if (remainingCapacity == BigDecimal.ZERO || itemNbr == 0) {
            return new Item();   // Recursion level complete
        }
        final int currentIndex = itemNbr - 1;
        final Item currentItem = items[currentIndex];
        if (currentItem.getWeight().compareTo(remainingCapacity) > 0) {
            LOGGER.info("RECURSION-LEVEL-{}: Item #{} with a weight of {} lbs. does not fit in the remaining capacity [{}]", level, currentItem.getNumber(), currentItem.getWeight(), remainingCapacity);
            return pack(++level, remainingCapacity, currentIndex, items);
        }
        // Choose the maximum weight that can fit into the remaining capacity:
        // NOTE-1: The LEFT branch is the solution when we do not choose the item at the parent node
        // NOTE-2: The RIGHT branch is the solution when we already picked up the item at the parent node
        final Item leftBranch = pack(++level, remainingCapacity, currentIndex, items);
        final Item rightBranch = currentItem.addCost(pack(++level, remainingCapacity.subtract(currentItem.getWeight()), currentIndex, items));
        final Item maxItem = Item.max(leftBranch, rightBranch);
        LOGGER.info("RECURSION-LEVEL-{}: Item #{} with a cost of ${} fits into the knapsack!!!", level, currentItem.getNumber(), currentItem.getCost());
        return maxItem;
    }

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this, ToStringStyle.SHORT_PREFIX_STYLE);
    }
}