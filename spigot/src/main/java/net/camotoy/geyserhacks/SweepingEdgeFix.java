package net.camotoy.geyserhacks;

import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.EnchantmentStorageMeta;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.Nullable;

import net.kyori.adventure.text.Component;

import org.bukkit.event.inventory.InventoryType;

public final class SweepingEdgeFix implements Listener 
{
	private final Plugin plugin;
	
    public SweepingEdgeFix(Plugin plugin) {
        this.plugin = plugin;
    }
    
    @EventHandler
    public void findEnchant(InventoryClickEvent e) 
    {
		Player player = (Player) e.getWhoClicked();
		//Inventory becomes null for some reason after player clicks on item then drops it out their inventory.
		if(e.getClickedInventory()!=null)
		{
			if (e.getClickedInventory().getType() == InventoryType.PLAYER) 
			{
				ItemStack item = e.getCurrentItem();
				if (item.getType().equals(Material.DIAMOND_SWORD)
						|| item.getType().equals(Material.NETHERITE_SWORD) || item.getType().equals(Material.IRON_SWORD)
						|| item.getType().equals(Material.GOLDEN_SWORD) || item.getType().equals(Material.STONE_SWORD)
						|| item.getType().equals(Material.WOODEN_SWORD)) 
				{
					player.sendMessage(item.getType().name());
					ItemMeta meta = item.getItemMeta();
					if (meta.hasEnchant(Enchantment.SWEEPING_EDGE)) 
					{
						player.sendMessage("detected sweeping edge");
						int sweepingLevel = meta.getEnchantLevel(Enchantment.SWEEPING_EDGE);
						String displayName = item.getType().name();
						meta.setDisplayName("Sweeping Edge " + sweepingLevel);
						if(meta.getEnchants().size()==1)
						{
							item.addEnchantment(Enchantment.DURABILITY, 1);
							player.sendMessage("Unbreaking added for usability of Sweeping Edge enchant.");
						}
						player.sendMessage("Sweeping Edge fixed on "+displayName);
						item.setItemMeta(meta);
						e.setCurrentItem(item);
					}
				}
				else if(item.getType().equals(Material.ENCHANTED_BOOK))
				{
					player.sendMessage(item.getType().name());
					EnchantmentStorageMeta meta = (EnchantmentStorageMeta) item.getItemMeta();
					if (meta.hasStoredEnchant(Enchantment.SWEEPING_EDGE)) 
					{
						player.sendMessage("detected sweeping edge");
						int sweepingLevel = meta.getStoredEnchantLevel(Enchantment.SWEEPING_EDGE);
						String displayName = item.getType().name();
						meta.setDisplayName("Sweeping Edge " + sweepingLevel);
						if(meta.getStoredEnchants().size()==1)
						{
							meta.addStoredEnchant(Enchantment.DURABILITY, 1, false);
							player.sendMessage("Unbreaking added for usability of Sweeping Edge enchant.");
						}
						player.sendMessage("Sweeping Edge fixed on "+displayName);
						item.setItemMeta(meta);
						e.setCurrentItem(item);
					}
				}
			}
		}
	}
}