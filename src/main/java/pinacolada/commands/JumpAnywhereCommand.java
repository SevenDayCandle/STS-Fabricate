package pinacolada.commands;

import basemod.DevConsole;
import basemod.devcommands.ConsoleCommand;
import pinacolada.resources.PGR;

import java.util.ArrayList;

public class JumpAnywhereCommand extends ConsoleCommand
{

    public JumpAnywhereCommand()
    {
        this.requiresPlayer = false;
        this.simpleCheck = true;
    }

    @Override
    protected void execute(String[] strings, int depth)
    {
        PGR.dungeon.setJumpAnywhere(true);
        DevConsole.log("Can jump anywhere");
    }

    public ArrayList<String> extraOptions(String[] tokens, int depth)
    {
        complete = true;
        return new ArrayList<>();
    }
}
