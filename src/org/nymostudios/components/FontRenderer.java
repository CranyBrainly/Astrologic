package org.nymostudios.components;

import org.nymostudios.engine.entity.Component;

public class FontRenderer extends Component {

    @Override
    public void start() {
        if (gameObject.getComponent(SpriteRenderer.class) != null) {
            System.out.println("Hey! I found el' font renderer!");
        }
    }

    @Override
    public void update(float dt) {
        // TODO Auto-generated method stub
        
    }
    
}
