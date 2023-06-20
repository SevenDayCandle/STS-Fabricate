package pinacolada.monsters;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.MathUtils;
import com.evacipated.cardcrawl.mod.stslib.patches.core.AbstractCreature.TempHPField;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.powers.AbstractPower;
import extendedui.EUI;
import extendedui.EUIUtils;
import extendedui.ui.tooltips.EUICardPreview;
import pinacolada.actions.PCLActions;
import pinacolada.actions.special.PCLCreatureAttackAnimationAction;
import pinacolada.cards.base.PCLCard;
import pinacolada.dungeon.CombatManager;
import pinacolada.dungeon.PCLUseInfo;
import pinacolada.interfaces.markers.SummonOnlyMove;
import pinacolada.monsters.animations.PCLAllyAnimation;
import pinacolada.powers.PSkillPower;
import pinacolada.powers.PSpecialCardPower;
import pinacolada.skills.PSkill;
import pinacolada.skills.Skills;
import pinacolada.skills.delay.DelayTiming;
import pinacolada.utilities.GameUtilities;

import static pinacolada.utilities.GameUtilities.scale;

public abstract class PCLCardCreature extends PCLSkillCreature {
    protected EUICardPreview preview;
    public PCLCard card;
    public AbstractCreature target;
    public DelayTiming priority;

    public PCLCardCreature(PCLCreatureData data, float xPos, float yPos) {
        super(data, xPos, yPos);
    }

    @Override
    public void atEndOfRound() {
        super.atEndOfRound();
        for (AbstractPower p : powers) {
            p.atEndOfRound();
        }
    }

    @Override
    public void performActions(boolean manual) {
        if (card != null) {
            refreshAction();
            final PCLUseInfo info = CombatManager.playerSystem.generateInfo(card, this, target);
            PCLActions.bottom.add(new PCLCreatureAttackAnimationAction(this, !manual));
            card.useEffectsWithoutPowers(info);
            PCLActions.delayed.callback(() -> CombatManager.removeDamagePowers(this));
            applyTurnPowers();
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
        }
    }

    // Unused
    @Override
    protected void getMove(int i) {

    }

    @Override
    public void applyPowers() {
        refreshAction();
    }

    public EUICardPreview getPreview() {
        if (preview != null) {
            AbstractCard c = preview.getCard();
            if (c != null) {
                c.current_x = c.target_x = this.hb.x + (AbstractCard.IMG_WIDTH * 0.9F + 16.0F) * (this.hb.x > (float) Settings.WIDTH * 0.7F ? card.drawScale : -card.drawScale);
                c.current_y = c.target_y = this.hb.y + scale(60f);
                c.hb.move(c.current_x, c.current_y);
            }
        }
        return preview;
    }

    @Override
    public Skills getSkills() {
        return card != null ? card.skills : null;
    }

    public boolean hasCard() {
        return card != null;
    }

    public void initializeForCard(PCLCard card, boolean clearPowers, boolean delayForTurn) {
        card.owner = this;
        this.card = card;
        this.preview = new EUICardPreview(card, false);
        this.name = card.name;
        this.maxHealth = Math.max(1, card.heal);
        this.currentHealth = MathUtils.clamp(card.currentHealth, 1, this.maxHealth);
        this.priority = card.timing;
        this.hasTakenTurn = delayForTurn;
        this.showHealthBar();
        this.healthBarUpdatedEvent();
        this.unhover();
        card.stopFlash();

        if (clearPowers) {
            for (AbstractPower po : powers) {
                po.onRemove();
            }
            this.powers.clear();
            this.currentBlock = 0;
            TempHPField.tempHp.set(this, 0);
        }
        else {
            for (AbstractPower p : powers) {
                if (p instanceof PSkillPower || p instanceof PSpecialCardPower) {
                    p.onRemove();
                }
            }
            this.powers.removeIf(p -> p instanceof PSkillPower || p instanceof PSpecialCardPower);
        }

        for (PSkill<?> s : card.getFullEffects()) {
            if (s instanceof SummonOnlyMove) {
                s.use(CombatManager.playerSystem.generateInfo(card, this, this), PCLActions.bottom);
            }
        }

        refreshAction();
    }

    public boolean isHovered() {
        return card != null && (hb.hovered || intentHb.hovered);
    }

    public void refreshAction() {
        if (card != null) {
            acquireTarget();
            // TODO base intent on card moves
            if (stunned) {
                this.setMove(card.name, (byte) -1, Intent.STUN);
            }
            else {
                this.setMove(card.name, (byte) -1, Intent.ATTACK, card.damage, card.hitCount, card.hitCount > 1);
            }
        }
    }

    public void acquireTarget() {
        if (card.pclTarget.targetsRandom()) {
            target = GameUtilities.getRandomEnemy(true);
        }
        if (target == null || GameUtilities.isDeadOrEscaped(target)) {
            target = EUIUtils.findMin(GameUtilities.getEnemies(true), e -> e.currentHealth);
        }
        this.card.calculateCardDamage(GameUtilities.asMonster(target));
    }

    @Override
    public void render(SpriteBatch sb) {
        if (this.animation != null) {
            super.render(sb);
        }
        if (isHovered()) {
            renderTip(sb);
        }
    }

    public void renderName(SpriteBatch sb) {
        if (hasCard()) {
            super.renderName(sb);
        }
    }

    public void renderHealth(SpriteBatch sb) {
        if (hasCard()) {
            super.renderHealth(sb);
        }
    }

    public void setTarget(AbstractCreature target) {
        if (!GameUtilities.isDeadOrEscaped(target)) {
            this.target = target;
            refreshAction();
        }
    }
}
