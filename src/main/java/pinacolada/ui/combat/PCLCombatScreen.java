package pinacolada.ui.combat;

import basemod.ReflectionHacks;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import extendedui.EUIUtils;
import extendedui.ui.EUIBase;
import pinacolada.cards.base.PCLCard;
import pinacolada.misc.CombatManager;
import pinacolada.monsters.PCLCardAlly;
import pinacolada.resources.PCLEnum;
import pinacolada.resources.PCLHotkeys;
import pinacolada.resources.PGR;
import pinacolada.utilities.GameUtilities;

import static com.megacrit.cardcrawl.dungeons.AbstractDungeon.player;

// Copied and modified from STS-AnimatorMod
// TODO Merge with CombatStats or PlayerSystem
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
        setActive(GameUtilities.inBattle());
        helper.clear();
    }

    @Override
    public void updateImpl()
    {
        if (player == null || player.hand == null || AbstractDungeon.overlayMenu.energyPanel.isHidden)
        {
            return;
        }

        boolean draggingCard = false;
        AbstractCreature target = ReflectionHacks.getPrivate(player, AbstractPlayer.class,"hoveredMonster");
        PCLCard hoveredCard = null;
        if (player.hoveredCard != null)
        {
            hoveredCard = EUIUtils.safeCast(player.hoveredCard, PCLCard.class);
            if (player.isDraggingCard || player.inSingleTargetMode)
            {
                draggingCard = true;
            }
            if (target == null && draggingCard && player.hoveredCard.target == AbstractCard.CardTarget.SELF)
            {
                target = player;
            }
        }

        // If you are dragging a Summon over another one, highlight the target Summon instead
        if (player.hoveredCard == null || player.hoveredCard.type == PCLEnum.CardType.SUMMON)
        {
            for (PCLCardAlly summon : CombatManager.summons.summons)
            {
                if (summon.isHovered())
                {
                    hoveredCard = summon.card;
                    target = summon.target;
                }
            }
        }

        CombatManager.update();
        CombatManager.playerSystem.update(hoveredCard, target, draggingCard);
        CombatManager.summons.update();
        CombatManager.controlPile.update();
        helper.update();

        if (PGR.core.config.showFormulaDisplay.get())
        {
            formulaDisplay.update(hoveredCard, target, draggingCard);
        }
        if (PCLHotkeys.toggleFormulaDisplay.isJustPressed())
        {
            PGR.core.config.showFormulaDisplay.set(!PGR.core.config.showFormulaDisplay.get(), true);
        }
    }

    @Override
    public void renderImpl(SpriteBatch sb)
    {
        CombatManager.playerSystem.render(sb);
        CombatManager.summons.render(sb);
        CombatManager.controlPile.render(sb);
        if (PGR.core.config.showFormulaDisplay.get())
        {
            formulaDisplay.renderImpl(sb);
        }
    }
}
