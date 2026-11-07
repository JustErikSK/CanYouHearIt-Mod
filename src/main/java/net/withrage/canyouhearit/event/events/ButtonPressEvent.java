package net.withrage.canyouhearit.event.events;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.block.ButtonBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.withrage.canyouhearit.event.ParanormalEvent;
import net.withrage.canyouhearit.event.ParanormalEventManager;

import java.util.ArrayList;
import java.util.List;

public class ButtonPressEvent implements ParanormalEvent {

    private final ParanormalEventManager manager;

    public ButtonPressEvent(ParanormalEventManager manager) {
        this.manager = manager;
    }

    @Override
    public boolean canTrigger(ServerPlayer player) {
        return player.isAlive()
                && !player.isSpectator()
                && !findNearbyButtons(player).isEmpty();
    }

    @Override
    public void trigger(ServerPlayer player) {
        List<BlockPos> buttons = findNearbyButtons(player);

        if (buttons.isEmpty()) {
            return;
        }

        BlockPos selected = buttons.get(
                player.getRandom().nextInt(buttons.size())
        );

        BlockState state = player.level().getBlockState(selected);

        manager.addButtonPressSequence(
                new ButtonPressSequence(player, selected, state)
        );
    }

    private List<BlockPos> findNearbyButtons(ServerPlayer player) {
        List<BlockPos> buttons = new ArrayList<>();

        BlockPos center = player.blockPosition();

        int radius = 8;
        int verticalRadius = 5;

        for (BlockPos pos : BlockPos.betweenClosed(
                center.offset(-radius, -verticalRadius, -radius),
                center.offset(radius, verticalRadius, radius)
        )) {
            BlockState state = player.level().getBlockState(pos);

            if (state.getBlock() instanceof ButtonBlock) {
                buttons.add(pos.immutable());
            }
        }
        return buttons;
    }
}
