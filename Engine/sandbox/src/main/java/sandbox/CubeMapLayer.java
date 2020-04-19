package sandbox;

import engine.event.Event;
import engine.event.EventType;
import engine.event.KeyPressedEvent;
import engine.event.MouseMovedEvent;
import engine.event.MouseScrolledEvent;
import engine.graphics.CubeMap;
import engine.graphics.DefaultModels;
import engine.graphics.ForwardRenderer;
import engine.graphics.Model;
import engine.graphics.lighting.DirectionalLight;
import engine.graphics.lighting.SceneLights;
import engine.graphics.lighting.Spotlight;
import engine.graphics.material.NormalMaterial;
import engine.input.Button;
import engine.input.Input;
import engine.layer.Layer;
import engine.main.PerspectiveCamera;
import engine.main.Window;
import engine.math.Matrix4f;
import engine.math.Vector3f;
import org.jetbrains.annotations.NotNull;

public class CubeMapLayer extends Layer
{
    private ForwardRenderer renderer = new ForwardRenderer();
    private PerspectiveCamera perspectiveCamera = new PerspectiveCamera();
    private final Spotlight flashLight = new Spotlight(perspectiveCamera.getPosition(), perspectiveCamera.getDirection(), new Vector3f(1f), 10f, 25f);
    private final DirectionalLight directionalLight = new DirectionalLight(new Vector3f(0f), new Vector3f(-1f, -2f, -3f), new Vector3f(1f, 1f, 0.8f));
    private final SceneLights sceneLights = new SceneLights();

    private CubeMap environmentMap = new CubeMap("default_hdris/cape_hill_4k.hdr");
    private Model model = new Model(
            DefaultModels.PLANE.mesh,
            new NormalMaterial(
                    "default_textures/brickwall.jpg",
                    "default_textures/brickwall_normal.jpg",
                    1f, 0.5f, 480f, 1f
            ),
            Matrix4f.rotate(new Vector3f(0f, 1f, 0f), 45f)
    );

    public CubeMapLayer(String name)
    {
        super(name);
        sceneLights.addLight(flashLight);
        sceneLights.addLight(directionalLight);
        renderer.setSceneLights(sceneLights);
    }

    @Override
    public void onEvent(Window window, @NotNull Event event)
    {
        if (event.eventType == EventType.MOUSE_MOVED)
        {
            if (window.isFocused())
            {
                perspectiveCamera.getController().handleRotation(perspectiveCamera, (MouseMovedEvent) event);
            }
        }
        if (event.eventType == EventType.KEY_PRESSED)
        {
            EventHandler.onKeyPress((KeyPressedEvent) event, window);
        }
        if (event.eventType == EventType.MOUSE_SCROLLED)
        {
            perspectiveCamera.getController().handleScroll((MouseScrolledEvent) event);
        }
        if (event.eventType == EventType.MOUSE_PRESSED)
        {
            if (Input.wasMouseButton(event, Button.MOUSE_BUTTON_1))
            {
                flashLight.setColor(new Vector3f(0f));
            }
            if (Input.wasMouseButton(event, Button.MOUSE_BUTTON_2))
            {
                flashLight.setColor(new Vector3f(1f));
            }
        }
    }

    private float rotation;

    @Override
    public void onUpdate(@NotNull Window window, float deltaTime)
    {
        if (window.isFocused())
        {
            perspectiveCamera.getController().handleMovement(perspectiveCamera, window.getWindowHandle(), deltaTime);
        }
        rotation += 10f * deltaTime;
        //reflectiveSuzanne.setTransformationMatrix(Matrix4f.translate(new Vector3f(0f, 0f, 8f)).multiply(Matrix4f.rotate(new Vector3f(1f), rotation)).multiply(Matrix4f.scale(new Vector3f(5f))));
        //refractiveSuzanne.setTransformationMatrix(Matrix4f.translate(new Vector3f(0f, 0f, -8f)).multiply(Matrix4f.rotate(new Vector3f(-1f), rotation)).multiply(Matrix4f.scale(new Vector3f(5f))));
        flashLight.setDirection(perspectiveCamera.getDirection());
        flashLight.setPosition(perspectiveCamera.getPosition());
    }

    @Override
    public void onRender()
    {
        renderer.begin(perspectiveCamera);
        model.render(renderer);
        environmentMap.renderSkyBox();
    }

    @Override
    public void onDispose()
    {
        environmentMap.delete();
        model.delete();
    }
}
