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
    private boolean gameRun;

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
        if (gameRun && (parsed[0] == 'n' || parsed[0] == 'N')) {
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
        } else if (gameRun) { //TODO: track gameRun boolean
            System.out.println("There is no N or S input indicating the creation of a world");
            return null;
        } else {
            StdDraw.clear();
            return null;
        }
    }

    public void startMenu() { //TODO: can only handle keyboard interaction --> implement interactWithString()
        StdDraw.setCanvasSize(this.WIDTH * 16, this.HEIGHT * 16);
        Font font = new Font("Monaco", Font.BOLD, 30);
        StdDraw.setFont(font);
        StdDraw.setXscale(0, this.WIDTH);
        StdDraw.setYscale(0, this.HEIGHT);
        StdDraw.clear(Color.BLACK);
        StdDraw.enableDoubleBuffering();
        //baseline STDDraw setup
        this.menuTurn = true;
        this.gameRun = true;
        while (menuTurn) {
            drawMenu();
            if (StdDraw.hasNextKeyTyped()) {
                Character currentChar = StdDraw.nextKeyTyped();
                if (currentChar == 'n' || currentChar == 'N') {
                    this.menuTurn = false;
                    //New World
                } else if (currentChar == 'l' || currentChar == 'L') {
                    this.menuTurn = false;
                    System.out.println("save states not supported yet");
                    //TODO: implement save state; if no state, just quit UI
                } else if (currentChar == 'q' || currentChar == 'Q') {
                    this.menuTurn = false;
                    StdDraw.clear();
                    this.gameRun = false;
                }
            }
        }
    }

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

    public static void main(String[] args) {
        Engine engine = new Engine();
        engine.interactWithInputString("n04032002s");
    }

}
