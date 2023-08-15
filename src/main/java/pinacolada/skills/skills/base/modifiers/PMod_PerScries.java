package pinacolada.skills.skills.base.modifiers;

import pinacolada.annotations.VisibleSkill;
import pinacolada.cards.base.fields.PCLCardTarget;
import pinacolada.dungeon.CombatManager;
import pinacolada.dungeon.PCLUseInfo;
import pinacolada.resources.PGR;
import pinacolada.resources.pcl.PCLCoreStrings;
import pinacolada.skills.PSkillData;
import pinacolada.skills.PSkillSaveData;
import pinacolada.skills.fields.PField_Not;

@VisibleSkill
public class PMod_PerScries extends PMod_Per<PField_Not> {
    public static final PSkillData<PField_Not> DATA = register(PMod_PerScries.class, PField_Not.class).selfTarget();

    public PMod_PerScries(PSkillSaveData content) {
        super(DATA, content);
    }

    public PMod_PerScries() {
        super(DATA);
    }

    public PMod_PerScries(int amount) {
        super(DATA, amount);
    }

    @Override
    public int getMultiplier(PCLUseInfo info, boolean isUsing) {
        return CombatManager.scriesThisTurn;
    }

    @Override
    public String getSubSampleText() {
        return PGR.core.tooltips.scry.title;
    }

    @Override
    public String getSubText(PCLCardTarget perspective) {
        return TEXT.subjects_thisTurn(PCLCoreStrings.pluralForce(PGR.core.tooltips.scry.plural()));
    }
}
