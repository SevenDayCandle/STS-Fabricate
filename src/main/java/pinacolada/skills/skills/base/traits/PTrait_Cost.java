package pinacolada.skills.skills.base.traits;

import com.megacrit.cardcrawl.cards.AbstractCard;
import pinacolada.cards.base.PCLCard;
import pinacolada.skills.PSkillData;
import pinacolada.skills.PSkillSaveData;
import pinacolada.skills.PTrait;
import pinacolada.skills.fields.PField_Empty;
import pinacolada.utilities.GameUtilities;

public class PTrait_Cost extends PTrait<PField_Empty>
{

    public static final PSkillData<PField_Empty> DATA = register(PTrait_Cost.class, PField_Empty.class);

    public PTrait_Cost()
    {
        this(1);
    }

    public PTrait_Cost(PSkillSaveData content)
    {
        super(content);
    }

    public PTrait_Cost(int amount)
    {
        super(DATA, amount);
    }

    @Override
    public String getSubText()
    {
        return TEXT.actions.costs(getAmountRawString());
    }

    @Override
    public String getSampleText()
    {
        return TEXT.actions.costs("+X");
    }

    @Override
    public void applyToCard(AbstractCard c, boolean conditionMet)
    {
        if (c instanceof PCLCard)
        {
            GameUtilities.modifyCardBaseCost((PCLCard) c, conditionMet ? amount : -amount, true);
        }
        else
        {
            GameUtilities.modifyCostForCombat(c, conditionMet ? amount : -amount, true);
        }
    }

    @Override
    public String getSubDescText()
    {
        return TEXT.subjects.cost;
    }

    @Override
    public String getSubSampleText()
    {
        return TEXT.subjects.cost;
    }

    @Override
    public boolean isDetrimental()
    {
        return amount > 0;
    }

}
