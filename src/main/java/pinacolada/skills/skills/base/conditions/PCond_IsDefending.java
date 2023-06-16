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
public class PCond_IsDefending extends PPassiveCond<PField_Not> {
    public static final PSkillData<PField_Not> DATA = register(PCond_IsDefending.class, PField_Not.class);

    public PCond_IsDefending(PSkillSaveData content) {
        super(DATA, content);
    }

    public PCond_IsDefending() {
        super(DATA, PCLCardTarget.AllEnemy, 1);
    }

    public PCond_IsDefending(PCLCardTarget target) {
        super(DATA, target, 1);
    }

    @Override
    public boolean checkCondition(PCLUseInfo info, boolean isUsing, PSkill<?> triggerSource) {
        if (target == PCLCardTarget.Single) {
            return fields.not ^ (GameUtilities.isDefending(info.target));
        }
        return fields.not ^ EUIUtils.any(GameUtilities.getIntents(), i -> fields.not ^ GameUtilities.isDefending(i.intent));
    }

    @Override
    public String getSampleText(PSkill<?> callingSkill) {
        return isUnderWhen(callingSkill) ? TEXT.cond_whenSingle(PGR.core.tooltips.block.present()) : TEXT.cond_xIsY(TEXT.subjects_target, PGR.core.tooltips.block.progressive());
    }

    @Override
    public String getSubText() {
        return getTargetIsString(fields.not ? TEXT.cond_not(PGR.core.tooltips.block.progressive()) : PGR.core.tooltips.block.progressive());
    }
}
