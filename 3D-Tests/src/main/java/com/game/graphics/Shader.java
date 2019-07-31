package com.game.graphics;

import java.io.IOException;
import java.io.InputStream;

import static org.lwjgl.opengl.GL20.glDeleteProgram;
import static org.lwjgl.opengl.GL20.glGetProgramInfoLog;
import static org.lwjgl.opengl.GL20.glGetShaderInfoLog;
import static org.lwjgl.opengl.GL20.glGetUniformLocation;
import static org.lwjgl.opengl.GL20.glUniformMatrix4fv;
import static org.lwjgl.opengl.GL46.GL_FRAGMENT_SHADER;
import static org.lwjgl.opengl.GL46.GL_VERTEX_SHADER;
import static org.lwjgl.opengl.GL46.glAttachShader;
import static org.lwjgl.opengl.GL46.glCompileShader;
import static org.lwjgl.opengl.GL46.glCreateProgram;
import static org.lwjgl.opengl.GL46.glCreateShader;
import static org.lwjgl.opengl.GL46.glDeleteShader;
import static org.lwjgl.opengl.GL46.glLinkProgram;
import static org.lwjgl.opengl.GL46.glShaderSource;
import static org.lwjgl.opengl.GL46.glUseProgram;
import static org.lwjgl.opengl.GL46.glValidateProgram;

/**
 * Loads a shader program with a vertex and fragment shader.
 */
public class Shader {

    private int shaderProgram;

    /**
     * Constructor with a parameter for the general shader file path.
     * Both kinds of shaders must have the same name as the extensions are added afterwards for simplicity.
     * @param generalShaderPath the general shader file path, with no extension.
     */
    Shader(String generalShaderPath){
        this(generalShaderPath + ".vert", generalShaderPath + ".frag");
    }

    /**
     * Private constructor that takes in the different paths of the shader files. Reads the shader files, creates shaders and compiles them into a shader program.
     * @param vertexShaderPath the path of the vertex shader.
     * @param fragmentShaderPath the path of the fragment shader.
     */
    Shader(String vertexShaderPath, String fragmentShaderPath){
        int vertexShader = createShader(GL_VERTEX_SHADER, readShaderFile(vertexShaderPath));
        int fragmentShader = createShader(GL_FRAGMENT_SHADER, readShaderFile(fragmentShaderPath));

        glCompileShader(vertexShader);
        //System.out.println(glGetShaderInfoLog(vertexShader));
        glCompileShader(fragmentShader);
        //System.out.println(glGetShaderInfoLog(fragmentShader));

        shaderProgram = glCreateProgram();
        glAttachShader(shaderProgram, vertexShader);
        glAttachShader(shaderProgram, fragmentShader);
        glLinkProgram(shaderProgram);
        glValidateProgram(shaderProgram);
        //System.out.println(glGetProgramInfoLog(shaderProgram));
        glDeleteShader(vertexShader);
        glDeleteShader(fragmentShader);
    }

    /**
     * Creates a shader object for the specified shader type with the specified source code.
     * @param shaderType the shader type to create (GL_VERTEX_SHADER or GL_FRAGMENT_SHADER).
     * @param source the source code in a single string.
     * @return the shader object ID.
     */
    private int createShader(int shaderType, String source){
        int shader = glCreateShader(shaderType);
        glShaderSource(shader, source);
        return shader;
    }

    /**
     * Reads a shader file and creates a string with the source code.
     * @param path the path of the file to be read.
     * @return the source code string.
     */
    private String readShaderFile(String path){
        StringBuilder sb = new StringBuilder();
        try (InputStream file = Shader.class.getClassLoader().getResourceAsStream(path)){
            if (file == null){
                throw new NullPointerException("Shader file not found!");
            }
            int data = file.read();
            while (data != -1){
                sb.append((char) data);
                data = file.read();
            }
        }catch (IOException e){
            e.printStackTrace();
        }
        return sb.toString();
    }

    /**
     * Sets data of a uniform mat4 with the specified name.
     * @param matrix the matrix data.
     * @param name the name of the mat4 uniform.
     */
    void setMatrix(float[] matrix, String name){
        int matrixLocation = glGetUniformLocation(shaderProgram, name);
        glUniformMatrix4fv(matrixLocation, false, matrix);
    }

    /**
     * Uses the shader program.
     */
    public void use(){
        glUseProgram(shaderProgram);
    }

    /**
     * Unuses the shader program.
     */
    private void unuse(){
        glUseProgram(0);
    }

    /**
     * Deletes the shader program.
     */
    void delete(){
        unuse();
        glDeleteProgram(shaderProgram);
    }
}