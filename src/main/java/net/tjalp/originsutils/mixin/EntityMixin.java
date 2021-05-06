package net.tjalp.originsutils.mixin;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.tjalp.originsutils.object.OUPlayer;
import net.tjalp.originsutils.util.ShopUtil;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.UUID;

@Mixin(Entity.class)
public abstract class EntityMixin {

    @Shadow public abstract EntityType<?> getType();

    @Shadow public abstract double getX();

    @Shadow public abstract double getZ();

    @Shadow public abstract UUID getUuid();

    @Inject(method = "tick", at = @At("TAIL"))
    private void onTick(CallbackInfo ci) {
        if (this.getType() != EntityType.PLAYER) return;
        OUPlayer ouPlayer = OUPlayer.getFromUuid(this.getUuid()) == null ? new OUPlayer(this.getUuid()).register().load() : OUPlayer.getFromUuid(this.getUuid());
        double x = this.getX();
        double z = this.getZ();
        if (ShopUtil.isInShop(x, z)) {
            ouPlayer.setInShop(true, !ouPlayer.isInShop());
        } else {
            ouPlayer.setInShop(false, ouPlayer.isInShop());
        }
    }
}
