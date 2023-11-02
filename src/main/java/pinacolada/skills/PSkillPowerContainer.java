package pinacolada.skills;

import pinacolada.skills.skills.PTrigger;

import java.util.ArrayList;

public class PSkillPowerContainer extends PSkillContainer {
    public final ArrayList<PSkill<?>> powerEffects = new ArrayList<>();

    public void clear() {
        super.clear();
        powerEffects.clear();
    }

    public ArrayList<PSkill<?>> getPowerEffects() {
        return powerEffects;
    }
}
