package pinacolada.skills.skills.base.conditions;

import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import pinacolada.annotations.VisibleSkill;
import pinacolada.cards.base.PCLCardTarget;
import pinacolada.cards.base.PCLUseInfo;
import pinacolada.resources.PGR;
import pinacolada.skills.PCond;
import pinacolada.skills.PSkillData;
import pinacolada.skills.PSkillSaveData;
import pinacolada.skills.fields.PField_Empty;

@VisibleSkill
public class PCond_AtTurnStart extends PCond<PField_Empty>
{

    public static final PSkillData<PField_Empty> DATA = register(PCond_AtTurnStart.class, PField_Empty.class, 1, 1)
            .selfTarget();

    public PCond_AtTurnStart()
    {
        super(DATA, PCLCardTarget.None, 0);
    }

    public PCond_AtTurnStart(PSkillData<PField_Empty> data, PSkillSaveData content)
    {
        super(DATA, content);
    }

    // This should not activate the child effect when played normally

    @Override
    public String getSampleText()
    {
        return TEXT.conditions.atStartOfTurn();
    }

    @Override
    public String getSubText()
    {
        return isTrigger() ? TEXT.conditions.atStartOfTurn() : TEXT.conditions.onGeneric(PGR.core.tooltips.retain.title);
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
    public boolean triggerOnStartOfTurn()
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
