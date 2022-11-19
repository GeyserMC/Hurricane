package net.camotoy.geyserhacks;

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
    		//event.getMerchant().getTrader().sendMessage("Ingredients for each recipes: "+recipe.getIngredients());
    		event.getMerchant().getTrader().getServer().broadcastMessage("Ingredients for each recipes: "+recipe.getIngredients());
    		for (ItemStack s : recipe.getIngredients())
    		{
				//event.getMerchant().getTrader().sendMessage("ItemStack: "+s);
	    		event.getMerchant().getTrader().getServer().broadcastMessage("ItemStack: "+s);
    		}
		}
        
    }
}
