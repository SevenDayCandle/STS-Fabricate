package pinacolada.skills.skills.special.moves;

import com.megacrit.cardcrawl.cards.AbstractCard;
import extendedui.EUIRM;
import extendedui.interfaces.delegates.ActionT1;
import pinacolada.interfaces.markers.Hidden;
import pinacolada.resources.PGR;
import pinacolada.skills.PSkillData;
import pinacolada.skills.PSkillSaveData;
import pinacolada.skills.skills.base.moves.PMove_Modify;

import java.util.ArrayList;

public class PMove_ModifyCardHP extends PMove_Modify implements Hidden
{
    public static final PSkillData DATA = PMove_Modify.register(PMove_ModifyCardHP.class, PCLEffectType.CardGroup)
            .pclOnly();

    public PMove_ModifyCardHP()
    {
        this(1, 1, new ArrayList<>());
    }

    public PMove_ModifyCardHP(PSkillSaveData content)
    {
        super(content);
    }

    public PMove_ModifyCardHP(int amount, int block)
    {
        super(DATA, amount, block);
    }

    public PMove_ModifyCardHP(int amount, int block, ArrayList<AbstractCard> cards)
    {
        super(DATA, amount, block, cards);
    }

    @Override
    public ActionT1<AbstractCard> getAction()
    {
        return (c) -> getActions().modifyCardHp(c, extra, true, true);
    }

    @Override
    public String getObjectSampleText()
    {
        return PGR.core.tooltips.hp.title;
    }

    @Override
    public String getObjectText()
    {
        return EUIRM.strings.numNoun(getExtraRawString(), PGR.core.tooltips.hp);
    }

    @Override
    public boolean isDetrimental()
    {
        return extra < 0;
    }
}
