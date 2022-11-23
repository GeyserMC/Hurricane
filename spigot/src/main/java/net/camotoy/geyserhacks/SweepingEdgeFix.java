package net.camotoy.geyserhacks;

import java.util.ArrayList;
import java.util.List;

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
import org.geysermc.floodgate.api.FloodgateApi;
import org.jetbrains.annotations.Nullable;

import net.kyori.adventure.text.Component;

public final class SweepingEdgeFix implements Listener {
	private final Plugin plugin;

	public SweepingEdgeFix(Plugin plugin) {
		this.plugin = plugin;
	}

	/*
	 * TBYT adds Sweeping Edge fix. This adds unbreaking 1 to sweeping edge items if
	 * they only have the 1 enchant(sweeping edge). If has sweeping edge and another
	 * enchant, or after applying unbreaking fix, will update lore to sweeping edge
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
								if (meta.getStoredEnchants().size() == 1) {
									meta.addStoredEnchant(Enchantment.DURABILITY, 1, false);
									// player.sendMessage("Sweeping Edge Fixed on Enchanted Book.");
								}
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
								if (meta.getEnchants().size() == 1) {
									meta.addEnchant(Enchantment.DURABILITY, 1, false);
									// player.sendMessage("Sweeping Edge Fixed.");
								}
								item.setItemMeta(meta);
								event.setCurrentItem(item);
							} else
								meta.lore(new ArrayList<Component>());
						}
					}
				}
				// trying to fix anvil not giving results on bedrock, that normally would on Java. 
				else if (event.getClickedInventory().getType() == InventoryType.ANVIL) {
					if (event.getSlotType() == InventoryType.SlotType.RESULT)
						return;
					ItemStack item = event.getInventory().getItem(1);
					if (item != null && item.hasItemMeta()) {
						if (item.getItemMeta().hasLore()) {
							if (item.getType().equals(Material.ENCHANTED_BOOK)) {
								EnchantmentStorageMeta meta = (EnchantmentStorageMeta) item.getItemMeta();
								meta.lore(new ArrayList<Component>());
								item.setItemMeta(meta);
							} else {
								ItemMeta meta = item.getItemMeta();
								meta.lore(new ArrayList<Component>());
								item.setItemMeta(meta);
							}
							event.getInventory().setItem(0, item);
						}
					}
				}
			}
		}
	}
}
