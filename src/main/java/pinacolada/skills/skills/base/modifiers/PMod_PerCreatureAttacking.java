package pinacolada.skills.skills.base.modifiers;

import com.megacrit.cardcrawl.core.AbstractCreature;
import extendedui.EUIRM;
import extendedui.EUIUtils;
import pinacolada.cards.base.PCLCardTarget;
import pinacolada.cards.base.PCLUseInfo;
import pinacolada.skills.PMod;
import pinacolada.skills.PSkill;
import pinacolada.skills.PSkillData;
import pinacolada.skills.PSkillSaveData;
import pinacolada.utilities.GameUtilities;

import java.util.List;

import static pinacolada.skills.PSkill.PCLEffectType.Power;

public class PMod_PerCreatureAttacking extends PMod
{

    public static final PSkillData DATA = register(PMod_PerCreatureAttacking.class, Power);

    public PMod_PerCreatureAttacking(PSkillSaveData content)
    {
        super(content);
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
    public int getModifiedAmount(PSkill be, PCLUseInfo info)
    {
        List<AbstractCreature> targetList = getTargetList(info);
        return be.baseAmount * EUIUtils.count(targetList, GameUtilities::isAttacking);
    }

    @Override
    public String getSampleText()
    {
        return TEXT.conditions.per("X", EUIRM.strings.adjNoun(TEXT.subjects.attacking, "Y"));
    }

    @Override
    public String getSubText()
    {
        return EUIRM.strings.adjNoun(TEXT.subjects.attacking, target == PCLCardTarget.Any ? TEXT.subjects.character : TEXT.subjects.enemy);
    }
}
