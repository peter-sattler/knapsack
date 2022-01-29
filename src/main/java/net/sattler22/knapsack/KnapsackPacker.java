package net.sattler22.knapsack;

import net.sattler22.knapsack.Knapsack.Item;

/**
 * A knapsack item packer
 *
 * @author Pete Sattler
 * @version November 2018
 */
public sealed interface KnapsackPacker permits KnapsackPackerBaseImpl {

    /**
     * Get knapsack
     *
     * @return Either the packed knapsack or an empty one
     */
    Knapsack knapsack();

    /**
     * Get items
     *
     * @return The items to pack
     */
    Item[] items();

    /**
     * Pack the knapsack
     */
    void pack();
}
