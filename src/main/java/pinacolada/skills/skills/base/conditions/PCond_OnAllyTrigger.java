package pinacolada.skills.skills.base.conditions;

import com.megacrit.cardcrawl.core.AbstractCreature;
import extendedui.ui.tooltips.EUIKeywordTooltip;
import pinacolada.annotations.VisibleSkill;
import pinacolada.cards.base.PCLCard;
import pinacolada.cards.base.fields.PCLCardTarget;
import pinacolada.interfaces.subscribers.OnAllyTriggerSubscriber;
import pinacolada.monsters.PCLCardAlly;
import pinacolada.resources.PGR;
import pinacolada.skills.PSkillData;
import pinacolada.skills.PSkillSaveData;
import pinacolada.skills.fields.PField_CardCategory;
import pinacolada.skills.skills.PDelegateCardCond;

@VisibleSkill
public class PCond_OnAllyTrigger extends PDelegateCardCond implements OnAllyTriggerSubscriber {
    public static final PSkillData<PField_CardCategory> DATA = register(PCond_OnAllyTrigger.class, PField_CardCategory.class, 1, 1)
            .setSourceTypes(PSkillData.SourceType.Card, PSkillData.SourceType.Power)
            .pclOnly()
            .noTarget();

    public PCond_OnAllyTrigger() {
        super(DATA);
    }

    public PCond_OnAllyTrigger(PSkillSaveData content) {
        super(DATA, content);
    }

    @Override
    public EUIKeywordTooltip getDelegateTooltip() {
        return PGR.core.tooltips.trigger;
    }

    @Override
    public String getSubText(PCLCardTarget perspective, Object requestor) {
        if (isWhenClause()) {
            return TEXT.cond_aObjectIs(fields.getFullSummonStringSingular(), getDelegatePastText());
        }
        return super.getSubText(perspective, requestor);
    }

    // When the ally is triggered, trigger the effect on the ally
    @Override
    public void onAllyTrigger(PCLCard card, AbstractCreature target, PCLCardAlly ally, PCLCardAlly caller) {
        if (isWhenClause() || ally == caller) {
            triggerOnCard(card, ally);
        }
    }
}
