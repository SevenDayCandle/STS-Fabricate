package pinacolada.skills.skills.base.conditions;

import extendedui.EUIRM;
import pinacolada.cards.base.PCLCardTarget;
import pinacolada.cards.base.PCLUseInfo;
import pinacolada.resources.PGR;
import pinacolada.skills.PCond;
import pinacolada.skills.PSkillData;
import pinacolada.skills.PSkillSaveData;
import pinacolada.skills.fields.PField_Not;
import pinacolada.utilities.GameUtilities;

public class PCond_MatchCombo extends PCond<PField_Not>
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
        super(content);
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
        String base = EUIRM.strings.numNoun(amount, PGR.core.tooltips.matchCombo);
        return fields.not ? TEXT.conditions.not(base) : base;
    }
}
