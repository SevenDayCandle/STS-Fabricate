package pinacolada.skills.skills.base.modifiers;

import extendedui.EUIRM;
import extendedui.EUIUtils;
import pinacolada.annotations.VisibleSkill;
import pinacolada.cards.base.fields.PCLCardTarget;
import pinacolada.misc.PCLUseInfo;
import pinacolada.resources.PGR;
import pinacolada.skills.PSkillData;
import pinacolada.skills.PSkillSaveData;
import pinacolada.skills.fields.PField_Empty;
import pinacolada.utilities.GameUtilities;

@VisibleSkill
public class PMod_PerCreatureAttacking extends PMod_Per<PField_Empty>
{

    public static final PSkillData<PField_Empty> DATA = register(PMod_PerCreatureAttacking.class, PField_Empty.class);

    public PMod_PerCreatureAttacking(PSkillSaveData content)
    {
        super(DATA, content);
    }

    public PMod_PerCreatureAttacking()
    {
        super(DATA);
    }

    public PMod_PerCreatureAttacking(int amount)
    {
        super(DATA, PCLCardTarget.AllEnemy, amount);
    }

    public PMod_PerCreatureAttacking(PCLCardTarget target, int amount)
    {
        super(DATA, target, amount);
    }

    @Override
    public int getMultiplier(PCLUseInfo info)
    {
        return EUIUtils.count(getTargetList(info), GameUtilities::isAttacking);
    }

    @Override
    public String getSubText()
    {
        return EUIRM.strings.adjNoun(PGR.core.tooltips.attack.progressive(), TEXT.subjects_x);
    }

    @Override
    public String getConditionText()
    {
        return EUIRM.strings.adjNoun(PGR.core.tooltips.attack.progressive(), target == PCLCardTarget.Any ? TEXT.subjects_character : TEXT.subjects_enemy);
    }
}
