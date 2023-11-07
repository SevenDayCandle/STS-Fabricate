package pinacolada.skills;

import pinacolada.utilities.UniqueList;

import java.util.ArrayList;

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
