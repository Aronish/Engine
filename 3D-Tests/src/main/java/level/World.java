package main.java.level;

import main.java.level.tiles.EnumTiles;
import main.java.level.tiles.Tile;
import main.java.level.tiles.TileFactory;
import main.java.level.tiles.TileTree;
import main.java.math.SimplexNoise;
import main.java.math.Vector2f;
import main.java.math.Vector3f;

import java.util.ArrayList;
import java.util.Random;

/**
 * Main world object, on which everything should be located to be inside the world.
 * For future development style, this class should manage everything to do with the world in a level.
 */
public class World {

    private static final TileFactory TILE_FACTORY = new TileFactory();
    private static final Random RANDOM = new Random();
    private static final int WORLD_SIZE = 10000;

    private static double noiseScale = 0.042d;
    private ArrayList<Entity> tiles;
    private Grid grid;

    /**
     * Initializes the grid, list of tiles and generates the world.
     */
    World(){
        tiles = new ArrayList<>();
        grid = new Grid();
        generateWorld();
    }

    /**
     * Generates a list of WorldTiles with varying heights based on SimplexNoise with a RANDOM seed.
     */
    private void generateWorld(){
        double seed = RANDOM.nextDouble() * 100000.0d;
        for (int i = 0; i < WORLD_SIZE; ++i){
            int y = (int) ((SimplexNoise.noise(i * noiseScale, 0.0d, seed) + 1.0d) / 2.0d * 60.0d);
            fillColumn(new Vector3f(i, y + 20));
        }
        grid.populateGrid(tiles);
    }

    /**
     * Fills a one block wide column with different blocks according to the generation algorithm.
     * @param position the position of the initial block.
     */
    private void fillColumn(Vector3f position){
        if (position.getY() < 58.0f && RANDOM.nextBoolean()){
            tiles.add(new TileTree(position.add(new Vector3f(0.0f, 3.0f))));
        }
        Tile topTile = TILE_FACTORY.createTile(position, position.getY() > 55.0f ? EnumTiles.GRASS_SNOW : EnumTiles.GRASS);
        if (RANDOM.nextBoolean()){
            topTile.setScale(new Vector2f(-1.0f, 1.0f));
        }
        tiles.add(topTile);
        {
            int counter = 0;
            for (float i = position.subtractY(1.0f).getY(); i >= 0.0f; --i, ++counter) {
                tiles.add(TILE_FACTORY.createTile(new Vector3f(position.getX(), i), i < 40.0f ? (counter > 18 ? EnumTiles.STONE : EnumTiles.DIRT) : (counter > 20 ? EnumTiles.STONE : EnumTiles.DIRT)));
            }
        }
    }

    /**
     * Clears the world list an generates a new one.
     */
    public void regenerateWorld(){
        tiles.clear();
        grid.clear();
        generateWorld();
    }

    /**
     * Increases/decreases the noise scale with the provided value.
     * @param dScale how much to change the noise scale.
     */
    public void increaseNoiseScale(double dScale){
        noiseScale += dScale;
    }

    /**
     * Resets the noise scale to a specified value.
     */
    public void resetNoiseScale(){
        noiseScale = 0.042d;
    }

    /**
     * Gets the grid of chunks.
     * @return the grid.
     */
    public Grid getGrid(){
        return grid;
    }

    /**
     * Cleans up the all the Tiles.
     */
    void cleanUp() {
        tiles.forEach(Entity::cleanUp);
    }
}