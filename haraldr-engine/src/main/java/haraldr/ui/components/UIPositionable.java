package haraldr.ui.components;

import haraldr.math.Vector2f;

public interface UIPositionable
{
    default void setPosition(Vector2f position) {}
    default void setSize(Vector2f size) {}
    default float getVerticalSize()
    {
        return 0f;
    }
}