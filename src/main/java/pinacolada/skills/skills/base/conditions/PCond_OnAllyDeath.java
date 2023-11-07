package pinacolada.skills.skills.base.conditions;

import extendedui.ui.tooltips.EUIKeywordTooltip;
import pinacolada.annotations.VisibleSkill;
import pinacolada.cards.base.PCLCard;
import pinacolada.cards.base.fields.PCLCardTarget;
import pinacolada.interfaces.subscribers.OnAllyDeathSubscriber;
import pinacolada.monsters.PCLCardAlly;
import pinacolada.resources.PGR;
import pinacolada.skills.PSkillData;
import pinacolada.skills.PSkillSaveData;
import pinacolada.skills.fields.PField_CardCategory;
import pinacolada.skills.skills.PDelegateCardCond;

@VisibleSkill
public class PCond_OnAllyDeath extends PDelegateCardCond implements OnAllyDeathSubscriber {
    public static final PSkillData<PField_CardCategory> DATA = register(PCond_OnAllyDeath.class, PField_CardCategory.class, 1, 1)
            .setSourceTypes(PSkillData.SourceType.Card, PSkillData.SourceType.Power)
            .pclOnly()
            .noTarget();

    public PCond_OnAllyDeath() {
        super(DATA);
    }

    public PCond_OnAllyDeath(PSkillSaveData content) {
        super(DATA, content);
    }

    @Override
    public EUIKeywordTooltip getDelegateTooltip() {
        return PGR.core.tooltips.kill;
    }

    @Override
    public String getSubText(PCLCardTarget perspective, Object requestor) {
        if (isWhenClause()) {
            return TEXT.cond_aObjectIs(fields.getFullSummonStringSingular(), getDelegatePastText());
        }
        return TEXT.cond_when(getDelegatePastText());
    }

    @Override
    public void onAllyDeath(PCLCard card, PCLCardAlly ally) {
        triggerOnCard(card, ally);
    }
}
