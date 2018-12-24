# Classic Knapsack Problem

The Knapsack Problem is a well known problem of combinatorial optimization. Given a set of items, each with a weight and a cost, we must determine the number of each item to include in a collection so that the total weight is less than or equal to a given limit and the total value must be maximized.

I provide two different implementations, both are _whole item_ (0/1) solutions meaning fractional items are not allowed. An item is either packed or it is left out.

* _Recursive_ - uses simple, naive recursion with no memoization optimization.
* _Branch and Bound_ - We can use Backtracking to optimize the Brute Force solution. In the tree representation, we can do DFS of tree. If we reach a point where a solution no longer is feasible, there is no need to continue exploring.

Special thanks for these two articles which were a fundamental source of information for me.

* [Solving 0/1 Knapsack problem using Recursion<](http://techieme.in/solving-01-knapsack-problem-using-recursion/) 
* [Implementation of 0/1 Knapsack using Branch and Bound](https://www.geeksforgeeks.org/implementation-of-0-1-knapsack-using-branch-and-bound/)

Pete Sattler  
December 2018  
_peter@sattler22.net_
