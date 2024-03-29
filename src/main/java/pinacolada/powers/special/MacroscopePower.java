package pinacolada.powers.special;

import com.evacipated.cardcrawl.mod.stslib.patches.NeutralPowertypePatch;
import com.evacipated.cardcrawl.mod.stslib.powers.interfaces.InvisiblePower;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.orbs.AbstractOrb;
import pinacolada.dungeon.CombatManager;
import pinacolada.interfaces.subscribers.OnOrbApplyFocusSubscriber;
import pinacolada.powers.PCLPower;
import pinacolada.powers.PCLPowerData;
import pinacolada.relics.pcl.Macroscope;
import pinacolada.resources.PGR;
import pinacolada.utilities.GameUtilities;

public class MacroscopePower extends PCLPower implements InvisiblePower, OnOrbApplyFocusSubscriber {
    public static final PCLPowerData DATA = new PCLPowerData(MacroscopePower.class, PGR.core)
            .setEndTurnBehavior(PCLPowerData.Behavior.Special)
            .setType(NeutralPowertypePatch.NEUTRAL)
            .setLimits(-1, -1); // Do not register

    public MacroscopePower(AbstractCreature owner) {
        super(DATA, owner, owner, -1);

        owner.maxHealth = owner.maxHealth * Macroscope.MULTIPLIER;
        owner.currentHealth = owner.currentHealth * Macroscope.MULTIPLIER;
        owner.healthBarUpdatedEvent();
        if (owner instanceof AbstractPlayer) {
            CombatManager.subscribe(this);
            PGR.dungeon.setDivisor(Macroscope.MULTIPLIER);
        }
    }

    @Override
    public float atDamageFinalGive(float damage, DamageInfo.DamageType type) {
        return damage * Macroscope.MULTIPLIER;
    }

    // Purposely not using ModifyOrbOutput because this will cause Magic attacks to get multiplied twice
    public int getFinalOrbDamage(int initial) {
        return initial * Macroscope.MULTIPLIER;
    }

    @Override
    public String getUpdatedDescription() {
        return formatDescription(0, Macroscope.MULTIPLIER);
    }

    @Override
    public float modifyBlockLast(float block) {
        return block * Macroscope.MULTIPLIER;
    }

    @Override
    public void onApplyFocus(AbstractOrb orb) {
        if (GameUtilities.canOrbApplyFocus(orb)) {
            orb.passiveAmount = getFinalOrbDamage(orb.passiveAmount);
            if (GameUtilities.canOrbApplyFocusToEvoke(orb)) {
                orb.evokeAmount = getFinalOrbDamage(orb.evokeAmount);
            }
        }
    }

    @Override
    public int onHeal(int amount) {
        return amount * Macroscope.MULTIPLIER;
    }

    @Override
    public void onRemove() {
        super.onRemove();
        if (PGR.dungeon.getDivisor() > 1) {
            owner.maxHealth = Math.max(1, AbstractDungeon.player.maxHealth / Macroscope.MULTIPLIER);
            owner.currentHealth = Math.max(1, AbstractDungeon.player.currentHealth / Macroscope.MULTIPLIER);
            owner.healthBarUpdatedEvent();
            if (owner instanceof AbstractPlayer) {
                PGR.dungeon.setDivisor(1);
            }
        }
    }
}