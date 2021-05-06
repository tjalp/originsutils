package net.tjalp.originsutils.callback;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.passive.CowEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.text.LiteralText;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Formatting;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

public class UseEntityCallback implements net.fabricmc.fabric.api.event.player.UseEntityCallback {

    @Override
    public ActionResult interact(PlayerEntity player, World world, Hand hand, Entity entity, @Nullable EntityHitResult hitResult) {
        if (entity instanceof LivingEntity) {
            if (entity.getType() == EntityType.COW) {
                CowEntity cow = (CowEntity) entity;
                player.sendSystemMessage(new LiteralText("You hit a cow at " + cow.getX() + ", " + cow.getZ()).formatted(Formatting.RED), entity.getUuid());
            }
        }
        return ActionResult.PASS;
    }
}
