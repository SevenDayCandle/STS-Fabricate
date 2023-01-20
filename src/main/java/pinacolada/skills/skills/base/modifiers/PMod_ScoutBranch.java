package pinacolada.skills.skills.base.modifiers;

import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.CardGroup;
import com.megacrit.cardcrawl.core.AbstractCreature;
import extendedui.EUIRM;
import extendedui.interfaces.delegates.FuncT5;
import extendedui.ui.tooltips.EUITooltip;
import pinacolada.actions.pileSelection.ScoutCards;
import pinacolada.actions.pileSelection.SelectFromPile;
import pinacolada.annotations.VisibleSkill;
import pinacolada.cards.base.PCLCardGroupHelper;
import pinacolada.cards.base.PCLCardTarget;
import pinacolada.resources.PGR;
import pinacolada.skills.PSkillData;
import pinacolada.skills.PSkillSaveData;
import pinacolada.skills.fields.PField_CardCategory;
import pinacolada.utilities.ListSelection;


@VisibleSkill
public class PMod_ScoutBranch extends PMod_DoBranch
{
    public static final PSkillData<PField_CardCategory> DATA = register(PMod_ScoutBranch.class, PField_CardCategory.class)
            .selfTarget()
            .setGroups(PCLCardGroupHelper.DrawPile);

    public PMod_ScoutBranch(PSkillSaveData content)
    {
        super(DATA, content);
    }

    public PMod_ScoutBranch()
    {
        super(DATA);
    }

    public PMod_ScoutBranch(int amount)
    {
        super(DATA, PCLCardTarget.None, amount);
    }

    @Override
    public String getSubText()
    {
        return EUIRM.strings.verbNoun(getActionTitle(), getAmountRawString());
    }

    @Override
    public EUITooltip getActionTooltip()
    {
        return PGR.core.tooltips.scout;
    }

    @Override
    public FuncT5<SelectFromPile, String, AbstractCreature, Integer, ListSelection<AbstractCard>, CardGroup[]> getAction()
    {
        return (s, c, i, o, g) -> new ScoutCards(s, i);
    }
}
