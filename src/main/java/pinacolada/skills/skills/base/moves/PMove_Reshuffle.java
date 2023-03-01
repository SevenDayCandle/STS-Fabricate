package pinacolada.skills.skills.base.moves;

import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.CardGroup;
import com.megacrit.cardcrawl.core.AbstractCreature;
import extendedui.interfaces.delegates.FuncT5;
import extendedui.ui.tooltips.EUITooltip;
import pinacolada.actions.piles.ReshuffleFromPile;
import pinacolada.actions.piles.SelectFromPile;
import pinacolada.annotations.VisibleSkill;
import pinacolada.cards.base.PCLCardGroupHelper;
import pinacolada.resources.PGR;
import pinacolada.skills.PSkillData;
import pinacolada.skills.PSkillSaveData;
import pinacolada.skills.fields.PField_CardCategory;
import pinacolada.utilities.ListSelection;

@VisibleSkill
public class PMove_Reshuffle extends PMove_Select
{
    public static final PSkillData<PField_CardCategory> DATA = register(PMove_Reshuffle.class, PField_CardCategory.class)
            .selfTarget()
            .setGroups(PCLCardGroupHelper.Hand, PCLCardGroupHelper.DiscardPile, PCLCardGroupHelper.ExhaustPile);

    public PMove_Reshuffle()
    {
        this(1);
    }

    public PMove_Reshuffle(PSkillSaveData content)
    {
        super(DATA, content);
    }

    public PMove_Reshuffle(int amount, PCLCardGroupHelper... h)
    {
        super(DATA, amount, h);
    }

    @Override
    public EUITooltip getActionTooltip()
    {
        return PGR.core.tooltips.reshuffle;
    }

    @Override
    public FuncT5<SelectFromPile, String, AbstractCreature, Integer, ListSelection<AbstractCard>, CardGroup[]> getAction()
    {
        return ReshuffleFromPile::new;
    }
}
