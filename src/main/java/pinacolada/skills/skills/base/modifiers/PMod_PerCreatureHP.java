package pinacolada.skills.skills.base.modifiers;

import com.megacrit.cardcrawl.core.AbstractCreature;
import extendedui.EUIRM;
import extendedui.EUIUtils;
import pinacolada.annotations.VisibleSkill;
import pinacolada.cards.base.fields.PCLCardTarget;
import pinacolada.misc.PCLUseInfo;
import pinacolada.resources.PGR;
import pinacolada.skills.PSkillData;
import pinacolada.skills.PSkillSaveData;
import pinacolada.skills.fields.PField_Empty;

import java.util.List;

@VisibleSkill
public class PMod_PerCreatureHP extends PMod_Per<PField_Empty>
{
    public static final PSkillData<PField_Empty> DATA = register(PMod_PerCreatureHP.class, PField_Empty.class).selfTarget();

    public PMod_PerCreatureHP(PSkillSaveData content)
    {
        super(DATA, content);
    }

    public PMod_PerCreatureHP()
    {
        super(DATA);
    }

    public PMod_PerCreatureHP(int amount)
    {
        super(DATA, PCLCardTarget.Self, amount);
    }

    public PMod_PerCreatureHP(PCLCardTarget target, int amount)
    {
        super(DATA, target, amount);
    }

    @Override
    public int getMultiplier(PCLUseInfo info)
    {
        List<AbstractCreature> targetList = getTargetList(info);
        return EUIUtils.sumInt(targetList, t -> t.currentHealth);
    }

    @Override
    public String getSubText()
    {
        return PGR.core.tooltips.hp.title;
    }

    @Override
    public String getConditionText()
    {
        String baseString = getSubText();
        if (amount > 1)
        {
            baseString = EUIRM.strings.numNoun(getAmountRawString(), baseString);
        }
        switch (target)
        {
            case All:
            case Any:
                return TEXT.subjects_onAnyCharacter(baseString);
            case AllEnemy:
                return TEXT.subjects_onAnyEnemy(baseString);
            case Single:
                return TEXT.subjects_onTheEnemy(baseString);
            case Self:
                return TEXT.subjects_onYou(baseString);
            default:
                return baseString;
        }
    }
}