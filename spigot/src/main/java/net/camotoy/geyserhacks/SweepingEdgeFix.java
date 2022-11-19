package net.camotoy.geyserhacks;

import java.util.Iterator;
import java.util.List;

import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Villager;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.TradeSelectEvent;
import org.bukkit.event.player.PlayerInteractAtEntityEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.MerchantRecipe;
import org.bukkit.inventory.meta.EnchantmentStorageMeta;
import org.bukkit.plugin.Plugin;

import com.google.common.collect.Lists;

public final class SweepingEdgeFix implements Listener 
{

    public SweepingEdgeFix(Plugin plugin) {
        
    }
    
    /*
     * Answer to add unbreaking to villager trade if villager has sweeping edge. Geyser users can interact with book now.
     * https://www.spigotmc.org/threads/change-villager-trade-result-item.553322/page-2#post-4395892
     */
    @EventHandler
    public void onVillagerInteract(final PlayerInteractAtEntityEvent e) {
        if (!(e.getRightClicked() instanceof Villager)) return;

        Villager villager = (Villager) e.getRightClicked();

        /* This uses google guava (which spigot includes) */
        /* google guava, just takes the elements from the list and puts it into a new list (that we can modify) */
        final List<MerchantRecipe> recipes = Lists.newArrayList(villager.getRecipes());

        /* Convert the list to an iterator so we can safely remove values from it while looping through the iterator */

        Iterator<MerchantRecipe> recipeIterator;
        for (recipeIterator = recipes.iterator(); recipeIterator.hasNext(); ) {
            MerchantRecipe recipe = recipeIterator.next();

            if (recipe.getResult().getType().equals(Material.ENCHANTED_BOOK)) {
                EnchantmentStorageMeta meta = (EnchantmentStorageMeta) recipe.getResult().getItemMeta();

                if (meta.hasStoredEnchant(Enchantment.SWEEPING_EDGE)) 
                {
                	if (meta.hasStoredEnchant(Enchantment.DURABILITY)) { return; }
                	int lvl = meta.getEnchantLevel(Enchantment.SWEEPING_EDGE);
                    recipeIterator.remove();

                    /* would probably be best to save this into a static variable since this code will reuse it a lot */
                    ItemStack is = new ItemStack(Material.ENCHANTED_BOOK);
                    EnchantmentStorageMeta esm = (EnchantmentStorageMeta) is.getItemMeta();
                    esm.addStoredEnchant(Enchantment.SWEEPING_EDGE, lvl, false);
                    esm.addStoredEnchant(Enchantment.DURABILITY, 1, false);
                    is.setItemMeta(esm);

                    recipes.add(new MerchantRecipe(is, 1));
                }
            }
        }

        // erase all recipes add put in new ones
        villager.setRecipes(recipes);

        if (villager.getRecipes().size() == 0) {
            e.setCancelled(true);
        }
    }
}
