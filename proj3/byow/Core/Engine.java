package byow.Core;

import byow.InputDemo.InputSource;
import byow.InputDemo.KeyboardInputSource;
import byow.InputDemo.StringInputDevice;
import byow.TileEngine.TERenderer;
import byow.TileEngine.TETile;
import byow.TileEngine.Tileset;
import edu.princeton.cs.algs4.StdDraw;

import java.awt.*;
import java.util.ArrayList;
import java.util.Arrays;

import static java.lang.Character.isDigit;

public class Engine {
    TERenderer ter = new TERenderer();
    /* Feel free to change the width and height. */
    public static final int WIDTH = 80;
    public static final int HEIGHT = 30;
    private String playerInputs; //TODO: load from .txt file
    private Point avatarPosition;
    private TETile[][] state;
    InputSource inputSource;
    private boolean menuTurn;

    /**
     * Method used for exploring a fresh world. This method should handle all inputs,
     * including inputs from the main menu.
     */
    public void interactWithKeyboard() {
        this.inputSource = new KeyboardInputSource();
        startMenu();
    }

    /**
     * Method used for autograding and testing your code. The input string will be a series
     * of characters (for example, "n123sswwdasdassadwas", "n123sss:q", "lwww". The engine should
     * behave exactly as if the user typed these characters into the engine using
     * interactWithKeyboard.
     *
     * Recall that strings ending in ":q" should cause the game to quite save. For example,
     * if we do interactWithInputString("n123sss:q"), we expect the game to run the first
     * 7 commands (n123sss) and then quit and save. If we then do
     * interactWithInputString("l"), we should be back in the exact same state.
     *
     * In other words, running both of these:
     *   - interactWithInputString("n123sss:q")
     *   - interactWithInputString("lww")
     *
     * should yield the exact same world state as:
     *   - interactWithInputString("n123sssww")
     *
     * @param input the input string to feed to your program
     * @return the 2D TETile[][] representing the state of the world
     */
    public TETile[][] interactWithInputString(String input) {
        // Fill out this method so that it run the engine using the input
        // passed in as an argument, and return a 2D tile representation of the
        // world that would have been drawn if the same inputs had been given
        // to interactWithKeyboard().
        //
        // See proj3.byow.InputDemo for a demo of how you can make a nice clean interface
        // that works for many different input types.
        this.inputSource = new StringInputDevice(input);
        processInputString();
        return this.state;
    }

    /**
     * startMenu provides an interface for interactWithKeyboard():
     * 'n' starts a new world -> prompt user to enter seed
     * 'l' loads previous save
     * 'q' quits
     */
    public void startMenu() {
        StdDraw.setCanvasSize(this.WIDTH * 16, this.HEIGHT * 16);
        Font font = new Font("Monaco", Font.BOLD, 30);
        StdDraw.setFont(font);
        StdDraw.setXscale(0, this.WIDTH);
        StdDraw.setYscale(0, this.HEIGHT);
        StdDraw.clear(Color.BLACK);
        StdDraw.enableDoubleBuffering();
        //baseline STDDraw setup
        drawMenu();
        this.menuTurn = true;
        while (menuTurn) {
            char c = this.inputSource.getNextKey();
            //this.playerInputs = this.playerInputs + c;
            drawMenu();
            if (c == 'n' || c == 'N') {
                this.menuTurn = false;
                StdDraw.clear(Color.BLACK);
                StdDraw.show();
                TETile[][] loadWorld = startGame();
                loadGame(loadWorld);
                while (true) {
                    if (inputSource.possibleNextInput()) {
                        char c2 = inputSource.getNextKey();
                        if (c2 == 'q' || c2 == 'Q') {
                            break;
                        }
                        moveAvatar(c2);
                    }
                }

            } else if (c == 'l' || c == 'L') {
                this.menuTurn = false;
                loadGame(interactWithInputString(playerInputs)); //TODO: load .txt file
            } else if (c == 'q' || c == 'Q') {
                this.menuTurn = false;
                StdDraw.clear(Color.BLACK);
                StdDraw.show();
            }
        }
    }

    /**
     * drawMenu() draws the menu interface for the options in startMenu()
     */
    public void drawMenu() {
        StdDraw.clear(Color.BLACK);
        StdDraw.setPenColor(Color.WHITE);
        Font fontBig = new Font("Monaco", Font.BOLD, 30);
        StdDraw.setFont(fontBig);
        StdDraw.text(this.WIDTH / 2, this.HEIGHT / 2 + 10, "Create World (N)");
        StdDraw.text(this.WIDTH / 2, this.HEIGHT / 2, "Load World (L)");
        StdDraw.text(this.WIDTH / 2, this.HEIGHT / 2 - 10, "Quit (Q)");

        if (menuTurn) {
            Font fontSmall = new Font("Monaco", Font.BOLD, 20);
            StdDraw.setFont(fontSmall);
            StdDraw.line(0, this.HEIGHT - 2, this.WIDTH, this.HEIGHT - 2);
            StdDraw.textLeft(0, this.HEIGHT - 1, "61B Sp'23");
            StdDraw.text(this.WIDTH / 2, this.HEIGHT - 1, "SCUFFED UNDERTALE");
            StdDraw.textRight(this.WIDTH, this.HEIGHT - 1, "By: Max Boston");
        }
        StdDraw.show();
    }

    /**
     * This method is exclusively for interactWithInputString() because a visible menu is not necessary.
     * If the beginning of the string has 'n' -> new world creation
     * If the beginning of the string has 'l' -> continue from previous save
     */
    public void processInputString() {
        char c = this.inputSource.getNextKey();
        if (c == 'n' || c == 'N') {
            TETile[][] loadWorld = startGame();
            loadGame(loadWorld);
            while (inputSource.possibleNextInput()) {
                char c2 = inputSource.getNextKey();
                if (c2 == ':') {
                    if (inputSource.possibleNextInput()) {
                        char nextChar = inputSource.getNextKey();
                        if (nextChar == 'q' || nextChar == 'Q') { //TODO: adjust with :Q instead of Q
                            break;
                        }
                    }
                }
                moveAvatar(c);
            }
        } else if (c == 'l' || c == 'L') {
            loadGame(interactWithInputString(playerInputs));
        }
    }

    /**
     * This method is for both keyboard and string interaction, and prepares a world to load in loadGame().
     * @return TETile[][] randomly generated world.
     */
    public TETile[][] startGame() { //TODO: collect movement inputs while in game
        String newSeed = "";
        StdDraw.setPenColor(Color.WHITE);
        Font font = new Font("Monaco", Font.BOLD, 30);
        StdDraw.setFont(font);
        StdDraw.text(this.WIDTH / 2 + 3, this.HEIGHT / 2 + 3, "Enter seed:");
        StdDraw.show();
        while (this.inputSource.possibleNextInput()) {
            char c = this.inputSource.getNextKey();
            if (c == 's' || c == 'S' || newSeed.length() > 9) { // seed can't exceed 10 digits
                System.out.println(newSeed);
                WorldGenerator initGenerator = new WorldGenerator(WIDTH, HEIGHT, Long.parseLong(newSeed));
                TETile[][] finalWorldFrame = initGenerator.getTiles();
                return finalWorldFrame;
            } else if (isDigit(c)) {
                System.out.println(c);
                newSeed = newSeed + c;
                StdDraw.setPenColor(Color.BLACK);
                StdDraw.filledRectangle(this.WIDTH / 2, this.HEIGHT / 2 - 5, 50, 5);
                StdDraw.setPenColor(Color.WHITE);
                StdDraw.text(this.WIDTH / 2, this.HEIGHT / 2 - 5, newSeed);
                StdDraw.show();
            }
        }
        return null;
    }

    /**
     * Renders and initializes the world fed as an input
     * @param worldState created from startGame()
     */
    public void loadGame(TETile[][] worldState) {
        this.state = worldState;
        // Place avatar and store its position
        Point startPosition = findEmptyTile(state);
        avatarPosition = startPosition;
        assert startPosition != null;
        this.state[startPosition.getX()][startPosition.getY()] = Tileset.AVATAR;
        ter.initialize(WIDTH, HEIGHT);
        ter.renderFrame(worldState);
    }

    /**
     * @param world generated by startGame(). This method accesses it via loadGame()
     * @return deterministic starting point for player based on seed.
     */
    private Point findEmptyTile(TETile[][] world) {
        for (int x = 0; x < world.length; x++) {
            for (int y = 0; y < world[0].length; y++) {
                if (world[x][y] == Tileset.FLOOR) {
                    return new Point(x, y);
                }
            }
        }
        return null;
    }

    /**
     * @source Chat GPT
     * This helper method handles avatar movement.
     * @param direction given either by string input or realtime keyboard input.
     */
    public void moveAvatar(char direction) {
        int newX = avatarPosition.getX();
        int newY = avatarPosition.getY();

        switch (direction) {
            case 'w':
            case 'W':
                newY += 1;
                break;
            case 'a':
            case 'A':
                newX -= 1;
                break;
            case 's':
            case 'S':
                newY -= 1;
                break;
            case 'd':
            case 'D':
                newX += 1;
                break;
        }

        if (state[newX][newY] == Tileset.FLOOR) {
            state[avatarPosition.getX()][avatarPosition.getY()] = Tileset.FLOOR;
            avatarPosition.setX(newX);
            avatarPosition.setY(newY);
            state[newX][newY] = Tileset.AVATAR;
            ter.renderFrame(state);
        }
    }

    /**
     * Main method for debugging between interactWithInputString() and interactWithKeyboard()
     */
    public static void main(String[] args) {
        Engine engine = new Engine();
        engine.interactWithInputString("n1swaddaw");
        //engine.interactWithKeyboard();
    }

}
