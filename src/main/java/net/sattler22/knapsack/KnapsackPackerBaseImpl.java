package net.sattler22.knapsack;

import java.util.Arrays;

import net.sattler22.knapsack.Knapsack.Item;

/**
 * Knapsack packer base implementation
 *
 * @implSpec All sub-classes are required to be thread-safe
 * @author Pete Sattler
 * @version November 2018
 */
public abstract sealed class KnapsackPackerBaseImpl
    implements KnapsackPacker permits KnapsackPackerWholeItemBranchAndBoundImpl, KnapsackPackerWholeItemRecursiveImpl {

    protected final Knapsack knapsack;
    protected final Item[] items;

    /**
     * Constructs a new knapsack packer
     *
     * @param knapsack The knapsack to pack
     * @param items The items to pack
     */
    protected KnapsackPackerBaseImpl(Knapsack knapsack, Item[] items) {
        this.knapsack = knapsack;
        this.items = Arrays.copyOf(items, items.length);
    }

    @Override
    public Knapsack knapsack() {
        return knapsack;
    }

    @Override
    public Item[] items() {
        return Arrays.copyOf(items, items.length);
    }

    @Override
    public String toString() {
        return String.format("%s [knapsack=%s, items=%s]", getClass().getSimpleName(), knapsack, Arrays.toString(items));
    }
}
