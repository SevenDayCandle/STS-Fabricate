package pinacolada.skills.skills.base.modifiers;

import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.CardGroup;
import com.megacrit.cardcrawl.core.AbstractCreature;
import extendedui.interfaces.delegates.FuncT4;
import extendedui.ui.tooltips.EUITooltip;
import pinacolada.actions.PCLActionWithCallback;
import pinacolada.actions.pileSelection.DiscardFromPile;
import pinacolada.actions.pileSelection.Scry;
import pinacolada.actions.pileSelection.SelectFromPile;
import pinacolada.cards.base.PCLCardGroupHelper;
import pinacolada.cards.base.PCLCardTarget;
import pinacolada.cards.base.PCLUseInfo;
import pinacolada.resources.PGR;
import pinacolada.skills.PSkillData;
import pinacolada.skills.PSkillSaveData;

import java.util.ArrayList;



public class PMod_ScryPerCard extends PMod_Do
{

    public static final PSkillData DATA = register(PMod_ScryPerCard.class, CardGroupFull)
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
    protected PCLActionWithCallback<ArrayList<AbstractCard>> createPileAction(PCLUseInfo info)
    {
        return new Scry(amount);
    }

    @Override
    public EUITooltip getActionTooltip()
    {
        return PGR.core.tooltips.scry;
    }

    @Override
    public FuncT4<SelectFromPile, String, AbstractCreature, Integer, CardGroup[]> getAction()
    {
        return DiscardFromPile::new;
    }
}
