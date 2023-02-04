package pinacolada.skills.skills.special.moves;

import com.megacrit.cardcrawl.cards.AbstractCard;
import extendedui.EUIRM;
import extendedui.interfaces.delegates.ActionT1;
import pinacolada.resources.PGR;
import pinacolada.skills.PSkillData;
import pinacolada.skills.PSkillSaveData;
import pinacolada.skills.fields.PField_CardCategory;
import pinacolada.skills.skills.base.moves.PMove_Modify;

public class PMove_RestoreCardHP extends PMove_Modify<PField_CardCategory>
{
    public static final PSkillData<PField_CardCategory> DATA = PMove_Modify.register(PMove_RestoreCardHP.class, PField_CardCategory.class)
            .pclOnly();

    public PMove_RestoreCardHP()
    {
        this(1, 1);
    }

    public PMove_RestoreCardHP(PSkillSaveData content)
    {
        super(DATA, content);
    }

    public PMove_RestoreCardHP(int amount, int block)
    {
        super(DATA, amount, block);
    }

    @Override
    public ActionT1<AbstractCard> getAction()
    {
        return (c) -> getActions().modifyCardHp(c, extra, false, true);
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
    public String getSubText()
    {
        return TEXT.act_healOn(getAmountRawString(),
                useParent ? getInheritedString() :
                        fields.hasGroups() ? fields.getFullCardString() : TEXT.subjects_thisX);
    }

    @Override
    public boolean isDetrimental()
    {
        return extra < 0;
    }
}
