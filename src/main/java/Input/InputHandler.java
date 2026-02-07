package Input;

import Jade.Camera;

// Singleton that handles inputs that change the editor
public class InputHandler {
    private static InputHandler instance;
    private Camera camera;

    private InputCamera inputCamera;

    private InputHandler(){}

    // Singleton
    public static InputHandler get(){
        if(instance == null){
            instance = new InputHandler();
        }
        return instance;
    }

    // Create a camera input handler
    public void bindCameraInputs(Camera camera){
        if (this.inputCamera == null) {
            this.inputCamera = new InputCamera(camera);
        }
    }

    // Check inputs every frame
    public void process(){
        if (inputCamera != null) {
            inputCamera.process();
        }
    }


}
