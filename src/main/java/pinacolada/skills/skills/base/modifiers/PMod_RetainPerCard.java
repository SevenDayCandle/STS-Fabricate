package pinacolada.skills.skills.base.modifiers;

import com.megacrit.cardcrawl.cards.CardGroup;
import com.megacrit.cardcrawl.core.AbstractCreature;
import extendedui.interfaces.delegates.FuncT4;
import extendedui.ui.tooltips.EUITooltip;
import pinacolada.actions.pileSelection.RetainFromPile;
import pinacolada.actions.pileSelection.SelectFromPile;
import pinacolada.cards.base.PCLCardGroupHelper;
import pinacolada.cards.base.PCLCardTarget;
import pinacolada.resources.PGR;
import pinacolada.skills.PSkillData;
import pinacolada.skills.PSkillSaveData;



public class PMod_RetainPerCard extends PMod_Do
{

    public static final PSkillData DATA = register(PMod_RetainPerCard.class, CardGroupFull)
            .selfTarget()
            .setGroups(PCLCardGroupHelper.Hand);

    public PMod_RetainPerCard(PSkillSaveData content)
    {
        super(content);
    }

    public PMod_RetainPerCard()
    {
        super(DATA);
    }

    public PMod_RetainPerCard(int amount)
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
}
