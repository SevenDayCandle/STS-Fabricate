package pinacolada.skills.skills.base.conditions;

import com.badlogic.gdx.graphics.Color;
import com.megacrit.cardcrawl.core.Settings;
import pinacolada.annotations.VisibleSkill;
import pinacolada.cards.base.PCLCardTarget;
import pinacolada.cards.base.PCLUseInfo;
import pinacolada.resources.PGR;
import pinacolada.skills.PCond;
import pinacolada.skills.PSkillData;
import pinacolada.skills.PSkillSaveData;
import pinacolada.skills.fields.PField_Empty;
import pinacolada.utilities.GameUtilities;

@VisibleSkill
public class PCond_Chance extends PCond<PField_Empty>
{
    public static final PSkillData<PField_Empty> DATA = register(PCond_Chance.class, PField_Empty.class)
            .selfTarget();

    public PCond_Chance(PSkillSaveData content)
    {
        super(DATA, content);
    }

    public PCond_Chance()
    {
        super(DATA, PCLCardTarget.None, 1);
    }

    public PCond_Chance(int amount)
    {
        super(DATA, PCLCardTarget.None, amount);
    }

    @Override
    public Color getConditionColor()
    {
        return Settings.GOLD_COLOR;
    }

    @Override
    public boolean checkCondition(PCLUseInfo info, boolean isUsing, boolean fromTrigger)
    {
        return GameUtilities.chance(amount);
    }

    @Override
    public String getSampleText()
    {
        return TEXT.actions.generic2(PGR.core.tooltips.chance.title, "X%");
    }

    @Override
    public String getSubText()
    {
        return TEXT.actions.generic2(PGR.core.tooltips.chance.title, amount + "%");
    }
}
