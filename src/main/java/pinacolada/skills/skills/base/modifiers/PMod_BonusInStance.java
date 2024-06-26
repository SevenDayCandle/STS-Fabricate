package pinacolada.skills.skills.base.modifiers;

import com.megacrit.cardcrawl.stances.NeutralStance;
import extendedui.EUIUtils;
import pinacolada.annotations.VisibleSkill;
import pinacolada.cards.base.fields.PCLCardTarget;
import pinacolada.dungeon.PCLUseInfo;
import pinacolada.resources.PGR;
import pinacolada.resources.pcl.PCLCoreStrings;
import pinacolada.skills.PSkillData;
import pinacolada.skills.PSkillSaveData;
import pinacolada.skills.fields.PField_Stance;
import pinacolada.stances.PCLStanceHelper;
import pinacolada.utilities.GameUtilities;

@VisibleSkill
public class PMod_BonusInStance extends PMod_BonusOn<PField_Stance> {

    public static final PSkillData<PField_Stance> DATA = register(PMod_BonusInStance.class, PField_Stance.class).noTarget();

    public PMod_BonusInStance(PSkillSaveData content) {
        super(DATA, content);
    }

    public PMod_BonusInStance() {
        super(DATA, 0);
    }

    public PMod_BonusInStance(int amount, PCLStanceHelper... stance) {
        super(DATA, amount);
        fields.setStance(stance);
    }

    @Override
    public String getSubText(PCLCardTarget perspective, Object requestor) {
        return PGR.core.tooltips.stance.title;
    }

    @Override
    public String getText(PCLCardTarget perspective, Object requestor, boolean addPeriod) {
        String base = fields.getAnyStanceString();
        return TEXT.cond_xConditional(childEffect != null ? capital(childEffect.getText(perspective, requestor, false), addPeriod) : "", TEXT.cond_bonusIf(getAmountRawString(requestor), fields.random ? TEXT.cond_not(base) : base)) + PCLCoreStrings.period(addPeriod);
    }

    @Override
    public boolean meetsCondition(PCLUseInfo info, boolean isUsing) {
        return fields.random ^ (fields.stances.isEmpty() ? !GameUtilities.inStance(NeutralStance.STANCE_ID) : EUIUtils.any(fields.stances, GameUtilities::inStance));
    }
}
