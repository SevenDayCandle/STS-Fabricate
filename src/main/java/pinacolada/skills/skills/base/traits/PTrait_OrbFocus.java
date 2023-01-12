package pinacolada.skills.skills.base.traits;

import com.megacrit.cardcrawl.orbs.AbstractOrb;
import pinacolada.resources.PGR;
import pinacolada.skills.PSkillData;
import pinacolada.skills.PSkillSaveData;
import pinacolada.skills.PTrait;
import pinacolada.skills.fields.PField_Empty;
import pinacolada.utilities.GameUtilities;

public class PTrait_OrbFocus extends PTrait<PField_Empty>
{

    public static final PSkillData<PField_Empty> DATA = register(PTrait_OrbFocus.class, PField_Empty.class);

    public PTrait_OrbFocus()
    {
        this(1);
    }

    public PTrait_OrbFocus(PSkillSaveData content)
    {
        super(content);
    }

    public PTrait_OrbFocus(int amount)
    {
        super(DATA, amount);
    }

    @Override
    public String getSubDescText()
    {
        return PGR.core.tooltips.focus.title;
    }

    @Override
    public String getSubSampleText()
    {
        return PGR.core.tooltips.focus.title;
    }

    @Override
    public boolean triggerOnOrbFocus(AbstractOrb orb)
    {
        GameUtilities.modifyOrbTemporaryFocus(orb, amount, true, false);
        return true;
    }
}
