package me.bariahq.bariahqmod.command;

import me.bariahq.bariahqmod.player.FPlayer;
import me.bariahq.bariahqmod.rank.Rank;
import me.bariahq.bariahqmod.util.FUtil;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;

@CommandPermissions(level = Rank.MOD, source = SourceType.BOTH)
@CommandParameters(description = "Purge everything! (except for bans).", usage = "/<command>")
public class Command_purgeall extends FreedomCommand
{

    @Override
    public boolean run(CommandSender sender, Player playerSender, Command cmd, String commandLabel, String[] args, boolean senderIsConsole)
    {
        FUtil.staffAction(sender.getName(), "Purging all player data", true);

        // Purge entities
        plugin.ew.wipeEntities(true, true);

        for (Player player : server.getOnlinePlayers())
        {
            FPlayer fPlayer = plugin.pl.getPlayer(player);

            // Unmute all players
            if (fPlayer.isMuted())
            {
                fPlayer.setMuted(false);
            }

            // Unblock all commands
            if (fPlayer.allCommandsBlocked())
            {
                fPlayer.setCommandsBlocked(false);
            }

            // Stop orbiting
            if (fPlayer.isOrbiting())
            {
                fPlayer.stopOrbiting();
            }

            // Unfreeze
            if (fPlayer.getFreezeData().isFrozen())
            {
                fPlayer.getFreezeData().setFrozen(false);
            }

            // Purge potion effects
            for (PotionEffect potion_effect : player.getActivePotionEffects())
            {
                player.removePotionEffect(potion_effect.getType());
            }

            // Uncage
            if (fPlayer.getCageData().isCaged())
            {
                fPlayer.getCageData().setCaged(false);
            }
        }

        // Unfreeze all players
        plugin.fm.setGlobalFreeze(false);

        // Remove all mobs
        Command_mobpurge.purgeMobs();

        return true;
    }
}