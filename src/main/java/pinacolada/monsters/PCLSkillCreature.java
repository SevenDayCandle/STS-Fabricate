package pinacolada.monsters;

import pinacolada.interfaces.providers.PointerProvider;

public abstract class PCLSkillCreature extends PCLCreature implements PointerProvider {
    public PCLSkillCreature(PCLCreatureData data) {
        super(data);
    }

    public PCLSkillCreature(PCLCreatureData data, float offsetX, float offsetY) {
        super(data, offsetX, offsetY);
    }

    public PCLSkillCreature(PCLCreatureData data, float offsetX, float offsetY, boolean ignoreBlights) {
        super(data, offsetX, offsetY, ignoreBlights);
    }

    @Override
    public String getID() {
        return id;
    }

    @Override
    public String getName() {
        return name;
    }
}
