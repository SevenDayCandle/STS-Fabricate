package pinacolada.skills.skills.special.traits;

import com.megacrit.cardcrawl.cards.AbstractCard;
import pinacolada.cards.base.PCLCard;
import pinacolada.cards.base.PCLCardTarget;
import pinacolada.interfaces.markers.Hidden;
import pinacolada.skills.PSkillData;
import pinacolada.skills.PSkillSaveData;
import pinacolada.skills.PTrait;
import pinacolada.skills.PTrigger;

// Only used for augments
public class PTrait_CardTarget extends PTrait implements Hidden
{

    public static final PSkillData DATA = register(PTrait_CardTarget.class, PField_Empty.class);

    protected PCLCardTarget newTarget = PCLCardTarget.Single;

    public PTrait_CardTarget()
    {
        this(PCLCardTarget.Single);
    }

    public PTrait_CardTarget(PSkillSaveData content)
    {
        super(content);
    }

    public PTrait_CardTarget(PCLCardTarget type)
    {
        super(DATA);
        this.newTarget = type;
    }

    @Override
    public String getSubText()
    {
        return hasParentType(PTrigger.class) ? getSubDescText() :
                alt ? TEXT.actions.remove(getSubDescText()) : TEXT.actions.has(getSubDescText());
    }

    @Override
    public PTrait_CardTarget makeCopy()
    {
        PTrait_CardTarget other = (PTrait_CardTarget) super.makeCopy();
        other.newTarget = this.newTarget;
        return other;
    }

    @Override
    public void applyToCard(AbstractCard c, boolean conditionMet)
    {
        if (c instanceof PCLCard)
        {
            ((PCLCard) c).setTarget(conditionMet ? newTarget : ((PCLCard) c).cardData.cardTarget);
        }
    }

    @Override
    public String getSubDescText()
    {
        return newTarget.getTitle();
    }

    @Override
    public String getSubSampleText()
    {
        return TEXT.cardEditor.cardTarget;
    }
}
