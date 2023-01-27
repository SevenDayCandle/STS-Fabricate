package pinacolada.skills.skills.special.moves;

import com.megacrit.cardcrawl.cards.AbstractCard;
import extendedui.EUIRM;
import extendedui.interfaces.delegates.ActionT1;
import pinacolada.resources.PGR;
import pinacolada.skills.PSkillData;
import pinacolada.skills.PSkillSaveData;
import pinacolada.skills.fields.PField_CardCategory;
import pinacolada.skills.skills.base.moves.PMove_Modify;

public class PMove_ModifyCardHP extends PMove_Modify<PField_CardCategory>
{
    public static final PSkillData<PField_CardCategory> DATA = PMove_Modify.register(PMove_ModifyCardHP.class, PField_CardCategory.class)
            .pclOnly();

    public PMove_ModifyCardHP()
    {
        this(1, 1);
    }

    public PMove_ModifyCardHP(PSkillSaveData content)
    {
        super(DATA, content);
    }

    public PMove_ModifyCardHP(int amount, int hp)
    {
        super(DATA, amount, hp);
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
