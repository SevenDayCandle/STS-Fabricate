package pinacolada.commands;

import basemod.DevConsole;
import basemod.devcommands.ConsoleCommand;
import pinacolada.cards.base.PCLCustomCardSlot;

import java.util.ArrayList;

public class ReloadCustomCommand extends ConsoleCommand
{

    public ReloadCustomCommand()
    {
        this.requiresPlayer = false;
        this.simpleCheck = true;
    }

    @Override
    protected void execute(String[] strings, int depth)
    {
        try
        {
            PCLCustomCardSlot.initialize();
            DevConsole.log("Reloaded custom cards");
        }
        catch (Exception e)
        {
            DevConsole.log("Could not reload custom cards.");
            e.printStackTrace();
        }
    }

    public ArrayList<String> extraOptions(String[] tokens, int depth)
    {
        complete = true;
        return new ArrayList<>();
    }
}
