package Rendering.ImGui;

import Input.Actions.ActionHandler;
import Jade.SceneManager;
import Observers.InputImGui;
import Input.InputShapes;
import Observers.InputShapesEvents;
import Rendering.Objects.Components.Blending;
import Rendering.Objects.Components.ComponentRounded;
import Rendering.Objects.Components.Transform2D;
import Rendering.Objects.GameObject;
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
import org.joml.Vector2f;
import org.joml.Vector3f;

import java.awt.event.ActionEvent;

public class ImGuiEditor {
    private static final float EDITOR_WIDTH = 370f;
    private static final float PERCENT = 100f;
    private static final float DEGREES = 360f;
    private final ImBoolean SHOW_DEMO_WINDOW = new ImBoolean(false);
    private static final float[] DEFAULT_COLOUR = new float[] {1f, 0.6f, 0.3f};

    private ImInt toolSelected = new ImInt(InputShapes.TOOLS.SELECT.ordinal());
    private ImFloat scale = new ImFloat(10.0f);
    private float[] blend = new float[1];
    private float[] round = new float[1];
    private float[] rotation = new float[1];
    private float[] position = new float[2];

    // private InputShapes inputShapes;
    private static float[] colour = DEFAULT_COLOUR;
    private ImGuiMenubar menubar = new ImGuiMenubar();
    private int shapeSelected = 0;
    private GameObject selectedObject = null;
    private static final float DPI_SCALAR = ImGuiWindow.getDPIScalar();

    public ImGuiEditor() {}

    private void bindObservers() {
        InputShapesEvents.onSelectedChanged((object) ->{
            selectedObject = object;
            if (object == null) return;

            Transform2D<Vector2f> transform = object.getComponent(Transform2D.class);
            Blending blending = object.getComponent(Blending.class);
            ComponentRounded rounding = object.getComponent(ComponentRounded.class);

            if (transform != null) {
                position[0] = transform.getPosition().x;
                position[1] = transform.getPosition().y;
                scale.set(transform.getScale());
                rotation[0] = ((float) Math.toDegrees(transform.getRotation())) % DEGREES;
            } else {
                position[0] = 0f;
                position[1] = 0f;
                scale.set(Transform2D.DEFAULT_SCALE);
                rotation[0] = 0f;
            }

            if (blending != null) {
                blend[0] = (blending.getBlend() / Blending.MAX_BLEND) * PERCENT;
            } else {
                blend[0] = 0f;
            }
            if (rounding != null) {
                // Fake value between 0 and 100
                round[0] = (rounding.getRounded() / ComponentRounded.MAX_ROUNDED) * PERCENT;
            } else {
                round[0] = 0f;
            }
        });

        InputShapesEvents.onBlendChanged((blend) ->{
            this.blend[0] = (blend / Blending.MAX_BLEND) * PERCENT;
        });
        InputShapesEvents.onRoundChanged((round) ->{
            this.round[0] = (round / ComponentRounded.MAX_ROUNDED) * PERCENT;
        });
        InputShapesEvents.onPosChanged((pos) ->{
            this.position[0] = pos.x;
            this.position[1] = pos.y;
        });
        InputShapesEvents.onScaleChanged((value) ->{
            scale.set(value);
        });
        InputShapesEvents.onRotationChanged((rotation) ->{
            this.rotation[0] = ((float) Math.toDegrees(rotation)) % DEGREES;
        });
    }

    public void init() {
        bindObservers();
        menubar.init();
    }

    // All rendering
    public void render() {
        menubar.render();

        ImGui.setNextWindowSize(EDITOR_WIDTH * DPI_SCALAR, 0); // width 300, height 0 = auto
        if (ImGui.begin("Editor " + FontAwesomeIcons.SlidersH, ImGuiWindowFlags.AlwaysAutoResize)) {
            showShapeButtons();

            if (SceneManager.get().getActionHandler().hasSelected()) {
                ImGui.spacing();

                ImGui.pushItemWidth(-90 * DPI_SCALAR);
                tweakMenu();
                ImGui.popItemWidth();
            }
        }
        ImGui.end();
    }

    // Edit Selected
    private void tweakMenu() {
        String TWO_DECIMALS = "%.2f";
        boolean roundable = false;

        ImGui.separatorText("Edit Selected Shape  " + FontAwesomeIcons.Edit);

        // Position
        if (ImGui.dragFloat2("Position", position, 0.1f, 1f, 0f, TWO_DECIMALS)) {
            InputImGui.setPosCallback(new Vector2f(position[0], position[1]), true);
        }
        if (ImGui.isItemDeactivated()) InputImGui.setPosCallback(new Vector2f(position[0], position[1]), false);

        // Scale
        if (ImGui.inputFloat("Scale", scale, 1.1f, 3.1f, TWO_DECIMALS, ImGuiSliderFlags.AlwaysClamp)){
            InputImGui.setScaleCallback(scale.get(), true);
        }
        if (ImGui.isItemDeactivated()) InputImGui.setScaleCallback(scale.get(), false);

        // Rotation
        if (ImGui.dragFloat("Angle", rotation, 0.1f, 0f, DEGREES, TWO_DECIMALS, ImGuiSliderFlags.WrapAround)) {
            InputImGui.setRotationCallback(rotation[0], true);
        }
        if (ImGui.isItemDeactivated()) InputImGui.setRotationCallback(rotation[0], false);
        ImGui.sameLine(); helpMarker("Click and drag to edit value.\n"+
                "Hold Shift/Alt for faster/slower edit.\n"+
                "Double-Click or Ctrl+Click to input value."
        );

        ImGui.separator();
        ImGui.spacing();

        // Blend
        if (ImGui.dragFloat("Blend", blend, 0.1f, 0f, PERCENT, TWO_DECIMALS, ImGuiSliderFlags.AlwaysClamp)) {
            InputImGui.setBlendCallback((blend[0] / PERCENT) * Blending.MAX_BLEND, true);
        }
        if (ImGui.isItemDeactivated()) InputImGui.setBlendCallback((blend[0] / PERCENT) * Blending.MAX_BLEND, false);

        // Round
        // Disable if the shape is unroundable
        if (selectedObject != null && selectedObject.getClass() == Shape.class) {
            Shape shape = (Shape) selectedObject;
            roundable = !InputShapes.UNROUNDABLE_SHAPES.contains(shape.getShapeType());

            if (!roundable) ImGui.beginDisabled(true);
        }

        if (ImGui.dragFloat("Round", round, 0.1f, 0f, PERCENT, TWO_DECIMALS, ImGuiSliderFlags.AlwaysClamp)) {
            InputImGui.setRoundCallback((round[0] / PERCENT) * ComponentRounded.MAX_ROUNDED , true);
        }
        if (ImGui.isItemDeactivated()) InputImGui.setRoundCallback((round[0] / PERCENT) * ComponentRounded.MAX_ROUNDED, false);
        if (!roundable) ImGui.endDisabled();
    }

    // Shape Buttons
    private void showShapeButtons() {
        float BOX_SIZE = 70f * DPI_SCALAR;
        float SPACING = ImGui.getStyle().getItemSpacing().x;
        float totalWidth = (BOX_SIZE * 4) + (SPACING * 3);
        ImVec2 pos;

        ImGui.separatorText("Shapes  " + FontAwesomeIcons.Shapes);

        ImGui.setCursorPosX((ImGui.getContentRegionAvailX() - totalWidth) / 2);

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
        float thickness = 1.7f * DPI_SCALAR;
        float size = ImGuiWindow.ICON_SIZE * DPI_SCALAR - thickness;
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
