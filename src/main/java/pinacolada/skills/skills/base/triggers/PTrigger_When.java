package pinacolada.skills.skills.base.triggers;

import pinacolada.skills.PSkillData;
import pinacolada.skills.PSkillSaveData;
import pinacolada.skills.PTrigger;

import static pinacolada.skills.PSkill.PCLEffectType.General;

public class PTrigger_When extends PTrigger
{

    public static final PSkillData DATA = register(PTrigger_When.class, General, TRIGGER_PRIORITY, -1, DEFAULT_MAX);

    public PTrigger_When()
    {
        super(DATA);
    }

    public PTrigger_When(PSkillSaveData content)
    {
        super(content);
    }

    @Override
    public String getSampleText()
    {
        return TEXT.conditions.whenSingle("X");
    }
}
