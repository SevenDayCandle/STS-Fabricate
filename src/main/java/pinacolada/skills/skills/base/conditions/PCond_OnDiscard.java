package pinacolada.skills.skills.base.conditions;

import com.megacrit.cardcrawl.cards.AbstractCard;
import extendedui.ui.tooltips.EUIKeywordTooltip;
import pinacolada.annotations.VisibleSkill;
import pinacolada.interfaces.subscribers.OnCardDiscardedSubscriber;
import pinacolada.resources.PGR;
import pinacolada.skills.PSkillData;
import pinacolada.skills.PSkillSaveData;
import pinacolada.skills.fields.PField_CardCategory;
import pinacolada.skills.skills.PDelegateCardCond;

@VisibleSkill
public class PCond_OnDiscard extends PDelegateCardCond implements OnCardDiscardedSubscriber {
    public static final PSkillData<PField_CardCategory> DATA = register(PCond_OnDiscard.class, PField_CardCategory.class, 1, 1)
            .selfTarget();

    public PCond_OnDiscard() {
        super(DATA);
    }

    public PCond_OnDiscard(PSkillSaveData content) {
        super(DATA, content);
    }

    @Override
    public EUIKeywordTooltip getDelegateTooltip() {
        return PGR.core.tooltips.discard;
    }

    @Override
    public void onCardDiscarded(AbstractCard card) {
        triggerOnCard(card);
    }
}
