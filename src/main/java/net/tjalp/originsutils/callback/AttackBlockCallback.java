package net.tjalp.originsutils.callback;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.GameMode;
import net.minecraft.world.World;
import net.tjalp.originsutils.util.ShopUtil;

public class AttackBlockCallback implements net.fabricmc.fabric.api.event.player.AttackBlockCallback {

    @Override
    public ActionResult interact(PlayerEntity player, World world, Hand hand, BlockPos pos, Direction direction) {
        if (!(player instanceof ServerPlayerEntity)) return ActionResult.PASS;
        if (((ServerPlayerEntity) player).interactionManager.getGameMode() != GameMode.CREATIVE
                && ShopUtil.isInShop(pos.getX(), pos.getZ())) return ActionResult.FAIL;
        return ActionResult.PASS;
    }
}
