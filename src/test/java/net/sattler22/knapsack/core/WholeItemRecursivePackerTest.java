package net.sattler22.knapsack.core;

import net.sattler22.knapsack.model.Inventory;
import net.sattler22.knapsack.model.Package;
import net.sattler22.knapsack.test.util.FileTestUtils;
import net.sattler22.knapsack.test.util.TestData;
import org.hamcrest.MatcherAssert;
import org.hamcrest.collection.IsIterableContainingInAnyOrder;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import tools.jackson.databind.ObjectReader;
import tools.jackson.databind.json.JsonMapper;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.List;

/**
 * Whole Item Recursive Packer Unit Tests
 *
 * @author Pete Sattler
 * @since December 2018
 * @version November 2025
 */
final class WholeItemRecursivePackerTest {

    private static final Logger logger = LoggerFactory.getLogger(WholeItemRecursivePackerTest.class);
    private static final ObjectReader jsonReader = new JsonMapper().readerFor(TestData.class);

    @Test
    void pack_whenTwoItemsFitExactly_thenReturnThem() {
        packAssert("/01-two-items-fit-exactly.json");
    }

    @Test
    void pack_whenOneItemDoesNotFit_thenReturnEmpty() {
        packAssert("/02-one-item-does-not-fit.json");
    }

    @Test
    void pack_whenMoreExpensiveItemIsTooHeavy_thenReturnLessExpensiveOne() {
        packAssert("/03-more-expensive-item-too-heavy.json");
    }

    @Test
    void pack_whenTwoItemsSamePrice_thenReturnLighterOne() {
        packAssert("/04-same-price-less-weight.json");
    }

    @Test
    void pack_whenTwoPossibleCombinations_thenReturnMostExpensiveOne() {
        packAssert("/05-most-expensive-combo.json");
    }

    @Test
    void pack_whenHigherCostButOverWeight_thenReturnLowerCostButUnderWeight() {
        packAssert("/06-lower-cost-under-weight.json");
    }

    private static void packAssert(String resource) {
        final TestData testData;
        try {
            testData = jsonReader.readValue(FileTestUtils.readResourceAsString(resource));
        }
        catch (IOException | URISyntaxException exception) {
            throw new IllegalStateException(exception);
        }
        final Package actual = pack(testData);
        MatcherAssert.assertThat(actual.ids(), IsIterableContainingInAnyOrder.containsInAnyOrder(toArray(testData.solution())));
    }

    private static Package pack(TestData testData) {
        logger.info("Friend: {}", testData.name());
        logger.info("Capacity: {} lbs.", testData.capacity());
        final Package targetPackage = new Package(testData.capacity());
        final Inventory inventory = new Inventory(testData.items());
        logger.info("{}", inventory);
        final Packer packer = new WholeItemRecursivePacker(inventory);
        packer.pack(targetPackage);
        if (targetPackage.isEmpty())
            logger.info("No gift for you!!!");  //S7 E6
        else
            logger.info("Package contains {} with a total cost of ${} and total weight of {} lbs.",
                    targetPackage.items(), targetPackage.totalCost(), targetPackage.totalWeight());
        return targetPackage;
    }

    private static Integer[] toArray(List<Integer> source) {
        return source.toArray(new Integer[0]);
    }
}
