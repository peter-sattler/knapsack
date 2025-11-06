package net.sattler22.knapsack.test.util;

import net.sattler22.knapsack.model.Item;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;

/**
 * Test Data Container
 *
 * @author Pete Sattler
 * @version November 2025
 */
public record TestData(String name, String original, BigDecimal capacity, List<Item> items, List<Integer> solution) {

    public TestData(String name, String original, BigDecimal capacity, List<Item> items, List<Integer> solution) {
        this.name = name;
        this.original = original;
        this.capacity = capacity;
        this.items = items != null ? List.copyOf(items) : Collections.emptyList();  //Defensive copy
        this.solution = solution != null ? List.copyOf(solution) : Collections.emptyList();  //Defensive copy
    }

    @Override
    public List<Item> items() {
        return !items.isEmpty() ? List.copyOf(items) : items;  //Defensive copy
    }

    @Override
    public List<Integer> solution() {
        return !solution.isEmpty() ? List.copyOf(solution) : solution;  //Defensive copy
    }
}
