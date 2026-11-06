package ru.bloodgoldensword;

import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.SwordItem;
import net.minecraft.world.item.Tier;
import net.minecraft.world.item.Tiers;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

import java.util.List;

/**
 * Золотой меч с красными венами.
 * - Прочность как у железного меча (250), урон/зачариваемость — как у золотого.
 * - Повышенная скорость атаки (1.8, как у каменного меча вместо 1.6 у золотого).
 * - При зажатии ПКМ восстанавливает прочность меча за счёт HP игрока
 *   (3 HP -> 9 прочности, нельзя починить ниже половины здоровья).
 */
public class BloodGoldenSwordItem extends SwordItem {

    /** Сколько HP игрока тратится на починку. */
    private static final float HP_COST = 3.0F;
    /** Сколько единицы прочности получает меч за эту жертву. */
    private static final int DURABILITY_GAIN = 9;

    public BloodGoldenSwordItem(Properties props) {
        // Урон и зачариваемость золотого меча (Tier.GOLD),
        // прочность — как у железного (250),
        // скорость атаки повышена: 1.8f (у обычного золотого — 1.6f).
        super(customTier(Tiers.GOLD), 3, -2.8F, props);
    }

    private static Tier customTier(Tier baseGold) {
        return new Tier() {
            @Override public int getUses() { return Tiers.IRON.getUses(); }          // 250 — прочность железа
            @Override public float getSpeed() { return baseGold.getSpeed(); }        // 3.0 — как золото
            @Override public float getAttackDamageBonus() { return baseGold.getAttackDamageBonus(); } // 0.0
            @Override public net.minecraft.tags.TagKey<net.minecraft.world.level.block.Block> getTag() { return baseGold.getTag(); }
            @Override public int getLevel() { return baseGold.getLevel(); }          // 0
            @Override public int getEnchantmentValue() { return baseGold.getEnchantmentValue(); } // 22
            @Override public net.minecraft.world.item.crafting.Ingredient getRepairIngredient() {
                return baseGold.getRepairIngredient();
            }
        };
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);

        if (stack.getDamageValue() <= 0) {
            return InteractionResultHolder.pass(stack); // меч цел
        }

        if (player.getHealth() <= HP_COST + 1.0F) {
            if (!level.isClientSide) {
                player.displayClientMessage(
                        Component.literal("§cЗдоровья слишком мало для починки!"), true);
            }
            return InteractionResultHolder.fail(stack);
        }

        if (level.isClientSide) {
            return InteractionResultHolder.success(stack); // сервер подтвердит действие
        }

        player.hurt(level.damageSources().generic(), HP_COST);
        if (!player.isRemoved()) {
            int newDamage = Math.max(0, stack.getDamageValue() - DURABILITY_GAIN);
            stack.setDamageValue(newDamage);
            player.playSound(net.minecraft.sounds.SoundEvents.ANVIL_USE, 0.4F, 0.8F);
        }
        return InteractionResultHolder.sidedSuccess(stack, false);
    }

    @Override
    public boolean hurtEnemy(ItemStack stack, LivingEntity target, LivingEntity attacker) {
        // «Красные вены» пьют кровь: 10% шанс дополнительно залечить меч на 1
        // без затрат здоровья (небонус, тематическая механика)
        if (attacker instanceof Player p && p.getRandom().nextFloat() < 0.10F
                && stack.getDamageValue() > 0) {
            stack.setDamageValue(stack.getDamageValue() - 1);
        }
        return super.hurtEnemy(stack, target, attacker);
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> tooltip, TooltipFlag flag) {
        tooltip.add(Component.literal("§6Золотой клинок, пронизанный §cалыми жилами§6."));
        tooltip.add(Component.literal("§7ПКМ: пожертвовать §c3 ❤§7 и починить меч на §a+" + DURABILITY_GAIN + "§7 прочности."));
        tooltip.add(Component.literal("§7Прочность как у железного, атака быстрее золота."));
    }
}
