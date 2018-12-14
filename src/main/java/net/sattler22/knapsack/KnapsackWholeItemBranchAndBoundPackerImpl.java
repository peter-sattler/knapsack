package net.sattler22.knapsack;

import java.io.Serializable;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.PriorityQueue;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.sattler22.knapsack.Knapsack.Item;

/**
 * Knapsack whole item packer implemented using branch and bound. It does not allow
 * fractional items to be packed. That is, an item is either packed or it is left
 * out.
 * <p/>
 * Since Dynamic Programming (DP) doesn’t work unless the item weights are integers,
 * a solution is to use Brute Force. With n items, there are 2^n solutions to be
 * generated, check each to see if they satisfy the constraint, save maximum
 * solution that satisfies constraint. This solution can be expressed as a tree.
 * <p/>
 * We can use Backtracking to optimize the Brute Force solution. In the tree
 * representation, we can do Depth-first search (DFS) of tree. If we reach a
 * point where a solution no longer is feasible, there is no need to continue
 * exploring. DFS is an algorithm for traversing or searching tree or graph data
 * structures. The algorithm starts at the root node (selecting some arbitrary
 * node as the root node in the case of a graph) and explores as far as possible
 * along each branch before backtracking.
 * <p/>
 * The backtracking based solution works better than brute force by ignoring
 * infeasible solutions. We can do better than backtracking if we know a
 * bound on best possible solution subtree rooted with every node. If the best
 * in subtree is worse than current best, we can simply ignore this node and
 * its subtrees. So we compute bound (best solution) for every node and compare
 * the bound with current best solution before exploring the node.
 * <p/>
 * Branch and bound is very useful technique for searching a solution but in worst
 * case, we need to fully calculate the entire tree. At best, we only need to fully
 * calculate one path through the tree and prune the rest of it.
 *
 * @see <a href="https://www.geeksforgeeks.org/implementation-of-0-1-knapsack-using-branch-and-bound">
 *      Implementation of 0/1 Knapsack using Branch and Bound</a>
 * @author Pete Sattler
 * @version December 2018
 */
public final class KnapsackWholeItemBranchAndBoundPackerImpl extends KnapsackBasePackerImpl implements Serializable {

    private static final long serialVersionUID = -2520636296982850885L;
    private static final Logger LOGGER = LoggerFactory.getLogger(KnapsackWholeItemBranchAndBoundPackerImpl.class);

    /**
     * Constructs a new whole item branch and bound knapsack packer
     * 
     * @param knapsack The knapsack to pack
     * @param items The items to pack
     */
    public KnapsackWholeItemBranchAndBoundPackerImpl(Knapsack knapsack, Item[] items) {
        super(knapsack, items);
    }

    @Override
    public synchronized void pack() {
        final BigDecimal capacity = knapsack.getCapacity();
        final PriorityQueue<Node> queue = new PriorityQueue<>();
        Node bestNode = new Node();
        Node rootNode = new Node();
        queue.offer(rootNode);
        Arrays.sort(items, Item.BY_COST_WEIGHT_RATIO_DESCENDING);
        while (!queue.isEmpty()) {
            Node currentNode = queue.poll();
            LOGGER.info("Pulled from queue: {}", currentNode);
            if (currentNode.bound >= bestNode.cost && currentNode.head < items.length - 1) {
                final Item currentItem = items[currentNode.head];
                LOGGER.info("Considering {}", currentItem);
                //Take the item:
                Node tookItemNode = new Node(currentNode).addWeight(currentItem.getWeight());
                if (tookItemNode.weight.compareTo(capacity) <= 0) {
                    tookItemNode.taken.add(currentItem);
                    tookItemNode = tookItemNode.addCost(currentItem.getCost()).computeBound(capacity, items);
                    if (tookItemNode.cost > bestNode.cost) {
                        bestNode = tookItemNode;
                        LOGGER.info("New Best {}", bestNode);
                    }
                    if (tookItemNode.bound > bestNode.cost) {
                        LOGGER.info("Placing TOOK {} on queue", tookItemNode);
                        queue.offer(tookItemNode);
                    }
                }
                //Leave the item:
                final Node leftItemNode = new Node(currentNode).computeBound(capacity, items);
                if (leftItemNode.bound > bestNode.cost) {
                    LOGGER.info("Placing LEFT {} on queue", leftItemNode);
                    queue.offer(leftItemNode);
                }
            }
        }
        //Add the best items to the knapsack:
        bestNode.taken.forEach(bestItem -> knapsack.add(bestItem));
    }

    /**
     * An immutable decision tree node
     */
    static class Node implements Comparable<Node>, Serializable {

        private static final long serialVersionUID = -6606469639279557400L;
        private final int head;
        private final List<Item> taken;
        private final BigDecimal weight;
        private final int cost;
        private final int bound;

        /**
         * Constructs a new, empty decision tree node
         */
        public Node() {
            this(0, new ArrayList<>(), BigDecimal.ZERO, 0, 0);
        }

        /**
         * Constructs a new decision tree node
         * 
         * @param head The item number of the next item to add
         * @param taken The list of items taken at the current selection
         * @param weight The total weight at the current selection
         * @param cost The total cost at the current selection
         * @param bound The upper bound of the node
         */
        private Node(int head, List<Item> taken, BigDecimal weight, int cost, int bound) {
            this.head = head;
            this.taken = taken;
            this.weight = Objects.requireNonNull(weight, "Weight is required");
            this.cost = cost;
            this.bound = bound;
        }

        /**
         * Copy constructs a new decision tree node
         * 
         * @param parent The parent node
         */
        public Node(Node parent) {
            this.head = parent.head + 1;
            this.taken = new ArrayList<>(parent.taken);
            this.weight = parent.weight;
            this.cost = parent.cost;
            this.bound = parent.bound;
        }

        public Node addWeight(BigDecimal additionalWeight) {
            return new Node(head, taken, weight.add(additionalWeight), cost, bound);
        }

        public Node addCost(int additionalCost) {
            return new Node(head, taken, weight, cost + additionalCost, bound);
        }

        /**
         * Compute upper bound
         * 
         * @param capacity The maximum weight (in pounds) that the knapsack can hold
         * @param items The items to pack
         * @return The upper bound of the node
         */
        public Node computeBound(BigDecimal capacity, Item[] items) {
            int itemNbr = head;
            BigDecimal totalWeight = weight;
            int bound = cost;
            Item currentItem;
            do {
                currentItem = items[itemNbr];
                if (currentItem.getWeight().add(totalWeight).compareTo(capacity) > 0)
                    break;
                totalWeight = currentItem.getWeight().add(totalWeight);
                bound += currentItem.getCost();
                itemNbr++;
            } while (itemNbr < items.length);
            bound += capacity.subtract(totalWeight).multiply(currentItem.getCostWeightRatio()).setScale(0, RoundingMode.HALF_UP).intValue();
            return new Node(head, taken, weight, cost, bound);
        }

        @Override
        public int compareTo(Node that) {
            return that.bound - this.bound;
        }

        @Override
        public String toString() {
            return String.format("%s [head=%s, taken=%s, weight=%s, cost=%s, bound=%s]", getClass().getSimpleName(), head, taken, weight, cost, bound);
        }
    }
}
