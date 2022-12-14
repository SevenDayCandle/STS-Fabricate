package pinacolada.cards.pcl.glyphs;

import pinacolada.cards.base.PCLAffinity;
import pinacolada.cards.base.PCLCardData;
import pinacolada.cards.base.PCLUseInfo;
import pinacolada.misc.CombatStats;
import pinacolada.skills.skills.PSpecialSkill;
import pinacolada.ui.combat.PCLPlayerMeter;

public class Glyph05 extends Glyph
{
    public static final PCLCardData DATA = registerInternal(Glyph05.class);

    public Glyph05()
    {
        super(DATA);
    }

    public void action(PSpecialSkill move, PCLUseInfo info)
    {
        for (PCLAffinity af : move.affinities)
        {
            for (PCLPlayerMeter meter : CombatStats.playerSystem.getMeters())
            {
                meter.disableAffinity(af);
            }
        }
    }

    public void setup(Object input)
    {
        addSpecialMove(0, this::action, 1).setAffinity(randomAffinity()).setCustomUpgrade((s, f, u) -> {
            if (u >= 50 && s.affinities.size() <= 1)
            {
                PCLAffinity newAf = randomAffinity();
                if (newAf != null && !s.affinities.contains(newAf))
                {
                    s.addAffinity(newAf);
                }
            }
        });
    }
}