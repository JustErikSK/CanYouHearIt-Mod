package net.withrage.canyouhearit.util;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.DoorBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.DoubleBlockHalf;

import java.util.ArrayList;
import java.util.List;

public class DoorUtils {

        public static List<BlockPos> findNearbyDoors(ServerPlayer player,
        boolean includeIronDoors
) {
            List<BlockPos> doors = new ArrayList<>();

            BlockPos center = player.blockPosition();
            Level level = player.level();

            for (BlockPos pos : BlockPos.betweenClosed(
                    center.offset(-8, -4, -8),
                    center.offset(8, 4, 8))) {

                BlockState state = level.getBlockState(pos);

                if (state.getBlock() instanceof DoorBlock
                        && (includeIronDoors || !state.is(Blocks.IRON_DOOR))
                        && state.getValue(DoorBlock.HALF)
                        == DoubleBlockHalf.LOWER) {

                    doors.add(pos.immutable());
                }
            }
            return doors;
        }

    }
