package com.sattler.knapsack;

import static org.junit.Assert.assertEquals;

import java.math.BigDecimal;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sattler.knapsack.Knapsack.Item;

/**
 * Knapsack algorithm unit test harness
 * 
 * @author Pete Sattler
 */
public class KnapsackUnitTestHarness {

    private static final Logger LOGGER = LoggerFactory.getLogger(KnapsackUnitTestHarness.class);

    @Test
    public void testKnapsackZeroOneRecursiveImpl() {
        final String unparsedData = "81 : (1,53.38,$45) (2,88.62,$98) (3,78.48,$3) (4,72.30,$76) (5,30.18,$9) (6,46.34,$48)";
        final Item expected = new Item(4, new BigDecimal("72.30"), 76);
        final KnapsackZeroOneRecursiveImpl knapsackZeroOneRecursiveImpl = new KnapsackZeroOneRecursiveImpl(unparsedData);
        final Item actual = knapsackZeroOneRecursiveImpl.pack();
        LOGGER.info("Knapsack recursive algorithm selects item #{} that weighs {} lbs. and costs ${}", actual.getNumber(), actual.getWeight(), actual.getCost());
        assertEquals(expected, actual);
    }
}
