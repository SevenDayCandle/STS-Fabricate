package pinacolada.monsters;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Interpolation;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.helpers.FontHelper;
import com.megacrit.cardcrawl.helpers.ImageMaster;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.powers.AbstractPower;
import com.megacrit.cardcrawl.relics.AbstractRelic;
import com.megacrit.cardcrawl.rooms.AbstractRoom;
import com.megacrit.cardcrawl.vfx.ExhaustBlurEffect;
import extendedui.EUI;
import extendedui.EUIInputManager;
import extendedui.EUIRM;
import extendedui.EUIRenderHelpers;
import extendedui.interfaces.delegates.FuncT1;
import extendedui.ui.EUIBase;
import extendedui.utilities.EUIColors;
import extendedui.utilities.EUITextHelper;
import pinacolada.actions.PCLActions;
import pinacolada.actions.special.PCLCreatureAttackAnimationAction;
import pinacolada.cards.base.PCLCard;
import pinacolada.cards.base.fields.PCLCardTarget;
import pinacolada.dungeon.CombatManager;
import pinacolada.dungeon.PCLUseInfo;
import pinacolada.effects.PCLEffects;
import pinacolada.effects.PCLSFX;
import pinacolada.monsters.animations.PCLAllyAnimation;
import pinacolada.monsters.animations.PCLSlotAnimation;
import pinacolada.monsters.animations.pcl.PCLGeneralAllyAnimation;
import pinacolada.resources.PCLEnum;
import pinacolada.resources.PGR;
import pinacolada.skills.PSkill;
import pinacolada.utilities.GameUtilities;
import pinacolada.utilities.PCLRenderHelpers;

import java.util.HashMap;

import static pinacolada.utilities.GameUtilities.scale;

public class PCLCardAlly extends PCLCardCreature {
    protected static final HashMap<AbstractCard.CardColor, FuncT1<PCLAllyAnimation, PCLCardAlly>> ANIMATION_MAP = new HashMap<>();
    public static final Color FADE_COOLDOWN_COLOR = EUIColors.lerpNew(Color.DARK_GRAY, Settings.GREEN_TEXT_COLOR, 0.5f);
    public static final PCLCreatureData DATA = register(PCLCardAlly.class).setHb(0, 0, 128, 128);
    public static final float INTENT_OFFSET = 14.0F * Settings.scale;
    public static PCLSlotAnimation emptyAnimation = new PCLSlotAnimation();
    protected float damageMult = 1;
    protected boolean forPreview;
    public int index;

    public PCLCardAlly(int index, float xPos, float yPos) {
        super(DATA, xPos, yPos);
        this.index = index;
        this.animation = emptyAnimation;
    }

    public static void registerAnimation(AbstractCard.CardColor color, FuncT1<PCLAllyAnimation, PCLCardAlly> animationFunc) {
        ANIMATION_MAP.putIfAbsent(color, animationFunc);
    }

    public float atBlockLastModify(PCLUseInfo info, float block) {
        return block * damageMult;
    }

    public float atDamageLastModify(PCLUseInfo info, float damage) {
        return damage * damageMult;
    }

    @Override
    public void die() {
        die(true);
    }

    @Override
    public void die(boolean triggerRelics) {
        for (AbstractPower po : powers) {
            po.onDeath();
        }
        PCLCard releasedCard = releaseCard(true);
        if (releasedCard != null) {
            PCLEffects.Queue.callback(() -> {
                PCLSFX.play(PCLSFX.CARD_EXHAUST, 0.2F);
                for (int i = 0; i < 140; ++i) {
                    AbstractDungeon.effectsQueue.add(new ExhaustBlurEffect(this.hb.cX, this.hb.cY));
                }
            });

            if (triggerRelics) {
                for (AbstractRelic relic : AbstractDungeon.player.relics) {
                    relic.onMonsterDeath(this);
                }
            }
            CombatManager.onAllyDeath(releasedCard, this);

            // Heal on summons should be at least 1
            if (releasedCard.currentHealth < 1) {
                releasedCard.currentHealth = 1;
            }

            // Killed summons are treated as being Purged
            releasedCard.untip();
            releasedCard.unhover();
            releasedCard.unfadeOut();
            CombatManager.PURGED_CARDS.addToTop(releasedCard);
            CombatManager.onCardPurged(releasedCard);
        }

        // Health needs to be 1 so that the slot can be re-selected
        this.currentHealth = 1;
    }

    public void initializeForCard(PCLCard card, boolean retainPowers, boolean delayForTurn) {
        super.initializeForCard(card, retainPowers, delayForTurn);

        FuncT1<PCLAllyAnimation, PCLCardAlly> animFunc = ANIMATION_MAP.get(card.cardData.resources.cardColor);
        PCLAllyAnimation anim = null;
        if (animFunc != null) {
            this.animation = anim = animFunc.invoke(this);
        }
        if (anim == null) {
            this.animation = anim = new PCLGeneralAllyAnimation(this);
        }
        anim.fadeIn();

        damageMult = 1;
    }

    public void onHover() {
        damageMult = 1 + (CombatManager.summons.damageBonus / 100f);
        forPreview = true;
    }

    public void onOtherAllyTrigger() {

    }

    public void onRemoveDamagePowers() {
        damageMult = 1;
        forPreview = false;
    }

    public void onUnhover() {
        if (forPreview) {
            damageMult = 1;
            forPreview = false;
        }
    }

    public void onWithdraw() {
        damageMult = 1 + (CombatManager.summons.damageBonus / 100f);
        forPreview = false;
    }

    @Override
    public void performActions(boolean manual) {
        if (card != null) {
            refreshAction();
            final PCLUseInfo info = CombatManager.playerSystem.generateInfo(card, this, target);
            PCLActions.bottom.add(new PCLCreatureAttackAnimationAction(this, !manual));
            card.useEffectsWithoutPowers(info);
            PCLActions.delayed.callback(() -> CombatManager.removeDamagePowers(this));
            CombatManager.playerSystem.onCardPlayed(card, info, true);
            applyTurnPowers();
            CombatManager.onAllyTrigger(this.card, this.target, this);
        }
    }

    public PCLCard releaseCard(boolean clearPowers) {
        PCLCard releasedCard = this.card;
        if (releasedCard != null) {
            if (clearPowers) {
                for (AbstractPower po : powers) {
                    po.onRemove();
                }
                this.powers.clear();
            }
            releasedCard.owner = null;
            this.name = creatureData.strings.NAME;
            this.hideHealthBar();
            this.animation = emptyAnimation;
            this.card = null;
            return releasedCard;
        }
        return null;
    }

    @Override
    public void render(SpriteBatch sb) {
        if (this.animation != null) {
            super.render(sb);
        }
        if (isHovered()) {
            renderTip(sb);
            if (card.pclTarget == PCLCardTarget.AllEnemy) {
                for (AbstractMonster mo : GameUtilities.getEnemies(true)) {
                    PCLRenderHelpers.drawCurve(sb, ImageMaster.TARGET_UI_ARROW, Color.SCARLET, this.hb, mo.hb, EUIBase.scale(100), 0.25f, 0.02f, 20);
                }
            }
            else if (card.pclTarget.targetsSingle() && target != null) {
                PCLRenderHelpers.drawCurve(sb, ImageMaster.TARGET_UI_ARROW, Color.SCARLET, this.hb, target.hb, EUIBase.scale(100), 0.25f, 0.02f, 20);

                AbstractCard card = preview != null ? preview.getCard() : null;
                if (card != null) {
                    BitmapFont font = FontHelper.cardDescFont_N;
                    font.getData().setScale(card.drawScale * 0.9f);
                    EUIRenderHelpers.drawOnCardAuto(sb, preview.getCard(), EUIRM.images.rectangularButton.texture(), 0, -AbstractCard.RAW_H * 0.65f,
                            AbstractCard.IMG_WIDTH * 0.6f, font.getLineHeight() * 1.8f, Color.DARK_GRAY, 0.75f, 1);
                    EUITextHelper.writeOnCard(sb, card, font, PGR.core.strings.combat_rightClickRetarget, 0, -AbstractCard.RAW_H * 0.65f, Color.ORANGE);
                    EUITextHelper.resetFont(font);
                }
            }
        }
    }

    public void renderAnimation(SpriteBatch sb, Color color) {
        super.renderAnimation(sb, color);
        if (card != null) {
            float actualTransparency = card.transparency;
            card.transparency = hbAlpha;
            card.setPosition(this.hb.cX, this.hb.y + scale(60f) + getBobEffect().y * -0.5f);
            card.setDrawScale(0.2f);
            card.updateGlow();
            card.renderGlowManual(sb, !hasTakenTurn ? 2.3f : 1f);
            card.renderOuterGlow(sb);
            card.renderImage(sb, false, true);
            card.transparency = actualTransparency;
        }
    }

    @Override
    protected void renderDamageRange(SpriteBatch sb) {
        if (stunned) {
            super.renderIntent(sb);
        }
        else if (card != null) {
            float startY = this.intentHb.cY + getBobEffect().y - 12.0F * Settings.scale;
            for (PSkill<?> skill : card.getEffects()) {
                PSkill<?> cur = skill;
                while (cur != null) {
                    startY = cur.renderIntentIcon(sb, this, startY, forPreview);
                    cur = cur.getChild();
                }
            }
        }
    }

    public boolean shouldDim() {
        return hasTakenTurn && (!isHovered() || AbstractDungeon.player.hoveredCard == null || AbstractDungeon.player.hoveredCard.type != PCLEnum.CardType.SUMMON);
    }

    @Override
    public boolean shouldShowIntents() {
        return !this.isDying && !this.isEscaping && AbstractDungeon.getCurrRoom().phase == AbstractRoom.RoomPhase.COMBAT && !AbstractDungeon.player.isDead && !Settings.hideCombatElements;
    }

    public void tryTarget() {
        if (card.pclTarget.targetsSingle()) {
            PCLActions.bottom.selectCreature(card).addCallback(t -> {
                if (t != null) {
                    setTarget(t);
                }
            });
        }
    }

    @Override
    public void update() {
        super.update();
        if (card != null) {
            this.card.currentHealth = this.currentHealth;
            if (this.animation instanceof PCLAllyAnimation) {
                ((PCLAllyAnimation) this.animation).update(EUI.delta(), hb.cX, hb.cY);
            }
            if (AbstractDungeon.screen != AbstractDungeon.CurrentScreen.DEATH) {
                hb.update();
                intentHb.update();
                healthHb.update();
                for (int i = 0; i < powers.size(); i++) {
                    powers.get(i).update(i);
                }
                if ((hb.hovered || intentHb.hovered)
                        && EUIInputManager.rightClick.isJustPressed()
                        && !(AbstractDungeon.player.isDraggingCard || AbstractDungeon.player.inSingleTargetMode)) {
                    tryTarget();
                }
            }
        }
    }

    @Override
    protected void updateFastAttackAnimation() {
        this.animationTimer -= Gdx.graphics.getDeltaTime();

        if (this.animationTimer > 0.5F) {
            this.animX = Interpolation.pow3In.apply(0.0F, 60.0F * Settings.scale, (1.0F - this.animationTimer) * 2.0F);
        }
        else if (this.animationTimer < 0.0F) {
            this.animationTimer = 0.0F;
            this.animX = 0.0F;
        }
        else {
            this.animX = Interpolation.fade.apply(0.0F, 60.0F * Settings.scale, this.animationTimer * 2.0F);
        }

    }
}
