package pinacolada.skills.skills.base.traits;

import com.megacrit.cardcrawl.monsters.AbstractMonster;
import pinacolada.cards.base.PCLCard;
import pinacolada.resources.PGR;
import pinacolada.skills.PSkillData;
import pinacolada.skills.PSkillSaveData;
import pinacolada.skills.PTrait;

public class PTrait_BlockCount extends PTrait
{

    public static final PSkillData DATA = register(PTrait_BlockCount.class, PCLEffectType.General);

    public PTrait_BlockCount()
    {
        this(1);
    }

    public PTrait_BlockCount(PSkillSaveData content)
    {
        super(content);
    }

    public PTrait_BlockCount(int amount)
    {
        super(DATA, amount);
    }

    @Override
    public String getSubDescText()
    {
        return TEXT.subjects.count(PGR.core.tooltips.block);
    }

    @Override
    public String getSubSampleText()
    {
        return TEXT.subjects.count(PGR.core.tooltips.block);
    }

    @Override
    public float modifyRightCount(PCLCard card, AbstractMonster m, float amount)
    {
        return amount + this.amount;
    }

    @Override
    public boolean isDetrimental()
    {
        return amount < 0;
    }
}
