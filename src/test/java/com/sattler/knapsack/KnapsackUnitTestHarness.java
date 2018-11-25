package com.sattler.knapsack;

import static org.junit.Assert.assertArrayEquals;

import java.util.Arrays;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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
 * Input: Use the <code>KnapsackDataParser</code> parse the input data.
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
 * For each set of things produce a list of things (their index numbers separated by comma) that you put into the package. If none 
 * of the items will fit in the package, print a hyphen (-).
 * <p/>
 * Examples:
 * <ol>
 * <li>81 : (1,53.38,$45) (2,88.62,$98) (3,78.48,$3) (4,72.30,$76) (5,30.18,$9) (6,46.34,$48) ==> 4</li>
 * <li>75 : (1,85.31,$29) (2,14.55,$74) (3,3.98,$16) (4,26.24,$55) (5,63.69,$52) (6,76.25,$75) (7,60.02,$74) (8,93.18,$35) (9,89.95,$78) ==> (2,7)</li>
 * </ol>
 * 
 * @author Pete Sattler
 */
public class KnapsackUnitTestHarness {

    private static final Logger LOGGER = LoggerFactory.getLogger(KnapsackUnitTestHarness.class);

    @Test
    public void knapsackWholeItemPackerSingleItemTestCase() {
        final String unparsedData = "81 : (1,53.38,$45) (2,88.62,$98) (3,78.48,$3) (4,72.30,$76) (5,30.18,$9) (6,46.34,$48)";
        final int[] expected = new int[] { 4 };
        final int[] actual = knapsackImpl(unparsedData);
        assertArrayEquals(expected, actual);
    }

    @Test
    public void knapsackWholeItemPackerMultipleItemsTestCase() {
        final String unparsedData = "75 : (1,85.31,$29) (2,14.55,$74) (3,3.98,$16) (4,26.24,$55) (5,63.69,$52) (6,76.25,$75) (7,60.02,$74) (8,93.18,$35) (9,89.95,$78)";
        final int[] expected = new int[] { 2, 7 };
        final int[] actual = knapsackImpl(unparsedData);
        assertArrayEquals(expected, actual);
    }

    private int[] knapsackImpl(final String unparsedData) {
        final KnapsackDataParser parser = new KnapsackDataParser(unparsedData);
        final KnapsackParameter dataParm = parser.parse();
        final RetainMostExpensiveKnapsackImpl knapsack = new RetainMostExpensiveKnapsackImpl(dataParm.getCapacity());
        final KnapsackWholeItemRecursivePackerImpl knapsackPacker = new KnapsackWholeItemRecursivePackerImpl(knapsack);
        final Knapsack packedKnapsack = knapsackPacker.pack(dataParm.getItems());
        final int[] items = packedKnapsack.getItems();
        switch (items.length) {
            case 0:
                LOGGER.warn("Knapsack recursive algorithm did not find any items");
                break;
            case 1:
                LOGGER.info("Knapsack recursive algorithm selects item [{}]", items[0]);
                break;
            default:
                LOGGER.info("Knapsack recursive algorithm selects items {}", Arrays.toString(items));
        }
        return items;
    }
}
