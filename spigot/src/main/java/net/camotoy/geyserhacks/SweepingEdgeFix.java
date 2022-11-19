package net.camotoy.geyserhacks;

import java.util.List;

import org.bukkit.enchantments.Enchantment;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.TradeSelectEvent;
import org.bukkit.inventory.ItemStack;
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
    	for (int i = 0; i < event.getMerchant().getRecipes().size(); i++) 
    	{
    		List<ItemStack> Items = event.getMerchant().getRecipe(i).getIngredients();
    		for(int x=0; x < Items.size(); x++)
    		{
    			if(event.getMerchant().getRecipe(i).getIngredients().get(x).containsEnchantment(Enchantment.SWEEPING_EDGE))
    			{
    				event.getMerchant().getTrader().getServer().broadcastMessage("Item: "+event.getMerchant().getRecipe(i).getIngredients().get(x));
    				if(!event.getMerchant().getRecipe(i).getIngredients().get(x).containsEnchantment(Enchantment.DURABILITY))
    				{
    					event.getMerchant().getRecipe(i).getIngredients().get(x).addUnsafeEnchantment(Enchantment.DURABILITY, 1);
    				}
    				event.getMerchant().getTrader().getServer().broadcastMessage("Fixed enchantment?");
    			}
    		}
    	}
        
    }
}
