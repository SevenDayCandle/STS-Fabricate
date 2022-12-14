package pinacolada.skills.skills.base.conditions;

import extendedui.EUIRM;
import pinacolada.cards.base.PCLCardTarget;
import pinacolada.cards.base.PCLUseInfo;
import pinacolada.resources.PGR;
import pinacolada.skills.PCond;
import pinacolada.skills.PSkill;
import pinacolada.skills.PSkillData;
import pinacolada.skills.PSkillSaveData;
import pinacolada.utilities.GameUtilities;

public class PCond_MatchCombo extends PCond
{

    public static final PSkillData DATA = register(PCond_MatchCombo.class, PCLEffectType.General)
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

    public PCond_MatchCombo(PSkill effect)
    {
        this();
        setChild(effect);
    }

    public PCond_MatchCombo(PSkill... effect)
    {
        this();
        setChild(effect);
    }

    @Override
    public boolean checkCondition(PCLUseInfo info, boolean isUsing, boolean fromTrigger)
    {
        return alt ^ (GameUtilities.getCurrentMatchCombo() >= amount);
    }

    @Override
    public String getSubText()
    {
        String base = EUIRM.strings.numNoun(amount, PGR.core.tooltips.matchCombo);
        return alt ? TEXT.conditions.not(base) : base;
    }
}
