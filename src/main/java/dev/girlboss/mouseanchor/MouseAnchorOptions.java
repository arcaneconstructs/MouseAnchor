package dev.girlboss.mouseanchor;

import com.google.gson.*;
import net.minecraft.client.OptionInstance;
import net.minecraft.client.Options;
import net.minecraft.network.chat.Component;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;

public class MouseAnchorOptions {
    private final File file;
    
    private final Logger logger = LoggerFactory.getLogger("mouse-anchor");
    private final Gson gson = new GsonBuilder().setPrettyPrinting().create();
    
    /*
        0: Never
        1-5: 1-5 seconds
        6: 10 seconds
        7: 30 seconds
        8: 1 minute
        9: 5 minutes
        10: 10 minutes
        11: Always
     */
    private int keepCursorTime = 0;
    
    private final OptionInstance<Integer> keepCursorPositionOption = new OptionInstance<>(
            "options.mouseanchor.keepcursorposition.title",
            OptionInstance.cachedConstantTooltip(Component.translatable("options.mouseanchor.keepcursorposition.tooltip")),
            (caption, value) -> value == 0
                ? Options.genericValueLabel(caption, Component.translatable("options.off"))
                : Options.genericValueLabel(caption, Component.translatable("options.mouseanchor.keepcursorposition.value.%d".formatted(value))),
            new OptionInstance.IntRange(0, 11),
            7,
            value -> keepCursorTime = value
    );
    
    public MouseAnchorOptions(File file) {
        this.file = file;
    }
    
    public OptionInstance<Integer> getKeepCursorPositionOption() {
        return keepCursorPositionOption;
    }
    
    public long getKeepCursorTime() {
        return switch (keepCursorTime) {
            case 0 -> 0;
            case 1, 2, 3, 4, 5 -> keepCursorTime * 1000;
            case 6 -> 10 * 1000;
            case 7 -> 30 * 1000;
            case 8 -> 60 * 1000;
            case 9 -> 5 * 60 * 1000;
            case 10 -> 10 * 60 * 1000;
            default -> -1;
        };
    }

    public void load() {
        if (!file.exists()) {
            return;
        }

        try {
            try (var reader = new FileReader(file)) {
                var object = JsonParser.parseReader(reader).getAsJsonObject();
                keepCursorPositionOption.set(object.getAsJsonPrimitive("keepMouseTime").getAsInt());
            }
        } catch (Exception e) {
            logger.warn("Failed to load configuration file, using defaults: ", e);
        }
    }

    public void save() {
        try {
            file.createNewFile();

            var object = new JsonObject();
            object.add("keepMouseTime", new JsonPrimitive(keepCursorPositionOption.get()));
            try (var writer = new FileWriter(file)) {
                writer.write(gson.toJson(object));
            }
        } catch (Exception e) {
            logger.warn("Failed to save configuration file: ", e);
        }
    }
}
