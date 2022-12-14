package pinacolada.ui.combat;

import basemod.DevConsole;
import basemod.ReflectionHacks;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import extendedui.EUIGameUtils;
import extendedui.EUIUtils;
import extendedui.ui.EUIBase;
import pinacolada.cards.base.PCLCard;
import pinacolada.misc.CombatStats;
import pinacolada.misc.PCLHotkeys;
import pinacolada.resources.PGR;
import pinacolada.utilities.GameUtilities;

import static com.megacrit.cardcrawl.dungeons.AbstractDungeon.player;

public class PCLCombatScreen extends EUIBase
{
    public final PowerFormulaDisplay formulaDisplay;
    public final CombatHelper helper = new CombatHelper();

    public PCLCombatScreen()
    {
        formulaDisplay = new PowerFormulaDisplay();
        setActive(false);
    }

    public void initialize() {
        setActive(GameUtilities.inBattle() && GameUtilities.isPCLPlayerClass());
        helper.clear();
    }

    @Override
    public void updateImpl()
    {
        if ((Settings.isDebug || DevConsole.infiniteEnergy) && EUIGameUtils.inGame())
        {
            PGR.core.dungeon.setCheating();
        }

        if (player == null || player.hand == null || AbstractDungeon.overlayMenu.energyPanel.isHidden)
        {
            return;
        }

        boolean draggingCard = false;
        AbstractCreature target = ReflectionHacks.getPrivate(player, AbstractPlayer.class,"hoveredMonster");
        if (player.hoveredCard != null)
        {
            if (player.isDraggingCard || player.inSingleTargetMode)
            {
                draggingCard = true;
            }
            if ( target == null && draggingCard && player.hoveredCard.target == AbstractCard.CardTarget.SELF)
            {
                target = player;
            }
        }

        CombatStats.update();
        CombatStats.playerSystem.update(EUIUtils.safeCast(player.hoveredCard, PCLCard.class), target, draggingCard);
        CombatStats.summons.update();
        CombatStats.controlPile.update();
        helper.update();

        if (PGR.core.config.showFormulaDisplay.get())
        {
            formulaDisplay.update(player.hoveredCard, target, draggingCard);
        }
        if (PCLHotkeys.toggleFormulaDisplay.isJustPressed())
        {
            PGR.core.config.showFormulaDisplay.set(!PGR.core.config.showFormulaDisplay.get(), true);
        }
    }

    @Override
    public void renderImpl(SpriteBatch sb)
    {
        CombatStats.playerSystem.render(sb);
        CombatStats.summons.render(sb);
        CombatStats.controlPile.render(sb);
        if (PGR.core.config.showFormulaDisplay.get())
        {
            formulaDisplay.renderImpl(sb);
        }
    }
}
