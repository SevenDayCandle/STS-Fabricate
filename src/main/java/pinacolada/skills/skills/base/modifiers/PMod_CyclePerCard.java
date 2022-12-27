package pinacolada.skills.skills.base.modifiers;

import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.CardGroup;
import com.megacrit.cardcrawl.core.AbstractCreature;
import extendedui.EUIRM;
import extendedui.interfaces.delegates.FuncT4;
import extendedui.ui.tooltips.EUITooltip;
import pinacolada.actions.PCLActionWithCallback;
import pinacolada.actions.pileSelection.CycleCards;
import pinacolada.actions.pileSelection.DiscardFromPile;
import pinacolada.actions.pileSelection.SelectFromPile;
import pinacolada.cards.base.PCLCardGroupHelper;
import pinacolada.cards.base.PCLCardTarget;
import pinacolada.cards.base.PCLUseInfo;
import pinacolada.resources.PGR;
import pinacolada.skills.PSkillData;
import pinacolada.skills.PSkillSaveData;

import java.util.ArrayList;

import static pinacolada.skills.PSkill.PCLEffectType.CardGroupFull;

public class PMod_CyclePerCard extends PMod_Do
{

    public static final PSkillData DATA = register(PMod_CyclePerCard.class, CardGroupFull)
            .selfTarget()
            .setGroups(PCLCardGroupHelper.Hand);

    public PMod_CyclePerCard(PSkillSaveData content)
    {
        super(content);
    }

    public PMod_CyclePerCard()
    {
        super(DATA);
    }

    public PMod_CyclePerCard(int amount)
    {
        super(DATA, PCLCardTarget.None, amount);
    }

    @Override
    protected PCLActionWithCallback<ArrayList<AbstractCard>> createPileAction(PCLUseInfo info)
    {
        SelectFromPile action = new CycleCards(getName(), amount, alt);
        if (alt2)
        {
            action = action.setFilter(c -> getFullCardFilter().invoke(c));
        }
        return action;
    }

    @Override
    public String getMoveString(boolean addPeriod)
    {
        return alt2 ? super.getMoveString(addPeriod) : EUIRM.strings.verbNoun(tooltipTitle(), getAmountRawString());
    }

    @Override
    public EUITooltip getActionTooltip()
    {
        return PGR.core.tooltips.cycle;
    }

    @Override
    public FuncT4<SelectFromPile, String, AbstractCreature, Integer, CardGroup[]> getAction()
    {
        return DiscardFromPile::new;
    }
}
