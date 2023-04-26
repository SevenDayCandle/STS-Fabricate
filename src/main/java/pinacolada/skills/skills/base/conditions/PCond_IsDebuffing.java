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
import pinacolada.skills.skills.base.primary.PTrigger_When;
import pinacolada.utilities.GameUtilities;

@VisibleSkill
public class PCond_IsDebuffing extends PPassiveCond<PField_Not> {
    public static final PSkillData<PField_Not> DATA = register(PCond_IsDebuffing.class, PField_Not.class);

    public PCond_IsDebuffing(PSkillSaveData content) {
        super(DATA, content);
    }

    public PCond_IsDebuffing() {
        super(DATA, PCLCardTarget.AllEnemy, 1);
    }

    public PCond_IsDebuffing(PCLCardTarget target) {
        super(DATA, target, 1);
    }

    @Override
    public boolean checkCondition(PCLUseInfo info, boolean isUsing, PSkill<?> triggerSource) {
        if (target == PCLCardTarget.Single) {
            return fields.not ^ (GameUtilities.isDebuffing(info.target));
        }
        return fields.not ^ EUIUtils.any(GameUtilities.getIntents(), i -> fields.not ^ GameUtilities.isDebuffing(i.intent));
    }

    @Override
    public String getSampleText(PSkill<?> callingSkill) {
        return callingSkill instanceof PTrigger_When ? TEXT.cond_whenSingle(PGR.core.tooltips.debuff.present()) : TEXT.cond_objIs(TEXT.subjects_target, PGR.core.tooltips.debuff.progressive());
    }

    @Override
    public String getSubText() {
        return getTargetIsString(fields.not ? TEXT.cond_not(PGR.core.tooltips.debuff.progressive()) : PGR.core.tooltips.debuff.progressive());
    }
}
