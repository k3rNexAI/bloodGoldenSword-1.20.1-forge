package ru.bloodgoldensword;

import com.mojang.logging.LogUtils;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.slf4j.Logger;

@Mod(BloodGoldenSwordMod.MODID)
public class BloodGoldenSwordMod {
    public static final String MODID = "bloodgoldensword";
    private static final Logger LOGGER = LogUtils.getLogger();

    public BloodGoldenSwordMod() {
        IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();

        ModItems.ITEMS.register(modEventBus);

        LOGGER.info("Blood Golden Sword mod загружен!");
    }
}
