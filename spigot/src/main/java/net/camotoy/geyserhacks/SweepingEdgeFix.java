package net.camotoy.geyserhacks;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.TradeSelectEvent;
import org.bukkit.plugin.Plugin;

public final class SweepingEdgeFix implements Listener 
{

    public SweepingEdgeFix(Plugin plugin) {
        
    }

    /**
     * 
     */
    @EventHandler
    public void onVillagerInteract(final TradeSelectEvent event) {
    	if (event.getMerchant().getTrader() == null ||
                !event.getMerchant().getTrader().getName().equals("Geyser"))
    	{
    		event.getMerchant().getTrader().sendMessage("Is Geyser");
    		return;
    	}
    	
    	event.getMerchant().getTrader().sendMessage("Not Geyser");
        
    }
}
