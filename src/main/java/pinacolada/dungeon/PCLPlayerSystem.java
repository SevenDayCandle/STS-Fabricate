package pinacolada.dungeon;

import basemod.abstracts.AbstractCardModifier;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.orbs.AbstractOrb;
import extendedui.EUIGameUtils;
import extendedui.EUIUtils;
import extendedui.ui.EUIBase;
import pinacolada.cards.base.PCLCard;
import pinacolada.cards.base.fields.PCLAffinity;
import pinacolada.powers.replacement.PCLLockOnPower;
import pinacolada.utilities.GameUtilities;

import java.util.*;

import static com.megacrit.cardcrawl.dungeons.AbstractDungeon.player;

public class PCLPlayerSystem extends EUIBase {
    private final LinkedHashMap<AbstractPlayer.PlayerClass, PCLPlayerMeter> playerMeters = new LinkedHashMap<>();
    private final ArrayList<PCLPlayerMeter> activePlayerMeters = new ArrayList<>();
    private final ArrayList<PCLMiscMeter> miscMeters = new ArrayList<>();
    protected PCLUseInfo info;

    public PCLPlayerSystem() {
    }

    public void flash(int target) {
        PCLPlayerMeter active = getActiveMeter();
        if (active != null) {
            active.flash(target);
        }
    }

    public void flashAffinity(PCLAffinity target) {
        PCLPlayerMeter active = getActiveMeter();
        if (active != null) {
            active.flashAffinity(target);
        }
    }

    /* Creates a NEW info object. To be used when executing infos in effects to ensure that data is not interfered with during the action execution process */
    public PCLUseInfo generateInfo(AbstractCard card, AbstractCreature source, AbstractCreature target) {
        PCLUseInfo newInfo = new PCLUseInfo(card, source, target);
        for (PCLPlayerMeter meter : playerMeters.values()) {
            meter.setupInfo(newInfo);
        }
        return newInfo;
    }

    public PCLPlayerMeter getActiveMeter() {
        if (player != null) {
            return playerMeters.getOrDefault(player.chosenClass, null);
        }
        return null;
    }

    public Collection<PCLPlayerMeter> getActiveMeters() {
        return activePlayerMeters;
    }

    public PCLAffinity getAffinity(int index) {
        PCLPlayerMeter active = getActiveMeter();
        return active != null ? active.get(index) : PCLAffinity.General;
    }

    public Color getGlowColor(AbstractCard c) {
        for (PCLPlayerMeter meter : getActiveMeters()) {
            if (meter.canGlow(c)) {
                return PCLCard.SYNERGY_GLOW_COLOR;
            }
        }
        return PCLCard.REGULAR_GLOW_COLOR;
    }

    /* Updates a CACHED info object. To be used in updating calls to avoid memory churn */
    public PCLUseInfo getInfo(AbstractCard card, AbstractCreature source, AbstractCreature target) {
        if (info == null) {
            info = generateInfo(card, source, target);
        }
        else {
            info.set(card, source, target);
            for (PCLPlayerMeter meter : playerMeters.values()) {
                meter.setupInfo(info);
            }
        }
        return info;
    }

    public PCLPlayerMeter getMeter(AbstractPlayer.PlayerClass playerClass) {
        return playerMeters.get(playerClass);
    }

    public void initialize() {
        activePlayerMeters.clear();
        // Meter should be factored in if it corresponds with the active player, or if the player has a card of that color
        PCLPlayerMeter active = getActiveMeter();
        HashSet<AbstractPlayer.PlayerClass> playerClasses = player != null ? EUIUtils.mapAsSet(player.masterDeck.group,
                c -> c instanceof PCLCard ? ((PCLCard) c).cardData.resources.playerClass : EUIGameUtils.getPlayerClassForCardColor(c.color)) :
                new HashSet<>();
        playerClasses.remove(null); // Ignore cards not corresponding to a player class
        for (Map.Entry<AbstractPlayer.PlayerClass, PCLPlayerMeter> entry : playerMeters.entrySet()) {
            PCLPlayerMeter meter = entry.getValue();
            meter.initialize();
            if (meter == active || playerClasses.contains(entry.getKey())) {
                activePlayerMeters.add(meter);
            }
        }
        for (PCLMiscMeter meter : miscMeters) {
            meter.initialize();
        }

        EUIUtils.logInfoIfDebug(this, "Initialized PCL Affinity System.");
    }

    public float modifyBlock(float block, PCLCard source, PCLCard card, AbstractCreature target) {
        for (PCLAffinity p : PCLAffinity.basic()) {
            card.addDefendDisplay(p, block, block);
        }

        for (PCLPlayerMeter meter : getActiveMeters()) {
            block = meter.modifyBlock(block, source, card, target);
        }

        return block;
    }

    public float modifyDamage(float damage, PCLCard source, PCLCard card, AbstractCreature target) {
        for (PCLAffinity p : PCLAffinity.basic()) {
            card.addAttackDisplay(p, damage, damage);
        }

        for (PCLPlayerMeter meter : getActiveMeters()) {
            damage = meter.modifyDamage(damage, source, card, target);
        }

        return damage;
    }

    public int modifyOrbOutput(float initial, AbstractCreature target, AbstractOrb orb) {
        if (GameUtilities.getPowerAmount(target, com.megacrit.cardcrawl.powers.LockOnPower.POWER_ID) > 0) {
            initial *= PCLLockOnPower.getOrbMultiplier(target.isPlayer);
        }

        for (PCLPlayerMeter meter : getActiveMeters()) {
            initial = meter.modifyOrbOutput(initial, target, orb);
        }

        return (int) initial;
    }

    public void onCardCreated(AbstractCard card, boolean startOfBattle) {
        for (PCLPlayerMeter meter : getActiveMeters()) {
            meter.onCardCreated(card, startOfBattle);
        }
    }

    public AbstractCardModifier onCardModified(AbstractCard card, AbstractCardModifier modifier) {
        for (PCLPlayerMeter meter : getActiveMeters()) {
            modifier = meter.onCardModified(card, modifier);
        }
        return modifier;
    }

    public void onCardPlayed(PCLCard card, PCLUseInfo info, boolean fromSummon) {
        for (PCLPlayerMeter meter : getActiveMeters()) {
            meter.onCardPlayed(card, info, fromSummon);
        }
    }

    public void onEndOfTurn() {
        for (PCLPlayerMeter meter : getActiveMeters()) {
            meter.onEndOfTurn();
        }
        for (PCLMiscMeter meter : miscMeters) {
            meter.onEndOfTurn();
        }
    }

    public void onStartOfTurn() {
        for (PCLPlayerMeter meter : getActiveMeters()) {
            meter.onStartOfTurn();
        }
        for (PCLMiscMeter meter : miscMeters) {
            meter.onStartOfTurn();
        }
    }

    public void registerMeter(AbstractPlayer.PlayerClass playerClass, PCLPlayerMeter meter) {
        playerMeters.put(playerClass, meter);
    }

    public void registerMisc(PCLMiscMeter meter) {
        miscMeters.add(meter);
    }

    public void renderImpl(SpriteBatch sb) {
        if (player == null || player.hand == null || AbstractDungeon.overlayMenu.energyPanel.isHidden) {
            return;
        }

        for (PCLPlayerMeter meter : getActiveMeters()) {
            meter.renderImpl(sb);
        }
        for (PCLMiscMeter meter : miscMeters) {
            meter.renderImpl(sb);
        }
    }

    public boolean tryUpdate(PCLCard card, AbstractCreature target, boolean draggingCard) {
        if (this.isActive) {
            this.update(card, card, target, target, draggingCard);
        }
        return this.isActive;
    }

    public void update(PCLCard hoveredCard, PCLCard originalCard, AbstractCreature target, AbstractCreature originalTarget, boolean draggingCard) {
        for (PCLPlayerMeter meter : getActiveMeters()) {
            meter.update(hoveredCard, originalCard, target, originalTarget, draggingCard);
        }
        for (PCLMiscMeter meter : miscMeters) {
            meter.updateImpl();
        }
    }

    // Unused
    @Override
    public void updateImpl() {
    }
}