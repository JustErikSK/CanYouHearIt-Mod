package net.withrage.canyouhearit.event.events;

import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.protocol.game.ClientboundSoundPacket;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;

public class ButtonPressSequence {

    private final ServerPlayer player;
    private final BlockPos buttonPos;

    private final SoundEvent clickOn;
    private final SoundEvent clickOff;

    private int remainingPresses;
    private int ticksUntilNextSound;
    private boolean pressed;

    public ButtonPressSequence(ServerPlayer player,
                               BlockPos buttonPos,
                               BlockState state) {
        this.player = player;
        this.buttonPos = buttonPos;

        boolean stone = state.is(Blocks.STONE_BUTTON)
                || state.is(Blocks.POLISHED_BLACKSTONE_BUTTON);

        this.clickOn = stone
                ? SoundEvents.STONE_BUTTON_CLICK_ON
                : SoundEvents.WOODEN_BUTTON_CLICK_ON;

        this.clickOff = stone
                ? SoundEvents.STONE_BUTTON_CLICK_OFF
                : SoundEvents.WOODEN_BUTTON_CLICK_OFF;

        this.remainingPresses = player.getRandom().nextFloat() < 0.25F
                ? 2
                : 1;

        this.ticksUntilNextSound = 0;
        this.pressed = false;
    }

    public boolean tick() {
        if (!player.isAlive() || player.isRemoved()) {
            return true;
        }

        if (ticksUntilNextSound > 0) {
            ticksUntilNextSound--;
            return false;
        }

        if (!pressed) {
            playSound(clickOn);
            pressed = true;

            ticksUntilNextSound = 8 + player.getRandom().nextInt(8);

            return false;
        }

        playSound(clickOff);
        pressed = false;
        remainingPresses--;

        if (remainingPresses <= 0) {
            return true;
        }

        ticksUntilNextSound = 10 + player.getRandom().nextInt(11);

        return false;
    }

    private void playSound(SoundEvent sound) {
        double x = buttonPos.getX() + 0.5;
        double y = buttonPos.getY() + 0.5;
        double z = buttonPos.getZ() + 0.5;

        float volume = 0.25F;
        float pitch = 0.9F + player.getRandom().nextFloat() * 0.2F;

        player.connection.send(
                new ClientboundSoundPacket(
                        BuiltInRegistries.SOUND_EVENT.wrapAsHolder(sound),
                        SoundSource.BLOCKS,
                        x,
                        y,
                        z,
                        volume,
                        pitch,
                        player.getRandom().nextLong()
                )
        );
    }
}
