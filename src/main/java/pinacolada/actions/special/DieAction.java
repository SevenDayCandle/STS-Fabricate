package pinacolada.actions.special;

import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.screens.DeathScreen;
import com.megacrit.cardcrawl.vfx.combat.DeckPoofEffect;
import extendedui.EUIUtils;
import pinacolada.actions.PCLAction;
import pinacolada.effects.PCLEffects;
import pinacolada.utilities.GameUtilities;

// Copied and modified from STS-AnimatorMod
public class DieAction extends PCLAction
{
    public DieAction(AbstractCreature target)
    {
        super(AbstractGameAction.ActionType.DAMAGE, Settings.FAST_MODE ? Settings.ACTION_DUR_XFAST : Settings.ACTION_DUR_FAST);

        initialize(target, target, 1);
    }

    @Override
    protected void firstUpdate()
    {
        if (!GameUtilities.isDeadOrEscaped(target))
        {
            AbstractMonster m = EUIUtils.safeCast(target, AbstractMonster.class);
            if (m != null)
            {
                m.currentHealth = 0;
                m.die();
                m.hideHealthBar();

                if (GameUtilities.areMonstersBasicallyDead())
                {
                    AbstractDungeon.actionManager.cleanCardQueue();
                    PCLEffects.List.add(new DeckPoofEffect(64f * Settings.scale, 64f * Settings.scale, true));
                    PCLEffects.List.add(new DeckPoofEffect((float) Settings.WIDTH - 64f * Settings.scale, 64f * Settings.scale, false));
                    AbstractDungeon.overlayMenu.hideCombatPanels();
                }
            }
            else if (target instanceof AbstractPlayer)
            {
                player.isDead = true;
                player.currentHealth = 0;
                AbstractDungeon.deathScreen = new DeathScreen(AbstractDungeon.getMonsters());
            }
        }
    }
}
