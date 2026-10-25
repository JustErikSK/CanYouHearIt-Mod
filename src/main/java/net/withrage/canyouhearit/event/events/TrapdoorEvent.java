package net.withrage.canyouhearit.event.events;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.TrapDoorBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.withrage.canyouhearit.event.ParanormalEvent;
import net.withrage.canyouhearit.event.ParanormalEventManager;

import java.util.ArrayList;
import java.util.List;

public class TrapdoorEvent implements ParanormalEvent {

    private final ParanormalEventManager manager;

    public TrapdoorEvent(ParanormalEventManager manager) {
        this.manager = manager;
    }

    @Override
    public boolean canTrigger(ServerPlayer player) {
        return player.isAlive()
                && !player.isSpectator()
                && !findNearbyTrapdoors(player).isEmpty();
    }

    @Override
    public void trigger(ServerPlayer player) {
        List<BlockPos> trapdoors = findNearbyTrapdoors(player);

        if (trapdoors.isEmpty()) {
            return;
        }

        BlockPos pos = trapdoors.get(
                player.getRandom().nextInt(trapdoors.size())
        );

        BlockState state = player.level().getBlockState(pos);

        if (!(state.getBlock() instanceof TrapDoorBlock)) {
            return;
        }

        boolean isOpen = state.getValue(TrapDoorBlock.OPEN);

        player.level().setBlock(
                pos,
                state.setValue(TrapDoorBlock.OPEN, !isOpen),
                3
        );

        player.serverLevel().playSound(
                null,
                pos,
                isOpen
                        ? SoundEvents.WOODEN_TRAPDOOR_CLOSE
                        : SoundEvents.WOODEN_TRAPDOOR_OPEN,
                SoundSource.BLOCKS,
                0.7F,
                0.9F + player.getRandom().nextFloat() * 0.1F
        );

        if (!isOpen && player.getRandom().nextBoolean()) {
            int delay = 40 + player.getRandom().nextInt(61);

            manager.addTrapdoorSequence(
                    new TrapdoorSequence(
                            player.serverLevel(), pos, delay
                    )
            );
        }
    }

    private List<BlockPos> findNearbyTrapdoors(ServerPlayer player) {
        List<BlockPos> trapdoors = new ArrayList<>();

        BlockPos center = player.blockPosition();

        for (BlockPos pos : BlockPos.betweenClosed(
                center.offset(-8, -4, -8),
                center.offset(8, 4, 8))) {

            BlockState state = player.level().getBlockState(pos);

            if (state.getBlock() instanceof TrapDoorBlock
                    && !state.is(Blocks.IRON_TRAPDOOR)) {

                trapdoors.add(pos.immutable());
            }
        }
        return trapdoors;
    }
}
