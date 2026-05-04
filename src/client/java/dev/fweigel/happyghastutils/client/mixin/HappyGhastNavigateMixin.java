package dev.fweigel.happyghastutils.client.mixin;

import dev.fweigel.happyghastutils.client.GhastAutopilot;
import net.minecraft.world.entity.animal.happyghast.HappyGhast;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.Vec2;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(HappyGhast.class)
public abstract class HappyGhastNavigateMixin {

    @Inject(method = "getRiddenRotation", at = @At("HEAD"), cancellable = true)
    private void onGetRiddenRotation(net.minecraft.world.entity.LivingEntity passenger, CallbackInfoReturnable<Vec2> cir) {
        if (GhastAutopilot.isActive()) {
            cir.setReturnValue(GhastAutopilot.computeRotation((HappyGhast) (Object) this));
        }
    }

    @Inject(method = "getRiddenInput", at = @At("HEAD"), cancellable = true)
    private void onGetRiddenInput(Player player, Vec3 travelVector, CallbackInfoReturnable<Vec3> cir) {
        if (GhastAutopilot.isActive()) {
            if (GhastAutopilot.hasPlayerInput(player)) {
                GhastAutopilot.cancel();
                return; // fall through to original
            }
            cir.setReturnValue(GhastAutopilot.computeInput((HappyGhast) (Object) this));
        }
    }
}
