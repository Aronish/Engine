package sandbox;

import haraldr.graphics.Renderer;
import haraldr.main.Application;
import haraldr.main.ProgramArguments;
import haraldr.scenegraph.Scene2D;
import haraldr.scenegraph.Scene3D;
import haraldr.main.Window;

class ExampleApplication extends Application
{
    @Override
    public void start()
    {
        int samples = ProgramArguments.getIntOrDefault("MSAA", 0);
        Window.WindowProperties windowProperties = new Window.WindowProperties(1280, 720, samples, true, false, false);
        init(windowProperties);
        loop();
    }

    @Override
    protected void init(Window.WindowProperties windowProperties)
    {
        super.init(windowProperties);
        Renderer.setClearColor(0.1f, 0.1f, 0.1f, 1f);
        setActiveScene(new TestScene());
        setActiveOverlay(new DebugOverlay());
    }
}