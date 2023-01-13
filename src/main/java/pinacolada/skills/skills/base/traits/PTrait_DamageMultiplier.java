package pinacolada.skills.skills.base.traits;

import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import pinacolada.skills.PSkillData;
import pinacolada.skills.PSkillSaveData;
import pinacolada.skills.PTrait;
import pinacolada.skills.fields.PField_Empty;

public class PTrait_DamageMultiplier extends PTrait<PField_Empty>
{

    public static final PSkillData<PField_Empty> DATA = register(PTrait_Damage.class, PField_Empty.class);

    public PTrait_DamageMultiplier()
    {
        this(1);
    }

    public PTrait_DamageMultiplier(PSkillSaveData content)
    {
        super(DATA, content);
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
