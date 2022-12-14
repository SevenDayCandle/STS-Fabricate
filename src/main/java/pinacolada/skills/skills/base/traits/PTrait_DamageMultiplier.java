package pinacolada.skills.skills.base.traits;

import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import pinacolada.skills.PSkillData;
import pinacolada.skills.PSkillSaveData;
import pinacolada.skills.PTrait;

public class PTrait_DamageMultiplier extends PTrait
{

    public static final PSkillData DATA = register(PTrait_Damage.class, PCLEffectType.General);

    public PTrait_DamageMultiplier()
    {
        this(1);
    }

    public PTrait_DamageMultiplier(PSkillSaveData content)
    {
        super(content);
    }

    public PTrait_DamageMultiplier(int amount)
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
        return amount * (1 + (this.amount / 100f));
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
