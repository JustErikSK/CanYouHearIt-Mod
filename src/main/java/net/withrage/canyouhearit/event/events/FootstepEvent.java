
package net.withrage.canyouhearit.event.events;

import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.protocol.game.ClientboundSoundPacket;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.level.block.state.BlockState;
import net.withrage.canyouhearit.event.ParanormalEvent;

public class FootstepEvent implements ParanormalEvent {

    @Override
    public boolean canTrigger(ServerPlayer player) {
        return player.isAlive() && !player.isSpectator();
    }


    @Override
    public void trigger(ServerPlayer player) {
        double angle = player.getRandom().nextDouble()
                * Math.PI * 2;

        double distance = 2 + player.getRandom()
                .nextDouble() * 4;

        double x = player.getX() + Math.cos(angle) * distance;
        double z = player.getZ() + Math.sin(angle) * distance;

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
