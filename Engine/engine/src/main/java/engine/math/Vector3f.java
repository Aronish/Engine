package engine.math;

import org.jetbrains.annotations.NotNull;

import static engine.main.Application.MAIN_LOGGER;

@SuppressWarnings("unused")
public class Vector3f
{
    private float x, y, z;

    public Vector3f() {}

    public Vector3f(float x, float y)
    {
        this(x, y, 0.0f);
    }

    public Vector3f(float x, float y, float z)
    {
        this.x = x;
        this.y = y;
        this.z = z;
    }

    public Vector3f(double x, double y, double z)
    {
        this.x = (float) x;
        this.y = (float) y;
        this.z = (float) z;
    }

    public void set(float x, float y)
    {
        this.x = x;
        this.y = y;
    }

    public void set(@NotNull Vector3f other)
    {
        x = other.x;
        y = other.y;
    }

    public void setX(float x)
    {
        this.x = x;
    }

    public void setY(float y)
    {
        this.y = y;
    }

    public float getX()
    {
        return x;
    }

    public float getY()
    {
        return y;
    }

    public float getZ()
    {
        return z;
    }

    public Vector3f addReturn(@NotNull Vector3f other)
    {
        return new Vector3f(x + other.getX(), y + other.getY(), z + other.getZ());
    }

    public void add(@NotNull Vector3f other)
    {
        x += other.x;
        y += other.y;
        z += other.z;
    }

    /**
     * Subtracts a value from the y component.
     * @param dy the value to subtract.
     * @return the resulting vector.
     */
    public Vector3f subtractY(float dy)
    {
        return new Vector3f(x, y - dy);
    }

    public Vector3f multiply(float scalar)
    {
        return new Vector3f(x * scalar, y * scalar, z * scalar);
    }

    public void printVector()
    {
        MAIN_LOGGER.info("X: " + x + " Y: " + y + " Z: " + z);
    }
}