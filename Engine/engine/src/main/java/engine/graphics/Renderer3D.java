package engine.graphics;

import engine.main.PerspectiveCamera;
import engine.math.Matrix4f;
import engine.math.Vector3f;
import org.jetbrains.annotations.NotNull;

@SuppressWarnings({"unused", "WeakerAccess"})
public class Renderer3D
{
    public static SceneData3D sceneData = new SceneData3D();

    public static Light light = new Light(new Vector3f(1f, 2f, 3f), new Vector3f(1f));

    public static void beginScene(@NotNull PerspectiveCamera camera)
    {
        sceneData.setViewMatrix(camera.getViewMatrix());
        sceneData.setViewPosition(camera.getPosition());
        Shader.DEFAULT3D.bind();
    }

    public static void drawCube(Vector3f position)
    {
        drawCube(position, 1f);
    }

    public static void drawCube(Vector3f position, float scale)
    {
        Shader.DEFAULT3D.setMatrix4f(Matrix4f.translate(position, false).multiply(Matrix4f.scale(new Vector3f(scale))), "model");
        Shader.DEFAULT3D.setMatrix4f(sceneData.getViewMatrix(), "view");
        Shader.DEFAULT3D.setMatrix4f(Matrix4f.perspective, "projection");
        SceneData3D.CUBE.bind();
        SceneData3D.CUBE.drawElements();
    }

    public static void drawCube(@NotNull Shader shader, Vector3f position)
    {
        drawCube(shader, position, 1f, new Vector3f(1f));
    }

    public static void drawCube(@NotNull Shader shader, Vector3f position, float scale)
    {
        drawCube(shader, position, scale, new Vector3f(1f));
    }

    public static void drawCube(@NotNull Shader shader, Vector3f position, Vector3f color)
    {
        drawCube(shader, position, 1f, color);
    }

    public static void drawCube(@NotNull Shader shader, Vector3f position, float scale, Vector3f color)
    {
        shader.bind();
        shader.setMatrix4f(Matrix4f.translate(position, false).multiply(Matrix4f.scale(new Vector3f(scale))), "model");
        shader.setMatrix4f(sceneData.getViewMatrix(), "view");
        shader.setMatrix4f(Matrix4f.perspective, "projection");
        shader.setVector3f(color, "objColor");
        shader.setVector3f(light.getColor(), "lightColor");
        shader.setVector3f(light.getPosition(), "lightPosition");
        shader.setVector3f(sceneData.getViewPosition(), "viewPosition");
        SceneData2D.defaultTexture.bind();
        SceneData3D.CUBE.bind();
        SceneData3D.CUBE.drawArrays();
    }

    public static void drawMesh(@NotNull Mesh mesh)
    {
        Shader.DEFAULT3D.bind();
        Shader.DEFAULT3D.setMatrix4f(Matrix4f.translate(new Vector3f(), false), "model");
        Shader.DEFAULT3D.setMatrix4f(sceneData.getViewMatrix(), "view");
        Shader.DEFAULT3D.setMatrix4f(Matrix4f.perspective, "projection");
        //Lighting
        Shader.DEFAULT3D.setVector3f(new Vector3f(1f), "objColor");
        Shader.DEFAULT3D.setVector3f(light.getColor(), "lightColor");
        Shader.DEFAULT3D.setVector3f(light.getPosition(), "lightPosition");
        Shader.DEFAULT3D.setVector3f(sceneData.getViewPosition(), "viewPosition");

        mesh.vertexArray.bind();
        mesh.vertexArray.drawElements();
    }

    /////ROTATION////////////////////////////////////////////////////
    /*
    public static void drawCube(Vector3f position, Vector3f rotation)
    {
        drawCube(position, rotation, 1f);
    }

    public static void drawCube(Vector3f position, Vector3f rotation, float scale)
    {
        Shader.DEFAULT3D.setMatrix4f(Matrix4f.translate(position, false).multiply(Matrix4f.rotate(rotation)).multiply(Matrix4f.scale(new Vector3f(scale))), "model");
        Shader.DEFAULT3D.setMatrix4f(sceneData.getViewMatrix(), "view");
        Shader.DEFAULT3D.setMatrix4f(Matrix4f.perspective, "projection");
        SceneData3D.CUBE.bind();
        SceneData3D.CUBE.drawElements();
    }
    */
}