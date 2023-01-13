package pinacolada.skills.skills.base.traits;

import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import pinacolada.resources.PGR;
import pinacolada.skills.PSkillData;
import pinacolada.skills.PSkillSaveData;
import pinacolada.skills.PTrait;
import pinacolada.skills.fields.PField_Empty;

public class PTrait_Block extends PTrait<PField_Empty>
{

    public static final PSkillData<PField_Empty> DATA = register(PTrait_Block.class, PField_Empty.class);

    public PTrait_Block()
    {
        this(1);
    }

    public PTrait_Block(PSkillSaveData content)
    {
        super(DATA, content);
    }

    public PTrait_Block(int amount)
    {
        super(DATA, amount);
    }

    @Override
    public String getSubDescText()
    {
        return PGR.core.tooltips.block.toString();
    }

    @Override
    public String getSubSampleText()
    {
        return PGR.core.tooltips.block.title;
    }

    @Override
    public float modifyBlock(AbstractCard card, AbstractMonster m, float amount)
    {
        return amount + this.amount;
    }

    @Override
    public boolean isDetrimental()
    {
        return amount < 0;
    }
}
