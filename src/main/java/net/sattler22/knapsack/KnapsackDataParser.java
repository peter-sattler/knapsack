package net.sattler22.knapsack;

import net.jcip.annotations.Immutable;
import net.sattler22.knapsack.Knapsack.Item;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.regex.Pattern;

/**
 * Knapsack Data Parser
 * <p/>
 * Parses knapsack data, each line formatted as follows:
 * <ul>
 * <li>The maximum weight (in pounds) that the knapsack can hold (before the colon)</li>
 * <li>The list of items you need to pick from. Each item is enclosed in parentheses where:
 * <ol>
 * <li>Item's unique identifier</li>
 * <li>Its weight (in US pounds)</li>
 * <li>Its cost (in USD)</li>
 * </ol>
 * </ul>
 * <b>LIMITATION:</b> Having multiple items with the same cost, but different weights may lead to different implementation specific
 * solutions, so it is therefore considered unsupported due to this ambiguity.
 *
 * @author Pete Sattler
 * @version December 2018
 */
@Immutable
public final class KnapsackDataParser {

    private static final Pattern KNAPSACK_CAPACITY_PATTERN = Pattern.compile("\\s*:\\s*");
    private static final Pattern ITEM_LIST_PATTERN = Pattern.compile(" \\s*");
    private static final String ITEM_LIST_REPLACEMENT_CHARS = "[()$]";
    private static final Pattern ITEM_COMPONENT_PATTERN = Pattern.compile("\\s*,\\s*");
    private final String unparsedData;

    /**
     * Constructs a new knapsack data parser
     *
     * @param unparsedData The unparsed data
     */
    public KnapsackDataParser(String unparsedData) {
        this.unparsedData = Objects.requireNonNull(unparsedData).trim();
    }

    /**
     * Parse the data
     *
     * @return The knapsack's capacity and list of possible items to pack
     */
    public KnapsackParameter parse() {
        final String[] splitData = KNAPSACK_CAPACITY_PATTERN.split(unparsedData);
        if (splitData.length != 2)
            throw new IllegalArgumentException("Input data has incorrect format");
        return parseItems(new BigDecimal(splitData[0]), splitData[1]);
    }

    private static KnapsackParameter parseItems(BigDecimal capacity, String unparsedItemData) {
        final List<Item> itemList = new ArrayList<>();
        final Map<Integer, BigDecimal> itemsWithSameCostDiffWeights = new HashMap<>();
        for (final String item : ITEM_LIST_PATTERN.split(unparsedItemData)) {
            final String[] splitData = ITEM_COMPONENT_PATTERN.split(item.replaceAll(ITEM_LIST_REPLACEMENT_CHARS, ""));
            if (splitData.length != 3)
                throw new IllegalArgumentException("Item components have incorrect format");
            final int id = Integer.parseInt(splitData[0]);
            final BigDecimal weight = new BigDecimal(splitData[1]);
            final Integer cost = Integer.valueOf(splitData[2]);
            if (itemsWithSameCostDiffWeights.containsKey(cost))
                throw new IllegalStateException(String.format("Found multiple items which cost $%s, but have different weights which is an unsupported ambiguity", cost));
            itemsWithSameCostDiffWeights.put(cost, weight);
            itemList.add(new Item(id, weight, cost));
        }
        final Item[] itemArray = itemList.toArray(new Item[0]);
        return new KnapsackParameter(capacity, itemArray);
    }

    /**
     * Knapsack parameter
     */
    record KnapsackParameter(BigDecimal capacity, Item[] items) {

        @Override
        public int hashCode() {
            return Objects.hashCode(capacity) + Arrays.hashCode(items);
        }

        @Override
        public boolean equals(Object other) {
            if (this == other)
                return true;
            if (other == null)
                return false;
            if (this.getClass() != other.getClass())
                return false;
            final KnapsackParameter that = (KnapsackParameter) other;
            return Objects.equals(this.capacity, that.capacity) && Arrays.equals(this.items, that.items);
        }

        @Override
        public String toString() {
            return String.format("%s [capacity=%s, items=%s]", getClass().getSimpleName(), capacity, Arrays.toString(items));
        }
    }
}
