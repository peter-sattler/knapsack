package net.sattler22.knapsack;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.ArrayList;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * A immutable knapsack which retains items as long as there is enough room
 * 
 * @author Pete Sattler
 * @version December 2018
 */
public final class KnapsackDefaultImpl extends KnapsackBaseImpl implements Serializable {

    private static final long serialVersionUID = -9114309537208209855L;
    private static final Logger LOGGER = LoggerFactory.getLogger(KnapsackDefaultImpl.class);

    /**
     * Constructs a new knapsack
     * 
     * @param capacity The maximum weight (in pounds) that the knapsack can hold
     */
    public KnapsackDefaultImpl(BigDecimal capacity) {
        super(capacity, new ArrayList<>());
    }

    @Override
    protected boolean addImpl(Item newItem) {
        if (hasEnoughCapacity(newItem) && items.add(newItem)) {
            LOGGER.info("Added new {}", newItem);
            return true;
        }
        return false;
    }
}
