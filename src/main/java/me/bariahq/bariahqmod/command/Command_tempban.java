package me.bariahq.bariahqmod.command;

import me.bariahq.bariahqmod.banning.Ban;
import me.bariahq.bariahqmod.punishment.Punishment;
import me.bariahq.bariahqmod.punishment.PunishmentType;
import me.bariahq.bariahqmod.rank.Rank;
import me.bariahq.bariahqmod.util.FUtil;
import net.pravian.aero.util.Ips;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.text.SimpleDateFormat;
import java.util.Date;

@CommandPermissions(level = Rank.MOD, source = SourceType.BOTH)
@CommandParameters(description = "Temporarily ban someone.", usage = "/<command> [playername] [duration] [reason]")
public class Command_tempban extends FreedomCommand
{

    private static final SimpleDateFormat date_format = new SimpleDateFormat("yyyy-MM-dd \'at\' HH:mm:ss z");

    @Override
    public boolean run(CommandSender sender, Player playerSender, Command cmd, String commandLabel, String[] args, boolean senderIsConsole)
    {
        if (args.length < 1)
        {
            return false;
        }

        final Player player = getPlayer(args[0]);

        if (player == null)
        {
            msg(FreedomCommand.PLAYER_NOT_FOUND);
            return true;
        }

        final StringBuilder message = new StringBuilder("Temporarily banned " + player.getName());

        Date expires = FUtil.parseDateOffset("30m");
        if (args.length >= 2)
        {
            Date parsed_offset = FUtil.parseDateOffset(args[1]);
            if (parsed_offset != null)
            {
                expires = parsed_offset;
            }
        }
        message.append(" until ").append(date_format.format(expires));

        String reason = "Banned by " + sender.getName();
        if (args.length >= 3)
        {
            reason = StringUtils.join(ArrayUtils.subarray(args, 2, args.length), " ") + " (" + sender.getName() + ")";
            message.append(", Reason: \"").append(reason).append("\"");
        }

        // strike with lightning effect:
        final Location targetPos = player.getLocation();
        for (int x = -1; x <= 1; x++)
        {
            for (int z = -1; z <= 1; z++)
            {
                final Location strike_pos = new Location(targetPos.getWorld(), targetPos.getBlockX() + x, targetPos.getBlockY(), targetPos.getBlockZ() + z);
                targetPos.getWorld().strikeLightning(strike_pos);
            }
        }

        FUtil.staffAction(sender.getName(), message.toString(), true);

        plugin.bm.addBan(Ban.forPlayer(player, sender, expires, reason));

        player.kickPlayer(sender.getName() + " - " + message.toString());
        plugin.pul.logPunishment(new Punishment(player.getName(), Ips.getIp(player), sender.getName(), PunishmentType.TEMPBAN, reason));

        return true;
    }
}
