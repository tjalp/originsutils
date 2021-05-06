package net.tjalp.originsutils.mixin;

import net.minecraft.entity.damage.DamageSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.network.ServerPlayerInteractionManager;
import net.tjalp.originsutils.object.OUPlayer;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ServerPlayerEntity.class)
public class ServerPlayerEntityMixin {

    @Shadow @Final public ServerPlayerInteractionManager interactionManager;

    @Inject(method = "damage", at = @At("HEAD"), cancellable = true)
    public void onDamage(DamageSource source, float amount, CallbackInfoReturnable<Boolean> cir) {
        ServerPlayerEntity player = this.interactionManager.player;
        OUPlayer ouPlayer = OUPlayer.getFromUuid(player.getUuid()) == null ? new OUPlayer(player.getUuid()).register() : OUPlayer.getFromUuid(player.getUuid());
        if (ouPlayer.isInShop()) cir.setReturnValue(false);
    }
}
