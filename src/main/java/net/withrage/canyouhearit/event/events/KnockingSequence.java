package net.withrage.canyouhearit.event.events;

import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.protocol.game.ClientboundSoundPacket;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.DoorBlock;
import net.minecraft.world.level.block.state.BlockState;

public class KnockingSequence {

    private final ServerPlayer player;
    private final ServerLevel level;
    private final BlockPos pos;

    private final int totalKnocks;
    private final boolean openAfterKnocking;
    private final boolean openImmediately;

    private int knocksPlayed = 0;
    private int ticksUntilNextKnock = 0;
    private int ticksUntilOpening = -1;

    public KnockingSequence(ServerPlayer player, BlockPos pos) {
        this.player = player;
        this.level = player.serverLevel();
        this.pos = pos.immutable();

        this.totalKnocks = 2 + player.getRandom().nextInt(3);

        this.openAfterKnocking =
                player.getRandom().nextFloat() < 0.30F;

        this.openImmediately =
                player.getRandom().nextFloat() < 0.05F;
    }

    public boolean tick() {
        if (!player.isAlive() || player.isRemoved()) {
            return true;
        }

        if (!(level.getBlockState(pos).getBlock() instanceof DoorBlock)) {
            return true;
        }

        if (ticksUntilOpening >= 0) {
            if (ticksUntilOpening-- == 0) {
                openDoor();
                return true;
            }
            return false;
        }

        if (ticksUntilNextKnock > 0) {
            ticksUntilNextKnock--;
            return false;
        }

        if (knocksPlayed < totalKnocks) {
            playKnock();
            knocksPlayed++;

            if (knocksPlayed == 1 && openImmediately) {
                openDoor();
            }

            if (knocksPlayed < totalKnocks) {
                ticksUntilNextKnock =
                        8 + player.getRandom().nextInt(9);
                return false;
            }

            if (openAfterKnocking && !openImmediately) {
                ticksUntilOpening =
                        20 + player.getRandom().nextInt(41);
                return false;
            }

            return true;
        }

        return true;
    }

    private void playKnock() {
        BlockState state = level.getBlockState(pos);

        SoundEvent sound = state.is(Blocks.IRON_DOOR)
                ? SoundEvents.ZOMBIE_ATTACK_IRON_DOOR
                : SoundEvents.ZOMBIE_ATTACK_WOODEN_DOOR;

        player.connection.send(
                new ClientboundSoundPacket(
                        BuiltInRegistries.SOUND_EVENT.wrapAsHolder(sound),
                        SoundSource.BLOCKS,
                        pos.getX() + 0.5,
                        pos.getY() + 1.0,
                        pos.getZ() + 0.5,
                        0.25F,
                        0.75F + player.getRandom().nextFloat() * 0.15F,
                        player.getRandom().nextLong()
                )
        );
    }

    private void openDoor() {
        BlockState state = level.getBlockState(pos);

        if (state.is(Blocks.IRON_DOOR)) {
            return;
        }

        if (state.getBlock() instanceof DoorBlock door
                && !state.getValue(DoorBlock.OPEN)) {

            door.setOpen(null, level, state, pos, true);
        }
    }
}
