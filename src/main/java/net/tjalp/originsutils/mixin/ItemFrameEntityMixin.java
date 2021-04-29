package net.tjalp.originsutils.mixin;

import net.minecraft.entity.decoration.ItemFrameEntity;
import net.minecraft.item.ElytraItem;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundTag;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ItemFrameEntity.class)
public class ItemFrameEntityMixin {

    @Inject(method = "writeCustomDataToTag", at = @At("TAIL"))
    private void onWriteCustomData(CompoundTag tag, CallbackInfo ci) {
        if (tag.contains("Item")) {
            ItemStack item = ItemStack.fromTag(tag.getCompound("Item"));
            if (item.getItem() instanceof ElytraItem) {
                tag.remove("Item");
                tag.remove("ItemRotation");
                tag.remove("ItemDropChance");
            }
        }
    }
}
