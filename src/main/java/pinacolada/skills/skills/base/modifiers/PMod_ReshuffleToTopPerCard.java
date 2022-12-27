package pinacolada.skills.skills.base.modifiers;

import com.megacrit.cardcrawl.cards.CardGroup;
import com.megacrit.cardcrawl.core.AbstractCreature;
import extendedui.EUIRM;
import extendedui.interfaces.delegates.FuncT4;
import extendedui.ui.tooltips.EUITooltip;
import pinacolada.actions.pileSelection.ReshuffleFromPile;
import pinacolada.actions.pileSelection.SelectFromPile;
import pinacolada.cards.base.PCLCardGroupHelper;
import pinacolada.cards.base.PCLCardTarget;
import pinacolada.resources.PGR;
import pinacolada.skills.PSkillData;
import pinacolada.skills.PSkillSaveData;
import pinacolada.utilities.CardSelection;

import java.util.List;

import static pinacolada.skills.PSkill.PCLEffectType.CardGroupFull;

public class PMod_ReshuffleToTopPerCard extends PMod_Do
{

    public static final PSkillData DATA = register(PMod_ReshuffleToTopPerCard.class, CardGroupFull)
            .selfTarget()
            .setGroups(PCLCardGroupHelper.DrawPile, PCLCardGroupHelper.DiscardPile, PCLCardGroupHelper.Hand);

    public PMod_ReshuffleToTopPerCard(PSkillSaveData content)
    {
        super(content);
    }

    public PMod_ReshuffleToTopPerCard()
    {
        super(DATA);
    }

    public PMod_ReshuffleToTopPerCard(int amount, PCLCardGroupHelper... groups)
    {
        super(DATA, PCLCardTarget.None, amount, groups);
    }

    public PMod_ReshuffleToTopPerCard(int amount, List<PCLCardGroupHelper> groups)
    {
        super(DATA, PCLCardTarget.None, amount, groups.toArray(new PCLCardGroupHelper[]{}));
    }

    @Override
    public EUITooltip getActionTooltip()
    {
        return PGR.core.tooltips.reshuffle;
    }

    @Override
    public FuncT4<SelectFromPile, String, AbstractCreature, Integer, CardGroup[]> getAction()
    {
        return (a, b, c, d) -> new ReshuffleFromPile(a, b, c, d).setDestination(CardSelection.Top);
    }

    @Override
    public String getMoveString(boolean addPeriod)
    {
        String endString = TEXT.subjects.topOf(TEXT.cardPile.drawPile);
        return capital(useParent ? TEXT.actions.move(getInheritedString(), endString) :
                !groupTypes.isEmpty() ? TEXT.actions.moveTo(EUIRM.strings.numNoun(getAmountRawString(), getFullCardString(getRawString(EFFECT_CHAR))), getGroupString(), endString)
                        : TEXT.actions.move(TEXT.subjects.thisObj, endString), addPeriod);
    }
}
