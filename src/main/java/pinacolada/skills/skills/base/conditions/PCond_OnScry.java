package pinacolada.skills.skills.base.conditions;

import com.megacrit.cardcrawl.cards.AbstractCard;
import extendedui.ui.tooltips.EUITooltip;
import pinacolada.annotations.VisibleSkill;
import pinacolada.interfaces.subscribers.OnCardScrySubscriber;
import pinacolada.resources.PGR;
import pinacolada.skills.PSkillData;
import pinacolada.skills.PSkillSaveData;
import pinacolada.skills.fields.PField_CardCategory;
import pinacolada.skills.skills.PDelegateCardCond;

@VisibleSkill
public class PCond_OnScry extends PDelegateCardCond implements OnCardScrySubscriber {
    public static final PSkillData<PField_CardCategory> DATA = register(PCond_OnScry.class, PField_CardCategory.class, 1, 1)
            .selfTarget();

    public PCond_OnScry() {
        super(DATA);
    }

    public PCond_OnScry(PSkillSaveData content) {
        super(DATA, content);
    }

    @Override
    public EUITooltip getDelegateTooltip() {
        return PGR.core.tooltips.scry;
    }

    @Override
    public void onScry(AbstractCard card) {
        triggerOnCard(card);
    }
}
