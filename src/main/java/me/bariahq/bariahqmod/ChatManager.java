package me.bariahq.bariahqmod;

import me.bariahq.bariahqmod.player.FPlayer;
import me.bariahq.bariahqmod.rank.Displayable;
import me.bariahq.bariahqmod.shop.ShopData;
import me.bariahq.bariahqmod.staff.StaffMember;
import me.bariahq.bariahqmod.util.FLog;
import me.bariahq.bariahqmod.util.FSync;
import me.bariahq.bariahqmod.util.FUtil;
import org.bukkit.ChatColor;
import org.bukkit.Sound;
import org.bukkit.SoundCategory;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.player.AsyncPlayerChatEvent;

import java.util.Arrays;
import java.util.List;

public class ChatManager extends FreedomService
{
    // Putting an end to free hosted server advertisements
    public static final List<String> DISGUSTING_HOST_DOMAINS = Arrays.asList("my-serv.com", "mymcserver.org", "serv.gs", "myserver.gs", "g-s.nu", "mcserv.me",
            "mcpro.io", "1337srv.com", "mcnetwork.me", "serv.nu", "mygs.co", "mchosting.pro",
            "server-minecraft.pro", "mcraft.pro", "mcserv.pro", "mchost.pro", "crafted.pro",
            "cubed.pro", "minecraft-crafting.pro", "aternos.me");
    public static ChatColor scc = ChatColor.AQUA;
    public static boolean scr = false;
    public static boolean scn = false;

    public ChatManager(BariaHQMod plugin)
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

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onPlayerChatFormat(AsyncPlayerChatEvent event)
    {
        try
        {
            handleChatEvent(event);
        }
        catch (Exception ex)
        {
            FLog.severe(ex);
        }
    }

    private void handleChatEvent(AsyncPlayerChatEvent event)
    {
        final Player player = event.getPlayer();
        ShopData sd = plugin.sh.getData(player);
        String message = event.getMessage().trim();

        if (!sd.isColoredchat())
        {
            // Strip color from messages
            message = ChatColor.stripColor(message);
        }
        else
        {
            // Format color
            message = FUtil.colorize(message);
        }

        // Execs can use formatting :^)
        if (!FUtil.isManager(player.getName()))
        {
            message = message.replaceAll(ChatColor.BOLD.toString(), "&l");
            message = message.replaceAll(ChatColor.MAGIC.toString(), "&k");
            message = message.replaceAll(ChatColor.ITALIC.toString(), "&o");
            message = message.replaceAll(ChatColor.UNDERLINE.toString(), "&n");
            message = message.replaceAll(ChatColor.STRIKETHROUGH.toString(), "&m");
        }

        // Truncate messages that are too long - 256 characters is vanilla client max
        if (message.length() > 256)
        {
            message = message.substring(0, 256);
            FSync.playerMsg(player, "Message was shortened because it was too long to send.");
        }

        // Check for caps
        if (!plugin.al.isStaffMember(player))
        {
            if (message.length() >= 6)
            {
                int caps = 0;
                for (char c : message.toCharArray())
                {
                    if (Character.isUpperCase(c))
                    {
                        caps++;
                    }
                }
                if (((float) caps / (float) message.length()) > 0.65) //Compute a ratio so that longer sentences can have more caps.
                {
                    message = message.toLowerCase();
                }
            }
        }

        if (!plugin.al.isStaffMember(player))
        {
            for (String domain : DISGUSTING_HOST_DOMAINS)
            {
                if (ChatColor.stripColor(message).toLowerCase().contains(domain))
                {
                    player.sendMessage(ChatColor.RED + "Ew, stop trying to advertise that server ran on a terrible host. Get real hosting.");
                    event.setCancelled(true);
                    return;
                }
            }
        }

        // Check for adminchat
        final FPlayer fPlayer = plugin.pl.getPlayerSync(player);
        if (fPlayer.inAdminChat())
        {
            FSync.adminChatMessage(player, message);
            event.setCancelled(true);
            return;
        }

        // Finally, set message
        event.setMessage(message);

        // Make format
        String format = "%1$s " + FUtil.colorize("&7»&r") + " %2$s";

        String tag = fPlayer.getTag();
        if (tag != null && !tag.isEmpty())
        {
            format = tag.replace("%", "%%") + " " + format;
        }

        // Check for mentions
        Boolean mentionEveryone = ChatColor.stripColor(message).toLowerCase().contains("@everyone") && plugin.al.isStaffMember(player);
        for (Player p : server.getOnlinePlayers())
        {
            if (ChatColor.stripColor(message).toLowerCase().contains("@" + p.getName().toLowerCase()) || mentionEveryone)
            {
                p.playSound(p.getLocation(), Sound.BLOCK_NOTE_PLING, SoundCategory.MASTER, 1337F, 0.9F);
            }
        }

        // Set format
        event.setFormat(format);
    }

    public ChatColor getColor(StaffMember staff, Displayable display)
    {
        ChatColor color = display.getColor();
        return color;
    }

    public void adminChat(CommandSender sender, String message)
    {
        String name = sender.getName();
        String tag = plugin.rm.getDisplay(sender).getColoredTag() + " ";
        FLog.info("[STAFF] " + name + ": " + message);
        Displayable display = plugin.rm.getDisplay(sender);

        for (Player player : server.getOnlinePlayers())
        {
            StaffMember staff = plugin.al.getStaffMember(player);
            if (plugin.al.isStaffMember(player))
            {
                ChatColor cc = scc;
                if (scr == true)
                {
                    cc = FUtil.randomChatColor();
                    player.sendMessage(ChatColor.DARK_GRAY + "[" + ChatColor.DARK_AQUA + "STAFF" + ChatColor.DARK_GRAY + "] " + tag + ChatColor.RESET + ChatColor.DARK_AQUA + name + ChatColor.GRAY + " » " + cc + message);
                }
                else if (scn == true)
                {
                    String rm = "";
                    for (char c : message.toCharArray())
                    {
                        ChatColor rc = FUtil.randomChatColor();
                        rm = rm + rc + c;
                    }
                    player.sendMessage(ChatColor.DARK_GRAY + "[" + ChatColor.DARK_AQUA + "STAFF" + ChatColor.DARK_GRAY + "] " + tag + ChatColor.RESET + ChatColor.DARK_AQUA + name + ChatColor.GRAY + " » " + ChatColor.AQUA + rm);
                }
                else
                {
                    player.sendMessage(ChatColor.DARK_GRAY + "[" + ChatColor.DARK_AQUA + "STAFF" + ChatColor.DARK_GRAY + "] " + tag + ChatColor.RESET + ChatColor.DARK_AQUA + name + ChatColor.GRAY + " » " + cc + message);
                }

            }
        }
    }

    public void reportAction(Player reporter, Player reported, String report)
    {
        for (Player player : server.getOnlinePlayers())
        {
            if (plugin.al.isStaffMember(player))
            {
                FUtil.playerMsg(player, ChatColor.RED + "[REPORTS] " + ChatColor.GOLD + reporter.getName() + " has reported " + reported.getName() + " for " + report);
            }
        }
    }

}
