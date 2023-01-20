package pinacolada.skills.skills.special.conditions;

import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import pinacolada.cards.base.PCLCardTarget;
import pinacolada.misc.PCLUseInfo;
import pinacolada.interfaces.subscribers.OnBattleStartSubscriber;
import pinacolada.resources.PGR;
import pinacolada.skills.PCond;
import pinacolada.skills.PSkill;
import pinacolada.skills.PSkillData;
import pinacolada.skills.PSkillSaveData;
import pinacolada.skills.fields.PField_Empty;

// Only for relics
public class PCond_Startup extends PCond<PField_Empty> implements OnBattleStartSubscriber
{
    public static final PSkillData<PField_Empty> DATA = register(PCond_Startup.class, PField_Empty.class, 1, 1)
            .selfTarget();

    public PCond_Startup()
    {
        super(DATA, PCLCardTarget.None, 0);
    }

    public PCond_Startup(PSkillSaveData content)
    {
        super(DATA, content);
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
    public void onBattleStart()
    {
        useFromTrigger(makeInfo(null));
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
    public boolean checkCondition(PCLUseInfo info, boolean isUsing, boolean fromTrigger)
    {
        return fromTrigger;
    }
}
