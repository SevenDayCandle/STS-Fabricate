package pinacolada.skills.skills.special.conditions;

import extendedui.EUIRM;
import pinacolada.cards.base.fields.PCLCardTarget;
import pinacolada.misc.PCLUseInfo;
import pinacolada.resources.PGR;
import pinacolada.skills.PSkillData;
import pinacolada.skills.PSkillSaveData;
import pinacolada.skills.fields.PField_Not;
import pinacolada.skills.skills.PPassiveCond;
import pinacolada.utilities.GameUtilities;

public class PCond_MatchCombo extends PPassiveCond<PField_Not>
{

    public static final PSkillData<PField_Not> DATA = register(PCond_MatchCombo.class, PField_Not.class)
            .pclOnly()
            .selfTarget();

    public PCond_MatchCombo()
    {
        this(1);
    }

    public PCond_MatchCombo(PSkillSaveData content)
    {
        super(DATA, content);
    }

    public PCond_MatchCombo(int amount)
    {
        super(DATA, PCLCardTarget.None, amount);
    }

    @Override
    public boolean checkCondition(PCLUseInfo info, boolean isUsing, boolean fromTrigger)
    {
        return fields.not ^ (GameUtilities.getCurrentMatchCombo() >= amount);
    }

    @Override
    public String getSubText()
    {
        String base = EUIRM.strings.numNoun(amount, PGR.core.tooltips.matchCombo.title);
        return fields.not ? TEXT.cond_not(base) : base;
    }
}