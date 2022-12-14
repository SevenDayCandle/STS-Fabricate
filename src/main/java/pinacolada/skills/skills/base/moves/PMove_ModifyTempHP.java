package pinacolada.skills.skills.base.moves;

import com.megacrit.cardcrawl.cards.AbstractCard;
import extendedui.EUIRM;
import extendedui.interfaces.delegates.ActionT1;
import pinacolada.resources.PGR;
import pinacolada.skills.PSkillData;
import pinacolada.skills.PSkillSaveData;

import java.util.ArrayList;

public class PMove_ModifyTempHP extends PMove_Modify
{
    public static final PSkillData DATA = PMove_Modify.register(PMove_ModifyTempHP.class, PCLEffectType.CardGroup);

    public PMove_ModifyTempHP()
    {
        this(1, 1, new ArrayList<>());
    }

    public PMove_ModifyTempHP(PSkillSaveData content)
    {
        super(content);
    }

    public PMove_ModifyTempHP(int amount, int block)
    {
        super(DATA, amount, block);
    }

    public PMove_ModifyTempHP(int amount, int block, ArrayList<AbstractCard> cards)
    {
        super(DATA, amount, block, cards);
    }

    @Override
    public ActionT1<AbstractCard> getAction()
    {
        return (c) -> getActions().modifyTempHP(c, extra, true, true);
    }

    @Override
    public String getObjectSampleText()
    {
        return PGR.core.tooltips.tempHP.title;
    }

    @Override
    public String getObjectText()
    {
        return EUIRM.strings.numNoun(getExtraRawString(), PGR.core.tooltips.tempHP);
    }

    @Override
    public boolean isDetrimental()
    {
        return extra < 0;
    }
}
