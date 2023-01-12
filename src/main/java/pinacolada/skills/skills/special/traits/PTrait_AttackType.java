package pinacolada.skills.skills.special.traits;

import com.megacrit.cardcrawl.cards.AbstractCard;
import pinacolada.cards.base.PCLAttackType;
import pinacolada.cards.base.PCLCard;
import pinacolada.interfaces.markers.Hidden;
import pinacolada.skills.PSkillData;
import pinacolada.skills.PSkillSaveData;
import pinacolada.skills.PTrait;
import pinacolada.skills.PTrigger;
import pinacolada.skills.fields.PField_Empty;

// Only used for augments
public class PTrait_AttackType extends PTrait implements Hidden
{

    public static final PSkillData DATA = register(PTrait_AttackType.class, PField_Empty.class);

    protected PCLAttackType attackType = PCLAttackType.Normal;

    public PTrait_AttackType()
    {
        this(PCLAttackType.Normal);
    }

    public PTrait_AttackType(PSkillSaveData content)
    {
        super(content);
    }

    public PTrait_AttackType(PCLAttackType type)
    {
        super(DATA);
        this.attackType = type;
    }

    @Override
    public String getSubText()
    {
        return hasParentType(PTrigger.class) ? getSubDescText() :
                alt ? TEXT.actions.remove(getSubDescText()) : TEXT.actions.has(getSubDescText());
    }

    @Override
    public PTrait_AttackType makeCopy()
    {
        PTrait_AttackType other = (PTrait_AttackType) super.makeCopy();
        other.attackType = this.attackType;
        return other;
    }

    @Override
    public void applyToCard(AbstractCard c, boolean conditionMet)
    {
        if (c instanceof PCLCard)
        {
            ((PCLCard) c).setAttackType(conditionMet ? attackType : ((PCLCard) c).cardData.attackType);
        }
    }

    @Override
    public String getSubDescText()
    {
        return attackType.getTooltip().title;
    }

    @Override
    public String getSubSampleText()
    {
        return TEXT.cardEditor.attackType;
    }
}
