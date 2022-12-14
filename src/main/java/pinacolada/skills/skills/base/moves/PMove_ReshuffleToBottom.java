package pinacolada.skills.skills.base.moves;

import com.megacrit.cardcrawl.cards.CardGroup;
import extendedui.EUIRM;
import extendedui.interfaces.delegates.FuncT3;
import extendedui.ui.tooltips.EUITooltip;
import pinacolada.actions.pileSelection.ReshuffleFromPile;
import pinacolada.actions.pileSelection.SelectFromPile;
import pinacolada.cards.base.PCLCardGroupHelper;
import pinacolada.resources.PGR;
import pinacolada.skills.PSkillData;
import pinacolada.skills.PSkillSaveData;
import pinacolada.utilities.CardSelection;

public class PMove_ReshuffleToBottom extends PMove_Select
{
    public static final PSkillData DATA = register(PMove_ReshuffleToBottom.class, PCLEffectType.CardGroupFull)
            .selfTarget()
            .setGroups(PCLCardGroupHelper.Hand, PCLCardGroupHelper.DiscardPile, PCLCardGroupHelper.ExhaustPile);

    public PMove_ReshuffleToBottom()
    {
        this(1, (PCLCardGroupHelper) null);
    }

    public PMove_ReshuffleToBottom(PSkillSaveData content)
    {
        super(content);
    }

    public PMove_ReshuffleToBottom(int amount, PCLCardGroupHelper... h)
    {
        super(DATA, amount, h);
    }

    @Override
    public EUITooltip getActionTooltip()
    {
        return PGR.core.tooltips.reshuffle;
    }

    @Override
    public FuncT3<SelectFromPile, String, Integer, CardGroup[]> getAction()
    {
        return (a, b, c) -> new ReshuffleFromPile(a, b, c).setDestination(CardSelection.Bottom);
    }

    @Override
    public String getSampleText()
    {
        return TEXT.actions.generic2(PGR.core.tooltips.reshuffle.title, TEXT.subjects.bottomOf("X"));
    }

    @Override
    public String getSubText()
    {
        String endString = TEXT.subjects.bottomOf(TEXT.cardPile.drawPile);
        return useParent ? TEXT.actions.move(getInheritedString(), endString) :
                !groupTypes.isEmpty() ? TEXT.actions.moveTo(EUIRM.strings.numNoun(getAmountRawString(), getFullCardString(getRawString(EFFECT_CHAR))), getGroupString(), endString)
                        : TEXT.actions.move(TEXT.subjects.thisObj, endString);
    }
}
