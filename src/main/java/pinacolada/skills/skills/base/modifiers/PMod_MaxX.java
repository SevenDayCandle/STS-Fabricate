package pinacolada.skills.skills.base.modifiers;

import pinacolada.annotations.VisibleSkill;
import pinacolada.cards.base.fields.PCLCardTarget;
import pinacolada.dungeon.PCLUseInfo;
import pinacolada.resources.PGR;
import pinacolada.resources.pcl.PCLCoreStrings;
import pinacolada.skills.PSkill;
import pinacolada.skills.PSkillData;
import pinacolada.skills.PSkillSaveData;
import pinacolada.skills.fields.PField_Empty;
import pinacolada.skills.skills.PMathMod;

@VisibleSkill
public class PMod_MaxX extends PMathMod {

    public static final PSkillData<PField_Empty> DATA = register(PMod_MaxX.class, PField_Empty.class).noTarget();

    public PMod_MaxX(PSkillSaveData content) {
        super(DATA, content);
    }

    public PMod_MaxX() {
        super(DATA);
    }

    public PMod_MaxX(int amount) {
        super(DATA, amount);
    }

    @Override
    public int getModifiedAmount(PCLUseInfo info, int baseAmount, boolean isUsing) {
        return Math.min(amount, baseAmount);
    }

    @Override
    public String getSampleText(PSkill<?> callingSkill, PSkill<?> parentSkill) {
        return PGR.core.strings.subjects_max(PGR.core.strings.subjects_x);
    }

    @Override
    public String getSubText(PCLCardTarget perspective, Object requestor) {
        return PGR.core.strings.subjects_max(getAmountRawString());
    }

    @Override
    public String getText(PCLCardTarget perspective, Object requestor, boolean addPeriod) {
        return ((childEffect != null ? capital(childEffect.getText(perspective, requestor, false), addPeriod) + " (" + capital(getSubText(perspective, requestor), true) + ")" : capital(getSubText(perspective, requestor), addPeriod))) + PCLCoreStrings.period(addPeriod);
    }
}
