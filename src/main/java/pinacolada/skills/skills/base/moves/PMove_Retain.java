package pinacolada.skills.skills.base.moves;

import com.megacrit.cardcrawl.cards.CardGroup;
import com.megacrit.cardcrawl.core.AbstractCreature;
import extendedui.interfaces.delegates.FuncT5;
import extendedui.ui.tooltips.EUIKeywordTooltip;
import pinacolada.actions.piles.RetainCards;
import pinacolada.actions.piles.SelectFromPile;
import pinacolada.annotations.VisibleSkill;
import pinacolada.cards.base.PCLCardGroupHelper;
import pinacolada.cards.base.fields.PCLCardSelection;
import pinacolada.resources.PGR;
import pinacolada.skills.PSkillData;
import pinacolada.skills.PSkillSaveData;
import pinacolada.skills.fields.PField_CardCategory;

@VisibleSkill
public class PMove_Retain extends PMove_DoCard<PField_CardCategory> {
    public static final PSkillData<PField_CardCategory> DATA =
            register(PMove_Retain.class, PField_CardCategory.class)
                    .setGroups(PCLCardGroupHelper.Hand)
                    .noTarget()
                    .setExtra(0, DEFAULT_MAX);

    public PMove_Retain() {
        this(1);
    }

    public PMove_Retain(int amount, PCLCardGroupHelper... h) {
        super(DATA, amount, h);
    }

    public PMove_Retain(PSkillSaveData content) {
        super(DATA, content);
    }

    @Override
    public FuncT5<SelectFromPile, String, AbstractCreature, Integer, PCLCardSelection, CardGroup[]> getAction() {
        return RetainCards::new;
    }

    @Override
    public EUIKeywordTooltip getActionTooltip() {
        return PGR.core.tooltips.retain;
    }
}
