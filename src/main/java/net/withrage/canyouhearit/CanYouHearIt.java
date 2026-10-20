package net.withrage.canyouhearit;

import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.Mod;
import net.withrage.canyouhearit.event.ParanormalEventManager;

@Mod(CanYouHearIt.MOD_ID)
public class CanYouHearIt {

    public static final String MOD_ID = "canyouhearit";

    public CanYouHearIt() {
        MinecraftForge.EVENT_BUS.register(
                new ParanormalEventManager()
        );
    }
}
