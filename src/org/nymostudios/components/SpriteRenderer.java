package org.nymostudios.components;

import org.nymostudios.engine.entity.Component;

public class SpriteRenderer extends Component {

    private boolean firstTime = false; 

    @Override
    public void start() {
        System.out.println("Starting...");
    }

    @Override
    public void update(float dt) {
        if (!firstTime) {
            System.out.println("Updating...");
            firstTime = true;
        }
    }
    
}
