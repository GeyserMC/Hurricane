package net.camotoy.geyserhacks;

import org.bukkit.enchantments.Enchantment;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.TradeSelectEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.MerchantRecipe;
import org.bukkit.plugin.Plugin;

public final class SweepingEdgeFix implements Listener 
{

    public SweepingEdgeFix(Plugin plugin) {
        
    }

    /**
     * 
     */
    @EventHandler
    public void onVillagerInteract(final TradeSelectEvent event) 
    {
    	for (MerchantRecipe recipe : event.getMerchant().getRecipes())
    	{
    		for (ItemStack s : recipe.getIngredients())
    		{
    			if(s.containsEnchantment(Enchantment.SWEEPING_EDGE))
    			{
    				s.addEnchantment(Enchantment.DURABILITY, 1);
    				event.getMerchant().getTrader().getServer().broadcastMessage("ItemStack: "+s);
    				event.getMerchant().getTrader().getServer().broadcastMessage("Fixed enchantment?");
    			}
	    		
    		}
		}
        
    }
}
