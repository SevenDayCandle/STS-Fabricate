package pinacolada.skills;

import pinacolada.skills.skills.PTrigger;
import pinacolada.utilities.UniqueList;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class PSkillContainer {
    private static final List<PTrigger> FAKE_POWER_LIST = Collections.emptyList();
    public final UniqueList<PSkill<?>> effectTextMapping = new UniqueList<>();
    public final ArrayList<PSkill<?>> onUseEffects = new ArrayList<>();

    public void clear() {
        onUseEffects.clear();
        effectTextMapping.clear();
    }

    public UniqueList<PSkill<?>> getPointers() {
        return effectTextMapping;
    }

    public List<PTrigger> getPowerEffects() {
        return FAKE_POWER_LIST;
    }
}
