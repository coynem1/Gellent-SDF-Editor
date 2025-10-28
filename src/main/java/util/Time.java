package util;

public class Time {
    private static Time instance;
    public static float startTime = System.nanoTime();
    private float frameStartTime;

    public Time() {
        frameStartTime = startTime;
    }

    // Singleton
    public static Time get(){
        if (Time.instance == null){
            Time.instance = new Time();
        }
        return Time.instance;
    }

    public void beginFrame() {
        frameStartTime = getTime();
    }

    // Returns delta time in secs
    public float endFrame() {
        return getTime() - frameStartTime;
    }

    public static float getTime()  {
        return (float)((System.nanoTime() - startTime) * 1E-9);     // Convert nano-secs to secs
    }

}
