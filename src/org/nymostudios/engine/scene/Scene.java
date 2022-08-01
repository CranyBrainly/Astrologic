package org.nymostudios.engine.scene;

import java.util.ArrayList;
import java.util.List;

import org.nymostudios.engine.entity.GameObject;
import org.nymostudios.engine.renderer.Camera;
import org.nymostudios.engine.renderer.Renderer;

public abstract class Scene {

    private boolean isRunning = false;

    protected List<GameObject> gameObjects = new ArrayList<>();
    protected Camera camera;
    protected Renderer renderer = new Renderer();

    public Scene() {}

    public void init() {
        
    }

    public void start() {
        for (GameObject go : gameObjects) {
            go.start();
            this.renderer.add(go);
        }
        isRunning = true;
    }

    public void addGameObjectToScene(GameObject go) {
        if (!isRunning) {
            gameObjects.add(go);
        } else {
            gameObjects.add(go);
            go.start();
            this.renderer.add(go);
        }
    }

    public abstract void update(float dt);

    public Camera camera() {
        return this.camera;
    }
}
