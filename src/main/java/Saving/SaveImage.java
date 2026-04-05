package Saving;

import Jade.Window;
import Observers.InputImGui;
import Observers.WindowEvents;
import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL11;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.nio.ByteBuffer;

import static org.lwjgl.opengl.GL11.GL_PACK_ALIGNMENT;
import static org.lwjgl.opengl.GL11.glPixelStorei;
import static org.lwjgl.opengl.GL30.*;


public class SaveImage {
    private static final int RGBA = 4;
    private static int width;
    private static int height;
    private boolean awaitUpdate = false;

    public SaveImage() {
        width = Window.get().getWidth();
        height = Window.get().getHeight();
        bindObservers();
    }

    private void bindObservers() {
        // User saves image
        InputImGui.onImageExportChanged((_) -> {
            awaitUpdate = true;
            Window.get().awaitNextFrame();
        });

        WindowEvents.onFrameRendered((_) -> {
            if (!awaitUpdate) return;
            awaitUpdate = false;

            String pathStr = SaveDialog.saveDialog("Save Image", "png", "PNG Image (*.png");
            if (pathStr == null) return;

            // Set alignment to 1 byte to fix interlacing
            glPixelStorei(GL_PACK_ALIGNMENT, 1);

            width = Window.get().getWidth();
            height = Window.get().getHeight();

            ByteBuffer buffer = BufferUtils.createByteBuffer(width * height * RGBA);
            GL11.glReadPixels(0, 0, width, height, GL11.GL_RGBA, GL11.GL_UNSIGNED_BYTE, buffer);

            try {
                saveImage(width, height, buffer, pathStr);
            } catch (Exception e) {
                IO.println("Failed to save image: " + e.getMessage());
            }
        });
    }

    public static void saveImage(int width, int height, ByteBuffer buffer, String path) throws Exception {
        BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        // Set each pixel to the correct colour
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                int r = buffer.get() & 0xFF;
                int g = buffer.get() & 0xFF;
                int b = buffer.get() & 0xFF;
                int a = buffer.get() & 0xFF;
                image.setRGB(x, height - 1 - y, (a << Byte.SIZE * 3) | (r << Byte.SIZE * 2) | (g << Byte.SIZE) | b);
            }
        }
        ImageIO.write(image, "PNG", new File(path));
    }

}
