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
     * Pack the knapsack
     * 
     * @param items The items to pack
     * @return A fully packed knapsack
     */
    Knapsack pack(Item[] items);
}
