package dev.girlboss.mouseanchor;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientLifecycleEvents;
import net.minecraft.client.Minecraft;
import org.apache.commons.lang3.tuple.Pair;
import org.jetbrains.annotations.Nullable;

import java.io.File;

public class MouseAnchor implements ClientModInitializer {
    private static MouseAnchor instance = null;
    
    private final MouseAnchorOptions options = new MouseAnchorOptions(new File(Minecraft.getInstance().gameDirectory, "config/mouseanchor.json"));
    
    private Pair<Double, Double> savedCursorPosition = null;
    private Long lastMouseReleaseTime = -1L;
    
    @Override
    public void onInitializeClient() {
        instance = this;
        
        options.load();
        ClientLifecycleEvents.CLIENT_STOPPING.register(_ -> options.save());
    }

    public MouseAnchorOptions getOptions() {
        return options;
    }
    
    public void saveCursorPosition(double normalizedX, double normalizedY) {
        savedCursorPosition = Pair.of(normalizedX, normalizedY);
        lastMouseReleaseTime = System.currentTimeMillis();
    }
    
    @Nullable
    public Pair<Double, Double> getSavedCursorPosition() {
        var expiryTime = options.getKeepCursorTime() == -1
                ? Long.MAX_VALUE
                : lastMouseReleaseTime + options.getKeepCursorTime();
        if (System.currentTimeMillis() > expiryTime) {
            return null;
        }
        
        return savedCursorPosition;
    }
    
    public static MouseAnchor getInstance() {
        if (instance == null) {
            throw new IllegalStateException("Attempted to get mod instance before initialization");
        }
        
        return instance;
    }
}
