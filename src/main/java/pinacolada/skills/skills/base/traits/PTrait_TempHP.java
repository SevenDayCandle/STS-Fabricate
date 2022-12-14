package pinacolada.skills.skills.base.traits;

import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import pinacolada.resources.PGR;
import pinacolada.skills.PSkillData;
import pinacolada.skills.PSkillSaveData;
import pinacolada.skills.PTrait;

public class PTrait_TempHP extends PTrait
{

    public static final PSkillData DATA = register(PTrait_TempHP.class, PCLEffectType.General);

    public PTrait_TempHP()
    {
        this(1);
    }

    public PTrait_TempHP(PSkillSaveData content)
    {
        super(content);
    }

    public PTrait_TempHP(int amount)
    {
        super(DATA, amount);
    }

    @Override
    public String getSubDescText()
    {
        return PGR.core.tooltips.tempHP.getTitleOrIcon();
    }

    @Override
    public String getSubSampleText()
    {
        return PGR.core.tooltips.tempHP.title;
    }

    @Override
    public float modifyMagicNumber(AbstractCard card, AbstractMonster m, float amount)
    {
        return amount + this.amount;
    }
}
