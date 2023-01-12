package pinacolada.skills.skills.base.modifiers;

import com.megacrit.cardcrawl.cards.CardGroup;
import com.megacrit.cardcrawl.core.AbstractCreature;
import extendedui.interfaces.delegates.FuncT4;
import extendedui.ui.tooltips.EUITooltip;
import pinacolada.actions.pileSelection.DiscardFromPile;
import pinacolada.actions.pileSelection.SelectFromPile;
import pinacolada.cards.base.PCLCardGroupHelper;
import pinacolada.cards.base.PCLCardTarget;
import pinacolada.resources.PGR;
import pinacolada.skills.PSkillData;
import pinacolada.skills.PSkillSaveData;
import pinacolada.skills.fields.PField_CardCategory;

public class PMod_DiscardBranch extends PMod_DoBranch
{

    public static final PSkillData<PField_CardCategory> DATA = register(PMod_DiscardBranch.class, PField_CardCategory.class)
            .selfTarget()
            .setGroups(PCLCardGroupHelper.DrawPile, PCLCardGroupHelper.ExhaustPile, PCLCardGroupHelper.Hand);

    public PMod_DiscardBranch(PSkillSaveData content)
    {
        super(content);
    }

    public PMod_DiscardBranch()
    {
        super(DATA);
    }

    public PMod_DiscardBranch(int amount, PCLCardGroupHelper... groups)
    {
        super(DATA, PCLCardTarget.None, amount, groups);
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
