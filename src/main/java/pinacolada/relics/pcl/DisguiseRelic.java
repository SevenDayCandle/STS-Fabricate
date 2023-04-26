package pinacolada.relics.pcl;

import extendedui.EUIInputManager;
import pinacolada.actions.PCLActions;
import pinacolada.cards.base.fields.PCLCardTarget;
import pinacolada.characters.CreatureAnimationInfo;
import pinacolada.relics.PCLRelic;
import pinacolada.resources.PGR;
import pinacolada.utilities.GameUtilities;

public abstract class DisguiseRelic extends PCLRelic {
    public DisguiseRelic(String id, RelicTier tier, LandingSound sfx) {
        super(id, tier, sfx);
    }

    @Override
    public void update() {
        super.update();

        if (GameUtilities.inBattle() && hb.hovered && EUIInputManager.rightClick.isJustPressed()) {
            PCLActions.bottom.selectCreature(PCLCardTarget.Any, name)
                    .addCallback(c -> {
                        if (c.id == null) {
                            String p = CreatureAnimationInfo.getRandomKey();
                            if (p != null) {
                                PGR.dungeon.setCreature(p);
                            }
                        }
                        else {
                            PGR.dungeon.setCreature(CreatureAnimationInfo.getIdentifierString(c));
                        }
                    });
        }
    }
}