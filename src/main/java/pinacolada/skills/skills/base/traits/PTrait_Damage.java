package pinacolada.skills.skills.base.traits;

import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import pinacolada.skills.PSkillData;
import pinacolada.skills.PSkillSaveData;
import pinacolada.skills.PTrait;
import pinacolada.skills.fields.PField_Empty;

public class PTrait_Damage extends PTrait<PField_Empty>
{

    public static final PSkillData<PField_Empty> DATA = register(PTrait_Damage.class, PField_Empty.class);

    public PTrait_Damage()
    {
        this(1);
    }

    public PTrait_Damage(PSkillSaveData content)
    {
        super(DATA, content);
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
