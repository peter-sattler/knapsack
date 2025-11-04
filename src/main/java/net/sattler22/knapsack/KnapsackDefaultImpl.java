package net.sattler22.knapsack;

import net.jcip.annotations.Immutable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigDecimal;
import java.util.ArrayList;

/**
 * A knapsack which retains items as long as there is enough room
 *
 * @author Pete Sattler
 * @version December 2018
 */
@Immutable
public final class KnapsackDefaultImpl extends KnapsackBaseImpl {

    private static final Logger logger = LoggerFactory.getLogger(KnapsackDefaultImpl.class);

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
            logger.info("Added new {}", newItem);
            return true;
        }
        return false;
    }
}
