package pinacolada.skills.skills.base.moves;

import com.megacrit.cardcrawl.cards.AbstractCard;
import extendedui.EUIRM;
import extendedui.interfaces.delegates.ActionT1;
import pinacolada.skills.PSkillData;
import pinacolada.skills.PSkillSaveData;

import java.util.ArrayList;

public class PMove_ModifyCost extends PMove_Modify
{
    public static final PSkillData DATA = PMove_Modify.register(PMove_ModifyCost.class, PCLEffectType.CardGroup);

    public PMove_ModifyCost()
    {
        this(1, 1, new ArrayList<>());
    }

    public PMove_ModifyCost(PSkillSaveData content)
    {
        super(content);
    }

    public PMove_ModifyCost(int amount, int cost)
    {
        super(DATA, amount, cost);
    }

    public PMove_ModifyCost(int amount, int cost, ArrayList<AbstractCard> cards)
    {
        super(DATA, amount, cost, cards);
    }

    @Override
    public ActionT1<AbstractCard> getAction()
    {
        return (c) -> getActions().modifyCost(c, extra, !alt, true);
    }

    @Override
    public String getObjectSampleText()
    {
        return TEXT.subjects.cost;
    }

    @Override
    public String getObjectText()
    {
        return EUIRM.strings.numNoun(getExtraRawString(), TEXT.subjects.cost);
    }

    @Override
    public boolean isDetrimental()
    {
        return extra < 0;
    }
}
