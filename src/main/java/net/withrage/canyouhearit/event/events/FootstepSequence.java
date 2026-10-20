package net.withrage.canyouhearit.event.events;

import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.protocol.game.ClientboundSoundPacket;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.level.block.state.BlockState;

public class FootstepSequence {

    private final ServerPlayer player;
    private final double directionX;
    private final double directionZ;

    private double x;
    private double z;

    private int remainingSteps;
    private int ticksUntilNextStep;

    public FootstepSequence(ServerPlayer player) {
        this.player = player;

        double angle = player.getRandom().nextDouble()
                * Math.PI * 2;

        double distance = 3 + player.getRandom()
                .nextDouble() * 4;

        this.x = player.getX() + Math.cos(angle) * distance;
        this.z = player.getZ() + Math.sin(angle) * distance;

        double movementAngle = player.getRandom().nextDouble()
                * Math.PI * 2;

        this.directionX = Math.cos(movementAngle) * 0.65;
        this.directionZ = Math.sin(movementAngle) * 0.65;

        this.remainingSteps = 2 + player.getRandom().nextInt(5);
        this.ticksUntilNextStep = 0;
    }

    public boolean tick() {
        if (!player.isAlive() || player.isRemoved()) {
            return true;
        }

        if (ticksUntilNextStep > 0) {
            ticksUntilNextStep--;
            return false;
        }

        playStep();

        remainingSteps--;

        if (remainingSteps <= 0) {
            return true;
        }

        x += directionX;
        z += directionZ;

        ticksUntilNextStep = 6 + player.getRandom().nextInt(7);

        return false;
    }

    private void playStep() {
        BlockPos pos = BlockPos.containing(
                x,
                player.getY() - 0.2,
                z
        );

        BlockState state = player.level().getBlockState(pos);

        var sound = state.isAir()
                ? SoundEvents.GRASS_STEP
                : state.getSoundType().getStepSound();

        player.connection.send(
                new ClientboundSoundPacket(
                        BuiltInRegistries.SOUND_EVENT.wrapAsHolder(sound),
                        SoundSource.BLOCKS,
                        x,
                        player.getY(),
                        z,
                        0.8F,
                        0.9F + player.getRandom().nextFloat() * 0.2F,
                        player.getRandom().nextLong()
                )
        );
    }
}
