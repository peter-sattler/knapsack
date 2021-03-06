package net.sattler22.knapsack;

import java.util.Arrays;

import net.sattler22.knapsack.Knapsack.Item;

/**
 * Knapsack base packer implementation
 * 
 * @author Pete Sattler
 * @version November 2018
 */
public abstract class KnapsackBasePackerImpl implements KnapsackPacker {

    protected final Knapsack knapsack;
    protected final Item[] items;

    /**
     * Constructs a new knapsack packer
     * 
     * @param knapsack The knapsack to pack
     * @param items The items to pack
     */
    protected KnapsackBasePackerImpl(Knapsack knapsack, Item[] items) {
        this.knapsack = knapsack;
        this.items = Arrays.copyOf(items, items.length);
    }

    @Override
    public Knapsack getKnapsack() {
        return knapsack;
    }

    @Override
    public Item[] getItems() {
        return Arrays.copyOf(items, items.length);
    }

    @Override
    public String toString() {
        return String.format("%s [knapsack=%s, items=%s]", getClass().getSimpleName(), knapsack, Arrays.toString(items));
    }
}
