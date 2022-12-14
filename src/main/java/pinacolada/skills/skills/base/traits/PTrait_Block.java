package pinacolada.skills.skills.base.traits;

import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import pinacolada.resources.PGR;
import pinacolada.skills.PSkillData;
import pinacolada.skills.PSkillSaveData;
import pinacolada.skills.PTrait;

public class PTrait_Block extends PTrait
{

    public static final PSkillData DATA = register(PTrait_Block.class, PCLEffectType.General);

    public PTrait_Block()
    {
        this(1);
    }

    public PTrait_Block(PSkillSaveData content)
    {
        super(content);
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
