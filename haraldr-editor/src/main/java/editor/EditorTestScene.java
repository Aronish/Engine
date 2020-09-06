package editor;

import haraldr.ecs.BoundingSphereComponent;
import haraldr.ecs.Entity;
import haraldr.ecs.ModelComponent;
import haraldr.event.Event;
import haraldr.graphics.CubeMap;
import haraldr.graphics.Renderer3D;
import haraldr.graphics.lighting.PointLight;
import haraldr.input.Input;
import haraldr.input.KeyboardKey;
import haraldr.main.Window;
import haraldr.math.Vector3f;
import haraldr.scene.Scene3D;

public class EditorTestScene extends Scene3D
{
    private Entity ape = registry.createEntity(new Vector3f(0f, 0f, 0f), new Vector3f(0.5f));
    private Entity another = registry.createEntity(new Vector3f(4f, 0f, 0f), new Vector3f(0.3f));
    private float interpolation;

    private PointLight pointLight = new PointLight(new Vector3f(), new Vector3f(5f, 3f, 1f));

    @Override
    public void onClientActivate()
    {
        setSceneLights(
                pointLight
        );
        setSkyBox(CubeMap.createEnvironmentMap("default_hdris/wooden_lounge_4k.hdr"));
        new ModelComponent("default_models/model.json");
        registry.addComponent(new ModelComponent("default_models/model.json"), ape);
        registry.addComponent(new BoundingSphereComponent(0.75f), ape);
        registry.addComponent(new ModelComponent("default_models/model.json"), another);
        registry.addComponent(new BoundingSphereComponent(0.3f), another);
    }

    @Override
    protected void onClientEvent(Event event, Window window)
    {
    }

    @Override
    protected void onClientUpdate(float deltaTime, Window window)
    {
        if (Input.isKeyPressed(window, KeyboardKey.KEY_UP))     Renderer3D.addExposure(deltaTime);
        if (Input.isKeyPressed(window, KeyboardKey.KEY_DOWN))   Renderer3D.addExposure(-deltaTime);
        if (Input.isKeyPressed(window, KeyboardKey.KEY_KP_9))   interpolation += 1f * deltaTime;
        if (Input.isKeyPressed(window, KeyboardKey.KEY_KP_7))   interpolation -= 1f * deltaTime;
        pointLight.setPosition(new Vector3f(Math.sin(interpolation), 0f, Math.cos(interpolation)));
    }

    @Override
    protected void onClientRender()
    {
        sceneLights.renderLights();
    }

    @Override
    protected void onClientDispose()
    {

    }
}