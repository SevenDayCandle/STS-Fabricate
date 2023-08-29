package pinacolada.cards.pcl.glyphs;

import pinacolada.actions.PCLActions;
import pinacolada.cards.base.PCLCardData;
import pinacolada.cards.base.fields.PCLAffinity;
import pinacolada.dungeon.CombatManager;
import pinacolada.dungeon.PCLPlayerMeter;
import pinacolada.dungeon.PCLUseInfo;
import pinacolada.skills.skills.PSpecialSkill;

public class Glyph05 extends Glyph {
    public static final PCLCardData DATA = registerInternal(Glyph05.class);

    public Glyph05() {
        super(DATA);
    }

    public void action(PSpecialSkill move, PCLUseInfo info, PCLActions order) {
        for (PCLAffinity af : move.fields.affinities) {
            for (PCLPlayerMeter meter : CombatManager.playerSystem.getActiveMeters()) {
                meter.disableAffinity(af);
            }
        }
    }

    public void setup(Object input) {
        addSpecialMove(0, this::action, 1).edit(f -> f.setAffinity(randomAffinity())).setCustomUpgrade((s, f, u) -> {
            if (u >= 50 && s.fields.affinities.size() <= 1) {
                PCLAffinity newAf = randomAffinity();
                if (newAf != null && !s.fields.affinities.contains(newAf)) {
                    s.fields.addAffinity(newAf);
                }
            }
        });
    }
}