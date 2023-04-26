package pinacolada.ui.combat;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.orbs.AbstractOrb;
import extendedui.EUIUtils;
import extendedui.ui.EUIBase;
import pinacolada.cards.base.PCLCard;
import pinacolada.cards.base.fields.PCLAffinity;
import pinacolada.dungeon.PCLUseInfo;
import pinacolada.powers.replacement.PCLLockOnPower;
import pinacolada.utilities.GameUtilities;

import java.util.Collection;
import java.util.LinkedHashMap;

import static com.megacrit.cardcrawl.dungeons.AbstractDungeon.player;

public class PCLPlayerSystem extends EUIBase {
    public final PCLEmptyMeter fakeMeter = new PCLEmptyMeter();
    protected final LinkedHashMap<AbstractPlayer.PlayerClass, PCLPlayerMeter> meters = new LinkedHashMap<>();
    protected PCLCard lastCardPlayed = null;

    public PCLPlayerSystem() {
    }

    public void addLevel(PCLAffinity affinity, int amount) {
        getActiveMeter().addLevel(affinity, amount);
    }

    public PCLPlayerMeter getActiveMeter() {
        if (player != null) {
            return meters.getOrDefault(player.chosenClass, fakeMeter);
        }
        return fakeMeter;
    }

    public void addSkip(int amount) {
        getActiveMeter().addSkip(amount);
    }

    public void flash(int target) {
        getActiveMeter().flash(target);
    }

    public void flashAffinity(PCLAffinity target) {
        getActiveMeter().flashAffinity(target);
    }

    public PCLUseInfo generateInfo(AbstractCard card, AbstractCreature source, AbstractCreature target) {
        return getActiveMeter().generateInfo(card, source, target);
    }

    public PCLAffinity getAffinity(int index) {
        return getActiveMeter().get(index);
    }

    public PCLAffinity getCurrentAffinity() {
        return getActiveMeter().getCurrentAffinity();
    }

    public Color getGlowColor(AbstractCard c) {
        return getActiveMeter().canGlow(c) ? PCLCard.SYNERGY_GLOW_COLOR : PCLCard.REGULAR_GLOW_COLOR;
    }

    public PCLCard getLastCardPlayed() {
        return lastCardPlayed;
    }

    public void setLastCardPlayed(AbstractCard card) {
        lastCardPlayed = EUIUtils.safeCast(card, PCLCard.class);
    }

    public int getLevel(PCLAffinity affinity) {
        return getActiveMeter().getLevel(affinity);
    }

    public PCLPlayerMeter getMeter(AbstractPlayer.PlayerClass playerClass) {
        return meters.get(playerClass);
    }

    public Object getRerollDescription() {
        return getActiveMeter().getRerollDescription();
    }

    public Object getRerollDescription2() {
        return getActiveMeter().getRerollDescription2();
    }

    public void initialize() {

        EUIUtils.logInfoIfDebug(this, "Initialized PCL Affinity System.");

        for (PCLPlayerMeter meter : getMeters()) {
            meter.initialize();
        }
    }

    public Collection<PCLPlayerMeter> getMeters() {
        return meters.values();
    }

    public float modifyBlock(float block, PCLCard source, PCLCard card, AbstractCreature target) {
        if (card.baseBlock > 0) {
            for (PCLAffinity p : PCLAffinity.basic()) {
                card.addDefendDisplay(p, block, block);
            }

            for (PCLPlayerMeter meter : getMeters()) {
                block = meter.modifyBlock(block, source, card, target);
            }
        }

        return block;
    }

    public float modifyDamage(float damage, PCLCard source, PCLCard card, AbstractCreature target) {
        if (card.baseDamage > 0) {
            for (PCLAffinity p : PCLAffinity.basic()) {
                card.addAttackDisplay(p, damage, damage);
            }

            for (PCLPlayerMeter meter : getMeters()) {
                damage = meter.modifyDamage(damage, source, card, target);
            }
        }

        return damage;
    }

    public int modifyOrbOutput(float initial, AbstractCreature target, AbstractOrb orb) {
        if (GameUtilities.getPowerAmount(target, com.megacrit.cardcrawl.powers.LockOnPower.POWER_ID) > 0) {
            initial *= PCLLockOnPower.getOrbMultiplier();
        }

        for (PCLPlayerMeter meter : getMeters()) {
            initial = meter.modifyOrbOutput(initial, target, orb);
        }

        return (int) initial;
    }

    public void onCardPlayed(PCLCard card, PCLUseInfo info, boolean fromSummon) {
        getActiveMeter().onCardPlayed(card, info, fromSummon);
    }

    public void onEndOfTurn() {
        setLastCardPlayed(null);
        getActiveMeter().onEndOfTurn();
    }

    public void onStartOfTurn() {
        getActiveMeter().onStartOfTurn();
    }

    public void registerMeter(AbstractPlayer.PlayerClass playerClass, PCLPlayerMeter meter) {
        meters.put(playerClass, meter);
    }

    public void renderImpl(SpriteBatch sb) {
        if (player == null || player.hand == null || AbstractDungeon.overlayMenu.energyPanel.isHidden) {
            return;
        }

        getActiveMeter().renderImpl(sb);
    }

    @Override
    public void updateImpl() {
        getActiveMeter().update(null, null, false);
    }

    public boolean tryUpdate(PCLCard card, AbstractCreature target, boolean draggingCard) {
        if (this.isActive) {
            this.update(card, target, draggingCard);
        }
        return this.isActive;
    }

    public void update(PCLCard card, AbstractCreature target, boolean draggingCard) {
        getActiveMeter().update(card, target, draggingCard);
    }
}