package pinacolada.skills.skills.base.modifiers;

import com.megacrit.cardcrawl.cards.CardGroup;
import com.megacrit.cardcrawl.core.AbstractCreature;
import extendedui.EUIRM;
import extendedui.interfaces.delegates.FuncT4;
import extendedui.ui.tooltips.EUITooltip;
import pinacolada.actions.pileSelection.RetainFromPile;
import pinacolada.actions.pileSelection.SelectFromPile;
import pinacolada.cards.base.PCLCardGroupHelper;
import pinacolada.cards.base.PCLCardTarget;
import pinacolada.resources.PGR;
import pinacolada.skills.PSkillData;
import pinacolada.skills.PSkillSaveData;

import static pinacolada.skills.PSkill.PCLEffectType.CardGroupFull;

public class PMod_RetainBranch extends PMod_DoBranch
{

    public static final PSkillData DATA = register(PMod_RetainBranch.class, CardGroupFull)
            .selfTarget()
            .setGroups(PCLCardGroupHelper.Hand);

    public PMod_RetainBranch(PSkillSaveData content)
    {
        super(content);
    }

    public PMod_RetainBranch()
    {
        super(DATA);
    }

    public PMod_RetainBranch(int amount)
    {
        super(DATA, PCLCardTarget.None, amount);
    }

    @Override
    public EUITooltip getActionTooltip()
    {
        return PGR.core.tooltips.retain;
    }

    @Override
    public FuncT4<SelectFromPile, String, AbstractCreature, Integer, CardGroup[]> getAction()
    {
        return RetainFromPile::new;
    }

    @Override
    public String getSubText()
    {
        return EUIRM.strings.verbNoun(tooltipTitle(), getAmountRawString());
    }
}
