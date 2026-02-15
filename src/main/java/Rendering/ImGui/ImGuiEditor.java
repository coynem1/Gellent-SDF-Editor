package Rendering.ImGui;

import imgui.ImGui;
import imgui.flag.ImGuiWindowFlags;
import imgui.type.ImBoolean;

public class ImGuiEditor {
    private static final ImBoolean SHOW_DEMO_WINDOW = new ImBoolean(false);

    private boolean showText = true;
    private final float flt[] = new float[1];
    private int count = 0;
    private float[] colour = new float[3];
    private ImGuiMenubar menubar = new ImGuiMenubar();
    private String[] shapeItems = {"Circle", "Square", "Triangle"};
    private int shapeSelected = 0;


    public ImGuiEditor() {

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
            this.colour[0] = colourImGui[0];
            this.colour[1] = colourImGui[1];
            this.colour[2] = colourImGui[2];
        }
    }

    private void showDemo() {
        ImGui.showDemoWindow(SHOW_DEMO_WINDOW);
    }

    static void helpMarker(String desc)
    {
        ImGui.textDisabled("(?)");
        if (ImGui.beginItemTooltip())
        {
            ImGui.pushTextWrapPos(ImGui.getFontSize() * 35.0f);
            ImGui.textUnformatted(desc);
            ImGui.popTextWrapPos();
            ImGui.endTooltip();
        }
    }

}
