package net.sattler22.knapsack;

import java.io.Serializable;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Arrays;
import java.util.Comparator;
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
 * solution that satisfies constraint. This solution can be expressed as tree.
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
 * infeasible solutions. We can do better (than backtracking) if we know a 
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
 *         Implementation of 0/1 Knapsack using Branch and Bound</a>
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
        Arrays.sort(items, Comparator.comparing(Item::getCostWeightRatio));
    }

    @Override
    public void pack() {
        packImpl(0, knapsack.getCapacity());
    }

    private void packImpl(int itemNbr, BigDecimal capacity) {
        //Loop thru decision tree nodes:
        final PriorityQueue<Node> queue = new PriorityQueue<>();
        Node bestNode = new Node();
        Node rootNode = new Node().computeBound(itemNbr, capacity, items);
        queue.offer(rootNode);
        while (!queue.isEmpty()) {
            Node currentNode = queue.poll();
            if (currentNode.bound > bestNode.cost && itemNbr < items.length - 1) {
                final Item currentItem = items[itemNbr];
                LOGGER.debug("Considering {}", currentItem);
                //Either take the item or leave it:
                Node tookItemNode = currentNode.addWeight(currentItem.getWeight());
                if (tookItemNode.weight.compareTo(capacity) <= 0) {
                    tookItemNode = tookItemNode.addCost(currentItem.getCost()).computeBound(itemNbr, capacity, items);
                    if (tookItemNode.cost > bestNode.cost) {
                        bestNode = tookItemNode;
                        LOGGER.debug("Best {}", bestNode);
                        knapsack.add(currentItem);
                    }
                    if (tookItemNode.bound > bestNode.cost)
                        queue.offer(tookItemNode);
                }
                final Node leftItemNode = new Node(currentNode).computeBound(itemNbr, capacity, items);
                if (leftItemNode.bound > bestNode.cost)
                    queue.offer(leftItemNode);
                itemNbr++;
            }
        }
    }

    /**
     * An immutable decision tree node
     *
     */
    static class Node implements Comparable<Node>, Serializable {

        private static final long serialVersionUID = -6606469639279557400L;
        private final BigDecimal weight;
        private final int cost;
        private final int bound;

        /**
         * Constructs a new, empty decision tree node
         */
        public Node() {
            this(BigDecimal.ZERO, 0, 0);
        }

        /**
         * Constructs a new decision tree node
         * 
         * @param weight The total weight of the subtree of this node
         * @param cost The total cost of the nodes on the path from the root node
         *             to this node (inclusive)
         * @param bound The total cost upper bound of the subtree of this node
         */
        private Node(BigDecimal weight, int cost, int bound) {
            this.weight = Objects.requireNonNull(weight, "Total weight is required");
            this.cost = cost;
            this.bound = bound;
        }

        /**
         * Copy constructs a new decision tree node
         * 
         * @param source The source node
         */
        public Node(Node source) {
            this.weight = source.weight;
            this.cost = source.cost;
            this.bound = source.bound;
        }

        public Node addWeight(BigDecimal additionalWeight) {
            return new Node(weight.add(additionalWeight), cost, bound);
        }

        public Node addCost(int additionalCost) {
            return new Node(weight, cost + additionalCost, bound);
        }

        /**
         * Compute upper bound
         * 
         * @param itemNbr The item number of the subtree
         * @param capacity The maximum weight (in pounds) that the knapsack can hold
         * @param items The items to pack
         * @return The total cost upper bound of the subtree root
         */
        public Node computeBound(int itemNbr, BigDecimal capacity, Item[] items) {
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
            return new Node(weight, cost, bound);
        }

        @Override
        public int compareTo(Node that) {
            return that.bound - this.bound;
        }

        @Override
        public String toString() {
            return String.format("%s [weight=%s, cost=%s, bound=%s]", getClass().getSimpleName(), weight, cost, bound);
        }
    }
}
