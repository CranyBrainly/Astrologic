package org.nymostudios.engine.scene;

import org.joml.Vector2f;
import org.joml.Vector4f;
import org.nymostudios.components.SpriteRenderer;
import org.nymostudios.engine.entity.GameObject;
import org.nymostudios.engine.renderer.Camera;
import org.nymostudios.engine.renderer.Transform;
import org.nymostudios.util.AssetPool;

public class LevelEditorScene extends Scene {

    public LevelEditorScene() {
    }

    @Override
    public void init() {
        this.camera = new Camera(new Vector2f());

        int xOffset = 10;
        int yOffset = 10;
        
        float totalWidth = (float)(600 - xOffset * 2);
        float totalHeight = (float)(300 - yOffset * 2);

        float sizeX = totalWidth / 100f;
        float sizeY = totalHeight / 100f;

        for (int x = 0; x < 100; x++) {
            for (int y = 0; y < 100; y++) {
                float xPos = xOffset + (x * sizeX);
                float yPos = yOffset + (y * sizeY);

                GameObject go = new GameObject("Object " + x + ", " + y, new Transform(new Vector2f(xPos, yPos), new Vector2f(sizeX, sizeY)));
                go.addComponent(new SpriteRenderer(new Vector4f(xPos / totalWidth, yPos / totalHeight, 1, 1)));
                this.addGameObjectToScene(go);
            }
        }

        loadResources();
    }

    private void loadResources() {
        AssetPool.getShader("/shaders/default.glsl");
    }

    @Override
    public void update(float dt) {
        // System.out.println("FPS: " + (1f / dt));

        for (GameObject go : this.gameObjects) {
            go.update(dt);
        }

        this.renderer.render();
    }
}
