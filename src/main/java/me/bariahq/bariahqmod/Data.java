package me.bariahq.bariahqmod;

import me.bariahq.bariahqmod.util.FLog;

import java.util.ArrayList;
import java.util.List;

public class Data extends FreedomService
/* This class is mainly for storing data that doesn't 
wipe when the player leaves but wipes on reload or restart*/
{
    public static final List<String> ADMIN_DEOPPED_PLAYERS = new ArrayList();

    public Data(BariaHQMod plugin)
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
        int totalEntries = ADMIN_DEOPPED_PLAYERS.size();
        FLog.info("Removed " + totalEntries + " data entries");
    }

    public void setAdminDeopped(String name, Boolean b)
    {
        if (b && !isAdminDeopped(name))
        {
            ADMIN_DEOPPED_PLAYERS.add(name);
        }
        else if (!b && isAdminDeopped(name))
        {
            ADMIN_DEOPPED_PLAYERS.remove(name);
        }
    }

    public boolean isAdminDeopped(String name)
    {
        return ADMIN_DEOPPED_PLAYERS.contains(name);
    }
}
