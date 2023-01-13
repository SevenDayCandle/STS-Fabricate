package pinacolada.skills.skills.base.moves;

import com.megacrit.cardcrawl.cards.AbstractCard;
import extendedui.EUIRM;
import extendedui.interfaces.delegates.ActionT1;
import pinacolada.cards.base.PCLCardGroupHelper;
import pinacolada.skills.PSkillData;
import pinacolada.skills.PSkillSaveData;
import pinacolada.skills.fields.PField_CardCategory;

public class PMove_ModifyCost extends PMove_Modify<PField_CardCategory>
{
    public static final PSkillData<PField_CardCategory> DATA = PMove_Modify.register(PMove_ModifyCost.class, PField_CardCategory.class)
            .setExtra(-DEFAULT_MAX, DEFAULT_MAX)
            .selfTarget()
            .pclOnly();

    public PMove_ModifyCost()
    {
        this(1, 1);
    }

    public PMove_ModifyCost(PSkillSaveData content)
    {
        super(DATA, content);
    }

    public PMove_ModifyCost(int amount, int cost)
    {
        super(DATA, amount, cost);
    }

    public PMove_ModifyCost(int amount, int damage, PCLCardGroupHelper... groups)
    {
        super(DATA, amount, damage, groups);
    }

    @Override
    public ActionT1<AbstractCard> getAction()
    {
        return (c) -> getActions().modifyCost(c, extra, fields.forced, true);
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
