package byow.Core;

import byow.TileEngine.TERenderer;
import byow.TileEngine.TETile;
import edu.princeton.cs.algs4.StdDraw;

import java.awt.*;
import java.util.Arrays;

public class Engine {
    TERenderer ter = new TERenderer();
    /* Feel free to change the width and height. */
    public static final int WIDTH = 80;
    public static final int HEIGHT = 30;
    public static final long DEFAULT_SEED = 04032002; // bounded by long.MAX_VALUE
    private boolean menuTurn;

    /**
     * Method used for exploring a fresh world. This method should handle all inputs,
     * including inputs from the main menu.
     */
    public void interactWithKeyboard() {
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
        char[] parsed = input.toCharArray();
        int n = 1; // n = 's' after while loop executes
        String seedExtract = "";
        startMenu();
        if ((parsed[0] == 'n' || parsed[0] == 'N')) {
            while (parsed[n] != 's') {
                seedExtract = seedExtract + parsed[n];
                n += 1;
            }
            long newSeed = Long.parseLong(seedExtract);
            WorldGenerator initGenerator = new WorldGenerator(WIDTH, HEIGHT, newSeed);
            TETile[][] finalWorldFrame = initGenerator.getTiles();
            ter.initialize(WIDTH, HEIGHT);
            ter.renderFrame(finalWorldFrame);
            return finalWorldFrame;
        } else {
            System.out.println("There is no N or S input indicating the creation of a world");
            return null;
        }
    }

    public void startMenu() {
        this.menuTurn = true;
        String typedString = "";
        int counter = 0;
        while (menuTurn) {
            drawFrame("START");
            if (counter >= 5) {
                menuTurn = false;
            }
            if (StdDraw.hasNextKeyTyped()) { //change to keyboard inputs N/L/Q instead of indiscriminate counter
                Character currentChar = StdDraw.nextKeyTyped();
                typedString = typedString + currentChar;
                StdDraw.clear(Color.BLACK);
                drawFrame(typedString);
                counter += 1;
            }
        }
    }

    public void drawFrame(String s) {
        /* Take the input string S and display it at the center of the screen,
         * with the pen settings given below. */
        StdDraw.clear(Color.BLACK);
        StdDraw.setPenColor(Color.WHITE);
        Font fontBig = new Font("Monaco", Font.BOLD, 30);
        StdDraw.setFont(fontBig);
        StdDraw.text(this.WIDTH / 2, this.HEIGHT / 2, s);

        /* If the game is not over, display encouragement, and let the user know if they
         * should be typing their answer or watching for the next round. */
        if (menuTurn) {
            Font fontSmall = new Font("Monaco", Font.BOLD, 20);
            StdDraw.setFont(fontSmall);
            StdDraw.line(0, this.HEIGHT - 2, this.WIDTH, this.HEIGHT - 2);
            StdDraw.textLeft(0, this.HEIGHT - 1, "61B Sp'23";
            StdDraw.text(this.WIDTH / 2, this.HEIGHT - 1, "SCUFFED UNDERTALE"); // or menu
            StdDraw.textRight(this.WIDTH, this.HEIGHT - 1, "By: Max Boston");
        }
        StdDraw.show();
    }

    public static void main(String[] args) {
        TERenderer ter2 = new TERenderer();
        WorldGenerator initGenerator = new WorldGenerator(WIDTH, HEIGHT, DEFAULT_SEED);
        TETile[][] finalWorldFrame = initGenerator.getTiles();
        ter2.initialize(WIDTH, HEIGHT);
        ter2.renderFrame(finalWorldFrame);
    }

}
