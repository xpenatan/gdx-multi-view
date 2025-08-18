package com.github.xpenatan.imgui.gdx.frame.viewport;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.graphics.Color;
import com.github.xpenatan.gdx.multiview.EmuApplicationWindow;
import com.github.xpenatan.gdx.multiview.EmuInput;
import com.github.xpenatan.gdx.multiview.EmuWindow;
import imgui.ImGui;
import imgui.ImGuiCol;
import imgui.ImGuiCond;
import imgui.ImGuiHoveredFlags;
import imgui.ImVec2;
import imgui.idl.IDLBase;

/**
 *
 *	Emulate gdx application inside a ImGui window
 *
 * @author xpenatan
 */
public class ImGuiGdxFrameWindow {

    private EmuWindow emuWindow;

    int startWidth;
    int startHeight;

    float startX;
    float startY;

    private String name = "";
    private String beginID = "BeginID";
    private String btnId = "btnId";

    private String widthLabel = "Width: ";
    private String heightLabel = " Height: ";
    private String mouseXLabel = " X: ";
    private String mouseYLabel = " Y: ";

    public int activeColor = Color.GREEN.toIntBits();

    private boolean curFrameFocus;
    private boolean isWindowHovered;

    private StringBuilder stringBuilder = new StringBuilder();

    private IDLBase idlTextureId;

    public ImGuiGdxFrameWindow(int width, int height, float x, float y) {
        this(new EmuApplicationWindow(), width, height, x, y);
        this.startWidth = width;
        this.startHeight = height;
    }

    public ImGuiGdxFrameWindow(EmuWindow emuWindow, int width, int height, float x, float y) {
        this.startWidth = width;
        this.startHeight = height;
        this.startX = x;
        this.startY = y;
        this.emuWindow = emuWindow;

        idlTextureId = IDLBase.createInstance();
    }

    public void setName(String name) {
        this.name = name;
    }

    public void render() {
        if(name == null)
            name = "";
        ImGui.SetNextWindowSize(ImVec2.TMP_1.set(startWidth, startHeight), ImGuiCond.FirstUseEver);
        ImGui.SetNextWindowPos(ImVec2.TMP_1.set(startX, startY), ImGuiCond.FirstUseEver);

        int mouseX = 0;
        int mouseY = 0;
        int windowWidth = 0;
        int windowHeight = 0;
        int windowX = 0;
        int windowY = 0;

        if(curFrameFocus)
            ImGui.PushStyleColor(ImGuiCol.Text, activeColor);

        EmuInput input = emuWindow.getEmuInput();

        ImGui.Begin(name);
        if(curFrameFocus)
            ImGui.PopStyleColor();
        boolean beginChild = ImGui.BeginChild(beginID, ImVec2.TMP_1.set(0, -ImGui.GetFrameHeightWithSpacing()));
        if(beginChild) {
            windowWidth = (int)ImGui.GetWindowWidth();
            windowHeight = (int)ImGui.GetWindowHeight();

            ImVec2 winPos = ImGui.GetWindowPos();
            windowX = (int)winPos.get_x();
            windowY = (int)winPos.get_y();

            if(input.needsFocus())
                ImGui.SetWindowFocus();

            curFrameFocus = ImGui.IsWindowFocused();
            isWindowHovered = ImGui.IsWindowHovered(ImGuiHoveredFlags.AllowWhenBlockedByActiveItem);

            if(ImGui.InvisibleButton(btnId, ImVec2.TMP_1.set(windowWidth, windowHeight)))
                curFrameFocus = true;

            emuWindow.begin(curFrameFocus, isWindowHovered, windowX, windowY, windowWidth, windowHeight);
            mouseX = Gdx.input.getX();
            mouseY = Gdx.input.getY();

            emuWindow.loop();

            emuWindow.end();

            ImGui.GetWindowDrawList().AddImage(idlTextureId.native_setVoid(emuWindow.getTextureID()), ImVec2.TMP_1.set(windowX, windowY), ImVec2.TMP_2.set(windowX + windowWidth, windowY + windowHeight), ImVec2.TMP_3.set(emuWindow.u, emuWindow.v), ImVec2.TMP_4.set(emuWindow.u2, emuWindow.v2));
        }

        ImGui.EndChild();
        ImGui.Separator();

        stringBuilder.setLength(0);

        stringBuilder.append(widthLabel);
        stringBuilder.append(windowWidth);

        stringBuilder.append(heightLabel);
        stringBuilder.append(windowHeight);

        stringBuilder.append(mouseXLabel);
        stringBuilder.append(mouseX);

        stringBuilder.append(mouseYLabel);
        stringBuilder.append(mouseY);

        ImGui.Text(stringBuilder.toString());
        ImGui.End();
    }

    public InputProcessor getInput() {
        return emuWindow.getEmuInput();
    }
}
