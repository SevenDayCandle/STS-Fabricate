package pinacolada.skills.skills.base.modifiers;

import com.megacrit.cardcrawl.cards.CardGroup;
import com.megacrit.cardcrawl.core.AbstractCreature;
import extendedui.interfaces.delegates.FuncT4;
import extendedui.ui.tooltips.EUITooltip;
import pinacolada.actions.pileSelection.ScryCards;
import pinacolada.actions.pileSelection.SelectFromPile;
import pinacolada.cards.base.PCLCardGroupHelper;
import pinacolada.cards.base.PCLCardTarget;
import pinacolada.resources.PGR;
import pinacolada.skills.PSkillData;
import pinacolada.skills.PSkillSaveData;
import pinacolada.skills.fields.PField_CardCategory;

public class PMod_ScryPerCard extends PMod_Do
{
    public static final PSkillData<PField_CardCategory> DATA = register(PMod_ScryPerCard.class, PField_CardCategory.class)
            .selfTarget()
            .setGroups(PCLCardGroupHelper.DrawPile);

    public PMod_ScryPerCard(PSkillSaveData content)
    {
        super(content);
    }

    public PMod_ScryPerCard()
    {
        super(DATA);
    }

    public PMod_ScryPerCard(int amount)
    {
        super(DATA, PCLCardTarget.None, amount);
    }

    @Override
    public EUITooltip getActionTooltip()
    {
        return PGR.core.tooltips.scry;
    }

    @Override
    public FuncT4<SelectFromPile, String, AbstractCreature, Integer, CardGroup[]> getAction()
    {
        return (s, c, i, g) -> new ScryCards(s, i);
    }
}
