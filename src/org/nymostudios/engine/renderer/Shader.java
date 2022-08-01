package org.nymostudios.engine.renderer;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.FloatBuffer;
import java.nio.file.Files;
import java.nio.file.Paths;
import org.joml.Matrix3f;
import org.joml.Matrix4f;
import org.joml.Vector2f;
import org.joml.Vector3f;
import org.joml.Vector4f;
import org.lwjgl.BufferUtils;
import org.nymostudios.engine.renderer.Shader;
import org.nymostudios.engine.Window;

import static org.lwjgl.opengl.GL20.*;

public class Shader {

    private int shaderProgramID;
    private boolean beingUsed = false;

    private String vertexSource;
    private String fragmentSource;
    private String filePath;

    public Shader(String filepath) {
        this.filePath = filepath;
        try {
            String preSource = "";

            try {
                // input stream
                InputStream i = Shader.class.getResourceAsStream("/org/nymostudios/" + filepath);
                if (i == null) {
                    throw new Exception("Error: Could not create InputStream for file '" + this.filePath + "'.");
                }
                BufferedReader r = new BufferedReader(new InputStreamReader(i));

                // reads each line
                String l;
                while((l = r.readLine()) != null) {
                    if (preSource == "") {
                        preSource = preSource + l;
                    } else {
                        preSource = preSource + "\n" + l;
                    }
                } 
                i.close();

            } catch(Exception e) {
                System.out.println(e);
            }

            String source = new String(preSource);

            String[] splitString = source.split("(#type)( )+([a-zA-Z]+)");

            // Find the first pattern after #type  <pattern> //
            int index = source.indexOf("#type") + 6;
            System.out.println(index);
            int eol = source.indexOf("\n", index);
            System.out.println(eol);
            String firstPattern = source.substring(index, eol).trim();
            System.out.println(firstPattern);

            index = source.indexOf("#type", eol) + 6;
            eol = source.indexOf("\n", index);
            String secondPattern = source.substring(index, eol).trim();

            if (firstPattern.equals("vertex")) {
                vertexSource = splitString[1];
            } else if (firstPattern.equals("fragment")) {
                fragmentSource = splitString[1];
            } else {
                throw new IOException("Error: Unexpected token '" + firstPattern + "'");
            }

            if (secondPattern.equals("vertex")) {
                vertexSource = splitString[2];
            } else if (secondPattern.equals("fragment")) {
                fragmentSource = splitString[2];
            } else {
                throw new IOException("Error: Unexpected token '" + secondPattern + "'");
            }

            // System.out.println(vertexSource);
            // System.out.println(fragmentSource);
        } catch (IOException e) {
            e.printStackTrace();
            assert false : "Error: Could not open file for shader '" + filepath + "'";
        }
    }

    public void compile() {
        int vertexID, fragmentID;

        // First load and compile the vertex shader //
        vertexID = glCreateShader(GL_VERTEX_SHADER);

        // Pass the shader source code to the GPU //
        glShaderSource(vertexID, vertexSource);
        glCompileShader(vertexID);
        
        // Chech for errors //
        int success = glGetShaderi(vertexID, GL_COMPILE_STATUS);
        if (success == GL_FALSE) {
            int len = glGetShaderi(vertexID, GL_INFO_LOG_LENGTH);
            System.out.println("Error: '" + filePath + "'\n\tVertex shader compilation failed.");
            System.out.println(glGetShaderInfoLog(vertexID, len));
            assert false : "";
        }
        
        // First load and compile the fragment shader //
        fragmentID = glCreateShader(GL_FRAGMENT_SHADER);

        // Pass the shader source code to the GPU //
        glShaderSource(fragmentID, fragmentSource);
        glCompileShader(fragmentID);
        
        // Chech for errors //
        success = glGetShaderi(fragmentID, GL_COMPILE_STATUS);
        if (success == GL_FALSE) {
            int len = glGetShaderi(fragmentID, GL_INFO_LOG_LENGTH);
            System.out.println("Error: '" + filePath + "'\n\tFragment shader compilation failed.");
            System.out.println(glGetShaderInfoLog(fragmentID, len));
            assert false : "";
        }

        // Link shaders and check for errors //
        shaderProgramID = glCreateProgram();
        glAttachShader(shaderProgramID, vertexID);
        glAttachShader(shaderProgramID, fragmentID);
        glLinkProgram(shaderProgramID);

        // Linking errors //
        success = glGetProgrami(shaderProgramID, GL_LINK_STATUS);
        if (success == GL_FALSE) {
            int len = glGetProgrami(shaderProgramID, GL_INFO_LOG_LENGTH);
            System.out.println("Error: '" + filePath + "'\n\tShader linking failed.");
            System.out.println(glGetProgramInfoLog(shaderProgramID, len));
            assert false : "";
        }
    }

    public void use() {
        if (!beingUsed) {
            // Bind shader program //
            glUseProgram(shaderProgramID);
            beingUsed = true;
        }
    }

    public void detach() {
        glUseProgram(0);
        beingUsed = false;
    }

    public void uploadMat4f(String varName, Matrix4f mat4) {
        int varLocation = glGetUniformLocation(shaderProgramID, varName);
        use();
        FloatBuffer matBuffer = BufferUtils.createFloatBuffer(16);
        mat4.get(matBuffer);
        glUniformMatrix4fv(varLocation, false, matBuffer);
    }

    public void uploadMat3f(String varName, Matrix3f mat3) {
        int varLocation = glGetUniformLocation(shaderProgramID, varName);
        use();
        FloatBuffer matBuffer = BufferUtils.createFloatBuffer(9);
        mat3.get(matBuffer);
        glUniformMatrix3fv(varLocation, false, matBuffer);
    }

    public void uploadVec4f(String varName, Vector4f vec) {
        int varLocation = glGetUniformLocation(shaderProgramID, varName);
        use();
        glUniform4f(varLocation, vec.x, vec.y, vec.z, vec.w);
    }

    public void uploadVec3f(String varName, Vector3f vec) {
        int varLocation = glGetUniformLocation(shaderProgramID, varName);
        use();
        glUniform3f(varLocation, vec.x, vec.y, vec.z);
    }

    public void uploadVec2f(String varName, Vector2f vec) {
        int varLocation = glGetUniformLocation(shaderProgramID, varName);
        use();
        glUniform2f(varLocation, vec.x, vec.y);
    }


    public void uploadFloat(String varName, float val) {
        int varLocation = glGetUniformLocation(shaderProgramID, varName);
        use();
        glUniform1f(varLocation, val);
    }

    public void uploadInt(String varName, int val) {
        int varLocation = glGetUniformLocation(shaderProgramID, varName);
        use();
        glUniform1i(varLocation, val);
    }

    public void uploadTexture(String varName, int slot) {
        int varLocation = glGetUniformLocation(shaderProgramID, varName);
        use();
        glUniform1i(varLocation, slot);
    }
}
