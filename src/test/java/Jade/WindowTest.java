package Jade;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class WindowTest {
    @Test
    void WindowSingletonInstanceIsSame() {
        Window instance1 = Window.get();
        Window instance2 = Window.get();

        assertSame(instance1, instance2, "Window instances are not the same");
    }

    @Test
    void WindowSizeGreaterThanZero() {
        Window instance = Window.get();

        assertTrue(instance.getWidth() > 0, "Window width is not greater than zero");
        assertTrue(instance.getHeight() > 0, "Window height is not greater than zero");
    }

    @Test
    void WindowSetTitleBeforeInitWorks() {
        Window instance = Window.get();

        instance.setTitle("Test Title");
        assertEquals("Test Title", instance.getTitle(), "When the Window title is set, it does not return the updated title");
    }

}