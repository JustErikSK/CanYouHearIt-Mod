package net.withrage.canyouhearit.event.events;

import net.minecraft.network.protocol.game.ClientboundSoundPacket;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import net.withrage.canyouhearit.event.ParanormalEvent;

public class CaveAmbientEvent implements ParanormalEvent {

    @Override
    public boolean canTrigger(ServerPlayer player) {
        return player.isAlive()
                && !player.isSpectator()
                && player.level().dimension() == Level.OVERWORLD;
    }

    @Override
    public void trigger(ServerPlayer player) {
        Vec3 pos = player.getEyePosition();

        float volume = 0.35F;
        float pitch = 0.95F
                + player.getRandom().nextFloat() * 0.1F;

        player.connection.send(
                new ClientboundSoundPacket(
                        SoundEvents.AMBIENT_CAVE,
                        SoundSource.AMBIENT,
                        pos.x,
                        pos.y,
                        pos.z,
                        volume,
                        pitch,
                        player.getRandom().nextLong()
                )
        );
    }
}
