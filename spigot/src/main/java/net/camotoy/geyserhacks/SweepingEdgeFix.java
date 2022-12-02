package net.camotoy.geyserhacks;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

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
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.inventory.PrepareAnvilEvent;
import org.geysermc.floodgate.api.FloodgateApi;
import net.kyori.adventure.text.Component;

public final class SweepingEdgeFix implements Listener {
	private final Plugin plugin;

	public SweepingEdgeFix(Plugin plugin) {
		this.plugin = plugin;
	}

	/*
	 * TBYT adds Sweeping Edge fix. This will update lore to sweeping edge
	 * and the enchantment level. (Jens helped with Lore)
	 */
	@EventHandler
	public void findEnchant(InventoryClickEvent event) {
		Player player = (Player) event.getWhoClicked();
		// Checking for floodgate/geyser player.
		if (FloodgateApi.getInstance().isFloodgatePlayer(player.getUniqueId())) {
			// Inventory becomes null after player clicks on item then drops it out their
			// inventory.
			if (event.getClickedInventory() != null) {
				if (event.getClickedInventory().getType() == InventoryType.PLAYER) {
					ItemStack item = event.getCurrentItem();
					if (item != null) // rare case this equals null
					{
						if (item.getType().equals(Material.ENCHANTED_BOOK)) {
							EnchantmentStorageMeta meta = (EnchantmentStorageMeta) item.getItemMeta();
							if (meta.hasStoredEnchant(Enchantment.SWEEPING_EDGE)) {
								int sweepingLevel = meta.getStoredEnchantLevel(Enchantment.SWEEPING_EDGE);
								// will overwrite any existing lore.
								List<Component> loreList = new ArrayList<Component>();
								loreList.add(Component.text("Sweeping Edge " + sweepingLevel));
								meta.lore(loreList);
								//if (meta.getStoredEnchants().size() == 1) {
									//meta.addStoredEnchant(Enchantment.DURABILITY, 1, false);
									// player.sendMessage("Sweeping Edge Fixed on Enchanted Book.");
								//}
								item.setItemMeta(meta);
								event.setCurrentItem(item);
							} else
								meta.lore(new ArrayList<Component>());
						} else if (item.hasItemMeta()) {
							ItemMeta meta = item.getItemMeta();
							if (meta.hasEnchant(Enchantment.SWEEPING_EDGE)) {
								int sweepingLevel = meta.getEnchantLevel(Enchantment.SWEEPING_EDGE);
								// will overwrite any existing lore.
								List<Component> loreList = new ArrayList<Component>();
								loreList.add(Component.text("Sweeping Edge " + sweepingLevel));
								meta.lore(loreList);
								//if (meta.getEnchants().size() == 1) {
									//meta.addEnchant(Enchantment.DURABILITY, 1, false);
									// player.sendMessage("Sweeping Edge Fixed.");
								//}
								item.setItemMeta(meta);
								event.setCurrentItem(item);
							} else
								meta.lore(new ArrayList<Component>());
						}
					}
				}
			}
		}
	}
	
	//remember take off lore after disenchant.
	
	/*
	 * https://bukkit.org/threads/how-to-put-unsafe-enchantments-to-result-item-in-anvil.412472/#post-3350913
	 */
	@EventHandler
    public void onPrepareAnvil(PrepareAnvilEvent event)
    {
		Player player = (Player) event.getViewers().get(0);
		// Checking for floodgate/geyser player.
		if (FloodgateApi.getInstance().isFloodgatePlayer(player.getUniqueId())) 
		{
	        if(event.getInventory().getItem(0) != null && event.getInventory().getItem(1) != null)
	        {
	        	//if(event.getInventory().getItem(0) != null && event.getInventory().getItem(1) != null)
	            ItemStack result = new ItemStack(event.getInventory().getItem(0));
	            if(event.getInventory().getItem(1).getType() == Material.ENCHANTED_BOOK)
	            {
	                EnchantmentStorageMeta bookmeta = (EnchantmentStorageMeta) event.getInventory().getItem(1).getItemMeta();
	                bookmeta.addStoredEnchant(Enchantment.DURABILITY, 0, false);
	                Map<Enchantment, Integer> enchantments = bookmeta.getStoredEnchants();
	                result.addUnsafeEnchantments(enchantments);
	            }
	            //result.addUnsafeEnchantments(event.getInventory().getItem(1).getEnchantments());
	            event.setResult(result);
	        }
		}
    }
}
