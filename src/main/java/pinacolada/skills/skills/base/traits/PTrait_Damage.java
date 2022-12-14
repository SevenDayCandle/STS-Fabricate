package pinacolada.skills.skills.base.traits;

import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import pinacolada.skills.PSkillData;
import pinacolada.skills.PSkillSaveData;
import pinacolada.skills.PTrait;

public class PTrait_Damage extends PTrait
{

    public static final PSkillData DATA = register(PTrait_Damage.class, PCLEffectType.General);

    public PTrait_Damage()
    {
        this(1);
    }

    public PTrait_Damage(PSkillSaveData content)
    {
        super(content);
    }

    public PTrait_Damage(int amount)
    {
        super(DATA, amount);
    }

    @Override
    public String getSubDescText()
    {
        return TEXT.subjects.damage;
    }

    @Override
    public String getSubSampleText()
    {
        return TEXT.subjects.damage;
    }

    @Override
    public float modifyDamage(AbstractCard card, AbstractMonster m, float amount)
    {
        return amount + this.amount;
    }

    @Override
    public boolean isDetrimental()
    {
        return amount < 0;
    }
}
