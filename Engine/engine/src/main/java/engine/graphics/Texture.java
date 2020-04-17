package engine.graphics;

import engine.main.IOUtils;
import org.lwjgl.stb.STBImage;
import org.lwjgl.system.MemoryStack;

import java.nio.ByteBuffer;
import java.nio.IntBuffer;

import static engine.main.Application.MAIN_LOGGER;
import static org.lwjgl.opengl.GL11.GL_LINEAR_MIPMAP_LINEAR;
import static org.lwjgl.opengl.GL11.GL_NEAREST;
import static org.lwjgl.opengl.GL11.GL_REPEAT;
import static org.lwjgl.opengl.GL11.GL_RGB;
import static org.lwjgl.opengl.GL11.GL_RGB8;
import static org.lwjgl.opengl.GL11.GL_RGBA;
import static org.lwjgl.opengl.GL11.GL_RGBA8;
import static org.lwjgl.opengl.GL11.GL_TEXTURE_2D;
import static org.lwjgl.opengl.GL11.GL_TEXTURE_MAG_FILTER;
import static org.lwjgl.opengl.GL11.GL_TEXTURE_MIN_FILTER;
import static org.lwjgl.opengl.GL11.GL_TEXTURE_WRAP_S;
import static org.lwjgl.opengl.GL11.GL_TEXTURE_WRAP_T;
import static org.lwjgl.opengl.GL11.GL_UNSIGNED_BYTE;
import static org.lwjgl.opengl.GL11.glBindTexture;
import static org.lwjgl.opengl.GL11.glDeleteTextures;
import static org.lwjgl.opengl.GL11.glGenTextures;
import static org.lwjgl.opengl.GL11.glTexImage2D;
import static org.lwjgl.opengl.GL11.glTexParameteri;
import static org.lwjgl.opengl.GL30.glGenerateMipmap;
import static org.lwjgl.opengl.GL45.glBindTextureUnit;

public class Texture
{
    public static final Texture DEFAULT_TEXTURE = new Texture(1, 1, new int[] { -1 });

    private int width, height;
    private int texture;

    private Texture(int width, int height, int[] pixelData)
    {
        this.width = width;
        this.height = height;
        texture = createTexture(pixelData);
    }

    public Texture(String path)
    {
        texture = load(path);
    }

    private int load(String path)
    {
        ByteBuffer image;
        STBImage.stbi_set_flip_vertically_on_load(true);
        int internalFormat, format;
        try (MemoryStack stack = MemoryStack.stackPush())
        {
            IntBuffer width = stack.mallocInt(1);
            IntBuffer height = stack.mallocInt(1);
            IntBuffer comps = stack.mallocInt(1);
            ByteBuffer data = IOUtils.readResource(path, (stream -> IOUtils.inputStreamToByteBuffer(stream, 2))); //I don't know what this buffer size is
            if (data != null) image = STBImage.stbi_load_from_memory(data, width, height, comps, 0);
            else throw new NullPointerException("Image at " + path + " not found!");

            this.width = width.get();
            this.height = height.get();
            switch (comps.get())
            {
                case 3:
                    internalFormat = GL_RGB8;
                    format = GL_RGB;
                    break;
                case 4:
                    internalFormat = GL_RGBA8;
                    format = GL_RGBA;
                    break;
                default:
                    MAIN_LOGGER.error("Image format not supported!");
                    internalFormat = GL_RGBA8;
                    format = GL_RGBA;
                    break;
            }
        }
        assert image != null : "Somehow image was null here!";
        int texture = glGenTextures();
        glBindTexture(GL_TEXTURE_2D, texture);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, GL_REPEAT);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, GL_REPEAT);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_NEAREST);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_LINEAR_MIPMAP_LINEAR);
        glTexImage2D(GL_TEXTURE_2D, 0, internalFormat, width, height, 0, format, GL_UNSIGNED_BYTE, image);
        glGenerateMipmap(GL_TEXTURE_2D);
        glBindTexture(GL_TEXTURE_2D, 0);
        STBImage.stbi_image_free(image);
        return texture;
    }

    private int createTexture(int[] data)
    {
        //RAW FORMAT: (ARGB)
        int[] image = new int[width * height];
        for (int i = 0; i < width * height; i++)
        {
            int a = (data[i] & 0xff000000) >> 24;
            int r = (data[i] & 0xff0000) >> 16;
            int g = (data[i] & 0xff00) >> 8;
            int b = (data[i] & 0xff);
            image[i] = a << 24 | b << 16 | g << 8 | r;
        }
        int result = glGenTextures();
        glBindTexture(GL_TEXTURE_2D, result);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, GL_REPEAT);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, GL_REPEAT);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_NEAREST);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_LINEAR_MIPMAP_LINEAR);
        glTexImage2D(GL_TEXTURE_2D, 0, GL_RGBA8, width, height, 0, GL_RGBA, GL_UNSIGNED_BYTE, image);
        glGenerateMipmap(GL_TEXTURE_2D);
        glBindTexture(GL_TEXTURE_2D, 0);
        return result;
    }

    public int getWidth()
    {
        return width;
    }

    public void bind(int unit)
    {
        glBindTextureUnit(unit, texture);
    }

    public void unbind(int unit)
    {
        glBindTextureUnit(unit, 0);
    }

    public void delete()
    {
        glDeleteTextures(texture);
    }
}
