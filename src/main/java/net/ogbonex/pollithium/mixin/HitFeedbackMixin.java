package net.ogbonex.pollithium.mixin;

import net.ogbonex.pollithium.PollithiumConfig;
import net.ogbonex.pollithium.PollithiumHud;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.network.packet.s2c.play.EntityDamageS2CPacket;
import net.minecraft.sound.SoundEvents;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ClientPlayNetworkHandler.class)
public abstract class HitFeedbackMixin {
    @Inject(method="onEntityDamage", at=@At("TAIL"))
    private void pollithium$onConfirmedDamage(EntityDamageS2CPacket packet, CallbackInfo ci) {
        MinecraftClient client=MinecraftClient.getInstance();
        if(client.player==null || client.world==null) return;
        if(client.targetedEntity==null || client.targetedEntity.getId()!=packet.entityId()) return;
        PollithiumHud.recordConfirmedHit();
        if(PollithiumConfig.hitSoundEnabled) client.player.playSound(SoundEvents.ENTITY_EXPERIENCE_ORB_PICKUP,0.55f,1.35f);
    }
}