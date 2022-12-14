package pinacolada.skills.skills.special.traits;

import com.megacrit.cardcrawl.orbs.AbstractOrb;
import extendedui.EUIRM;
import extendedui.EUIUtils;
import pinacolada.cards.base.PCLAffinity;
import pinacolada.interfaces.markers.Hidden;
import pinacolada.misc.CombatStats;
import pinacolada.skills.PSkillData;
import pinacolada.skills.PSkillSaveData;
import pinacolada.skills.PTrait;
import pinacolada.utilities.GameUtilities;

public class PTrait_OrbFocusScaling extends PTrait implements Hidden
{

    public static final PSkillData DATA = register(PTrait_OrbFocusScaling.class, PCLEffectType.Affinity);

    public PTrait_OrbFocusScaling()
    {
        this(1);
    }

    public PTrait_OrbFocusScaling(PSkillSaveData content)
    {
        super(content);
    }

    public PTrait_OrbFocusScaling(int amount, PCLAffinity... affinities)
    {
        super(DATA, amount, affinities);
    }

    @Override
    public String getSubDescText()
    {
        return EUIRM.strings.generic2(getAffinityPowerAndString(), TEXT.seriesUI.scalings);
    }

    @Override
    public String getSubSampleText()
    {
        return TEXT.seriesUI.scalings;
    }

    @Override
    public boolean triggerOnOrbFocus(AbstractOrb orb)
    {
        GameUtilities.modifyOrbTemporaryFocus(orb, EUIUtils.sumInt(affinities, CombatStats.playerSystem::getLevel) * amount, true, false);
        return true;
    }
}
