package pinacolada.skills.skills.base.conditions;

import com.megacrit.cardcrawl.cards.AbstractCard;
import extendedui.ui.tooltips.EUIKeywordTooltip;
import pinacolada.annotations.VisibleSkill;
import pinacolada.interfaces.subscribers.OnCardUpgradeSubscriber;
import pinacolada.resources.PGR;
import pinacolada.skills.PSkillData;
import pinacolada.skills.PSkillSaveData;
import pinacolada.skills.fields.PField_CardCategory;
import pinacolada.skills.skills.PDelegateCardCond;

@VisibleSkill
public class PCond_OnUpgrade extends PDelegateCardCond implements OnCardUpgradeSubscriber {
    public static final PSkillData<PField_CardCategory> DATA = register(PCond_OnUpgrade.class, PField_CardCategory.class, 1, 1)
            .selfTarget();

    public PCond_OnUpgrade() {
        super(DATA);
    }

    public PCond_OnUpgrade(PSkillSaveData content) {
        super(DATA, content);
    }

    @Override
    public EUIKeywordTooltip getDelegateTooltip() {
        return PGR.core.tooltips.upgrade;
    }

    @Override
    public void onUpgrade(AbstractCard card) {
        triggerOnCard(card);
    }
}
