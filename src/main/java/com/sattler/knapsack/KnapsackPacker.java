package com.sattler.knapsack;

import com.sattler.knapsack.Knapsack.Item;

/**
 * A knapsack item packer
 * 
 * @author Pete Sattler
 * @version November 2018
 */
public interface KnapsackPacker {

    /**
     * Get knapsack
     * 
     * @return Either the packed knapsack or an empty one
     */
    Knapsack getKnapsack();

    /**
     * Get items
     * 
     * @return The items to pack
     */
    Item[] getItems();

    /**
     * Pack the knapsack
     */
    void pack();
}
