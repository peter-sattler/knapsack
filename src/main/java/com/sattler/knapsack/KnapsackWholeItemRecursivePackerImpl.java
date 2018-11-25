package com.sattler.knapsack;

import static com.sattler.knapsack.Knapsack.Item.ZERO_ITEM;

import java.io.Serializable;
import java.math.BigDecimal;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sattler.knapsack.Knapsack.Item;

/**
 * Knapsack whole item packer implemented using simple, naive recursion. It does not allow fractional items to be 
 * packed. That is, an item is either packed or it is left out.
 * 
 * @author Pete Sattler
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
        final BigDecimal capacity = knapsack.getCapacity();
        LOGGER.info("Knapsack capacity: {} lbs.", capacity);
        for (Item item : items)
            LOGGER.info("Incoming item #{} weighs {} lbs. and costs ${}", item.getId(), item.getWeight(), item.getCost());
        packImpl(0, items, capacity, 0, 0);
        return knapsack;
    }

    private Item packImpl(int recursionLevel, Item[] items, BigDecimal remainingCapacity, int totalCost, int itemNbr) {
        if (itemNbr == items.length) {
            // Recursion level complete:
            return ZERO_ITEM;
        }
        final Item currentItem = items[itemNbr];
        if (remainingCapacity == BigDecimal.ZERO) {
            return currentItem;
        }
        if (currentItem.getWeight().compareTo(remainingCapacity) > 0) {
            LOGGER.info("[RECURSION-LEVEL={}]: {} does not fit into the remaining capacity of [{}] lbs.", recursionLevel, currentItem, remainingCapacity);
            return packImpl(++recursionLevel, items, remainingCapacity, totalCost, itemNbr + 1);
        }
        //Choose the most expensive item that fits into the remaining capacity:
        //NOTE-1: The LEFT branch is the solution where the item is not chosen
        //NOTE-2: The RIGHT branch is the solution where the item has already been chosen
        final Item leftBranch = packImpl(++recursionLevel, items, remainingCapacity, totalCost, itemNbr + 1);
        final Item rightBranch = ZERO_ITEM;
        final Item maxItem = Item.findMostExpensive(leftBranch, rightBranch);
        if(knapsack.add(maxItem)) {
            LOGGER.info("[RECURSION-LEVEL={}]: {} fits into the knapsack!!!", recursionLevel, maxItem);
        }
        return maxItem;
    }

    @Override
    public String toString() {
        return String.format("%s [knapsack=%s]", getClass().getSimpleName(), knapsack);
    }
}
