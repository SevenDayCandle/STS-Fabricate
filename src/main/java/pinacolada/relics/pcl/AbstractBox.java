package pinacolada.relics.pcl;

import com.megacrit.cardcrawl.relics.AbstractRelic;
import extendedui.EUIInputManager;
import extendedui.EUIUtils;
import pinacolada.cards.base.PCLCardTarget;
import pinacolada.characters.CreatureAnimationInfo;
import pinacolada.relics.PCLRelic;
import pinacolada.resources.PGR;
import pinacolada.utilities.GameActions;
import pinacolada.utilities.GameUtilities;

import java.util.ArrayList;

public abstract class AbstractBox extends PCLRelic
{
    public static final String ID = createFullID(AbstractBox.class);

    public AbstractBox(String id, RelicTier tier, LandingSound sfx)
    {
        super(id, tier, sfx);
    }

    protected void activateBattleEffect()
    {
        counter = 1;
    }

    @Override
    public void update()
    {
        super.update();

        if (GameUtilities.inBattle() && hb.hovered && EUIInputManager.rightClick.isJustPressed() && counter > 0)
        {
            addCounter(-1);
            GameActions.bottom.selectCreature(PCLCardTarget.Any, name)
                    .addCallback(c -> {
                        if (c.id == null)
                        {
                            String p = CreatureAnimationInfo.getRandomKey();
                            if (p != null)
                            {
                                PGR.core.dungeon.setCreature(p);
                            }
                        }
                        else
                        {
                            PGR.core.dungeon.setCreature(CreatureAnimationInfo.getIdentifierString(c));
                        }
                    });
        }
    }

    @Override
    public void obtain()
    {
        ArrayList<AbstractRelic> relics = player.relics;
        for (int i = 0; i < relics.size(); i++)
        {
            AbstractBox relic = EUIUtils.safeCast(relics.get(i), AbstractBox.class);
            if (relic != null)
            {
                instantObtain(player, i, true);
                setCounter(relic.counter);
                return;
            }
        }

        super.obtain();
    }
}