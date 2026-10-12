package dev.girlboss.mouseanchor.mixins;

import dev.girlboss.mouseanchor.MouseAnchor;
import net.minecraft.client.Options;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.options.MouseSettingsScreen;
import net.minecraft.client.gui.screens.options.OptionsSubScreen;
import net.minecraft.network.chat.Component;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(MouseSettingsScreen.class)
public abstract class MouseSettingsScreenMixin extends OptionsSubScreen {
    private MouseSettingsScreenMixin(Screen lastScreen, Options options, Component title) {
        super(lastScreen, options, title);
    }

    @Inject(method = "addOptions", at = @At("TAIL"))
    private void injectOption(CallbackInfo ci) {
        this.list.addSmall(MouseAnchor.getInstance().getOptions().getKeepCursorPositionOption());
    }
}
