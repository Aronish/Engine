package main.java.physics;

import main.java.Camera;
import main.java.debug.Logger;
import main.java.graphics.TexturedModel;
import main.java.level.Entity;
import main.java.level.Level;
import main.java.level.Player;
import main.java.math.Vector3f;

import static main.java.physics.EnumDirection.EAST;
import static main.java.physics.EnumDirection.NORTH;
import static main.java.physics.EnumDirection.SOUTH;
import static main.java.physics.EnumDirection.WEST;

/**
 * Class that handles collision detection.
 */
public class CollisionDetector {

    /**
     * Checks whether there is a collision between the Player in the Level and the current TexturedModel in the Entity.
     * @param level the Level that has the Player to check collisions with.
     * @param entity the Entity, whose TexturedModel is currently checked.
     * @param texturedModel the TexturedModel to check collision against.
     * @return true if there was a collision on both axis.
     */
    public static boolean checkCollision(Level level, Entity entity, TexturedModel texturedModel){
        Player player = level.getPlayer();
        boolean collisionX = player.getPosition().x + player.getWidth() > entity.getPosition().x + texturedModel.getRelativePosition().x && entity.getPosition().x + texturedModel.getRelativePosition().x + texturedModel.getAABB().getWidth() > player.getPosition().x;
        boolean collisionY = player.getPosition().y - player.getHeight() < entity.getPosition().y + texturedModel.getRelativePosition().y && entity.getPosition().y + texturedModel.getRelativePosition().y - texturedModel.getAABB().getHeight() < player.getPosition().y;
        return collisionX && collisionY;
    }

    /**
     * Gets the direction, in which the collision most likely happened.
     * @param level the Level that has the Player to check collisions with.
     * @param entity the Entity, whose TexturedModel is currently checked.
     * @param texturedModel the TexturedModel to check collision against.
     * @return a custom data pair with the direction and overlap distance.
     */
    public static CollisionDataMap getCollisionDirection(Level level, Entity entity, TexturedModel texturedModel){
        Player player = level.getPlayer();
        float topCollision = (entity.getPosition().y + texturedModel.getRelativePosition().y) - (player.getPosition().y - player.getHeight());
        float rightCollision = (entity.getPosition().x + texturedModel.getRelativePosition().x) + texturedModel.getAABB().getWidth() - player.getPosition().x;
        float leftCollision = player.getPosition().x + player.getWidth() - (entity.getPosition().x + texturedModel.getRelativePosition().x);
        float bottomCollision = player.getPosition().y - ((entity.getPosition().y + texturedModel.getRelativePosition().y) - texturedModel.getAABB().getHeight());
        CollisionDataMap collisionDataMap = null;
        if (topCollision < bottomCollision && topCollision < leftCollision && topCollision < rightCollision ) {
            collisionDataMap = new CollisionDataMap(NORTH, topCollision);
        }
        if (rightCollision < leftCollision && rightCollision < topCollision && rightCollision < bottomCollision ) {
            collisionDataMap = new CollisionDataMap(EAST, rightCollision);
        }
        if (leftCollision < rightCollision && leftCollision < topCollision && leftCollision < bottomCollision) {
            collisionDataMap = new CollisionDataMap(WEST, leftCollision);
        }
        if (bottomCollision < topCollision && bottomCollision < leftCollision && bottomCollision < rightCollision) {
            collisionDataMap = new CollisionDataMap(SOUTH, bottomCollision);
        }
        return collisionDataMap;
    }

    /**
     * Responds to a collision by displacing the Player with the overlap distance.
     * @param collisionDataMap the data pair with direction and overlap distance.
     * @param level level the Level that has the Player to check collisions with.
     */
    public static void doCollision(CollisionDataMap collisionDataMap, Level level) {
        if (collisionDataMap == null){
            Logger.setErrorLevel();
            Logger.log("Collision error");
            throw new RuntimeException("CollisionDataMap was null!");
        }
        float inside = collisionDataMap.getInside();
        Player player = level.getPlayer();
        switch (collisionDataMap.getCollisionDirection()) {
            case NORTH:
                player.addPosition(new Vector3f(0.0f, inside));
                Camera.addPosition(new Vector3f(0.0f, inside * Camera.scale));
                break;
            case EAST:
                player.addPosition(new Vector3f(inside, 0.0f));
                Camera.addPosition(new Vector3f(inside * Camera.scale, 0.0f));
                break;
            case WEST:
                player.addPosition(new Vector3f(-inside, 0.0f));
                Camera.addPosition(new Vector3f(-inside * Camera.scale, 0.0f));
                break;
            case SOUTH:
                player.addPosition(new Vector3f(0.0f, -inside));
                Camera.addPosition(new Vector3f(0.0f, -inside * Camera.scale));
                break;
        }
    }
}