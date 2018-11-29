package com.sattler.knapsack;

import static org.hamcrest.collection.IsIterableContainingInAnyOrder.containsInAnyOrder;
import static org.junit.Assert.assertThat;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sattler.knapsack.Knapsack.Item;
import com.sattler.knapsack.KnapsackDataParser.KnapsackParameter;

/**
 * Barclay's Programming Challenge 2018
 * <ol>
 * <li>You want to send your friend a package with different things.</li>
 * <li>Each thing you put inside of a package has such parameters as index number, weight and cost.</li>
 * <li>The package has a weight limitation.</li>
 * </ol>
 * <p/>
 * Your goal is to determine which things to put into the package so that the total weight is less than or equal to the package
 * limit and the total cost is as large as possible.
 * <p/>
 * You would prefer to send a package which has less weight if there is more than one package with the same price.
 * <p/>
 * This is a variation of the Knapsack problem.
 * <p/>
 * Constraints:
 * <ol>
 * <li>Max weight any package can take is <= 100.</li>
 * <li>There might be up to 15 things you need to choose from.</li>
 * <li>Max weight and max cost of any thing is <= 100.</li>
 * </ol>
 * <p/>
 * Output:
 * <p/>
 * For each set of things produce a list of things (their index numbers separated by comma) that you put into the package.
 * 
 * @author Pete Sattler
 * @version November 2018
 */
public final class KnapsackUnitTestHarness {

    private static final Logger LOGGER = LoggerFactory.getLogger(KnapsackUnitTestHarness.class);
    private static final Class<?>[] KNAPSACK_PACKER_IMPLS = new Class[] { KnapsackWholeItemRecursivePackerImpl.class };

    @Test
    public void knapsackWholeItemPackerWebsiteTestCase() {
        final String unparsedData = "7 : (1,2,$1) (2,3,$2) (3,3,$5) (4,4,$9)";
        checkAssertionsImpl(unparsedData, new Integer[] { 3, 4 });
    }

    @Test
    public void knapsackWholeItemPackerNoItemTestCase() {
        final String unparsedData = "8 : (1,15.3,$34)";
        checkAssertionsImpl(unparsedData, new Integer[] {});
    }

    @Test
    public void knapsackWholeItemPackerTwoMostExpensiveHaveDifferentWeightsTestCase() {
        final String unparsedData = "56 : (1,90.72,$13) (2,33.80,$40) (3,43.15,$10) (4,37.97,$16) (5,46.81,$36) (6,48.77,$79) (7,81.80,$45) (8,19.36,$79) (9,6.76,$64)";
        checkAssertionsImpl(unparsedData, new Integer[] { 6 });
    }

    @Test
    public void knapsackWholeItemPackerSingleItemTestCase() {
        final String unparsedData = "81 : (1,53.38,$45) (2,88.62,$98) (3,78.48,$3) (4,72.30,$76) (5,30.18,$9) (6,46.34,$48)";
        checkAssertionsImpl(unparsedData, new Integer[] { 4 });
    }

    @Test
    public void knapsackWholeItemPackerMultipleItemsTestCase() {
        final String unparsedData = "75 : (1,85.31,$29) (2,14.55,$74) (3,3.98,$16) (4,26.24,$55) (5,63.69,$52) (6,76.25,$75) (7,60.02,$74) (8,93.18,$35) (9,89.95,$78)";
        checkAssertionsImpl(unparsedData, new Integer[] { 2, 7 });
    }

    private static void checkAssertionsImpl(String unparsedData, Integer[] expected) {
        final KnapsackDataParser parser = new KnapsackDataParser(unparsedData);
        final KnapsackParameter knapsackParameter = parser.parse();
        final BigDecimal capacity = knapsackParameter.getCapacity();
        final Item[] items = knapsackParameter.getItems();
        for (Class<?> knapsackPackerImpl : KNAPSACK_PACKER_IMPLS) {
            final Knapsack knapsack = new KnapsackRetainMostExpensiveImpl(capacity);
            try {
                final Constructor<?> knapsackPackerCtor = knapsackPackerImpl.getDeclaredConstructor(new Class[] { Knapsack.class, Item[].class });
                final KnapsackPacker knapsackPacker = (KnapsackPacker) knapsackPackerCtor.newInstance(knapsack, items);
                final List<Integer> actual = executeKnapsackImpl(knapsack, items, knapsackPacker);
                assertThat(actual, containsInAnyOrder(expected));
            }
            catch (NoSuchMethodException | SecurityException | InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
                throw new IllegalStateException(e);
            }
        }
    }

    private static List<Integer> executeKnapsackImpl(Knapsack knapsack, Item[] items, KnapsackPacker knapsackPacker) {
        LOGGER.info("Knapsack capacity: {} lbs.", knapsack.getCapacity());
        LOGGER.info("Knapsack packer: {}", knapsackPacker.getClass().getSimpleName());
        for (Item item : knapsackPacker.getItems())
            LOGGER.info("Incoming item #{} weighs {} lbs. and costs ${}", item.getId(), item.getWeight(), item.getCost());
        knapsackPacker.pack();
        final int[] packedItems = knapsackPacker.getKnapsack().getItems();
        switch (packedItems.length) {
            case 0:
                LOGGER.warn("Knapsack packer did not find any items");
                break;
            case 1:
                LOGGER.info(String.format("Knapsack packer selects item [%s]", packedItems[0]));
                break;
            default:
                LOGGER.info(String.format("Knapsack packer selects items %s", Arrays.toString(packedItems)));
        }
        return Arrays.stream(packedItems).boxed().collect(Collectors.toList());
    }
}
