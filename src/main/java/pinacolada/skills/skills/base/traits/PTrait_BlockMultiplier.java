package pinacolada.skills.skills.base.traits;

import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import pinacolada.resources.PGR;
import pinacolada.skills.PSkillData;
import pinacolada.skills.PSkillSaveData;
import pinacolada.skills.PTrait;

public class PTrait_BlockMultiplier extends PTrait
{

    public static final PSkillData DATA = register(PTrait_BlockMultiplier.class, PCLEffectType.General);

    public PTrait_BlockMultiplier()
    {
        this(1);
    }

    public PTrait_BlockMultiplier(PSkillSaveData content)
    {
        super(content);
    }

    public PTrait_BlockMultiplier(int amount)
    {
        super(DATA, amount);
    }

    public String getSampleAmount()
    {
        return "+X%";
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
        return amount * (1f + (this.amount / 100f));
    }

    @Override
    public String wrapAmount(int input)
    {
        return (input > 0 ? "+" + input : String.valueOf(input)) + "%";
    }

    @Override
    public boolean isDetrimental()
    {
        return amount < 0;
    }
}
