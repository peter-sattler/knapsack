package net.sattler22.knapsack;

import net.sattler22.knapsack.Knapsack.Item;
import net.sattler22.knapsack.KnapsackDataParser.KnapsackParameter;
import org.hamcrest.MatcherAssert;
import org.hamcrest.collection.IsIterableContainingInAnyOrder;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 * Barclay's Programming Challenge 2018
 * <ol>
 * <li>You want to send your friend a package with different things.</li>
 * <li>Each thing you put in the package has such parameters as index number, weight and cost.</li>
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
 * <li>Max weight and max cost of anything is <= 100.</li>
 * </ol>
 * <p/>
 * Output:
 * <p/>
 * For each set of things produce a list of things (their index numbers separated by comma) that you put into the package.
 *
 * @author Pete Sattler
 * @version November 2025
 * @since December 2018
 */
final class KnapsackTest {

    private static final Logger logger = LoggerFactory.getLogger(KnapsackTest.class);
    private static final Class<?>[] knapsackPackerImpls = new Class[] {
            KnapsackPackerWholeItemRecursiveImpl.class,
            KnapsackPackerWholeItemBranchAndBoundImpl.class
    };

    @Test
    void knapsackWholeItemPackerGeeksForGeeksWebsiteTestCase() {
        final String data = "10 : (1,2,$40) (2,3.14,$50) (3,1.98,$100) (4,5,$95) (5,3,$30)";
        checkAssertionsImpl(data, new Integer[] { 1, 3, 4 });
    }

    @Test
    void knapsackWholeItemPackerTechiemeInWebsiteTestCase() {
        final String data = "7 : (1,2,$1) (2,3,$2) (3,3,$5) (4,4,$9)";
        checkAssertionsImpl(data, new Integer[] { 3, 4 });
    }

    @Test
    void knapsackWholeItemPackerNoItemTestCase() {
        final String data = "8 : (1,15.3,$34)";
        checkAssertionsImpl(data, new Integer[] {});
    }

    @Test
    void knapsackWholeItemPackerTwoMostExpensiveHaveDifferentWeightsTestCase() {
        final String data = """
            56 : (1,90.72,$13) (2,33.80,$40) (3,43.15,$10) (4,37.97,$16) (5,46.81,$36) (6,48.77,$79) (7,81.80,$45) (8,19.36,$79) \
            (9,6.76,$64)""";
        assertThrows(IllegalStateException.class, () ->
            checkAssertionsImpl(data, null)
        );
    }

    @Test
    void knapsackWholeItemPackerSingleItemTestCase() {
        final String data = "81 : (1,53.38,$45) (2,88.62,$98) (3,78.48,$3) (4,72.30,$76) (5,30.18,$9) (6,46.34,$48)";
        checkAssertionsImpl(data, new Integer[] { 4 });
    }

    @Test
    void knapsackWholeItemPackerMultipleItemsTestCase() {
        final String data = """
            75 : (1,85.31,$29) (2,14.55,$74) (3,3.98,$16) (4,26.24,$55) (5,63.69,$52) (6,76.25,$75) (7,60.02,$90) (8,93.18,$35) \
            (9,89.95,$78)""";
        checkAssertionsImpl(data, new Integer[] { 2, 7 });
    }

    private static void checkAssertionsImpl(String data, Integer[] expected) {
        final KnapsackDataParser parser = new KnapsackDataParser(data);
        final KnapsackParameter knapsackParameter = parser.parse();
        for (final Class<?> knapsackPackerImpl : knapsackPackerImpls)
            try {
                final Constructor<?> knapsackPackerConstructor =
                        knapsackPackerImpl.getDeclaredConstructor(Knapsack.class, Item[].class);
                final Knapsack knapsack = new KnapsackDefaultImpl(knapsackParameter.capacity());
                final KnapsackPacker knapsackPacker =
                        (KnapsackPacker) knapsackPackerConstructor.newInstance(knapsack, knapsackParameter.items());
                final List<Integer> actual = executeKnapsackImpl(knapsack, knapsackPacker);
                MatcherAssert.assertThat(actual, IsIterableContainingInAnyOrder.containsInAnyOrder(expected));
            }
            catch (NoSuchMethodException | InstantiationException | IllegalAccessException  | InvocationTargetException e) {
                throw new IllegalStateException(e);
            }
    }

    private static List<Integer> executeKnapsackImpl(Knapsack knapsack, KnapsackPacker knapsackPacker) {
        logger.info("Knapsack capacity: {} lbs.", knapsack.capacity());
        logger.info("Knapsack packer: {}", knapsackPacker.getClass().getSimpleName());
        for (final Item item : knapsackPacker.items())
            logger.info("Incoming item #{} weighs {} lbs. and costs ${}", item.id(), item.weight(), item.cost());
        knapsackPacker.pack();
        final int[] packedItems = knapsackPacker.knapsack().items();
        switch (packedItems.length) {
            case 0 -> logger.warn("Knapsack packer did not find any items");
            case 1 -> logger.info("Knapsack packer selects item {}", packedItems[0]);
            default -> logger.info("Knapsack packer selects items {}", Arrays.toString(packedItems));
        }
        return Arrays.stream(packedItems)
                .boxed()
                .toList();
    }
}
