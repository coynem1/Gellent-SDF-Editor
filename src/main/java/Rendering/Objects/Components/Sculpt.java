package Rendering.Objects.Components;

import Rendering.Objects.Component;

public class Sculpt extends Component {
    private boolean isSculpting = false;

    @Override
    public void update(float delta) {
        if (isSculpting) {return;}
        IO.println("Sculpting");
        isSculpting = true;
    }

    @Override
    public void start() {
        IO.println("Start Sculpt");
    }
}
