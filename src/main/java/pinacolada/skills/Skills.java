package pinacolada.skills;

import pinacolada.skills.skills.PTrigger;
import pinacolada.utilities.UniqueList;

import java.util.ArrayList;

public class Skills {
    public final UniqueList<PSkill<?>> effectTextMapping = new UniqueList<>();
    public final ArrayList<PSkill<?>> onUseEffects = new ArrayList<>();
    public final ArrayList<PTrigger> powerEffects = new ArrayList<>();

    public void clear() {
        onUseEffects.clear();
        powerEffects.clear();
        effectTextMapping.clear();
    }
}
