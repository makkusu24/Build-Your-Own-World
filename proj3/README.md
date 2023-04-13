# Build Your Own World Design Document

**Partner 1:** Max Boston

**Partner 2:**

## Classes and Data Structures
All work condensed in a board, room, and hallway generating class called WorldGenerator.java

Deterministic behavior from seed (RandomUtils.java) for the number
of rooms and hallways, as well as their locations.

## Algorithms
Brute force room generation with improved efficiency through memoization.

Randomness is dealt with by using nextInt() to determine the dimensions of a room as well as its coordinates on the map.

First handle room generation iteratively, then hallway generation once all rooms are randomly constructed and placed.

There is a comprehensive series of checks before finalizing the creation of a room or hallway, such as seeing if there is overlap
or if the planned place will result in "leaks" (i.e., enforce that all floor tiles are adjacent to only wall tiles or other floor tiles).

## Persistence
Things to add in phase 2:
- Main Menu (N -> S, L, Q)
    - Display seed as it's being typed
- WASD avatar that can interact with the world (walls CANNOT be traversable)
- save state/loading
    - ": -> Q" input immediately saves and quits
    - "L" to load previous save
- HUD that supports explaining block type when hovering mouse

Ambition score features:
- (Primary) TOGGLE LINE OF SIGHT
- (Secondary) MENU OPTION TO DETERMINE ENVIRONMENT (e.g., desert or ocean stage)