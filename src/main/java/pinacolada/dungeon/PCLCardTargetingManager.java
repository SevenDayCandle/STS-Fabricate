package pinacolada.dungeon;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.evacipated.cardcrawl.mod.stslib.cards.targeting.TargetingHandler;
import com.evacipated.cardcrawl.modthespire.lib.SpireEnum;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.core.GameCursor;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.helpers.Hitbox;
import com.megacrit.cardcrawl.helpers.input.InputHelper;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.rooms.AbstractRoom;
import extendedui.EUIInputManager;
import pinacolada.cards.base.PCLCard;
import pinacolada.monsters.PCLCardAlly;
import pinacolada.resources.PCLEnum;
import pinacolada.utilities.GameUtilities;

import java.util.ArrayList;

public class PCLCardTargetingManager extends TargetingHandler<AbstractCreature> {
    @SpireEnum
    public static AbstractCard.CardTarget PCL;

    private AbstractCreature hovered = null;
    private PCLCard card;

    @Override
    public void clearHovered() {
        // Do NOT clear the hovered target in this method because we need to obtain it later
    }

    @Override
    public AbstractCreature getHovered() {
        return hovered;
    }

    @Override
    public boolean hasTarget() {
        // Only single targeting requires an actual target
        return card != null && (hovered != null || isNonTargeting());
    }

    protected boolean isNonTargeting() {
        return !card.pclTarget.targetsSingle() && card.type != PCLEnum.CardType.SUMMON;
    }

    @Override
    public void renderReticle(SpriteBatch sb) {
        if (card != null) {
            if (hovered != null && (card.type == PCLEnum.CardType.SUMMON || !card.pclTarget.targetsMulti())) {
                hovered.renderReticle(sb);
            }
            else if (isNonTargeting()) {
                if (card.pclTarget.targetsSelf()) {
                    AbstractDungeon.player.renderReticle(sb);
                }
                if (card.pclTarget.targetsAllies()) {
                    for (PCLCardAlly m : CombatManager.summons.summons) {
                        if (m.hasCard()) {
                            m.renderReticle(sb);
                        }
                    }
                }
                if (card.pclTarget.targetsEnemies()) {
                    final AbstractRoom room = GameUtilities.getCurrentRoom();
                    if (room != null) {
                        for (AbstractMonster m : room.monsters.monsters) {
                            if (!GameUtilities.isDeadOrEscaped(m)) {
                                m.renderReticle(sb);
                            }
                        }
                    }
                }
            }
        }
    }

    public void setTargeting(PCLCard card) {
        this.card = card;
        if (card == null) {
            hovered = null;
            PCLCardAlly.emptyAnimation.unhighlight();
        }
    }

    public boolean shouldHideArrows() {
        return card != null && isNonTargeting();
    }

    @Override
    public void updateHovered() {
        hovered = null;
        if (card == null) {
            PCLCardAlly.emptyAnimation.unhighlight();
            return;
        }

        // Summons should always target an available slot, regardless of whether it is occupied or not
        if (card.type == PCLEnum.CardType.SUMMON) {
            PCLCardAlly.emptyAnimation.highlight();
            for (PCLCardAlly m : CombatManager.summons.summons) {
                m.hb.update();
                if (m.hb.hovered) {
                    hovered = m;
                    card.refresh(hovered);
                    return;
                }
            }
        }
        // Updated hovered even for cards that have multi targeting, because we need the hovered target for on-screen meters
        else {
            PCLCardAlly.emptyAnimation.unhighlight();

            // If the card doesn't show arrows, we need to reset the hidden cursor and draw scale that occurs when entering single target mode normally
            if (isNonTargeting()) {
                card.targetDrawScale = 1.0F;
                card.target_x = InputHelper.mX;
                card.target_y = InputHelper.mY;
                GameCursor.hidden = false;
            }

            if (card.pclTarget.targetsAllies()) {
                for (PCLCardAlly m : CombatManager.summons.summons) {
                    if (m.hasCard()) {
                        m.hb.update();
                        if (m.hb.hovered) {
                            hovered = m;
                            card.refresh(hovered);
                            return;
                        }
                    }
                }
            }

            if (card.pclTarget.targetsEnemies()) {
                final AbstractRoom room = GameUtilities.getCurrentRoom();
                if (room != null) {
                    for (AbstractMonster m : room.monsters.monsters) {
                        if (!GameUtilities.isDeadOrEscaped(m)) {
                            m.hb.update();
                            if (m.hb.hovered) {
                                hovered = m;
                                card.refresh(hovered);
                                return;
                            }
                        }
                    }
                }
            }

            // Hover the player by default for things that don't require a single target
            if (card.pclTarget.targetsSelf() && (AbstractDungeon.player.hb.hovered || !card.pclTarget.targetsSingle())) {
                hovered = AbstractDungeon.player;
                card.refresh(hovered);
            }
        }
    }

    @Override
    public void updateKeyboardTarget() {
        if (card == null) {
            return;
        }

        int directionIndex = 0;
        if (EUIInputManager.didInputLeft()) {
            directionIndex -= 1;
        }
        if (EUIInputManager.didInputRight()) {
            directionIndex += 1;
        }
        if (directionIndex != 0) {

            ArrayList<AbstractCreature> creatures = new ArrayList<>();
            if (card.type == PCLEnum.CardType.SUMMON) {
                creatures.addAll(CombatManager.summons.summons);
            }
            else {
                if (card.pclTarget.targetsSelf()) {
                    creatures.add(AbstractDungeon.player);
                }
                if (card.pclTarget.targetsAllies()) {
                    GameUtilities.fillWithSummons(true, creatures);
                }
                if (card.pclTarget.targetsEnemies()) {
                    GameUtilities.fillWithEnemies(true, creatures);
                }
            }

            if (!creatures.isEmpty()) {
                creatures.sort((o1, o2) -> (int) (o1.hb.cX - o2.hb.cX));

                int targetIndex;
                if (hovered != null) {
                    targetIndex = creatures.indexOf(hovered) + directionIndex;
                }
                else {
                    targetIndex = Math.min(directionIndex, 0);
                }

                targetIndex = (targetIndex + creatures.size()) % creatures.size();
                AbstractCreature newTarget = creatures.get(targetIndex);
                updateTargetFromCursor(newTarget);
            }
        }
    }

    protected void updateTargetFromCursor(AbstractCreature newTarget) {
        if (newTarget != null) {
            Hitbox target = newTarget.hb;
            Gdx.input.setCursorPosition((int) target.cX, Settings.HEIGHT - (int) target.cY); //cursor y position is inverted for some reason :)
            hovered = newTarget;
            AbstractDungeon.player.isDraggingCard = true;
        }

        if (hovered instanceof AbstractMonster && hovered.halfDead) {
            hovered = null;
        }
    }
}
