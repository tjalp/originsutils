package net.tjalp.originsutils.callback;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.world.GameMode;
import net.minecraft.world.World;
import net.tjalp.originsutils.util.ShopUtil;

public class UseBlockCallback implements net.fabricmc.fabric.api.event.player.UseBlockCallback {

    @Override
    public ActionResult interact(PlayerEntity player, World world, Hand hand, BlockHitResult hitResult) {
        if (!(player instanceof ServerPlayerEntity)) return ActionResult.PASS;
        if (((ServerPlayerEntity) player).interactionManager.getGameMode() != GameMode.CREATIVE
                && ShopUtil.isInShop(hitResult.getBlockPos().getX(), hitResult.getBlockPos().getZ())) {
            return ActionResult.FAIL;
        }
        return ActionResult.PASS;
    }
}
