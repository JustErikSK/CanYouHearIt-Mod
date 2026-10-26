package net.withrage.canyouhearit.event.events;

import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import net.withrage.canyouhearit.event.ParanormalEvent;

public class ParticleEvent implements ParanormalEvent {

    @Override
    public boolean canTrigger(ServerPlayer player) {
        return player.isAlive() && !player.isSpectator();
    }

    @Override
    public void trigger(ServerPlayer player) {
        ServerLevel level = player.serverLevel();
        Vec3 center = player.position();

        for (int attempt = 0; attempt < 15; attempt++) {

            double angle = player.getRandom().nextDouble() * Math.PI * 2;
            double distance = 2.0 + player.getRandom().nextDouble() * 4.0;

            int x = (int) Math.floor(center.x + Math.cos(angle) * distance);
            int z = (int) Math.floor(center.z + Math.sin(angle) * distance);

            for (int yOffset = 2; yOffset >= -4; yOffset--) {

                BlockPos pos = BlockPos.containing(
                        x, center.y + yOffset, z
                );

                BlockState ground = level.getBlockState(pos);
                BlockPos above = pos.above();

                if (ground.isFaceSturdy(
                        level, pos, net.minecraft.core.Direction.UP
                ) && level.getBlockState(above).isAir()) {

                    level.sendParticles(
                            player,
                            ParticleTypes.SMOKE,
                            false,
                            x + 0.5,
                            pos.getY() + 1.1,
                            z + 0.5,
                            12,
                            0.25,
                            0.08,
                            0.25,
                            0.005
                    );
                    return;
                }
            }
        }
    }
}
