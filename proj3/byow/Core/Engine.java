package byow.Core;

import byow.InputDemo.InputSource;
import byow.InputDemo.KeyboardInputSource;
import byow.InputDemo.StringInputDevice;
import byow.TileEngine.TERenderer;
import byow.TileEngine.TETile;
import byow.TileEngine.Tileset;
import edu.princeton.cs.algs4.StdDraw;

import java.io.*;

import java.awt.*;
import java.util.HashSet;

import static java.lang.Character.isDigit;

public class Engine {
    TERenderer ter = new TERenderer();
    /* Feel free to change the width and height. */
    public static final int WIDTH = 80;
    public static final int HEIGHT = 30;
    private String playerInputs;
    private Point avatarPosition;
    private TETile[][] state;
    InputSource inputSource;
    private boolean menuTurn;
    static boolean flowerDimension = false;
    private boolean lineOfSightActive = false;
    private StringBuilder inputBuilder;

    private static final int MAGICNUMBER10 = 10;
    private static final int MAGICNUMBER16 = 16;
    private static final int MAGICNUMBER20 = 20;
    private static final int MAGICNUMBER30 = 30;
    private static final int MAGICNUMBER50 = 50;

    /**
     * Method used for exploring a fresh world. This method should handle all inputs,
     * including inputs from the main menu.
     */
    public void interactWithKeyboard() {
        this.inputSource = new KeyboardInputSource();
        this.inputBuilder = new StringBuilder();
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
        this.inputBuilder = new StringBuilder();
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
        StdDraw.setCanvasSize(this.WIDTH * MAGICNUMBER16, this.HEIGHT * MAGICNUMBER16);
        Font font = new Font("Monaco", Font.BOLD, MAGICNUMBER30);
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
            inputBuilder.append(c);
            drawMenu();
            if (c == 'n' || c == 'N') {
                this.menuTurn = false;
                StdDraw.clear(Color.BLACK);
                StdDraw.show();
                TETile[][] loadWorld = startGame();
                loadGame(loadWorld);
                while (true) {
                    TETile currentTile = getTileUnderMouse();
                    if (currentTile != null) {
                        ter.renderFrame(state);
                        renderHUD(currentTile);
                    }
                    if (inputSource.possibleNextInput()) {
                        if (lineOfSightActive) {
                            HashSet<Point> visibleTiles = getVisibleTiles(avatarPosition, 5);
                            renderLineOfSight(loadWorld, visibleTiles);
                        } else {
                            ter.renderFrame(loadWorld);
                        }
                        TETile currentTile2 = getTileUnderMouse();
                        renderHUD(currentTile2);
                        char c2 = inputSource.getNextKey();
                        inputBuilder.append(c2);
                        if (c2 == ':') {
                            if (inputSource.possibleNextInput()) {
                                char nextChar = inputSource.getNextKey();
                                if (nextChar == 'q' || nextChar == 'Q') {
                                    saveGameState(inputBuilder.substring(0, inputBuilder.length() - 1).toString());
                                    StdDraw.clear(Color.BLACK);
                                    StdDraw.show();
                                    break;
                                }
                            }
                        }
                        if (c2 == 'r' || c2 == 'R') {
                            this.lineOfSightActive = !lineOfSightActive;
                        }
                        moveAvatar(c2);
                    }
                }

            } else if (c == 'l' || c == 'L') {
                this.menuTurn = false;
                String loadedInput = loadGameState();
                System.out.println(loadedInput);
                if (!loadedInput.isEmpty()) {
                    loadGame(interactWithInputString(loadedInput));
                }
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
        Font fontBig = new Font("Monaco", Font.BOLD, MAGICNUMBER30);
        StdDraw.setFont(fontBig);
        StdDraw.text(this.WIDTH / 2, this.HEIGHT / 2 + MAGICNUMBER10, "Create World (N)");
        StdDraw.text(this.WIDTH / 2, this.HEIGHT / 2, "Load World (L)");
        StdDraw.text(this.WIDTH / 2, this.HEIGHT / 2 - MAGICNUMBER10, "Quit (Q)");

        if (menuTurn) {
            Font fontSmall = new Font("Monaco", Font.BOLD, MAGICNUMBER20);
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
        System.out.println(c);
        inputBuilder.append(c);
        System.out.println(inputBuilder);
        if (c == 'n' || c == 'N') {
            TETile[][] loadWorld = startGame();
            loadGame(loadWorld);
            while (inputSource.possibleNextInput()) {
                char c2 = inputSource.getNextKey();
                System.out.println(c2);
                inputBuilder.append(c2);
                if (c2 == ':') {
                    if (inputSource.possibleNextInput()) {
                        char nextChar = inputSource.getNextKey();
                        if (nextChar == 'q' || nextChar == 'Q') {
                            saveGameState(inputBuilder.substring(0, inputBuilder.length() - 2));
                            return;
                        }
                    }
                }
                moveAvatar(c2);
            }
            while (true) {
                Point ghostAvatar = findEmptyTile(state);
                if (loadWorld[ghostAvatar.getX()][ghostAvatar.getY()] == Tileset.AVATAR) {
                    loadWorld[ghostAvatar.getX()][ghostAvatar.getY()] = Tileset.FLOOR;
                }
                this.inputSource = new KeyboardInputSource();
                System.out.println("transition inputString() -> keyboard");
                TETile currentTile = getTileUnderMouse();
                if (currentTile != null) {
                    ter.renderFrame(state);
                    renderHUD(currentTile);
                }
                if (inputSource.possibleNextInput()) {
                    if (lineOfSightActive) {
                        HashSet<Point> visibleTiles = getVisibleTiles(avatarPosition, 5);
                        renderLineOfSight(loadWorld, visibleTiles);
                    } else {
                        ter.renderFrame(loadWorld);
                    }
                    TETile currentTile2 = getTileUnderMouse();
                    renderHUD(currentTile2);
                    char c2 = inputSource.getNextKey();
                    inputBuilder.append(c2);
                    if (c2 == ':') {
                        if (inputSource.possibleNextInput()) {
                            char nextChar = inputSource.getNextKey();
                            if (nextChar == 'q' || nextChar == 'Q') {
                                saveGameState(inputBuilder.substring(0, inputBuilder.length() - 1).toString());
                                StdDraw.clear(Color.BLACK);
                                StdDraw.show();
                                break;
                            }
                        }
                    }
                    if (c2 == 'r' || c2 == 'R') {
                        this.lineOfSightActive = !lineOfSightActive;
                    }
                    moveAvatar(c2);
                }
            }
        } else if (c == 'l' || c == 'L') {
            String loadedInput = loadGameState();
            if (!loadedInput.isEmpty()) {
                this.playerInputs = loadedInput;
                loadGame(interactWithInputString(loadedInput));
            }
        }
    }

    /**
     * This method is for both keyboard and string interaction, and prepares a world to load in loadGame().
     * @return TETile[][] randomly generated world.
     */
    public TETile[][] startGame() {
        String newSeed = "";
        StdDraw.setPenColor(Color.WHITE);
        Font font = new Font("Monaco", Font.BOLD, MAGICNUMBER30);
        StdDraw.setFont(font);
        StdDraw.text(this.WIDTH / 2 + 3, this.HEIGHT / 2 + 3, "Enter seed:");
        StdDraw.show();
        while (this.inputSource.possibleNextInput()) {
            char c = this.inputSource.getNextKey();
            inputBuilder.append(c);
            if (c == 's' || c == 'S' || newSeed.length() > 9) { // seed can't exceed 10 digits
                /**
                StdDraw.clear(Color.BLACK);
                StdDraw.setPenColor(Color.WHITE);
                Font font2 = new Font("Monaco", Font.BOLD, MAGICNUMBER30);
                StdDraw.setFont(font2);
                StdDraw.text(this.WIDTH / 2 + 3, this.HEIGHT / 2 + 3, "(E) for flower biome, (S) for default biome");
                StdDraw.show();
                while (this.inputSource.possibleNextInput()) {
                    char c2 = this.inputSource.getNextKey();
                    inputBuilder.append(c2);
                    if (c2 == 'e' || c2 == 'E') {
                        this.flowerDimension = true;
                        break;
                    } else if (c2 == 's' || c2 == 'S') {
                        this.flowerDimension = false;
                        break;
                    }
                }
                */
                WorldGenerator initGenerator = new WorldGenerator(WIDTH, HEIGHT, Long.parseLong(newSeed));
                TETile[][] finalWorldFrame = initGenerator.getTiles();
                return finalWorldFrame;
            } else if (isDigit(c)) {
                newSeed = newSeed + c;
                StdDraw.setPenColor(Color.BLACK);
                StdDraw.filledRectangle(this.WIDTH / 2, this.HEIGHT / 2 - 5, MAGICNUMBER50, 5);
                StdDraw.setPenColor(Color.WHITE);
                StdDraw.text(this.WIDTH / 2 + 3, this.HEIGHT / 2 - 5, newSeed);
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
        TETile currentTile = getTileUnderMouse();
        renderHUD(currentTile);
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
     * @source Chat GPT provided the if satement at the bottom
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
            default:
                break;
        }

        if (state[newX][newY] == Tileset.FLOOR) {
            state[avatarPosition.getX()][avatarPosition.getY()] = Tileset.FLOOR;
            avatarPosition.setX(newX);
            avatarPosition.setY(newY);
            state[newX][newY] = Tileset.AVATAR;
            ter.renderFrame(state);
            TETile currentTile = getTileUnderMouse();
            renderHUD(currentTile);
        }
    }

    /**
     * Method for interacting with mouse hovering to display tile names.
     * @source ChatGPT provided formatting for the text
     * to be at the top of the screen while the world is also displayed.
     * @param tile gives us text that stays fixed above the game world as an HUD.
     */
    public void renderHUD(TETile tile) {
        int textX = WIDTH / 2;
        int textY = HEIGHT - 1;

        StdDraw.setPenColor(Color.WHITE);
        Font font = new Font("Monaco", Font.BOLD, MAGICNUMBER20);
        StdDraw.setFont(font);
        StdDraw.text(textX, textY, "Current tile: " + tile.description());
        StdDraw.text((WIDTH * 3) / 4, textY, "=^_^= R to toggle sight =^_^=");
        StdDraw.text(WIDTH / 4, textY, ":→Q to Quit");
        StdDraw.show();
    }

    /**
     * Used in loops that run while game is running to check what the mouse is interacting with.
     * @return specific tile that the user's mouse is hovering over.
     */
    public TETile getTileUnderMouse() {
        int mouseX = (int) StdDraw.mouseX();
        int mouseY = (int) StdDraw.mouseY();

        if (mouseX < 0 || mouseX >= WIDTH || mouseY < 0 || mouseY >= HEIGHT) {
            return null;
        }

        return state[mouseX][mouseY];
    }

    /**
     * Aggregates all player movement and the world seed into a .txt save file.
     * @param input indexed player inputs from either keyboard or string.
     */
    private void saveGameState(String input) {
        System.out.println(input);
        System.out.println("Running saveGameState()");
        String saveFilePath = "save-file.txt";
        File saveFile = new File(saveFilePath);
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(saveFile))) {
            bw.write(input);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Works in tandem with saveGameState() to let players continue where they left off from.
     * @source ChatGPT wrote the code for throwing an exception if there is no save file.
     * @return previously saved player movement from .txt file as a string.
     */
    private String loadGameState() {
        String saveFilePath = "save-file.txt";
        File saveFile = new File(saveFilePath);
        if (!saveFile.exists()) {
            System.out.println("No save file found.");
            return "";
        }
        StringBuilder gameState = new StringBuilder();
        try (BufferedReader reader = new BufferedReader(new FileReader(saveFile))) {
            String line;
            while ((line = reader.readLine()) != null) {
                gameState.append(line);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return gameState.toString();
    }

    /**
     * This function is called constantly to stay up to date with the player's position.
     * @param avatarP uses the current coordinate of the avatar to draw line of sight.
     * @param radius the length of the longest vertices of the diamond.
     * @return HashSet containing the tiles that should be rendered.
     */
    public HashSet<Point> getVisibleTiles(Point avatarP, int radius) {
        HashSet<Point> visibleTiles = new HashSet<>();
        for (int x = -radius; x <= radius; x++) {
            for (int y = -radius; y <= radius; y++) {
                int distance = Math.abs(x) + Math.abs(y);
                if (distance <= radius) {
                    int tileX = avatarP.getX() + x;
                    int tileY = avatarP.getY() + y;
                    if (tileX >= 0 && tileX < WIDTH && tileY >= 0 && tileY < HEIGHT) {
                        visibleTiles.add(new Point(tileX, tileY));
                    }
                }
            }
        }
        return visibleTiles;
    }

    /**
     * This method takes what's given from getVisibleTiles() and changes the framing of the world accordingly.
     * There is no parameter for radius because that was already determined in getVisibleTiles().
     * @source Chat-GPT
     * @param world the current world state of the game that's running.
     * @param visibleTiles the HashSet of coordinates for tiles within the line of sight.
     */
    private void renderLineOfSight(TETile[][] world, HashSet<Point> visibleTiles) {
        TETile[][] maskedTiles = new TETile[world.length][world[0].length];
        for (int x = 0; x < maskedTiles.length; x++) {
            for (int y = 0; y < maskedTiles[0].length; y++) {
                maskedTiles[x][y] = Tileset.NOTHING;
            }
        }
        for (Point p : visibleTiles) {
            int x = p.getX();
            int y = p.getY();
            maskedTiles[x][y] = world[x][y];
        }
        ter.renderFrame(maskedTiles);
    }

    /**
     * Main method for debugging between interactWithInputString() and interactWithKeyboard()
     */
    public static void main(String[] args) {
        Engine engine = new Engine();
        //engine.interactWithInputString("n1swaddaw:QLdd");
        engine.interactWithKeyboard();
    }

}
