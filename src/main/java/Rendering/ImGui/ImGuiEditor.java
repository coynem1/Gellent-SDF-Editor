package Rendering.ImGui;

import imgui.ImGui;
import imgui.flag.ImGuiInputTextFlags;
import imgui.flag.ImGuiWindowFlags;

public class ImGuiEditor {
    private boolean showText = true;
    private final float flt[] = new float[1];
    private int count = 0;

    public ImGuiEditor() {

    }

    public void render() {
        if (ImGui.begin("Demo", ImGuiWindowFlags.AlwaysAutoResize)) {
            ImGui.text("OS: [" + System.getProperty("os.name") + "] Arch: [" + System.getProperty("os.arch") + "]");
            if (ImGui.button(FontAwesomeIcons.Save + " Save")) {
                count++;
            }
            ImGui.sameLine();
            ImGui.text(String.valueOf(count));
            ImGui.sliderFloat("float", flt, 0, 1);
            ImGui.separator();
        }
        ImGui.end();
    }

}
