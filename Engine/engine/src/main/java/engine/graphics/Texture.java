package engine.graphics;

import engine.main.EntryPoint;
import org.lwjgl.stb.STBImage;
import org.lwjgl.system.MemoryStack;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.IntBuffer;

import static engine.main.Application.MAIN_LOGGER;
import static org.lwjgl.opengl.GL11.GL_LINEAR;
import static org.lwjgl.opengl.GL11.GL_NEAREST;
import static org.lwjgl.opengl.GL11.GL_REPEAT;
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
import static org.lwjgl.system.MemoryStack.stackPush;

/**
 * Represents an OpenGL texture.
 */
public class Texture
{
    private int width, height;
    private int texture;

    public Texture(String path, boolean stb)
    {
        if (stb)
        {
            texture = loadSTB(path);
        }else
        {
            texture = load(path);
        }
    }

    //TODO: Needs updating to module system.
    /**
     * Reads the pixel data of the texture file and creates an OpenGL texture.
     * Could probably use stb_image.h for cleaner code, but would require weird path recognization.
     * @param path the path of the texture.
     * @return the OpenGL texture ID.
     */
    private int load(String path)
    {
        int[] pixels = null;
        try
        {
            InputStream imageStream = EntryPoint.application.getClass().getModule().getResourceAsStream(path);
            if (imageStream == null)
            {
                throw new NullPointerException("Texture not found!");
            }
            BufferedImage image = ImageIO.read(imageStream);
            width = image.getWidth();
            height = image.getHeight();
            pixels = new int[width * height];
            image.getRGB(0, 0, width, height, pixels, 0, width);
        }catch (IOException e)
        {
            e.printStackTrace();
        }

        int[] data = new int[width * height];
        if (pixels != null)
        {
            for (int i = 0; i < width * height; i++)
            {
                int a = (pixels[i] & 0xff000000) >> 24;
                int r = (pixels[i] & 0xff0000) >> 16;
                int g = (pixels[i] & 0xff00) >> 8;
                int b = (pixels[i] & 0xff);
                data[i] = a << 24 | b << 16 | g << 8 | r;
            }
        }else
        {
            throw new IllegalStateException("Texture was not read and stored properly!");
        }

        int result = glGenTextures();
        glBindTexture(GL_TEXTURE_2D, result);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, GL_REPEAT);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, GL_REPEAT);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_NEAREST);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_LINEAR);
        glTexImage2D(GL_TEXTURE_2D, 0, GL_RGBA8, width, height, 0, GL_RGBA, GL_UNSIGNED_BYTE, data);
        glBindTexture(GL_TEXTURE_2D, 0);
        return result;
    }

    private int loadSTB(String path)
    {
        /*
        ByteBuffer data = null;
        STBImage.stbi_set_flip_vertically_on_load(true);
        try(MemoryStack stack = stackPush())
        {
            IntBuffer pWidth = stack.mallocInt(1);
            IntBuffer pHeight = stack.mallocInt(1);
            IntBuffer pChannels = stack.mallocInt(1);
            try
            {
                data = STBImage.stbi_load_from_memory(Font.ioResourceToByteBuffer(path, 256*256*4), pWidth, pHeight, pChannels, 4);
                MAIN_LOGGER.info(pWidth.get(0) + " " + pHeight.get(0) + " " + pChannels.get(0));
            }catch (IOException e)
            {
                MAIN_LOGGER.info("stbi_load_from_memory failed!");
                e.printStackTrace();
            }
            //TODO: Try getting stb_image working.
            width = pWidth.get(0);
            height = pHeight.get(0);
        }
        if (data == null) throw new RuntimeException("Data was null!");

        int result = glGenTextures();
        glBindTexture(GL_TEXTURE_2D, result);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, GL_REPEAT);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, GL_REPEAT);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_NEAREST);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_LINEAR);
        glTexImage2D(GL_TEXTURE_2D, 0, GL_RGBA8, width, height, 0, GL_RGBA, GL_UNSIGNED_BYTE, data);
        glBindTexture(GL_TEXTURE_2D, 0);

        STBImage.stbi_image_free(data);
        */
        return texture;
    }

    public int getWidth()
    {
        return width;
    }

    public void bind()
    {
        glBindTexture(GL_TEXTURE_2D, texture);
    }

    public void unbind()
    {
        glBindTexture(GL_TEXTURE_2D, 0);
    }

    public void delete()
    {
        unbind();
        glDeleteTextures(texture);
    }
}
