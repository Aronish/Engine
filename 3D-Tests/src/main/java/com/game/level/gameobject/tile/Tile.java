package com.game.level.gameobject.tile;

import com.game.level.Entity;
import com.game.level.gameobject.EnumGameObjects;
import com.game.math.Vector3f;

/**
 * Simple static entity.
 */
public abstract class Tile extends Entity {

    /**
     * Constructor without rotation and scale parameters.
     * @param position the initial position of this Tile.
     * @param tileType the type of this Tile.
     */
    Tile(Vector3f position, EnumGameObjects tileType){
        this(position, 0.0f, 1.0f, tileType);
    }

    /**
     * Constructor with position, rotation and scale parameters.
     * @param position the initial position of this Tile.
     * @param rotation the initial rotation of this Tile.
     * @param scale the initial scale of this Tile.
     * @param gameObjectType the type of this Tile.
     */
    private Tile(Vector3f position, float rotation, float scale, EnumGameObjects gameObjectType) {
        super(position, rotation, scale, gameObjectType);
    }
}
