package pinacolada.skills.skills.base.moves;

import com.megacrit.cardcrawl.cards.CardGroup;
import com.megacrit.cardcrawl.core.AbstractCreature;
import extendedui.interfaces.delegates.FuncT4;
import extendedui.ui.tooltips.EUITooltip;
import pinacolada.actions.pileSelection.DiscardFromPile;
import pinacolada.actions.pileSelection.SelectFromPile;
import pinacolada.cards.base.PCLCardGroupHelper;
import pinacolada.resources.PGR;
import pinacolada.skills.PSkillData;
import pinacolada.skills.PSkillSaveData;
import pinacolada.skills.fields.PField_CardCategory;

public class PMove_Discard extends PMove_Select
{
    public static final PSkillData<PField_CardCategory> DATA = register(PMove_Discard.class, PField_CardCategory.class)
            .selfTarget()
            .setGroups(PCLCardGroupHelper.DrawPile, PCLCardGroupHelper.Hand);

    public PMove_Discard()
    {
        this(1);
    }

    public PMove_Discard(PSkillSaveData content)
    {
        super(DATA, content);
    }

    public PMove_Discard(int amount, PCLCardGroupHelper... h)
    {
        super(DATA, amount, h);
    }

    @Override
    public EUITooltip getActionTooltip()
    {
        return PGR.core.tooltips.discard;
    }

    @Override
    public FuncT4<SelectFromPile, String, AbstractCreature, Integer, CardGroup[]> getAction()
    {
        return DiscardFromPile::new;
    }
}
