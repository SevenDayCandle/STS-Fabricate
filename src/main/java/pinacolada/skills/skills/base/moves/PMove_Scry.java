package pinacolada.skills.skills.base.moves;

import com.megacrit.cardcrawl.cards.CardGroup;
import com.megacrit.cardcrawl.core.AbstractCreature;
import extendedui.interfaces.delegates.FuncT5;
import extendedui.ui.tooltips.EUIKeywordTooltip;
import pinacolada.actions.piles.ScryCards;
import pinacolada.actions.piles.SelectFromPile;
import pinacolada.annotations.VisibleSkill;
import pinacolada.cards.base.PCLCardGroupHelper;
import pinacolada.cards.base.fields.PCLCardSelection;
import pinacolada.resources.PGR;
import pinacolada.skills.PSkillData;
import pinacolada.skills.PSkillSaveData;
import pinacolada.skills.fields.PField_CardCategory;

@VisibleSkill
public class PMove_Scry extends PMove_DoCard<PField_CardCategory> {
    public static final PSkillData<PField_CardCategory> DATA = register(PMove_Scry.class, PField_CardCategory.class)
            .noTarget()
            .setGroups(PCLCardGroupHelper.DrawPile)
            .setOrigins(PCLCardSelection.Top);

    public PMove_Scry() {
        this(1);
    }

    public PMove_Scry(int amount) {
        super(DATA, amount, PCLCardGroupHelper.DrawPile);
    }

    public PMove_Scry(PSkillSaveData content) {
        super(DATA, content);
    }

    @Override
    public FuncT5<SelectFromPile, String, AbstractCreature, Integer, PCLCardSelection, CardGroup[]> getAction() {
        return (s, c, i, o, g) -> new ScryCards(s, i);
    }

    @Override
    public EUIKeywordTooltip getActionTooltip() {
        return PGR.core.tooltips.scry;
    }
}
