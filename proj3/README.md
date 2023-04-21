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

## PHASE 2 IMPLEMENTATIONS
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

## PERSISTENCE
- IDEA: .txt file stores InputWithString() for save/load state --> all movements stored in a string -->
  - ":Q"/save & quit -> store all inputs in a file and load game state from menu
  - Ex) save seed after loading world from interactWithKeyboard()



- Issues with save/load state so far:
  - I can artificially add seed + inputs into save-file.txt
    -   -> doesn't actually generate TETile[][] to work from
    - (1) methods aren't actually changing save-file.txt
      - Yet still overrides manual seed + player inputs
  - (2) 'L' from menu is returning only a static TETile[][] (?) -> can't interact with UI or world

saving keyboard seed, not loading; not saving past n for string interaction; string interact not save-file.txt

<===> FINAL STRETCH OF FIXES TO IMPLEMENT <===>
//TODO: interactWithInputString() still not making save-file.txt
//TODO: Autograder fails because '==' false when "GHOST AVATAR" left behind after loading