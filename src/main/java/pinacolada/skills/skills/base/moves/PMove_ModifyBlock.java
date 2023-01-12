package pinacolada.skills.skills.base.moves;

import com.megacrit.cardcrawl.cards.AbstractCard;
import extendedui.EUIRM;
import extendedui.interfaces.delegates.ActionT1;
import pinacolada.cards.base.PCLCardGroupHelper;
import pinacolada.resources.PGR;
import pinacolada.skills.PSkillData;
import pinacolada.skills.PSkillSaveData;
import pinacolada.skills.fields.PField_CardCategory;

public class PMove_ModifyBlock extends PMove_Modify<PField_CardCategory>
{
    public static final PSkillData<PField_CardCategory> DATA = PMove_Modify.register(PMove_ModifyBlock.class, PField_CardCategory.class)
            .setExtra(-DEFAULT_MAX, DEFAULT_MAX)
            .selfTarget()
            .pclOnly();

    public PMove_ModifyBlock()
    {
        this(1, 1);
    }

    public PMove_ModifyBlock(PSkillSaveData content)
    {
        super(content);
    }

    public PMove_ModifyBlock(int amount, int block)
    {
        super(DATA, amount, block);
    }

    public PMove_ModifyBlock(int amount, int damage, PCLCardGroupHelper... groups)
    {
        super(DATA, amount, damage, groups);
    }

    @Override
    public ActionT1<AbstractCard> getAction()
    {
        return (c) -> getActions().modifyBlock(c, extra, true, true);
    }

    @Override
    public String getObjectSampleText()
    {
        return PGR.core.tooltips.block.title;
    }

    @Override
    public String getObjectText()
    {
        return EUIRM.strings.numNoun(getExtraRawString(), PGR.core.tooltips.block);
    }

    @Override
    public boolean isDetrimental()
    {
        return extra < 0;
    }
}
