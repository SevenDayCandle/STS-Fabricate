package pinacolada.skills.skills.base.primary;

import pinacolada.skills.PSkillData;
import pinacolada.skills.PSkillSaveData;
import pinacolada.skills.PTrigger;
import pinacolada.skills.fields.PField_Not;

public class PTrigger_When extends PTrigger
{
    public static final PSkillData<PField_Not> DATA = register(PTrigger_When.class, PField_Not.class, -1, DEFAULT_MAX);

    public PTrigger_When()
    {
        super(DATA);
    }

    public PTrigger_When(PSkillSaveData content)
    {
        super(DATA, content);
    }

    @Override
    public String getSampleText()
    {
        return TEXT.conditions.whenSingle(TEXT.subjects.x);
    }
}
