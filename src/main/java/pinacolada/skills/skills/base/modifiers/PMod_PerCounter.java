package pinacolada.skills.skills.base.modifiers;

import pinacolada.annotations.VisibleSkill;
import pinacolada.cards.base.fields.PCLCardTarget;
import pinacolada.dungeon.PCLUseInfo;
import pinacolada.resources.PGR;
import pinacolada.skills.PSkillData;
import pinacolada.skills.PSkillSaveData;
import pinacolada.skills.fields.PField_Not;
import pinacolada.utilities.GameUtilities;

@VisibleSkill
public class PMod_PerCounter extends PMod_Per<PField_Not> {

    public static final PSkillData<PField_Not> DATA = register(PMod_PerCounter.class, PField_Not.class).selfTarget();

    public PMod_PerCounter() {
        this(1);
    }

    public PMod_PerCounter(int amount) {
        super(DATA, amount);
    }

    public PMod_PerCounter(PSkillSaveData content) {
        super(DATA, content);
    }

    @Override
    public int getMultiplier(PCLUseInfo info, boolean isUsing) {
        return GameUtilities.getCounter(sourceCard);
    }

    @Override
    public String getSubText(PCLCardTarget perspective) {
        return PGR.core.tooltips.counter.title;
    }
}
