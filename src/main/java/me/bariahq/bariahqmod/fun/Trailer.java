package me.bariahq.bariahqmod.fun;

import me.bariahq.bariahqmod.BariaHQMod;
import me.bariahq.bariahqmod.FreedomService;
import me.bariahq.bariahqmod.util.DepreciationAggregator;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.player.PlayerMoveEvent;

import java.util.HashSet;
import java.util.Random;
import java.util.Set;

public class Trailer extends FreedomService
{

    private final Random random = new Random();
    private final Set<String> trailPlayers = new HashSet<>(); // player name

    public Trailer(BariaHQMod plugin)
    {
        super(plugin);
    }

    @Override
    protected void onStart()
    {
    }

    @Override
    protected void onStop()
    {
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onPlayerMove(PlayerMoveEvent event)
    {
        if (trailPlayers.isEmpty())
        {
            return;
        }

        if (!trailPlayers.contains(event.getPlayer().getName()))
        {
            return;
        }

        Block fromBlock = event.getFrom().getBlock();
        if (!fromBlock.isEmpty())
        {
            return;
        }

        Block toBlock = event.getTo().getBlock();
        if (fromBlock.equals(toBlock))
        {
            return;
        }

        fromBlock.setType(Material.WOOL);
        DepreciationAggregator.setData_Block(fromBlock, (byte) random.nextInt(16));
    }

    public void remove(Player player)
    {
        trailPlayers.remove(player.getName());
    }

    public void add(Player player)
    {
        trailPlayers.add(player.getName());
    }
}
