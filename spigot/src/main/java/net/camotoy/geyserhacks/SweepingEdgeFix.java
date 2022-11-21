package net.camotoy.geyserhacks;

import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.inventory.meta.EnchantmentStorageMeta;
import org.bukkit.plugin.Plugin;

public final class SweepingEdgeFix implements Listener 
{

    public SweepingEdgeFix(Plugin plugin) {
        
    }
    
    @EventHandler
    public void findEnchant(InventoryOpenEvent e) 
    {
    	PlayerInventory inv = e.getPlayer().getInventory();
        for(int i = 0; i < inv.getSize()-1; i++)
        {
        	 ItemStack item = inv.getItem(i);
             if(item.getType().equals(Material.BOOK) 
            		 || item.getType().equals(Material.DIAMOND_SWORD) 
            		 || item.getType().equals(Material.NETHERITE_SWORD)
            		 || item.getType().equals(Material.IRON_SWORD)
            		 || item.getType().equals(Material.GOLDEN_SWORD)
            		 || item.getType().equals(Material.STONE_SWORD)
            		 || item.getType().equals(Material.WOODEN_SWORD))
             {
            	 EnchantmentStorageMeta meta = (EnchantmentStorageMeta) item.getItemMeta();

                 if (meta.hasStoredEnchant(Enchantment.SWEEPING_EDGE)) 
                 {
                	 if (!meta.hasStoredEnchant(Enchantment.DURABILITY)) 
                     {
	                     meta.addStoredEnchant(Enchantment.DURABILITY, 1, false);
	                     meta.setDisplayName("Sweeping Edge "+item.getItemMeta().getEnchantLevel(Enchantment.SWEEPING_EDGE)+" "+item.getType().name());
	                     item.setItemMeta(meta);
                     }
                 }
             }
        }
    }
}
