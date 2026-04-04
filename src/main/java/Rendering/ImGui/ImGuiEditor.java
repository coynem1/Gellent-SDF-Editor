package Rendering.ImGui;

import Observers.InputImGui;
import Input.InputShapes;
import Rendering.Objects.Components.Blending;
import Rendering.Objects.Shape;
import imgui.ImColor;
import imgui.ImDrawList;
import imgui.ImGui;
import imgui.ImVec2;
import imgui.flag.ImGuiSliderFlags;
import imgui.flag.ImGuiWindowFlags;
import imgui.type.ImBoolean;
import imgui.type.ImFloat;
import imgui.type.ImInt;
import org.joml.Vector3f;

public class ImGuiEditor {
    private final ImBoolean SHOW_DEMO_WINDOW = new ImBoolean(false);
    private static final float[] DEFAULT_COLOUR = new float[] {1f, 0.6f, 0.3f};

    private ImInt toolSelected = new ImInt(InputShapes.TOOLS.SELECT.ordinal());
    private ImFloat scale = new ImFloat(10.0f);
    private float[] blend = new float[1];
    private float[] rotation = new float[1];
    private float[] position = new float[3];

    // private InputShapes inputShapes;
    private static float[] colour = DEFAULT_COLOUR;
    private ImGuiMenubar menubar = new ImGuiMenubar();
    private int shapeSelected = 0;

    public ImGuiEditor() {}

    public void init() {
        menubar.init();

        InputImGui.setScaleCallback(scale.get());
    }

    // All rendering
    public void render() {
        menubar.render();

        if (ImGui.begin("Editor " + FontAwesomeIcons.SlidersH, ImGuiWindowFlags.AlwaysAutoResize)) {
            showShapeButtons();
            ImGui.separator();
            // tweakMenu();
        }
        ImGui.end();
    }

    // Edit Selected
    private void tweakMenu() {
        ImGui.text("Edit Selected");

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
        if (ImGui.dragFloat("Blend", blend, 0.1f, 0f, Blending.MAX_BLEND, ImGuiSliderFlags.AlwaysClamp)) {
            InputImGui.setBlendCallback(blend[0]);
        }
    }

    // Shape Buttons
    private void showShapeButtons() {
        float BOX_SIZE = 60f;
        ImVec2 pos;

        if (ImGui.button(FontAwesomeIcons.Circle, BOX_SIZE, BOX_SIZE)) { InputImGui.setShapeCallback(InputShapes.SHAPES.CIRCLE); }
        ImGui.sameLine();
        if (ImGui.button(FontAwesomeIcons.Square, BOX_SIZE, BOX_SIZE)) { InputImGui.setShapeCallback(InputShapes.SHAPES.BOX); }
        ImGui.sameLine();
        pos = ImGui.getCursorScreenPos();
        if (ImGui.button(" ", BOX_SIZE, BOX_SIZE)) { InputImGui.setShapeCallback(InputShapes.SHAPES.TRIANGLE); }
        ImGui.sameLine(); drawTriangle(pos, BOX_SIZE);
        if (ImGui.button(FontAwesomeIcons.Star, BOX_SIZE, BOX_SIZE)) { InputImGui.setShapeCallback(InputShapes.SHAPES.STAR); }
    }

    // Draw ImGui triangle icon because the font doesn't have one
    private void drawTriangle(ImVec2 pos, float boundarySize) {
        float thickness = 1.7f;
        float size = ImGuiWindow.ICON_SIZE - thickness;
        ImDrawList drawList = ImGui.getWindowDrawList();

        float x = pos.x + (boundarySize - size) / 2;
        float y = pos.y + (boundarySize - size) / 2;

        drawList.addTriangle(
                x + size/2, y,           // top middle
                x,          y + size,    // bottom left
                x + size,   y + size,    // bottom right
                ImColor.rgba(255, 255, 255, 255),
                thickness
        );
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

    }

    // Tool Radio Buttons
    private void showTools() {
        for (InputShapes.TOOLS tool : InputShapes.TOOLS.values()) {
            if (ImGui.radioButton(InputShapes.TOOL_NAMES.get(tool), toolSelected, tool.ordinal())) {
                InputImGui.setToolCallback(tool);
            }

            // End of radios
            if (tool.ordinal() < InputShapes.TOOLS.values().length - 1) {
                ImGui.sameLine();
            }
        }
    }

    // Shape Combo Box
    private void showShape() {
        InputShapes.SHAPES shapes[] = InputShapes.SHAPES.values();

        // Uses lookup for shape nickname
        if (ImGui.beginCombo("Shape", InputShapes.SHAPE_NAMES.get(shapes[shapeSelected]))) {
            for (int n = 0; n < shapes.length; n++) {
                boolean isSelected = shapeSelected == n;

                if (ImGui.selectable(InputShapes.SHAPE_NAMES.get(shapes[n]), isSelected)) {
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
