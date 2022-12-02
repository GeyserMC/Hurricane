package net.camotoy.geyserhacks;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

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

        if (event.getClickedInventory() == null) {
            return;
        }

        ItemStack item = event.getCurrentItem();
        if (item == null) {
            return;
        }
        // all players must be checked for modifiedanvilbook tag.
        if (item.getType().equals(Material.ENCHANTED_BOOK)) {
            EnchantmentStorageMeta meta = (EnchantmentStorageMeta) item.getItemMeta();
            if (meta.hasStoredEnchant(Enchantment.SWEEPING_EDGE) && meta.hasLore()) {
                List<Component> loreCheck = new ArrayList<Component>();
                loreCheck.add(Component.text("modifiedanvilbook"));
                if (Objects.requireNonNull(meta.lore()).contains(loreCheck.get(0)) && meta.hasStoredEnchant(Enchantment.DURABILITY)) {
                    meta.removeStoredEnchant(Enchantment.DURABILITY);
                }

                item.setItemMeta(meta);
                event.setCurrentItem(item);
            }
        }
        
        if (!FloodgateApi.getInstance().isFloodgatePlayer(player.getUniqueId())) {
            return;
        }
        if (event.getClickedInventory().getType() == InventoryType.PLAYER) {
            item = event.getCurrentItem();

            if (item == null) {
                return;
            }

            if (item.getType().equals(Material.ENCHANTED_BOOK)) {
                EnchantmentStorageMeta meta = (EnchantmentStorageMeta) item.getItemMeta();
                if (meta.hasStoredEnchant(Enchantment.SWEEPING_EDGE)) {
                    int sweepingLevel = meta.getStoredEnchantLevel(Enchantment.SWEEPING_EDGE);
                    // will overwrite any existing lore.
                    List<Component> loreList = new ArrayList<>(); 
                    loreList.add(Component.text("Sweeping Edge " + sweepingLevel));
                    meta.lore(loreList);
                } else {
                    meta.lore(new ArrayList<>());
                }

                item.setItemMeta(meta);
                event.setCurrentItem(item);
            } else if (item.hasItemMeta()) {
                ItemMeta meta = item.getItemMeta();
                if (meta.hasEnchant(Enchantment.SWEEPING_EDGE)) {
                    int sweepingLevel = meta.getEnchantLevel(Enchantment.SWEEPING_EDGE);
                    // will overwrite any existing lore.
                    List<Component> loreList = new ArrayList<>();
                    loreList.add(Component.text("Sweeping Edge " + sweepingLevel));
                    meta.lore(loreList);
                } else {
                    meta.lore(new ArrayList<>());
                }

                item.setItemMeta(meta);
                event.setCurrentItem(item);
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
        if (!FloodgateApi.getInstance().isFloodgatePlayer(player.getUniqueId())) {
            return;
        }

        ItemStack secondItem = event.getInventory().getSecondItem();
        if (event.getInventory().getFirstItem() != null && secondItem != null) {
            if (secondItem.getType() == Material.ENCHANTED_BOOK) {
                EnchantmentStorageMeta bookmeta = (EnchantmentStorageMeta) secondItem.getItemMeta();
                if (bookmeta.hasStoredEnchant(Enchantment.SWEEPING_EDGE) && bookmeta.getStoredEnchants().size() == 1) {
                	// will overwrite any existing lore.
                    List<Component> loreList = new ArrayList<>();
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