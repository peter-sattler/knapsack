package com.sattler.knapsack;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.regex.Pattern;

import com.sattler.knapsack.Knapsack.Item;

/**
 * Knapsack Data Parser
 * <p/>
 * Parses knapsack data, each line formatted as follows:
 * <ul>
 * <li>The maximum weight (in pounds) that the knapsack can hold (before the colon)</li>
 * <li>The list of items you need to pick from. Each item is enclosed in parentheses where:
 * <ol>
 * <li>Item's unique identifier</li>
 * <li>Its weight (in lbs.)</li>
 * <li>Its cost (in USD)</li>
 * </ol>
 * </ul>
 * 
 * @author Pete Sattler
 */
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
        List<Item> itemList = new ArrayList<>();
        final String[] items = ITEM_LIST_PATTERN.split(unparsedItemData);
        for (String item : items) {
            final String[] splitData = ITEM_COMPONENT_PATTERN.split(item.replaceAll(ITEM_LIST_REPLACEMENT_CHARS, ""));
            if (splitData.length != 3)
                throw new IllegalArgumentException("Item components have incorrect format");
            final int id = Integer.parseInt(splitData[0]);
            final BigDecimal weight = new BigDecimal(splitData[1]);
            final int cost = Integer.parseInt(splitData[2]);
            itemList.add(new Item(id, weight, cost));
        }
        return new KnapsackParameter(capacity, itemList);
    }

    /**
     * Represents an immutable knapsack parameter
     */
    static final class KnapsackParameter implements Serializable {

        private static final long serialVersionUID = -3570309058766180832L;
        private final BigDecimal capacity;
        private final Item[] items;

        public KnapsackParameter(BigDecimal capacity, List<Item> items) {
            this.capacity = capacity;
            this.items = items.toArray(new Item[items.size()]);
        }

        public BigDecimal getCapacity() {
            return capacity;
        }

        public Item[] getItems() {
            return Arrays.copyOf(items, items.length);
        }

        @Override
        public String toString() {
            return String.format("%s [capacity=%s, items=%s]", getClass().getSimpleName(), capacity, Arrays.toString(items));
        }
    }
}
