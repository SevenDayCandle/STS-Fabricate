package pinacolada.skills.skills.base.conditions;

import extendedui.ui.tooltips.EUIKeywordTooltip;
import pinacolada.annotations.VisibleSkill;
import pinacolada.cards.base.PCLCard;
import pinacolada.cards.base.fields.PCLCardTarget;
import pinacolada.interfaces.subscribers.OnAllyWithdrawSubscriber;
import pinacolada.monsters.PCLCardAlly;
import pinacolada.resources.PGR;
import pinacolada.skills.PSkillData;
import pinacolada.skills.PSkillSaveData;
import pinacolada.skills.fields.PField_CardCategory;
import pinacolada.skills.skills.PDelegateCardCond;

@VisibleSkill
public class PCond_OnWithdraw extends PDelegateCardCond implements OnAllyWithdrawSubscriber {
    public static final PSkillData<PField_CardCategory> DATA = register(PCond_OnWithdraw.class, PField_CardCategory.class, 1, 1)
            .pclOnly()
            .noTarget();

    public PCond_OnWithdraw() {
        super(DATA);
    }

    public PCond_OnWithdraw(PSkillSaveData content) {
        super(DATA, content);
    }

    @Override
    public EUIKeywordTooltip getDelegateTooltip() {
        return PGR.core.tooltips.withdraw;
    }

    @Override
    public String getSubText(PCLCardTarget perspective, Object requestor) {
        if (isWhenClause()) {
            return TEXT.cond_aObjectIs(fields.getFullSummonStringSingular(), getDelegatePastText());
        }
        return super.getSubText(perspective, requestor);
    }

    @Override
    public void onAllyWithdraw(PCLCard returned, PCLCardAlly ally) {
        triggerOnCard(returned, ally);
    }
}
