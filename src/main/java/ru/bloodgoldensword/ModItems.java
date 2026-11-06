package ru.bloodgoldensword;

import net.minecraft.world.item.Item;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class ModItems {
    public static final DeferredRegister<Item> ITEMS =
            DeferredRegister.create(ForgeRegistries.ITEMS, BloodGoldenSwordMod.MODID);

    public static final RegistryObject<Item> BLOOD_GOLDEN_SWORD =
            ITEMS.register("blood_golden_sword",
                    () -> new BloodGoldenSwordItem(new Item.Properties().stacksTo(1)));
}
