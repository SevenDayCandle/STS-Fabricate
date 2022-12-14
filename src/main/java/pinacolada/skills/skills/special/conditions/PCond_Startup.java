package pinacolada.skills.skills.special.conditions;

import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import pinacolada.cards.base.PCLCardTarget;
import pinacolada.cards.base.PCLUseInfo;
import pinacolada.interfaces.markers.Hidden;
import pinacolada.resources.PGR;
import pinacolada.skills.PCond;
import pinacolada.skills.PSkill;
import pinacolada.skills.PSkillData;
import pinacolada.skills.PSkillSaveData;

// Only for relics
public class PCond_Startup extends PCond implements Hidden
{

    public static final PSkillData DATA = register(PCond_Startup.class, PCLEffectType.General, 1, 1)
            .selfTarget();

    public PCond_Startup()
    {
        super(DATA, PCLCardTarget.None, 0);
    }

    public PCond_Startup(PSkillSaveData content)
    {
        super(content);
    }

    public PCond_Startup(PSkill effect)
    {
        this();
        setChild(effect);
    }

    public PCond_Startup(PSkill... effect)
    {
        this();
        setChild(effect);
    }

    // This should not activate the child effect when played normally

    @Override
    public String getSampleText()
    {
        return PGR.core.tooltips.startup.title;
    }

    @Override
    public String getSubText()
    {
        return PGR.core.tooltips.startup.title;
    }

    @Override
    public void use(PCLUseInfo info)
    {
    }

    @Override
    public void use(PCLUseInfo info, int index)
    {
    }

    @Override
    public boolean canPlay(AbstractCard card, AbstractMonster m)
    {
        return true;
    }

    @Override
    public boolean triggerOnStartup()
    {
        if (this.childEffect != null)
        {
            this.childEffect.use(makeInfo(null));
        }
        return true;
    }

    @Override
    public boolean checkCondition(PCLUseInfo info, boolean isUsing, boolean fromTrigger)
    {
        return fromTrigger;
    }
}
