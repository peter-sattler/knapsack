package net.sattler22.knapsack.core;

import net.sattler22.knapsack.model.Package;

/**
 * A <code>Packer</code> selects zero or more items from inventory and adds them to the target package considering
 * cost, weight and overall capacity
 *
 * @author Pete Sattler
 * @since November 2018
 * @version November 2025
 */
public sealed interface Packer permits WholeItemRecursivePacker {

    /**
     * Select and pack the items
     *
     * @param targetPackage The target {@link Package}
     */
    void pack(Package targetPackage);
}
