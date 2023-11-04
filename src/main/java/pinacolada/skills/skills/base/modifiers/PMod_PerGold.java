package pinacolada.skills.skills.base.modifiers;

import pinacolada.annotations.VisibleSkill;
import pinacolada.cards.base.fields.PCLCardTarget;
import pinacolada.dungeon.PCLUseInfo;
import pinacolada.resources.PGR;
import pinacolada.skills.PSkillData;
import pinacolada.skills.PSkillSaveData;
import pinacolada.skills.fields.PField_Not;

@VisibleSkill
public class PMod_PerGold extends PMod_Per<PField_Not> {
    public static final PSkillData<PField_Not> DATA = register(PMod_PerGold.class, PField_Not.class).noTarget();

    public PMod_PerGold(PSkillSaveData content) {
        super(DATA, content);
    }

    public PMod_PerGold() {
        super(DATA);
    }

    public PMod_PerGold(int amount) {
        super(DATA, amount);
    }

    @Override
    public int getMultiplier(PCLUseInfo info, boolean isUsing) {
        return sumTargets(info, t -> t.gold);
    }

    @Override
    public String getSubSampleText() {
        return PGR.core.tooltips.gold.title;
    }

    @Override
    public String getSubText(PCLCardTarget perspective, Object requestor) {
        return PGR.core.tooltips.gold.title;
    }
}
