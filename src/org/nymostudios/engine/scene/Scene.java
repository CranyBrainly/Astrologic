package org.nymostudios.engine.scene;

import org.nymostudios.engine.Camera;

public abstract class Scene {
    protected Camera camera;

    public Scene() {}

    public void init() {
        
    }

    public abstract void update(float dt);
}