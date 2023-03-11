package pinacolada.monsters;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.MathUtils;
import com.evacipated.cardcrawl.mod.stslib.patches.core.AbstractCreature.TempHPField;
import com.megacrit.cardcrawl.actions.animations.AnimateFastAttackAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.helpers.FontHelper;
import com.megacrit.cardcrawl.helpers.ImageMaster;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.powers.AbstractPower;
import com.megacrit.cardcrawl.relics.AbstractRelic;
import com.megacrit.cardcrawl.vfx.ExhaustBlurEffect;
import extendedui.EUI;
import extendedui.EUIInputManager;
import extendedui.interfaces.delegates.FuncT1;
import extendedui.ui.EUIBase;
import extendedui.ui.tooltips.EUICardPreview;
import pinacolada.actions.PCLActions;
import pinacolada.cards.base.PCLCard;
import pinacolada.cards.base.fields.PCLCardTarget;
import pinacolada.effects.PCLEffects;
import pinacolada.effects.SFX;
import pinacolada.interfaces.markers.SummonOnlyMove;
import pinacolada.misc.CombatManager;
import pinacolada.misc.PCLUseInfo;
import pinacolada.monsters.animations.PCLAllyAnimation;
import pinacolada.monsters.animations.PCLAnimation;
import pinacolada.monsters.animations.PCLSlotAnimation;
import pinacolada.monsters.animations.pcl.PCLGeneralAllyAnimation;
import pinacolada.powers.PSkillPower;
import pinacolada.resources.PGR;
import pinacolada.skills.PSkill;
import pinacolada.skills.Skills;
import pinacolada.utilities.GameUtilities;
import pinacolada.utilities.PCLRenderHelpers;

import java.util.HashMap;

import static pinacolada.utilities.GameUtilities.scale;

public class PCLCardAlly extends PCLCreature
{
    protected static final HashMap<AbstractCard.CardColor, FuncT1<PCLAllyAnimation, PCLCardAlly>> ANIMATION_MAP = new HashMap<>();
    public static final PCLCreatureData DATA = register(PCLCardAlly.class).setHb(0,0, 128, 128);
    public static PCLSlotAnimation emptyAnimation = new PCLSlotAnimation();

    protected EUICardPreview preview;
    public PCLCard card;
    public AbstractCreature target;

    public static void registerAnimation(AbstractCard.CardColor color, FuncT1<PCLAllyAnimation, PCLCardAlly> animationFunc)
    {
        ANIMATION_MAP.putIfAbsent(color, animationFunc);
    }

    public PCLCardAlly(float xPos, float yPos)
    {
        super(DATA, xPos, yPos);
        this.animation = emptyAnimation;
    }

    public void initializeForCard(PCLCard card, boolean clearPowers, boolean stun)
    {
        card.owner = this;
        this.card = card;
        this.preview = new EUICardPreview(card, card.upgraded);
        this.name = card.name;
        this.maxHealth = Math.max(1, card.baseHeal);
        this.currentHealth = MathUtils.clamp(card.heal, 1, this.maxHealth);
        this.priority = card.magicNumber;
        this.showHealthBar();
        this.healthBarUpdatedEvent();
        this.unhover();

        FuncT1<PCLAllyAnimation, PCLCardAlly> animFunc = ANIMATION_MAP.get(card.color);
        if (animFunc != null)
        {
            this.animation = animFunc.invoke(this);
        }
        if (this.animation == null)
        {
            this.animation = new PCLGeneralAllyAnimation(this);
        }

        if (clearPowers)
        {
            this.powers.clear();
            this.currentBlock = 0;
            TempHPField.tempHp.set(this, 0);
        }
        else
        {
            this.powers.removeIf(p -> p instanceof PSkillPower);
        }
        if (stun)
        {
            this.stunned = true;
        }
        for (PSkill<?> s : card.getFullEffects())
        {
            if (s instanceof SummonOnlyMove)
            {
                s.use(CombatManager.playerSystem.generateInfo(card, this, this));
            }
        }

        refreshAction();
    }

    public boolean hasCard()
    {
        return card != null;
    }

    public PCLCard releaseCard()
    {
        PCLCard releasedCard = this.card;
        if (releasedCard != null)
        {
            releasedCard.owner = null;
            this.powers.clear();
            this.name = creatureData.strings.NAME;
            this.hideHealthBar();
            this.animation = emptyAnimation;
            this.card = null;
            return releasedCard;
        }
        return null;
    }

    public void refreshAction()
    {
        if (card != null)
        {
            if (target == null || GameUtilities.isDeadOrEscaped(target))
            {
                target = GameUtilities.getRandomEnemy(true);
            }
            this.card.calculateCardDamage(GameUtilities.asMonster(target));
            // TODO base intent on card moves
            if (stunned)
            {
                this.setMove(card.name, (byte) -1, Intent.STUN);
            }
            else
            {
                this.setMove(card.name, (byte) -1, Intent.ATTACK, card.damage, card.hitCount, card.hitCount > 1);
            }
        }
    }

    public void renderName(SpriteBatch sb) {
        if (hasCard())
        {
            super.renderName(sb);
        }
    }

    public void renderHealth(SpriteBatch sb) {
        if (hasCard())
        {
            super.renderHealth(sb);
        }
    }

    @Override
    public void applyPowers() {
        refreshAction();
    }

    @Override
    public Skills getSkills()
    {
        return card != null ? card.skills : null;
    }

    @Override
    public void performActions()
    {
        if (card != null)
        {
            refreshAction();
            final PCLUseInfo info = CombatManager.playerSystem.generateInfo(card, this, target);
            if (animation instanceof PCLAnimation)
            {
                PCLActions.bottom.callback(() -> {
                    ((PCLAnimation) animation).playActAnimation(hb.cX, hb.cY);
                });
            }
            PCLActions.bottom.add(new AnimateFastAttackAction(this));
            card.useEffectsWithoutPowers(info);
            CombatManager.playerSystem.onCardPlayed(card, info, true);
            PCLActions.delayed.callback(() -> CombatManager.removeDamagePowers(this));
            applyTurnPowers();
            CombatManager.onAllyTrigger(this.card, this);
        }
    }

    // Unused
    @Override
    protected void getMove(int i)
    {

    }

    @Override
    public void die()
    {
        die(true);
    }

    @Override
    public void die(boolean triggerRelics) {
        PCLCard releasedCard = releaseCard();
        if (releasedCard != null)
        {
            PCLEffects.Queue.callback(() -> {
                SFX.play(SFX.CARD_EXHAUST, 0.2F);
                for(int i = 0; i < 140; ++i) {
                    AbstractDungeon.effectsQueue.add(new ExhaustBlurEffect(this.hb.cX, this.hb.cY));
                }
            });

            for (AbstractPower po : powers)
            {
                po.onDeath();
            }
            this.powers.clear();

            if (triggerRelics) {
                for (AbstractRelic relic : AbstractDungeon.player.relics)
                {
                    relic.onMonsterDeath(this);
                }
            }
            CombatManager.onAllyDeath(releasedCard, this);

            // Heal on summons should be at least 1
            if (releasedCard.heal < 1)
            {
                releasedCard.heal = 1;
            }

            // Killed summons are treated as being Purged
            PCLActions.bottom.purge(releasedCard).showEffect(false, false);
        }

        // Health needs to be 1 so that the slot can be re-selected
        this.currentHealth = 1;
    }

    public EUICardPreview getPreview()
    {
        if (preview != null)
        {
            AbstractCard c = preview.getCard();
            if (c != null)
            {
                c.current_x = c.target_x = this.hb.x + (AbstractCard.IMG_WIDTH * 0.9F + 16.0F) * (this.hb.x > (float)Settings.WIDTH * 0.7F ? card.drawScale : -card.drawScale);
                c.current_y = c.target_y = this.hb.y + scale(60f);
                c.hb.move(c.current_x, c.current_y);
            }
        }
        return preview;
    }

    public void update()
    {
        super.update();
        if (card != null)
        {
            this.card.heal = this.currentHealth;
            if (this.animation instanceof PCLAllyAnimation)
            {
                ((PCLAllyAnimation) this.animation).update(EUI.delta(), hb.cX, hb.cY);
            }
            if (AbstractDungeon.screen != AbstractDungeon.CurrentScreen.DEATH)
            {
                hb.update();
                intentHb.update();
                healthHb.update();
                for (int i = 0; i < powers.size(); i++)
                {
                    powers.get(i).update(i);
                }
                if ((hb.hovered || intentHb.hovered)
                        && EUIInputManager.rightClick.isJustPressed()
                        && !(AbstractDungeon.player.isDraggingCard || AbstractDungeon.player.inSingleTargetMode))
                {
                    tryTarget();
                }
            }
        }
    }

    @Override
    public void render(SpriteBatch sb)
    {
        if (this.animation != null)
        {
            super.render(sb);
        }
        if (isHovered())
        {
            renderTip(sb);
            if (card.pclTarget == PCLCardTarget.AllEnemy)
            {
                for (AbstractMonster mo : GameUtilities.getEnemies(true))
                {
                    PCLRenderHelpers.drawCurve(sb, ImageMaster.TARGET_UI_ARROW, Color.SCARLET.cpy(), this.hb, mo.hb, EUIBase.scale(100), 0.25f, 0.02f, 20);
                }
            }
            else if (card.pclTarget.targetsSingle() && target != null)
            {
                PCLRenderHelpers.drawCurve(sb, ImageMaster.TARGET_UI_ARROW, Color.SCARLET.cpy(), this.hb, target.hb, EUIBase.scale(100), 0.25f, 0.02f, 20);
            }
        }
    }

    @Override
    protected void renderIntent(SpriteBatch sb)
    {
        if (stunned)
        {
            super.renderIntent(sb);
        }
        else if (card != null)
        {
            card.setPosition(this.hb.cX, this.hb.y + scale(60f) + getBobEffect().y * -0.5f);
            card.setDrawScale(0.2f);
            card.updateGlow(3f);
            card.renderGlow(sb);
            card.renderOuterGlow(sb);
            card.renderImage(sb, false, true);
        }
    }

    @Override
    protected void renderDamageRange(SpriteBatch sb)
    {
        if (stunned)
        {
            super.renderIntent(sb);
        }
        else if (card != null)
        {
            float startY = this.intentHb.cY + getBobEffect().y  - 12.0F * Settings.scale;
            if (card.onAttackEffect != null)
            {
                startY = renderIntentIcon(sb, card.attackType.getTooltip().icon, card.hitCount > 1 ? card.damage + "x" + card.hitCount : Integer.toString(card.damage), startY);
            }
            if (card.onBlockEffect != null)
            {
                startY = renderIntentIcon(sb, PGR.core.tooltips.block.icon, card.rightCount > 1 ? card.block + "x" + card.rightCount : Integer.toString(card.block), startY);
            }
        }
    }

    protected float renderIntentIcon(SpriteBatch sb, TextureRegion icon, String count, float startY)
    {
        PCLRenderHelpers.drawCentered(sb, Color.WHITE, icon, this.intentHb.cX - 40.0F * Settings.scale, startY, icon.getRegionWidth(), icon.getRegionHeight(), 0.9f, 0f);
        FontHelper.renderFontLeftTopAligned(sb, FontHelper.topPanelInfoFont, count, this.intentHb.cX, startY, Settings.CREAM_COLOR);
        return startY + icon.getRegionHeight() + Settings.scale * 10f;
    }

    public boolean isHovered()
    {
        return card != null && (hb.hovered || intentHb.hovered);
    }

    public void tryTarget()
    {
        if (card.pclTarget.targetsSingle())
        {
            PCLActions.bottom.selectCreature(card).addCallback(t -> {
                if (t != null)
                {
                    setTarget(t);
                }
            });
        }
    }

    public void setTarget(AbstractCreature target)
    {
        if (!GameUtilities.isDeadOrEscaped(target))
        {
            this.target = target;
            refreshAction();
        }
    }
}
