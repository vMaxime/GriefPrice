package fr.maximouz.griefprice.events;

import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;
import org.bukkit.event.player.PlayerEvent;

import java.util.List;

public class DynamiteExplodeEvent extends PlayerEvent {

    private static final HandlerList handlers = new HandlerList();

    private final List<Block> blocks;

    public DynamiteExplodeEvent(Player player, List<Block> blocks) {
        super(player);
        this.blocks = blocks;
    }

    public List<Block> getBlocks() {
        return blocks;
    }

    public HandlerList getHandlers() {
        return handlers;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }

}
