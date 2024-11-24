package io.github.epitech_game.lwjgl3;
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3Application;
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3ApplicationConfiguration;
import io.github.epitech_game.Main;
import org.lwjgl.glfw.GLFWErrorCallback;

public class Lwjgl3Launcher {
    public static void main(String[] args) {
        // Set a custom GLFW error callback to suppress Wayland-specific errors
        GLFWErrorCallback.create((error, description) -> {
            if (error == 65544) { // GLFW_FEATURE_UNAVAILABLE
                System.out.println("Wayland limitation: " + description);
            } else {
                System.err.println("GLFW Error [" + error + "]: " + description);
            }
        }).set();

        // macOS and Windows JVM helper
        if (StartupHelper.startNewJvmIfRequired()) return;

        createApplication();
    }

    private static Lwjgl3Application createApplication() {
        return new Lwjgl3Application(new Main(), getDefaultConfiguration());
    }

    private static Lwjgl3ApplicationConfiguration getDefaultConfiguration() {
        Lwjgl3ApplicationConfiguration configuration = new Lwjgl3ApplicationConfiguration();
        configuration.setTitle("Z-like");
        configuration.useVsync(true);
        configuration.setForegroundFPS(60); // Limit FPS to 60
        configuration.setWindowedMode(1200, 1200); // Set window size
        configuration.setWindowSizeLimits(1000, 1000, 1000, 1000);

        return configuration;
    }
}


