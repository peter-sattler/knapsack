package net.sattler22.knapsack.core;

import net.jcip.annotations.ThreadSafe;
import net.sattler22.knapsack.model.Inventory;
import net.sattler22.knapsack.model.Item;
import net.sattler22.knapsack.model.Package;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.Function;

/**
 * The Whole Item Recursive Packer selects and packs items using direct recursion (no memoization):
 * <ol>
 * <li>Breaks the packing down into smaller, simpler subproblems by repeated calls to itself.</li>
 * <li>Every time the function is called, it will either include the current item or exclude it.</li>
 * <li>This solution is inefficient for larger inventories due to overlapping subproblems.</li>
 * <li>The time complexity of this solution is O(2^n) due to the exhaustive nature of the recursion.</li>
 * </ol>
 *
 * @author Pete Sattler
 * @since November 2018
 * @version November 2025
 */
@ThreadSafe
public record WholeItemRecursivePacker(Inventory inventory) implements Packer {

    @Override
    public void pack(Package targetPackage) {
        if (targetPackage == null)
            throw new IllegalArgumentException("Target package is required");
        if (!targetPackage.isEmpty())
            throw new IllegalStateException("Target package has already been packed");
        for (final Item chosenItem : solve(inventory.items(), targetPackage.capacity(), inventory.items().size()))
            if (!targetPackage.add(chosenItem))
                throw new IllegalStateException("Could not pack %s".formatted(chosenItem));
    }

    private static List<Item> solve(List<Item> items, BigDecimal capacity, int nbrItems) {
        //No items (base case:):
        if (nbrItems == 0 || capacity.compareTo(BigDecimal.ZERO) == 0)
            return Collections.emptyList();

        //Item is too heavy:
        final Item currentItem = items.get(nbrItems - 1);
        if (currentItem.weight().compareTo(capacity) > 0)
            return solve(items, capacity, nbrItems - 1);

        //Include branch:
        final List<Item> includeItems = solve(items, capacity.subtract(currentItem.weight()), nbrItems - 1);
        final BigDecimal includeCost = currentItem.cost().add(sum(includeItems, Item::cost));
        final BigDecimal includeWeight = currentItem.weight().add(sum(includeItems, Item::weight));

        //Exclude branch:
        final List<Item> excludeItems = solve(items, capacity, nbrItems - 1);
        final BigDecimal excludeCost = sum(excludeItems, Item::cost);
        final BigDecimal excludeWeight = sum(excludeItems, Item::weight);

        //Choose branch with the highest cost:
        //NOTE: If cost is the same, then choose the lighter item!!!
        if (includeCost.compareTo(excludeCost) > 0 ||
                (includeCost.compareTo(excludeCost) == 0 && includeWeight.compareTo(excludeWeight) <= 0)) {
            final List<Item> chosenItems = new ArrayList<>(includeItems);
            chosenItems.add(currentItem);
            return chosenItems;
        }
        return excludeItems;
    }

    private static BigDecimal sum(List<Item> items, Function<Item, BigDecimal> mapper) {
        return items.stream()
                .map(mapper)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }
}
