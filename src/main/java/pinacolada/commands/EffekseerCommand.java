package pinacolada.commands;

import basemod.DevConsole;
import basemod.devcommands.ConsoleCommand;
import extendedui.EUIUtils;
import pinacolada.effects.PCLEffects;
import pinacolada.effects.PCLEffekseerEFX;

import java.util.ArrayList;

public class EffekseerCommand extends ConsoleCommand
{

    public EffekseerCommand()
    {
        this.requiresPlayer = true;
        this.minExtraTokens = 1;
        this.simpleCheck = true;
    }

    public static PCLEffekseerEFX[] getCustoms()
    {
        return PCLEffekseerEFX.values();
    }

    @Override
    protected void execute(String[] tokens, int depth)
    {
        try {
            PCLEffekseerEFX augment = PCLEffekseerEFX.valueOf(tokens[1]);
            PCLEffects.Queue.playEFX(augment);
        }
        catch (IllegalArgumentException e)
        {
            DevConsole.log("Could not find effect " + tokens[1]);
        }
    }

    public ArrayList<String> extraOptions(String[] tokens, int depth)
    {
        return EUIUtils.map(getCustoms(), Enum::name);
    }
}
