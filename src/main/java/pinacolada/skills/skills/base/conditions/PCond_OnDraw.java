package pinacolada.skills.skills.base.conditions;

import com.megacrit.cardcrawl.cards.AbstractCard;
import extendedui.ui.tooltips.EUIKeywordTooltip;
import pinacolada.annotations.VisibleSkill;
import pinacolada.interfaces.subscribers.OnCardDrawnSubscriber;
import pinacolada.resources.PGR;
import pinacolada.skills.PSkillData;
import pinacolada.skills.PSkillSaveData;
import pinacolada.skills.fields.PField_CardCategory;
import pinacolada.skills.skills.PDelegateCardCond;

@VisibleSkill
public class PCond_OnDraw extends PDelegateCardCond implements OnCardDrawnSubscriber {
    public static final PSkillData<PField_CardCategory> DATA = register(PCond_OnDraw.class, PField_CardCategory.class, 1, 1)
            .setSourceTypes(PSkillData.SourceType.Card, PSkillData.SourceType.Power)
            .noTarget();

    public PCond_OnDraw() {
        super(DATA);
    }

    public PCond_OnDraw(PSkillSaveData content) {
        super(DATA, content);
    }

    @Override
    public EUIKeywordTooltip getDelegateTooltip() {
        return PGR.core.tooltips.draw;
    }

    @Override
    public void onCardDrawn(AbstractCard card) {
        triggerOnCard(card);
    }
}
