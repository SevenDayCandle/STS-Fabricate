package pinacolada.skills;

import pinacolada.skills.skills.PTrigger;
import pinacolada.utilities.UniqueList;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class PSkillContainer {
    private final UniqueList<PSkill<?>> effectTextMapping = new UniqueList<>();
    public final ArrayList<PSkill<?>> onUseEffects = new ArrayList<>();
    public final ArrayList<PSkill<?>> powerEffects = new ArrayList<>();

    public void clear() {
        effectTextMapping.clear();
        onUseEffects.clear();
        powerEffects.clear();
    }

    public UniqueList<PSkill<?>> getPointers() {
        return effectTextMapping;
    }
}
