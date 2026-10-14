package dev.girlboss.mouseanchor.mixins;

import dev.girlboss.mouseanchor.MouseAnchor;
import net.minecraft.client.Minecraft;
import net.minecraft.client.MouseHandler;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

//? if >=26.3 {
import org.lwjgl.sdl.SDLMouse;
//? } else {
/*import org.lwjgl.glfw.GLFW;
*///? }

@Mixin(MouseHandler.class)
public abstract class MouseHandlerMixin {
    @Shadow
    @Final
    private Minecraft minecraft;

    @Shadow
    private double xpos;

    @Shadow
    private double ypos;

    @Inject(
            method = "grabMouse",
            at = @At(
                    value = "FIELD",
                    opcode = Opcodes.PUTFIELD,
                    target = "Lnet/minecraft/client/MouseHandler;mouseGrabbed:Z"
            )
    )
    private void savePositionOnGrab(CallbackInfo ci) {
        var x = this.xpos / this.minecraft.getWindow().getWidth();
        var y = this.ypos / this.minecraft.getWindow().getHeight();

        MouseAnchor.getInstance().saveCursorPosition(x, y);
    }

    //? if 26.1.2 {

    /*@Inject(
            method = "releaseMouse",
            at = @At(
                    value = "INVOKE",
                    target = "Lcom/mojang/blaze3d/platform/InputConstants;grabOrReleaseMouse(Lcom/mojang/blaze3d/platform/Window;IDD)V",
                    shift = At.Shift.AFTER
            )
    )
    private void loadPositionOnRelease(CallbackInfo ci) {
        var savedPosition = MouseAnchor.getInstance().getSavedCursorPosition();
        if (savedPosition == null) {
            return;
        }

        this.xpos = savedPosition.getLeft() * this.minecraft.getWindow().getWidth();
        this.ypos = savedPosition.getRight() * this.minecraft.getWindow().getHeight();

        // Minecraft already calls this function when releasing the mouse, but it 
        // seems to not do anything when it's before glfwSetInputMode?
        GLFW.glfwSetCursorPos(this.minecraft.getWindow().handle(), this.xpos, this.ypos);
    }

    *///? } else {
    
    @Inject(
            method = "releaseMouse",
            at = @At(
                    value = "INVOKE",
                    target = "Lcom/mojang/blaze3d/platform/InputConstants;releaseMouse(Lcom/mojang/blaze3d/platform/Window;DD)V",
                    shift = At.Shift.AFTER
            )
    )
    private void loadPositionOnRelease(CallbackInfo ci) {
        var savedPosition = MouseAnchor.getInstance().getSavedCursorPosition();
        if (savedPosition == null) {
            return;
        }

        this.xpos = savedPosition.getLeft() * this.minecraft.getWindow().getWidth();
        this.ypos = savedPosition.getRight() * this.minecraft.getWindow().getHeight();

        // the same issue persists
        SDLMouse.SDL_WarpMouseInWindow(this.minecraft.getWindow().handle(), (float) this.xpos, (float) this.ypos);
    }
    
    //? }
}
