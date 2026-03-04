package Rendering.ImGui;

import Input.InputImGui;
import Input.InputStampShapes;
import imgui.ImGui;
import imgui.flag.ImGuiWindowFlags;
import imgui.type.ImBoolean;
import org.joml.Vector3f;

public class ImGuiEditor {
    private final ImBoolean SHOW_DEMO_WINDOW = new ImBoolean(false);
    private static final float[] DEFAULT_COLOUR = new float[] {1f, 0.6f, 0.3f};

    private boolean showText = true;
    private final float flt[] = new float[1];
    private int count = 0;
    private static float[] colour = DEFAULT_COLOUR;
    private ImGuiMenubar menubar = new ImGuiMenubar();
    private String[] shapeItems = {"Circle", "Square", "Triangle"};
    private int shapeSelected = 0;


    public ImGuiEditor() {
        bindInputs();
    }

    private void bindInputs() {
        InputStampShapes inputStamper = new InputStampShapes();

        InputImGui.onColourChanged((colourSelected) -> {
            colour = new float[]{colourSelected.x, colourSelected.y, colourSelected.z};
        });
    }

    public void render() {
        menubar.render();

        if (ImGui.begin("Editor " + FontAwesomeIcons.SlidersH, ImGuiWindowFlags.AlwaysAutoResize)) {
            ImGui.text("OS: [" + System.getProperty("os.name") + "] Arch: [" + System.getProperty("os.arch") + "]");
            if (ImGui.button(FontAwesomeIcons.Save + " Save")) {
                count++;
            }
            ImGui.sameLine();
            ImGui.text(String.valueOf(count));
            ImGui.sliderFloat("float", flt, 0, 1);
            ImGui.separator();
            showExtras();
        }
        ImGui.end();
    }

    private void showExtras() {
        ImGui.text("Extras");
        ImGui.checkbox("Show Demo Window", SHOW_DEMO_WINDOW);

        if (SHOW_DEMO_WINDOW.get()) {
            showDemo();
        }
        ImGui.separator();

        colourPicker();
        showShape();

    }

    // Shape Combo Box
    private void showShape() {
        if (ImGui.beginCombo("Shape", shapeItems[shapeSelected])) {
            for (int n = 0; n < shapeItems.length; n++) {
                boolean isSelected = shapeSelected == n;

                if (ImGui.selectable(shapeItems[n], isSelected)) {
                    shapeSelected = n;
                }

                if (isSelected) {
                    ImGui.setItemDefaultFocus();
                }
            }
            ImGui.endCombo();
        }
    }

    private void colourPicker() {
        float[] colourImGui = colour;
        ImGui.text("Colour");
        ImGui.sameLine(); helpMarker("Click on the colour square to open the colour picker");
        if (ImGui.colorEdit3("Colour", colourImGui)) {
            colour[0] = colourImGui[0];
            colour[1] = colourImGui[1];
            colour[2] = colourImGui[2];
            InputImGui.setColourCallback(getColourSelected());
        }
    }

    private void showDemo() {
        ImGui.showDemoWindow(SHOW_DEMO_WINDOW);
    }

    // Tips when hovered
    static void helpMarker(String desc) {
        ImGui.textDisabled("(?)");
        if (ImGui.beginItemTooltip())
        {
            ImGui.pushTextWrapPos(ImGui.getFontSize() * 35.0f);
            ImGui.textUnformatted(desc);
            ImGui.popTextWrapPos();
            ImGui.endTooltip();
        }
    }

    public static Vector3f getColourSelected() { return new Vector3f(colour[0], colour[1], colour[2]);}
}
