package pinacolada.skills;

import pinacolada.skills.skills.PTrigger;
import pinacolada.utilities.UniqueList;

import java.util.ArrayList;

public class PSkillPowerContainer extends PSkillContainer {
    public final ArrayList<PTrigger> powerEffects = new ArrayList<>();

    public void clear() {
        super.clear();
        powerEffects.clear();
    }

    public ArrayList<PTrigger> getPowerEffects() {
        return powerEffects;
    }
}
