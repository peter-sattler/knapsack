package net.sattler22.knapsack;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.collection.IsIterableContainingInAnyOrder.containsInAnyOrder;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.lang.reflect.InvocationTargetException;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.sattler22.knapsack.Knapsack.Item;

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
 * @version December 2018
 */
final class KnapsackUnitTest {

    private static final Logger logger = LoggerFactory.getLogger(KnapsackUnitTest.class);
    private static final Class<?>[] knapsackPackerImpls = new Class[] {
            KnapsackPackerWholeItemRecursiveImpl.class,
            KnapsackPackerWholeItemBranchAndBoundImpl.class
    };

    @Test
    void knapsackWholeItemPackerGeeksForGeeksWebsiteTestCase() {
        final var unparsedData = "10 : (1,2,$40) (2,3.14,$50) (3,1.98,$100) (4,5,$95) (5,3,$30)";
        checkAssertionsImpl(unparsedData, new Integer[] { 1, 3, 4 });
    }

    @Test
    void knapsackWholeItemPackerTechiemeInWebsiteTestCase() {
        final var unparsedData = "7 : (1,2,$1) (2,3,$2) (3,3,$5) (4,4,$9)";
        checkAssertionsImpl(unparsedData, new Integer[] { 3, 4 });
    }

    @Test
    void knapsackWholeItemPackerNoItemTestCase() {
        final var unparsedData = "8 : (1,15.3,$34)";
        checkAssertionsImpl(unparsedData, new Integer[] {});
    }

    @Test
    void knapsackWholeItemPackerTwoMostExpensiveHaveDifferentWeightsTestCase() {
        final var unparsedData = """
            56 : (1,90.72,$13) (2,33.80,$40) (3,43.15,$10) (4,37.97,$16) (5,46.81,$36) (6,48.77,$79) (7,81.80,$45) (8,19.36,$79) \
            (9,6.76,$64)""";
        assertThrows(IllegalStateException.class, () -> {
            checkAssertionsImpl(unparsedData, null);
        });
    }

    @Test
    void knapsackWholeItemPackerSingleItemTestCase() {
        final var unparsedData = "81 : (1,53.38,$45) (2,88.62,$98) (3,78.48,$3) (4,72.30,$76) (5,30.18,$9) (6,46.34,$48)";
        checkAssertionsImpl(unparsedData, new Integer[] { 4 });
    }

    @Test
    void knapsackWholeItemPackerMultipleItemsTestCase() {
        final var unparsedData = """
            75 : (1,85.31,$29) (2,14.55,$74) (3,3.98,$16) (4,26.24,$55) (5,63.69,$52) (6,76.25,$75) (7,60.02,$90) (8,93.18,$35) \
            (9,89.95,$78)""";
        checkAssertionsImpl(unparsedData, new Integer[] { 2, 7 });
    }

    private static void checkAssertionsImpl(String unparsedData, Integer[] expected) {
        final var parser = new KnapsackDataParser(unparsedData);
        final var knapsackParameter = parser.parse();
        for (final var knapsackPackerImpl : knapsackPackerImpls)
            try {
                final var knapsackPackerCtor = knapsackPackerImpl.getDeclaredConstructor(new Class[] { Knapsack.class, Item[].class });
                final var knapsack = new KnapsackDefaultImpl(knapsackParameter.capacity());
                final var knapsackPacker = (KnapsackPacker) knapsackPackerCtor.newInstance(knapsack, knapsackParameter.items());
                final var actual = executeKnapsackImpl(knapsack, knapsackParameter.items(), knapsackPacker);
                assertThat(actual, containsInAnyOrder(expected));
            }
            catch (NoSuchMethodException | InstantiationException | IllegalAccessException  | InvocationTargetException e) {
                throw new IllegalStateException(e);
            }
    }

    private static List<Integer> executeKnapsackImpl(Knapsack knapsack, Item[] items, KnapsackPacker knapsackPacker) {
        logger.info("Knapsack capacity: {} lbs.", knapsack.capacity());
        logger.info("Knapsack packer: {}", knapsackPacker.getClass().getSimpleName());
        for (final var item : knapsackPacker.items())
            logger.info("Incoming item #{} weighs {} lbs. and costs ${}", item.id(), item.weight(), item.cost());
        knapsackPacker.pack();
        final var packedItems = knapsackPacker.knapsack().items();
        switch (packedItems.length) {
            case 0 -> logger.warn("Knapsack packer did not find any items");
            case 1 -> logger.info(String.format("Knapsack packer selects item [%s]", packedItems[0]));
            default -> logger.info(String.format("Knapsack packer selects items %s", Arrays.toString(packedItems)));
        }
        return Arrays.stream(packedItems).boxed().collect(Collectors.toList());
    }
}
