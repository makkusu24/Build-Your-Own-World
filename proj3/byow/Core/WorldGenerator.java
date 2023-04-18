package byow.Core;

import byow.TileEngine.TERenderer;
import byow.TileEngine.TETile;
import byow.TileEngine.Tileset;
import byow.Core.Point;

import java.util.*;

public class WorldGenerator {

    private final TETile[][] tiles;
    private int width;
    private int height;
    private Random seed;
    private Map<Point, Boolean> memo;
    private List<int[]> rooms;
    private final int minRoomSize = 4;
    private final int maxRoomSize = 10;
    private int roomAttempts;

    public WorldGenerator(int width, int height, long seed) {
        this.tiles = new TETile[width][height];
        this.width = width;
        this.height = height;
        this.seed = new Random(seed);
        this.memo = new HashMap<>();
        this.rooms = new ArrayList<>();
        this.roomAttempts = this.seed.nextInt(4000) + 1000;
        setTilesNothing();
        generateRooms();
        connectRooms(rooms);
    }

    private void setTilesNothing() {
        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                tiles[x][y] = Tileset.NOTHING;
            }
        }
        //System.out.println(Arrays.deepToString(tiles));
    }

    private void generateRooms() {
        int attempts = roomAttempts;
        while (attempts > 0) {
            int roomWidth = seed.nextInt(maxRoomSize - minRoomSize + 1) + minRoomSize;
            int roomHeight = seed.nextInt(maxRoomSize - minRoomSize + 1) + minRoomSize;
            int roomX = seed.nextInt(width - roomWidth - 1) + 1;
            int roomY = seed.nextInt(height - roomHeight - 1) + 1;

            // Ensure rooms are at least 2 tiles apart horizontally and vertically
            int separationX = 2;
            int separationY = 2;
            if (isRoomValid(roomX - separationX, roomY - separationY, roomWidth + separationX * 2, roomHeight + separationY * 2)) {
                createRoom(roomX, roomY, roomWidth, roomHeight);
                rooms.add(new int[] {roomX, roomY, roomWidth, roomHeight});
            }
            attempts -= 1;
        }
    }

    private boolean isRoomValid(int x, int y, int width, int height) {
        if (x < 0 || x + width >= this.width || y < 0 || y + height >= this.height) {
            return false;
        }

        // Check if we have already computed the validity of this room before
        Point position = new Point(x, y);
        if (memo.containsKey(position)) {
            return memo.get(position);
        }

        // Check if the room overlaps with any other rooms
        for (int i = x - 1; i <= x + width; i++) {
            for (int j = y - 1; j <= y + height; j++) {
                if (i < 0 || i >= this.width || j < 0 || j >= this.height) {
                    continue;
                }
                if (tiles[i][j] != Tileset.NOTHING) {
                    memo.put(position, false);
                    return false;
                }
            }
        }

        // The room is valid, add it to the map and return true
        memo.put(position, true);
        return true;
    }

    private void createRoom(int x, int y, int width, int height) {
        for (int i = x; i < x + width; i++) {
            for (int j = y; j < y + height; j++) {
                if ((i == x || i == x + width - 1 || j == y || j == y + height - 1)) {
                    if (!Engine.flowerDimension) {
                        tiles[i][j] = Tileset.WALL;
                    } else {
                        tiles[i][j] = Tileset.FLOWER;
                    }
                } else {
                    tiles[i][j] = Tileset.FLOOR;
                }
            }
        }
    }

    public TETile[][] getTiles() {
        return this.tiles;
    }

    private void connectRooms(List<int[]> rooms) {
        List<int[]> remainingRooms = new ArrayList<>(rooms);

        while (remainingRooms.size() > 1) {
            int[] currentRoom = remainingRooms.remove(0);
            int[] closestRoom = getClosestRoom(currentRoom, remainingRooms);

            addHallway(currentRoom, closestRoom);
        }
    }


    public void addHallway(int[] room1, int[] room2) {
        int x1 = room1[0] + room1[2] / 2;
        int y1 = room1[1] + room1[3] / 2;
        int x2 = room2[0] + room2[2] / 2;
        int y2 = room2[1] + room2[3] / 2;

        int startX = Math.min(x1, x2);
        int endX = Math.max(x1, x2);
        int startY = Math.min(y1, y2);
        int endY = Math.max(y1, y2);

        // Randomly choose whether to start with horizontal or vertical hallway
        if (seed.nextBoolean()) {
            createHorizontalHallway(startX, endX, y1);
            createVerticalHallway(startY, endY, x2);
        } else {
            createVerticalHallway(startY, endY, x1);
            createHorizontalHallway(startX, endX, y2);
        }
    }

    private void createHorizontalHallway(int startX, int endX, int y) {
        for (int x = startX; x <= endX; x++) {
            tiles[x][y] = Tileset.FLOOR;
            if (tiles[x][y - 1] == Tileset.NOTHING) {
                tiles[x][y - 1] = Engine.flowerDimension ? Tileset.FLOWER : Tileset.WALL;
            }
            if (tiles[x][y + 1] == Tileset.NOTHING) {
                tiles[x][y + 1] = Engine.flowerDimension ? Tileset.FLOWER : Tileset.WALL;
            }
        }
    }

    private void createVerticalHallway(int startY, int endY, int x) {
        for (int y = startY; y <= endY; y++) {
            tiles[x][y] = Tileset.FLOOR;
            if (tiles[x - 1][y] == Tileset.NOTHING) {
                tiles[x - 1][y] = Engine.flowerDimension ? Tileset.FLOWER : Tileset.WALL;
            }
            if (tiles[x + 1][y] == Tileset.NOTHING) {
                tiles[x + 1][y] = Engine.flowerDimension ? Tileset.FLOWER : Tileset.WALL;
            }
        }
    }

    private int[] getClosestRoom(int[] currentRoom, List<int[]> remainingRooms) {
        int[] closestRoom = null;
        int minDistance = Integer.MAX_VALUE;

        for (int[] room : remainingRooms) {
            int distance = Math.abs(room[0] - currentRoom[0]) + Math.abs(room[1] - currentRoom[1]);

            if (distance < minDistance) {
                closestRoom = room;
                minDistance = distance;
            }
        }

        return closestRoom;
    }

}
