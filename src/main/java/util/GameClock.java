package util;


import Jade.Window;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import static org.lwjgl.glfw.GLFW.glfwWindowShouldClose;

// Singleton game clock, observers for fixed updates
public class GameClock {
    protected static final double TICKS_PER_SEC = 60.0;
    protected static final double NS_PER_TICK = 1_000_000_000.0 / TICKS_PER_SEC;
    protected static final int MS_SECOND = 1000;
    protected static final int MAX_FRAME_LAG = 5; // Can be up to 5 frames behind
    protected static final String NAME = "Physics Clock";

    @FunctionalInterface
    public interface TickObserver {
        void handle(double delta);
    }

    // Handler and singleton
    protected static final List<TickObserver> onTick = new CopyOnWriteArrayList<>();
    protected static GameClock instance;

    protected double accumulator = 0.0;
    
    // Threading
    protected Thread thread;
    protected volatile boolean running = false;

    // Attaching/detaching observers
    public void addObserver(TickObserver observer) { onTick.add(observer); }
    public void removeObserver(TickObserver observer) { onTick.remove(observer); }

    private GameClock() {}

    public static GameClock get() {
        if (instance == null) {
            instance = new GameClock();
        }
        return instance;
    }

    public void startThread() {
        if (running) {return;}

        running = true;
        thread = new Thread(this::run, NAME);
        thread.start();
    }

    public void stopThread() {
        if (!running) {return;}

        // Stop the thread within 1 second
        running = false;
        try {
            thread.join(MS_SECOND);
        } catch (InterruptedException e) {
            assert false : "Thread " + NAME + " interrupted while waiting for shutdown";
        }
    }

    // Main loop
    protected void run() {
        long lastTime = System.nanoTime();
        long window = Window.get().getWindow();

        while (!glfwWindowShouldClose(window)) {
            long currentTime = System.nanoTime();
            double elapsed = currentTime - lastTime;
            lastTime = currentTime;

            accumulator += elapsed;

            // Fixed timestep updates
            while (accumulator >= NS_PER_TICK) {
                tickCallback(TICKS_PER_SEC / (float) MS_SECOND); // delta in seconds
                accumulator -= NS_PER_TICK;

                // Prevent spiral of death
                if (accumulator > NS_PER_TICK * MAX_FRAME_LAG) {
                    accumulator = 0;
                    break;
                }
            }
        }
    }

    // Update observers
    private void tickCallback(double delta) {
        for (TickObserver observer : onTick) {
            observer.handle(delta);
        }
    }
}
