package pinacolada.commands;

import basemod.DevConsole;
import basemod.devcommands.ConsoleCommand;
import pinacolada.resources.PGR;

import java.util.ArrayList;

public class ReloadCustomCommand extends ConsoleCommand {

    public ReloadCustomCommand() {
        this.requiresPlayer = false;
        this.simpleCheck = true;
    }

    @Override
    protected void execute(String[] strings, int depth) {
        try {
            PGR.reloadCustoms();
            DevConsole.log("Reloaded Fabricate items.");
        }
        catch (Exception e) {
            DevConsole.log("Could not reload Fabricate items.");
            e.printStackTrace();
        }
    }

    public ArrayList<String> extraOptions(String[] tokens, int depth) {
        complete = true;
        return new ArrayList<>();
    }
}
