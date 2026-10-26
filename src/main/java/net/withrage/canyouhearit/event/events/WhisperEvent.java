package net.withrage.canyouhearit.event.events;

import net.minecraft.network.protocol.game.ClientboundSoundPacket;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.phys.Vec3;
import net.withrage.canyouhearit.event.ParanormalEvent;
import net.withrage.canyouhearit.sound.ModSounds;

public class WhisperEvent implements ParanormalEvent {

    @Override
    public boolean canTrigger(ServerPlayer player) {
        return player.isAlive() && !player.isSpectator();
    }

    @Override
    public void trigger(ServerPlayer player) {

        Vec3 position;
        Vec3 eyePosition = player.getEyePosition();
        Vec3 lookDirection = player.getLookAngle();

        int type = player.getRandom().nextInt(3);

        // I should use comments more as I often forget what each part of the code does, and then I have to search it in my notes lol
        // type 1: close whisper (1-3 blocks away)
        // type 2: distant whisper (5-10 blocks away)
        // type 3: whisper directly behind the player

        switch (type) {
            case 0 -> {
                position = randomPosition(
                        player, eyePosition, 1.0, 3.0
                );
            }

            case 1 -> {
                position = randomPosition(
                        player, eyePosition, 5.0, 10.0
                );
            }

            default -> {
                Vec3 horizontalLook = new Vec3(
                        lookDirection.x,
                        0,
                        lookDirection.z
                ).normalize();

                position = eyePosition.subtract(
                        horizontalLook.scale(1.5)
                );
            }
        }

        float volume = switch (type) {
            case 0 -> 0.65F;
            case 1 -> 0.9F;
            default -> 0.55F;
        };

        float pitch = 0.9F
                + player.getRandom().nextFloat() * 0.2F;

        player.connection.send(
                new ClientboundSoundPacket(
                        ModSounds.WHISPER.getHolder().orElseThrow(),
                        SoundSource.AMBIENT,
                        position.x,
                        position.y,
                        position.z,
                        volume,
                        pitch,
                        player.getRandom().nextLong()
                )
        );
    }

    private Vec3 randomPosition(
            ServerPlayer player,
            Vec3 center,
            double minDistance,
            double maxDistance
    ) {
        double angle = player.getRandom().nextDouble()
                * Math.PI * 2;

        double distance = minDistance
                + player.getRandom().nextDouble()
                * (maxDistance - minDistance);

        return center.add(
                Math.cos(angle) * distance,
                0,
                Math.sin(angle) * distance
        );
    }
}
