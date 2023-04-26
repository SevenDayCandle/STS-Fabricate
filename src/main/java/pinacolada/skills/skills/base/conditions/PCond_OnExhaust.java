package pinacolada.skills.skills.base.conditions;

import com.megacrit.cardcrawl.cards.AbstractCard;
import extendedui.ui.tooltips.EUITooltip;
import pinacolada.annotations.VisibleSkill;
import pinacolada.interfaces.subscribers.OnCardExhaustedSubscriber;
import pinacolada.resources.PGR;
import pinacolada.skills.PSkillData;
import pinacolada.skills.PSkillSaveData;
import pinacolada.skills.fields.PField_CardCategory;
import pinacolada.skills.skills.PDelegateCardCond;

@VisibleSkill
public class PCond_OnExhaust extends PDelegateCardCond implements OnCardExhaustedSubscriber {
    public static final PSkillData<PField_CardCategory> DATA = register(PCond_OnExhaust.class, PField_CardCategory.class, 1, 1)
            .selfTarget();

    public PCond_OnExhaust() {
        super(DATA);
    }

    public PCond_OnExhaust(PSkillSaveData content) {
        super(DATA, content);
    }

    @Override
    public EUITooltip getDelegateTooltip() {
        return PGR.core.tooltips.exhaust;
    }

    @Override
    public void onCardExhausted(AbstractCard card) {
        triggerOnCard(card);
    }
}
