package pinacolada.ui.combat;

import basemod.ReflectionHacks;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.evacipated.cardcrawl.mod.stslib.patches.CustomTargeting;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import extendedui.EUIUtils;
import extendedui.ui.EUIBase;
import pinacolada.cards.base.PCLCard;
import pinacolada.dungeon.CombatManager;
import pinacolada.monsters.PCLCardAlly;
import pinacolada.resources.PCLEnum;
import pinacolada.resources.PCLHotkeys;
import pinacolada.resources.PGR;
import pinacolada.utilities.GameUtilities;

import static com.megacrit.cardcrawl.dungeons.AbstractDungeon.player;

// Copied and modified from STS-AnimatorMod
public class PCLCombatScreen extends EUIBase {
    public final PowerFormulaDisplay formulaDisplay;

    public PCLCombatScreen() {
        formulaDisplay = new PowerFormulaDisplay();
        setActive(false);
    }

    public void initialize() {
        setActive(GameUtilities.inBattle());
    }

    @Override
    public void renderImpl(SpriteBatch sb) {
        if (player == null || player.hand == null || AbstractDungeon.overlayMenu.energyPanel.isHidden) {
            return;
        }

        CombatManager.render(sb);
        DrawPileCardPreview.updateAndRenderCurrent(sb);
        if (PGR.config.showFormulaDisplay.get()) {
            formulaDisplay.renderImpl(sb);
        }
    }

    @Override
    public void updateImpl() {
        if (player == null || player.hand == null || AbstractDungeon.overlayMenu.energyPanel.isHidden) {
            return;
        }

        boolean draggingCard = false;
        AbstractCreature target = null;
        PCLCard hoveredCard = null;
        if (player.hoveredCard != null) {
            hoveredCard = EUIUtils.safeCast(player.hoveredCard, PCLCard.class);
            if (player.isDraggingCard || player.inSingleTargetMode) {
                draggingCard = true;
            }
            CombatManager.targeting.setTargeting(hoveredCard);
            if (hoveredCard != null) {
                target = CombatManager.targeting.getHovered();
            }
            else {
                target = ReflectionHacks.getPrivate(player, AbstractPlayer.class, "hoveredMonster");
            }
        }
        else {
            CombatManager.targeting.setTargeting(null);
        }

        AbstractCreature originalTarget = target;
        PCLCard originalCard = hoveredCard;

        // If you are dragging a Summon over another one, highlight the target Summon instead
        if (player.hoveredCard == null || player.hoveredCard.type == PCLEnum.CardType.SUMMON) {
            for (PCLCardAlly summon : CombatManager.summons.summons) {
                if (summon.isHovered()) {
                    hoveredCard = summon.card;
                    target = summon.target;
                    break;
                }
            }
        }

        CombatManager.update(hoveredCard, originalCard, target, originalTarget, draggingCard);

        if (PGR.config.showFormulaDisplay.get()) {
            formulaDisplay.update(hoveredCard, originalCard, target, originalTarget, draggingCard);
        }
        if (PCLHotkeys.toggleFormulaDisplay.isJustPressed()) {
            PGR.config.showFormulaDisplay.set(!PGR.config.showFormulaDisplay.get());
        }
    }
}
