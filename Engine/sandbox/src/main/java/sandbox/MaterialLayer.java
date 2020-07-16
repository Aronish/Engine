package sandbox;

import engine.event.Event;
import engine.event.EventType;
import engine.graphics.CubeMap;
import engine.graphics.JsonModel;
import engine.graphics.Renderer3D;
import engine.graphics.lighting.PointLight;
import engine.graphics.lighting.SceneLights;
import engine.input.Input;
import engine.input.Key;
import engine.layer.Layer;
import engine.main.Window;
import engine.math.Vector3f;
import org.jetbrains.annotations.NotNull;

public class MaterialLayer extends Layer
{
    private PointLight l1 = new PointLight(new Vector3f(0f, 1f, 0f), new Vector3f(7.5f, 2.5f, 2.5f));
    private float interpolation;

    private CubeMap environmentMap = CubeMap.createEnvironmentMap("default_hdris/NorwayForest_4K_hdri_sphere.hdr");
    private JsonModel model = new JsonModel("default_models/test.json");

    public MaterialLayer()
    {
        SceneLights sl = new SceneLights();
        sl.addLight(l1);
        Renderer3D.setSceneLights(sl);
    }

    @Override
    public void onEvent(Window window, @NotNull Event event)
    {
        if (event.eventType == EventType.KEY_PRESSED)
        {
            if (Input.wasKey(event, Key.KEY_R))
            {
                model.refresh();
            }
        }
    }

    @Override
    public void onUpdate(Window window, float deltaTime)
    {
        if (Input.isKeyPressed(window, Key.KEY_UP))     Renderer3D.addExposure(1f * deltaTime);
        if (Input.isKeyPressed(window, Key.KEY_DOWN))   Renderer3D.addExposure(-1f * deltaTime);
        if (Input.isKeyPressed(window, Key.KEY_KP_7))   interpolation += 1f * deltaTime;
        if (Input.isKeyPressed(window, Key.KEY_KP_9))   interpolation -= 1f * deltaTime;
        l1.setPosition(new Vector3f(Math.sin(interpolation), 0.5f, Math.cos(interpolation)));
    }

    @Override
    public void onRender()
    {
        model.render();
        l1.render();
        environmentMap.renderSkyBox();
    }

    @Override
    public void onDispose()
    {
    }
}
