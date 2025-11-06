# [It's the Knapsack Problem, Stupid](https://en.wikipedia.org/wiki/It's_the_economy,_stupid)

You want to send your friend a package with different items. Each item you include has an associated identifier (ID), 
cost and weight. The package has an overall capacity limitation. Your goal is to determine which items to place in the 
package, so that its total weight does not exceed its capacity and its total cost is as large as possible. You would 
prefer to send a package which has less weight if there is more than one package with the same price.

## Constraints

* There are up to 15 available items to choose from.
* The maximum capacity any package can hold is 100 lbs.
* The maximum cost of any item is $100.00 (USD).

## Output 

For each set of items in inventory, produce a package with zero or more items that you will send to your friend.

## Version History

### [Version 1.0.0] Initial Release
* Started life as Barclay's Programming Challenge 2018
* Included both <i>Recursive</i> and <i>Branch and Bound</i> implementations
* December 2018: Initial release (Gradle)
* February 2022: Upgraded to Java 17 (Maven)

### [Version 2.0.0] November 2025
* Overhauled the entire project
* Recursive implementation only
* Upgraded to Java 24

Pete Sattler  
November 2025  
_peter@sattler22.net_
