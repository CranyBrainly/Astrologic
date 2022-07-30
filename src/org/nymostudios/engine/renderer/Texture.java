package org.nymostudios.engine.renderer;

import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import org.lwjgl.BufferUtils;
import org.nymostudios.engine.Window;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.stb.STBImage.*;

public class Texture {
    private String filepath;
    private int texureID;

    public Texture (String filepath) {
        this.filepath = Window.get().wokingDir + filepath;

        // Generate texture on GPU //
        texureID = glGenTextures();
        // Bind texture //
        glBindTexture(GL_TEXTURE_2D, texureID);
        // Set the texture parameters //
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, GL_REPEAT); // Repeat the image in both directions
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, GL_REPEAT); // Repeat the image in both directions
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_NEAREST); // Pixilate when stretching the image
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_NEAREST); // Pixilate when shrinking the image

        // Load image //
        IntBuffer width    = BufferUtils.createIntBuffer(1);
        IntBuffer height   = BufferUtils.createIntBuffer(1);
        IntBuffer channels = BufferUtils.createIntBuffer(1);
        ByteBuffer image = stbi_load(this.filepath, width, height, channels, 0);

        if (image != null) {
            if (channels.get(0) == 3) {
                glTexImage2D(
                    GL_TEXTURE_2D, 0, GL_RGB,
                    width.get(0), height.get(0),
                    0, GL_RGB, GL_UNSIGNED_BYTE, image
                );
            } else if (channels.get(0) == 4) {
                glTexImage2D(
                    GL_TEXTURE_2D, 0, GL_RGBA,
                    width.get(0), height.get(0),
                    0, GL_RGBA, GL_UNSIGNED_BYTE, image
                );
            } else {
                assert false : "Error: (Texture) Unkown number of channels in texture '" + filepath + "'";
            }
        } else {
            assert false : "Error: (Texture) Could not load image '" + filepath + "'.";
        }

        stbi_image_free(image);
    }

    public void bind() {
        glBindTexture(GL_TEXTURE_2D, texureID);
    }

    public void unbind() {
        glBindTexture(GL_TEXTURE_2D, 0);
    }
}
