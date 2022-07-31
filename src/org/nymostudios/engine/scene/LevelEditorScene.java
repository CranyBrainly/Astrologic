package org.nymostudios.engine.scene;

import static org.lwjgl.opengl.GL20.*;
import static org.lwjgl.opengl.GL30.*;

import java.nio.FloatBuffer;
import java.nio.IntBuffer;

import org.joml.Vector2f;
import org.lwjgl.BufferUtils;
import org.nymostudios.components.FontRenderer;
import org.nymostudios.components.SpriteRenderer;
import org.nymostudios.engine.Camera;
import org.nymostudios.engine.entity.GameObject;
import org.nymostudios.engine.renderer.Shader;
import org.nymostudios.engine.renderer.Texture;
import org.nymostudios.util.Time;

public class LevelEditorScene extends Scene {

    private float[] vertexArray = {
        // Position             // Colour [R, G, B, A]    // UV Coordinates
        100.5f,   0.5f, 0.0f,   1.0f, 0.0f, 0.0f, 1.0f,   1, 1, // Bottom right
          0.5f, 100.5f, 0.0f,   0.0f, 1.0f, 0.0f, 1.0f,   0, 0, // Top Left
        100.5f, 100.5f, 0.0f,   0.0f, 0.0f, 1.0f, 1.0f,   1, 0, // Top Right
          0.5f,   0.5f, 0.0f,   1.0f, 1.0f, 0.0f, 1.0f,   0, 1, // Bottom Left
    };

    // Counter clockwise order //
    private int[] elementArray = {
        2, 1, 0, // Top right triangle //
        0, 1, 3, // Bottom left trangle //
    };

    private Shader defaultShader;
    private Texture testTexture;

    GameObject testObj;

    private boolean firstTime = false;

    private int vaoID, vboID, eboID;

    public LevelEditorScene() {
    }

    @Override
    public void init() {
        System.out.println("Test object in creation");
        this.testObj = new GameObject("test object");
        this.testObj.addComponent(new SpriteRenderer());
        this.testObj.addComponent(new FontRenderer());
        this.addGameObjectToScene(this.testObj);

        this.camera = new Camera(new Vector2f());

        defaultShader = new Shader("engine/shaders/default.glsl");
        defaultShader.compile();

        this.testTexture = new Texture("sword.png");
        
          /////////////////////////////////////////////////////////////////////
         //// Generate VAO, VBO, and EBO buffers and send them to the GPU ////
        /////////////////////////////////////////////////////////////////////
        vaoID = glGenVertexArrays();
        glBindVertexArray(vaoID);

        // Create a float buffer of vertices //
        FloatBuffer vertexBuffer = BufferUtils.createFloatBuffer(vertexArray.length);
        vertexBuffer.put(vertexArray).flip();

        // Create VBO upload the vertex buffer //
        vboID = glGenBuffers();
        glBindBuffer(GL_ARRAY_BUFFER, vboID);
        glBufferData(GL_ARRAY_BUFFER, vertexBuffer, GL_STATIC_DRAW);

        // Create the indices and upload //
        IntBuffer elementBuffer = BufferUtils.createIntBuffer(elementArray.length);
        elementBuffer.put(elementArray).flip();

        eboID = glGenBuffers();
        glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, eboID);
        glBufferData(GL_ELEMENT_ARRAY_BUFFER, elementBuffer, GL_STATIC_DRAW);

        // Add the vertex attribute pointers //
        int positionsSize = 3;
        int colorSize = 4;
        int uvSize = 2;
        int vertexSizeBytes = (positionsSize + colorSize + uvSize) * Float.BYTES;
        glVertexAttribPointer(0, positionsSize, GL_FLOAT, false, vertexSizeBytes, 0);
        glEnableVertexAttribArray(0);

        glVertexAttribPointer(1, colorSize, GL_FLOAT, false, vertexSizeBytes, positionsSize * Float.BYTES);
        glEnableVertexAttribArray(1);

        glVertexAttribPointer(2, uvSize, GL_FLOAT, false, vertexSizeBytes, (positionsSize + colorSize) * Float.BYTES);
        glEnableVertexAttribArray(2);
    }

    @Override
    public void update(float dt) {
        // camera.position.x -= dt * 50f;
        // camera.position.y -= dt * 20f;

        defaultShader.use();

        // Upload texture to Shader //
        defaultShader.uploadTexture("TEXTURE_SAMPLER", 0);
        glActiveTexture(GL_TEXTURE0);
        testTexture.bind();

        defaultShader.uploadMat4f("uProjection", camera.getProjectionMatrix());
        defaultShader.uploadMat4f("uView", camera.getViewMatrix());
        defaultShader.uploadFloat("uTime", Time.getTime());
        // Bind the VAO //
        glBindVertexArray(vaoID);

        // Enable the vertex attribute pointers //
        glEnableVertexAttribArray(0);
        glEnableVertexAttribArray(1);

        glDrawElements(GL_TRIANGLES, elementArray.length, GL_UNSIGNED_INT, 0);

        // Unbind everything //
        glDisableVertexAttribArray(0);
        glDisableVertexAttribArray(1);

        glBindVertexArray(0);

        defaultShader.detach();

        if (!firstTime) {
            System.out.println("Created game objects");
            GameObject go2 = new GameObject("obj number 2");
            go2.addComponent(new SpriteRenderer());
            this.addGameObjectToScene(go2);
            firstTime = true;
        }

        for (GameObject go : this.gameObjects) {
            go.update(dt);
        }
    }
    
}
