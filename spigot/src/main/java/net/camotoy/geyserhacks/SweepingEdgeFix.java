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
import org.bukkit.event.inventory.PrepareAnvilEvent;
import org.geysermc.floodgate.api.FloodgateApi;
import net.kyori.adventure.text.Component;

public final class SweepingEdgeFix implements Listener {
	private final Plugin plugin;

	public SweepingEdgeFix(Plugin plugin) {
		this.plugin = plugin;
	}

	/*
	 * TBYT adds Sweeping Edge fix. This will update lore to sweeping edge and the
	 * enchantment level. (Jens helped with Lore). Unbreaking 1 may be applied to
	 * the Anvil Result in the event of a sweeping edge only book in the 2nd anvil
	 * slot.
	 */
	@EventHandler
	public void findEnchant(InventoryClickEvent event) {
		Player player = (Player) event.getWhoClicked();

		ItemStack item = event.getCurrentItem();

		// This IF Block is for a circumstance of a geyser bedrock player
		// dropping/moving
		// the enchanted book out the 2nd anvil slot, after it is given the unbreaking
		// enchant. This is to prevent getting free unbreaking 1 books. It does not
		// prevent the anvil operation from happening. It does not prevent legit
		// possession of unbreaking 1 on sweeping edge books. The anvil operation will
		// still apply to the result, this means unbreaking 1 will be on the result if
		// detected sweeping edge book (with no other enchants) in the 2nd slot.
		if (item.getType().equals(Material.ENCHANTED_BOOK)) {
			EnchantmentStorageMeta meta = (EnchantmentStorageMeta) item.getItemMeta();
			if (meta.hasStoredEnchant(Enchantment.SWEEPING_EDGE)) {
				if (meta.hasLore()) {
					List<Component> loreCheck = new ArrayList<Component>();
					loreCheck.add(Component.text("modifiedanvilbook"));
					if (meta.lore().contains(loreCheck.get(0))) 
					{
						if (meta.hasStoredEnchant(Enchantment.DURABILITY)) {
							meta.removeStoredEnchant(Enchantment.DURABILITY);
						}
						item.setItemMeta(meta);
						event.setCurrentItem(item);
					}
				}
			}
		}

		// Checking for floodgate/geyser player.
		if (FloodgateApi.getInstance().isFloodgatePlayer(player.getUniqueId())) {
			// Inventory becomes null after player clicks on item then drops it out their
			// inventory.
			if (event.getClickedInventory() != null) {
				if (event.getClickedInventory().getType() == InventoryType.PLAYER) {
					item = event.getCurrentItem();
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
							} else {
								meta.lore(new ArrayList<Component>());
							}
							item.setItemMeta(meta);
							event.setCurrentItem(item);
						} else if (item.hasItemMeta()) {
							ItemMeta meta = item.getItemMeta();
							if (meta.hasEnchant(Enchantment.SWEEPING_EDGE)) {
								int sweepingLevel = meta.getEnchantLevel(Enchantment.SWEEPING_EDGE);
								// will overwrite any existing lore.
								List<Component> loreList = new ArrayList<Component>();
								loreList.add(Component.text("Sweeping Edge " + sweepingLevel));
								meta.lore(loreList);
							} else {
								meta.lore(new ArrayList<Component>());
							}
							item.setItemMeta(meta);
							event.setCurrentItem(item);
						}
					}
				}
			}
		}
	}

	/*
	 * https://bukkit.org/threads/how-to-put-unsafe-enchantments-to-result-item-in-
	 * anvil.412472/#post-3350913
	 */
	@EventHandler
	public void onPrepareAnvil(PrepareAnvilEvent event) {
		Player player = (Player) event.getViewers().get(0);
		// Checking for floodgate/geyser player.
		if (FloodgateApi.getInstance().isFloodgatePlayer(player.getUniqueId())) {
			ItemStack secondItem = event.getInventory().getSecondItem();
			if (event.getInventory().getFirstItem() != null && secondItem != null) {
				if (secondItem.getType() == Material.ENCHANTED_BOOK) {
					EnchantmentStorageMeta bookmeta = (EnchantmentStorageMeta) secondItem.getItemMeta();
					// if does not enters this if statement, bookmeta does not change.
					if (bookmeta.hasStoredEnchant(Enchantment.SWEEPING_EDGE)) {
						if (bookmeta.getStoredEnchants().size() == 1) {
							// adding tagged lore so book will stay inside anvil event, and the unbreaking
							// enchant will be removed if book exits anvil operation in the event anvil
							// result does take place.
							List<Component> loreList = new ArrayList<Component>();
							loreList.add(Component.text("modifiedanvilbook"));
							bookmeta.lore(loreList);
							bookmeta.addStoredEnchant(Enchantment.DURABILITY, 1, false);
							secondItem.setItemMeta(bookmeta);
							event.getInventory().setSecondItem(secondItem);
						}
					}
				}
			}
		}
	}
}