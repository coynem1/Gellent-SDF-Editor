package Rendering.ImGui;

import Input.InputImGui;
import Input.InputStampShapes;
import Jade.SceneManager;
import Rendering.Objects.Shape;
import imgui.ImGui;
import imgui.flag.ImGuiSliderFlags;
import imgui.flag.ImGuiWindowFlags;
import imgui.type.ImBoolean;
import imgui.type.ImFloat;
import imgui.type.ImInt;
import org.joml.Vector3f;

import java.util.ArrayList;
import java.util.Arrays;

public class ImGuiEditor {
    private final ImBoolean SHOW_DEMO_WINDOW = new ImBoolean(false);
    private static final float[] DEFAULT_COLOUR = new float[] {1f, 0.6f, 0.3f};

    // private boolean showText = true;
    private ImInt toolSelected = new ImInt(InputStampShapes.TOOLS.SELECT.ordinal());
    private ImFloat scale = new ImFloat(10.0f);
    private float[] blend = new float[1];
    private float[] rotation = new float[1];
    private float[] position = new float[3];
    private int count = 0;

    private InputStampShapes inputStamper;
    private static float[] colour = DEFAULT_COLOUR;
    private ImGuiMenubar menubar = new ImGuiMenubar();
    private int shapeSelected = 0;

    public ImGuiEditor() {}

    public void init() {
        inputStamper = SceneManager.get().getInputStamper();
        menubar.init();

        InputImGui.setScaleCallback(scale.get());
    }

    public void render() {
        menubar.render();

        if (ImGui.begin("Editor " + FontAwesomeIcons.SlidersH, ImGuiWindowFlags.AlwaysAutoResize)) {
            ImGui.text("OS: [" + System.getProperty("os.name") + "] Arch: [" + System.getProperty("os.arch") + "]");
            if (ImGui.button(FontAwesomeIcons.Save + " Save")) {
                count++;
            }
            ImGui.sameLine(); ImGui.text(String.valueOf(count));

            ImGui.text("Sliders");


            if (ImGui.inputFloat("Scale", scale, 1.1f, 3.1f, ImGuiSliderFlags.AlwaysClamp)){
                scale.set(Math.max(Shape.MINIMUM_SCALE, scale.get()));
                InputImGui.setScaleCallback(scale.get());
            }
            if (ImGui.dragFloat("Angle", rotation, 0.1f, 0f, 360f, ImGuiSliderFlags.WrapAround)) { InputImGui.setRotationCallback(rotation[0]);}
            ImGui.sameLine(); helpMarker("Click and drag to edit value.\n"+
                    "Hold Shift/Alt for faster/slower edit.\n"+
                    "Double-Click or Ctrl+Click to input value."
            );
            if (ImGui.dragFloat3("Position", position, 0.1f, 1f)) { InputImGui.setPosCallback(new Vector3f(position[0], position[1], position[2])); }

            ImGui.separator();
            if (ImGui.dragFloat("Blend", blend, 0.1f, 0f, 100f, ImGuiSliderFlags.AlwaysClamp)) {
                InputImGui.setBlendCallback(blend[0]);
            }
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

        showTools();
        colourPicker();
        showShape();
        ImGui.separator();

    }

    // Tool Radio Buttons
    private void showTools() {
        for (InputStampShapes.TOOLS tool : InputStampShapes.TOOLS.values()) {
            if (ImGui.radioButton(InputStampShapes.TOOL_NAMES.get(tool), toolSelected, tool.ordinal())) {
                inputStamper.setToolsMode(tool);
            }

            // End of radios
            if (tool.ordinal() < InputStampShapes.TOOLS.values().length - 1) {
                ImGui.sameLine();
            }
        }
    }

    // Shape Combo Box
    private void showShape() {
        InputStampShapes.SHAPES shapes[] = InputStampShapes.SHAPES.values();

        // Uses lookup for shape nickname
        if (ImGui.beginCombo("Shape", InputStampShapes.SHAPE_NAMES.get(shapes[shapeSelected]))) {
            for (int n = 0; n < shapes.length; n++) {
                boolean isSelected = shapeSelected == n;

                if (ImGui.selectable(InputStampShapes.SHAPE_NAMES.get(shapes[n]), isSelected)) {
                    shapeSelected = n;
                    InputImGui.setShapeCallback(shapes[n]);
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
