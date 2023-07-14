package pinacolada.skills.skills.base.conditions;

import extendedui.EUIUtils;
import pinacolada.annotations.VisibleSkill;
import pinacolada.cards.base.fields.PCLCardTarget;
import pinacolada.dungeon.PCLUseInfo;
import pinacolada.resources.PGR;
import pinacolada.skills.PSkill;
import pinacolada.skills.PSkillData;
import pinacolada.skills.PSkillSaveData;
import pinacolada.skills.fields.PField_Not;
import pinacolada.skills.skills.PPassiveCond;
import pinacolada.utilities.GameUtilities;

@VisibleSkill
public class PCond_IsBuffing extends PPassiveCond<PField_Not> {
    public static final PSkillData<PField_Not> DATA = register(PCond_IsBuffing.class, PField_Not.class);

    public PCond_IsBuffing(PSkillSaveData content) {
        super(DATA, content);
    }

    public PCond_IsBuffing() {
        super(DATA, PCLCardTarget.AllEnemy, 1);
    }

    public PCond_IsBuffing(PCLCardTarget target) {
        super(DATA, target, 1);
    }

    @Override
    public boolean checkCondition(PCLUseInfo info, boolean isUsing, PSkill<?> triggerSource) {
        if (target == PCLCardTarget.Single && info != null) {
            return fields.not ^ (GameUtilities.isBuffing(info.target));
        }
        return fields.not ^ EUIUtils.any(GameUtilities.getIntents(), i -> fields.not ^ GameUtilities.isBuffing(i.intent));
    }

    @Override
    public String getSampleText(PSkill<?> callingSkill, PSkill<?> parentSkill) {
        return isUnderWhen(callingSkill, parentSkill) ? TEXT.cond_whenSingle(PGR.core.tooltips.buff.present()) : TEXT.cond_xIsY(TEXT.subjects_target, PGR.core.tooltips.buff.progressive());
    }

    @Override
    public String getSubText() {
        return getTargetIsString(fields.not ? TEXT.cond_not(PGR.core.tooltips.buff.progressive()) : PGR.core.tooltips.buff.progressive());
    }
}
