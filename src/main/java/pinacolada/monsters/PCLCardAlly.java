package pinacolada.monsters;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.helpers.FontHelper;
import com.megacrit.cardcrawl.helpers.ImageMaster;
import com.megacrit.cardcrawl.vfx.BobEffect;
import com.megacrit.cardcrawl.vfx.ExhaustBlurEffect;
import extendedui.ui.EUIBase;
import extendedui.ui.tooltips.EUICardPreview;
import pinacolada.actions.PCLActions;
import pinacolada.cards.base.PCLCard;
import pinacolada.cards.base.PCLUseInfo;
import pinacolada.effects.PCLEffects;
import pinacolada.effects.SFX;
import pinacolada.misc.CombatManager;
import pinacolada.monsters.animations.PCLSlotAnimation;
import pinacolada.monsters.animations.conjurer.ConjurerFireAllyAnimation;
import pinacolada.skills.Skills;
import pinacolada.utilities.GameUtilities;
import pinacolada.utilities.PCLRenderHelpers;

public class PCLCardAlly extends PCLCreature
{
    public static final PCLCreatureData DATA = register(PCLCardAlly.class).setHb(0,0, 128, 128);
    public static PCLSlotAnimation emptyAnimation = new PCLSlotAnimation();


    protected EUICardPreview preview;
    public PCLCard card;
    public AbstractCreature target;

    public PCLCardAlly(float xPos, float yPos)
    {
        super(DATA, xPos, yPos);
        this.animation = emptyAnimation;
    }

    public void initializeForCard(PCLCard card, boolean clearPowers, boolean stun)
    {
        if (clearPowers)
        {
            this.powers.clear();
        }
        if (stun)
        {
            this.stunned = true;
        }
        card.owner = this;
        this.card = card;
        this.preview = new EUICardPreview(card, card.upgraded);
        this.name = card.name;
        this.maxHealth = card.baseHeal;
        this.currentHealth = Math.min(card.heal, this.maxHealth);
        this.priority = card.magicNumber;
        this.halfDead = false;
        this.showHealthBar();
        this.healthBarUpdatedEvent();
        this.unhover();

        this.animation = new ConjurerFireAllyAnimation(this);
    }

    public boolean hasCard()
    {
        return card != null;
    }

    public PCLCard releaseCard()
    {
        if (card != null)
        {
            this.card.owner = null;
            this.powers.clear();
            this.card.heal = this.currentHealth;
            this.halfDead = true;
            this.name = creatureData.strings.NAME;
            this.hideHealthBar();
            this.animation = emptyAnimation;
            return this.card;
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
            if (this.card.isAoE()) {
                this.card.calculateCardDamage(null);
            }
            else
            {
                this.card.refresh(GameUtilities.asMonster(target));
            }
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
        super.applyPowers();
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
            final PCLUseInfo info = new PCLUseInfo(card, this, target);
            card.onPreUse(info);
            card.onUse(info);
            card.onLateUse(info);
            card.triggerWhenTriggered(this);
        }
    }

    // Unused
    @Override
    protected void getMove(int i)
    {

    }

    public void die() {
        PCLEffects.Queue.callback(() -> {
            SFX.play(SFX.CARD_EXHAUST, 0.2F);
            for(int i = 0; i < 140; ++i) {
                AbstractDungeon.effectsQueue.add(new ExhaustBlurEffect(this.hb.cX, this.hb.cY));
            }
        });

        CombatManager.onAllyDeath(card, this);
        card.triggerWhenKilled(this);
        releaseCard();
    }

    public EUICardPreview getPreview()
    {
        return preview;
    }

    public void update()
    {
        super.update();
        if (hb.clicked && card != null)
        {
            PCLActions.bottom.selectCreature(card).addCallback(t -> {
               if (t != null)
               {
                   target = t;
               }
            });
        }
    }

    @Override
    public void render(SpriteBatch sb)
    {
        super.render(sb);
        if (target != null && (hb.hovered || intentHb.hovered))
        {
            PCLRenderHelpers.drawCurve(sb, ImageMaster.TARGET_UI_ARROW, Color.SCARLET.cpy(), this.hb, target.hb, EUIBase.scale(100), 20);
        }
    }

    @Override
    protected void renderIntent(SpriteBatch sb)
    {
        if (card != null)
        {
            card.setPosition(this.intentHb.cX, this.intentHb.cY + 96.0F + getBobEffect().y);
            card.setDrawScale(0.2f);
            card.render(sb);
        }
    }

    @Override
    protected void renderDamageRange(SpriteBatch sb)
    {
        if (card != null)
        {
            BobEffect bobEffect = getBobEffect();
            PCLRenderHelpers.drawCentered(sb, Color.WHITE, card.attackType.getTooltip().icon, this.intentHb.cX - 40.0F * Settings.scale, this.intentHb.cY + bobEffect.y - 12.0F * Settings.scale, card.attackType.getTooltip().icon.getRegionWidth(), card.attackType.getTooltip().icon.getRegionHeight(), 0.9f, 0f);
            if (card.hitCount > 1) {
                FontHelper.renderFontLeftTopAligned(sb, FontHelper.topPanelInfoFont, Integer.toString(card.damage) + "x" + Integer.toString(card.hitCount), this.intentHb.cX, this.intentHb.cY + bobEffect.y - 12.0F * Settings.scale, Settings.CREAM_COLOR);
            } else {
                FontHelper.renderFontLeftTopAligned(sb, FontHelper.topPanelInfoFont, Integer.toString(card.damage), this.intentHb.cX, this.intentHb.cY + bobEffect.y - 12.0F * Settings.scale, Settings.CREAM_COLOR);
            }
        }
    }

    public void setTarget(AbstractCreature target)
    {
        if (!GameUtilities.isDeadOrEscaped(target))
        {
            this.target = target;
        }
    }
}
