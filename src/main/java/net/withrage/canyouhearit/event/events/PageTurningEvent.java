package net.withrage.canyouhearit.event.events;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.withrage.canyouhearit.event.ParanormalEvent;
import net.withrage.canyouhearit.event.ParanormalEventManager;

import java.util.ArrayList;
import java.util.List;

public class PageTurningEvent implements ParanormalEvent {

    private final ParanormalEventManager manager;

    public PageTurningEvent(ParanormalEventManager manager) {
        this.manager = manager;
    }

    @Override
    public boolean canTrigger(ServerPlayer player) {
        return player.isAlive()
                && !player.isSpectator()
                && !findNearbyBooks(player).isEmpty();
    }

    @Override
    public void trigger(ServerPlayer player) {
        List<BlockPos> books = findNearbyBooks(player);

        if (books.isEmpty()) {
            return;
        }

        BlockPos selected = books.get(
                player.getRandom().nextInt(books.size())
        );

        manager.addPageTurningSequence(
                new PageTurningSequence(player, selected)
        );
    }

    private List<BlockPos> findNearbyBooks(ServerPlayer player) {
        List<BlockPos> books = new ArrayList<>();

        BlockPos center = player.blockPosition();

        int radius = 8;
        int verticalRadius = 5;

        for (BlockPos pos : BlockPos.betweenClosed(
                center.offset(-radius, -verticalRadius, -radius),
                center.offset(radius, verticalRadius, radius)
        )) {
            BlockState state = player.level().getBlockState(pos);

            if (state.is(Blocks.BOOKSHELF)
                    || state.is(Blocks.CHISELED_BOOKSHELF)
                    || state.is(Blocks.LECTERN)) {
                books.add(pos.immutable());
            }
        }
        return books;
    }
}
