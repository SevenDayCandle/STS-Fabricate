package pinacolada.skills.skills.base.moves;

import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.CardGroup;
import com.megacrit.cardcrawl.core.AbstractCreature;
import extendedui.EUIRM;
import extendedui.interfaces.delegates.FuncT5;
import extendedui.ui.tooltips.EUITooltip;
import pinacolada.actions.pileSelection.ScryCards;
import pinacolada.actions.pileSelection.SelectFromPile;
import pinacolada.annotations.VisibleSkill;
import pinacolada.cards.base.PCLCardGroupHelper;
import pinacolada.resources.PGR;
import pinacolada.skills.PSkillData;
import pinacolada.skills.PSkillSaveData;
import pinacolada.skills.fields.PField_CardCategory;
import pinacolada.utilities.ListSelection;

@VisibleSkill
public class PMove_Scry extends PMove_Select
{
    public static final PSkillData<PField_CardCategory> DATA = register(PMove_Scry.class, PField_CardCategory.class)
            .selfTarget()
            .setGroups(PCLCardGroupHelper.DrawPile);

    public PMove_Scry()
    {
        this(1);
    }

    public PMove_Scry(PSkillSaveData content)
    {
        super(DATA, content);
    }

    public PMove_Scry(int amount)
    {
        super(DATA, amount, PCLCardGroupHelper.DrawPile);
    }

    @Override
    public EUITooltip getActionTooltip()
    {
        return PGR.core.tooltips.scry;
    }

    @Override
    public FuncT5<SelectFromPile, String, AbstractCreature, Integer, ListSelection<AbstractCard>, CardGroup[]> getAction()
    {
        return (s, c, i, o, g) -> new ScryCards(s, i);
    }

    @Override
    public String getSubText()
    {
        return useParent ? EUIRM.strings.verbNoun(getActionTitle(), getInheritedString())
                : EUIRM.strings.verbNumNoun(getActionTitle(), getAmountRawString(), fields.getFullCardString());
    }
}
