package Jade;

import java.nio.file.Path;
import java.nio.file.Paths;

public class SceneBase extends Scene {
    public SceneBase(String name) {
        super(name);

        // File paths
        this.vShaderPath.put(RENDER_SDF, Paths.get("assets/shaders/vertexDemo.glsl"));
        this.fShaderPath.put(RENDER_SDF, Paths.get("assets/shaders/fragmentDemo.glsl"));
        this.vShaderPath.put(RENDER_DEBUG, Paths.get("assets/shaders/debugVertex.glsl"));
        this.fShaderPath.put(RENDER_DEBUG, Paths.get("assets/shaders/debugFragment.glsl"));

        this.render = new RenderSDF(this.vShaderPath.get(RENDER_SDF), this.fShaderPath.get(RENDER_SDF), this.camera);
        this.shaderSDF = this.render.getShader();
    }


}
