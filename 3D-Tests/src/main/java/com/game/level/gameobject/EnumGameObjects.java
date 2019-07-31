package com.game.level.gameobject;

import com.game.graphics.Models;
import com.game.graphics.Model;

/**
 * All possible types of game objects. Holds a reference to the Model to be associated with the object.
 */
public enum EnumGameObjects {

    ///// WORLD TILES /////////////////////////////////
    GRASS(Models.GRASS_TILE),
    DIRT(Models.DIRT_TILE),
    STONE(Models.STONE_TILE),
    GRASS_SNOW(Models.GRASS_SNOW_TILE),
    TREE(Models.TREE),

    ///// MOVABLES ////////////////////////////////////
    PLAYER(Models.PLAYER);

    public final Model model;

    EnumGameObjects(Model model){
        this.model = model;
    }
}