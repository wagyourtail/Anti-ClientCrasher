package xyz.wagyourtail.acc.mixin;

import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientPacketListener;
import net.minecraft.network.chat.Component;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;


@Mixin(ClientPacketListener.class)
public class MixinEntitySectionStorage {

    @Inject(method = "handleExplosion", at = @At("HEAD"), cancellable = true)
    private void onHandleExplosion(net.minecraft.network.protocol.game.ClientboundExplodePacket packet, CallbackInfo info) {
        if (
            // outside of world
            packet.getX() > 30_000_000 || packet.getY() > 30_000_000 || packet.getZ() > 30_000_000 || packet.getX() < -30_000_000 || packet.getY() < -30_000_000 || packet.getZ() < -30_000_000 ||
            // power too high
            packet.getPower() > 1000 ||
            // too many blocks
            packet.getToBlow().size() > 100_000
            // too much knockback, the server would cancel the movement anyway since it's > 10
            || packet.getKnockbackX() > 10 || packet.getKnockbackY() > 10 || packet.getKnockbackZ() > 10
            // knockback can be negative?
            || packet.getKnockbackX() < -10 || packet.getKnockbackY() < -10 || packet.getKnockbackZ() < -10
        ) {
            Minecraft.getInstance().gui.getChat().addMessage(Component.literal("Warning: the server attempted to crash you. This has been blocked.").withStyle(ChatFormatting.RED));
            info.cancel();
        }
    }
}
