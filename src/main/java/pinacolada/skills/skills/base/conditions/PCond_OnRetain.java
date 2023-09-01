package pinacolada.skills.skills.base.conditions;

import com.megacrit.cardcrawl.cards.AbstractCard;
import extendedui.ui.tooltips.EUIKeywordTooltip;
import pinacolada.annotations.VisibleSkill;
import pinacolada.interfaces.subscribers.OnCardRetainSubscriber;
import pinacolada.resources.PGR;
import pinacolada.skills.PSkillData;
import pinacolada.skills.PSkillSaveData;
import pinacolada.skills.fields.PField_CardCategory;
import pinacolada.skills.skills.PDelegateCardCond;

@VisibleSkill
public class PCond_OnRetain extends PDelegateCardCond implements OnCardRetainSubscriber {
    public static final PSkillData<PField_CardCategory> DATA = register(PCond_OnRetain.class, PField_CardCategory.class, 1, 1)
            .noTarget();

    public PCond_OnRetain() {
        super(DATA);
    }

    public PCond_OnRetain(PSkillSaveData content) {
        super(DATA, content);
    }

    @Override
    public EUIKeywordTooltip getDelegateTooltip() {
        return PGR.core.tooltips.retain;
    }

    @Override
    public void onRetain(AbstractCard card) {
        triggerOnCard(card);
    }
}
