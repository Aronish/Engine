package engine.main;

import engine.event.MouseMovedEvent;
import engine.event.MouseScrolledEvent;
import engine.event.WindowFocusEvent;
import engine.math.Matrix4f;
import engine.math.Vector3f;

@SuppressWarnings({"unused", "WeakerAccess"})
public class PerspectiveCamera extends Camera
{
    private float pitch = 0f, yaw = 0f;
    private Vector3f direction = new Vector3f(
            (float) Math.cos(Math.toRadians(yaw) * Math.cos(Math.toRadians(pitch))),
            (float) Math.sin(Math.toRadians(pitch)),
            (float) Math.sin(Math.toRadians(yaw) * Math.cos(Math.toRadians(pitch)))
    );
    private Matrix4f lookAt;

    private PerspectiveCameraController controller;

    public PerspectiveCamera()
    {
        this(Vector3f.IDENTITY);
    }

    public PerspectiveCamera(Vector3f position)
    {
        this.position = position;
        controller = new PerspectiveCameraController(this);
    }

    @Override
    public void calculateViewMatrix()
    {
        direction = new Vector3f(
                (float) Math.cos(Math.toRadians(yaw)) * Math.cos(Math.toRadians(pitch)),
                (float) Math.sin(Math.toRadians(pitch)),
                (float) Math.sin(Math.toRadians(yaw)) * Math.cos(Math.toRadians(pitch))
        );
        direction.normalize();
        viewMatrix = Matrix4f.lookAt(position, Vector3f.add(position, direction), Vector3f.UP);
    }

    @Override
    public void handleMovement(Window window, float deltaTime)
    {
        controller.handleMovement(window, deltaTime);
    }

    @Override
    public void handleRotation(MouseMovedEvent event)
    {
        controller.handleRotation(event);
    }

    @Override
    public void handleScroll(MouseScrolledEvent event)
    {
        controller.handleScroll(event);
    }

    @Override
    public void onFocus(WindowFocusEvent event)
    {
        controller.onFocus(event);
    }

    public void setDirection(Vector3f direction)
    {
        this.direction = direction;
    }

    public void addYaw(float yaw)
    {
        this.yaw += yaw;
        calculateViewMatrix();
    }

    public void addPitch(float pitch)
    {
        this.pitch += pitch;
        if (this.pitch > 89f) this.pitch = 89f;
        if (this.pitch < -89f) this.pitch = -89f;
        calculateViewMatrix();
    }

    public void rotate(float yaw, float pitch)
    {
        this.yaw += yaw;
        this.pitch += pitch;
        if (this.pitch > 89f) this.pitch = 89f;
        if (this.pitch < -89f) this.pitch = -89f;
        calculateViewMatrix();
    }

    public Vector3f getDirection()
    {
        return this.direction;
    }
}
