package net.sattler22.knapsack.model;

import java.math.BigDecimal;

/**
 * An <code>Item</code> has both cost and weight
 *
 * @author Pete Sattler
 * @since December 2018
 * @version November 2025
 */
public record Item(int id, BigDecimal cost, BigDecimal weight) {

    private static final BigDecimal MAX_COST_USD = BigDecimal.valueOf(100L);

    /**
     * Constructs a new <code>Item</code>
     *
     * @param id A unique identifier
     * @param cost The cost (USD)
     * @param weight The weight (in pounds)
     */
    public Item {
        if (cost.compareTo(MAX_COST_USD) > 0)
            throw new IllegalArgumentException("Maximum cost exceeded");
        if (weight.compareTo(BigDecimal.ZERO) <= 0)
            throw new IllegalArgumentException("Invalid weight");
    }
}
