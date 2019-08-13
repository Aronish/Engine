package com.game.layer;

import java.util.ArrayDeque;
import java.util.Iterator;

public class LayerStack implements Iterable<Layer> {

    public final ArrayDeque<Layer> layerStack;

    public LayerStack(){
        layerStack = new ArrayDeque<>();
    }

    public void pushLayer(Layer layer){
        layerStack.push(layer);
    }

    @Override
    public Iterator<Layer> iterator() {
        return layerStack.iterator();
    }
}