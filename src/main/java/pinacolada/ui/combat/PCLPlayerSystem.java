package pinacolada.ui.combat;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.MathUtils;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.orbs.AbstractOrb;
import extendedui.EUIUtils;
import extendedui.ui.EUIBase;
import pinacolada.cards.base.AffinityReactions;
import pinacolada.cards.base.PCLAffinity;
import pinacolada.cards.base.PCLCard;
import pinacolada.cards.base.PCLUseInfo;
import pinacolada.interfaces.subscribers.OnMatchCheckSubscriber;
import pinacolada.misc.CombatManager;
import pinacolada.powers.PCLAffinityPower;
import pinacolada.powers.common.PCLLockOnPower;
import pinacolada.utilities.GameUtilities;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;

import static com.megacrit.cardcrawl.dungeons.AbstractDungeon.player;

public class PCLPlayerSystem extends EUIBase
{
    protected final LinkedHashMap<AbstractPlayer.PlayerClass, PCLPlayerMeter> meters = new LinkedHashMap<>();
    public final PCLEmptyMeter fakeMeter = new PCLEmptyMeter();

    protected PCLAffinity currentAffinitySynergy = null;
    protected AbstractCard currentSynergy = null;
    protected PCLCard lastCardPlayed = null;

    public PCLPlayerSystem()
    {
    }

    public void addLevel(PCLAffinity affinity, int amount)
    {
        getActiveMeter().addLevel(affinity, amount);
    }

    public void addSkip(int amount)
    {
        getActiveMeter().addSkip(amount);
    }

    public float applyScaling(PCLAffinity affinity, PCLCard card, float base)
    {
        if (affinity == PCLAffinity.Star)
        {
            for (PCLAffinity p : PCLAffinity.extended())
            {
                base = applyScaling(p, card, base);
            }

            return base;
        }

        PCLAffinityPower power = getPower(affinity);
        return power != null ? (base + MathUtils.ceil(card.affinities.getLevel(affinity, true) * power.getEffectiveScaling())) : base;  // Decider Aura scaling is added onto level scaling
    }

    public void flash(int target)
    {
        getActiveMeter().flash(target);
    }

    public void flashAffinity(PCLAffinity target)
    {
        getActiveMeter().flashAffinity(target);
    }

    public void registerMeter(AbstractPlayer.PlayerClass playerClass, PCLPlayerMeter meter)
    {
        meters.put(playerClass, meter);
    }

    public PCLPlayerMeter getActiveMeter()
    {
        if (player != null)
        {
            return meters.getOrDefault(player.chosenClass, fakeMeter);
        }
        return fakeMeter;
    }

    public ArrayList<? extends PCLAffinityPower> getActivePowers()
    {
        return getActiveMeter().getActivePowers();
    }

    public PCLAffinity getAffinity(int index)
    {
        return getActiveMeter().get(index);
    }

    public PCLAffinity getCurrentAffinity()
    {
        return getActiveMeter().getCurrentAffinity();
    }

    public int getLastAffinityLevel(PCLAffinity affinity)
    {
        return lastCardPlayed == null ? 0 : lastCardPlayed.affinities.getLevel(affinity);
    }

    public PCLAffinity getLastAffinitySynergy()
    {
        return currentSynergy != null ? currentAffinitySynergy : null;
    }

    public PCLCard getLastCardPlayed()
    {
        return lastCardPlayed;
    }

    public int getLevel(PCLAffinity affinity)
    {
        return getActiveMeter().getLevel(affinity);
    }

    public Collection<PCLPlayerMeter> getMeters() {return meters.values();}

    public PCLAffinityPower getPower(PCLAffinity affinity)
    {
        return getActiveMeter().getPower(affinity);
    }

    public int getPowerAmount(PCLAffinity affinity)
    {
        return getActiveMeter().getPowerAmount(affinity);
    }

    public List<? extends PCLAffinityPower> getPowers()
    {
        return getActiveMeter().getPowers();
    }

    public AffinityReactions getReactions(AbstractCard c, Collection<? extends AbstractCreature> mo)
    {
        return getActiveMeter().getReactions(c, mo);
    }

    public Object getRerollDescription()
    {
        return getActiveMeter().getRerollDescription();
    }

    public Object getRerollDescription2()
    {
        return getActiveMeter().getRerollDescription2();
    }

    public void initialize()
    {

        EUIUtils.logInfoIfDebug(this, "Initialized PCL Affinity System.");

        for (PCLPlayerMeter meter : getMeters())
        {
            meter.initialize();
        }
    }

    public boolean isPowerActive(PCLAffinity affinity)
    {
        return getActiveMeter().isPowerActive(affinity);
    }

    public boolean isMatch(AbstractCard card)
    {
        return card != null && currentSynergy != null && currentSynergy.uuid == card.uuid;
    }

    public float modifyBlock(float block, PCLCard source, PCLCard card, AbstractCreature target)
    {
        if (card.baseBlock > 0)
        {
            for (PCLAffinity p : PCLAffinity.extended())
            {
                float oldBlock = block;
                block = applyScaling(p, source, block);
                card.addDefendDisplay(p, oldBlock, block);
            }

            for (PCLPlayerMeter meter : getMeters())
            {
                block = meter.modifyBlock(block, source, card, target);
            }
        }

        return block;
    }

    public float modifyDamage(float damage, PCLCard source, PCLCard card, AbstractCreature target)
    {
        if (card.baseDamage > 0)
        {
            for (PCLAffinity p : PCLAffinity.extended())
            {
                float oldDamage = damage;
                damage = applyScaling(p, source, damage);
                card.addAttackDisplay(p, oldDamage, damage);
            }

            for (PCLPlayerMeter meter : getMeters())
            {
                damage = meter.modifyDamage(damage, source, card, target);
            }
        }

        return damage;
    }

    public float modifyMagicNumber(float magicNumber, PCLCard source, PCLCard card)
    {
        if (card.baseMagicNumber > 0)
        {
            for (PCLAffinity p : PCLAffinity.extended())
            {
                magicNumber = applyScaling(p, source, magicNumber);
            }

            for (PCLPlayerMeter meter : getMeters())
            {
                magicNumber = meter.modifyMagicNumber(magicNumber, source, card);
            }
        }
        return magicNumber;
    }

    public int modifyOrbOutput(float initial, AbstractCreature target, AbstractOrb orb)
    {
        if (GameUtilities.getPowerAmount(target, com.megacrit.cardcrawl.powers.LockOnPower.POWER_ID) > 0)
        {
            initial *= PCLLockOnPower.getOrbMultiplier();
        }

        for (PCLPlayerMeter meter : getMeters())
        {
            initial = meter.modifyOrbOutput(initial, target, orb);
        }

        return (int) initial;
    }

    public void onCardPlayed(PCLCard card, AbstractCreature m, PCLUseInfo info, boolean fromSummon)
    {
        getActiveMeter().onCardPlayed(card, m, info, fromSummon);
    }

    public void onEndOfTurn()
    {
        setLastCardPlayed(null);
        getActiveMeter().onEndOfTurn();
    }

    public void onMatch(PCLCard card)
    {
        getActiveMeter().onMatch(card);
    }

    public void onNotMatch(PCLCard card)
    {
        getActiveMeter().onNotMatch(card);
    }

    public void onStartOfTurn()
    {
        getActiveMeter().onStartOfTurn();
    }

    public void setLastCardPlayed(AbstractCard card)
    {
        lastCardPlayed = EUIUtils.safeCast(card, PCLCard.class);
        currentSynergy = null;
        currentAffinitySynergy = null;
    }

    public boolean trySynergize(AbstractCard card)
    {
        if (wouldMatch(card))
        {
            currentSynergy = card;
            currentAffinitySynergy = getCurrentAffinity();
            return true;
        }

        currentSynergy = null;
        currentAffinitySynergy = null;
        return false;
    }

    @Override
    public void updateImpl()
    {
        getActiveMeter().update(null, null, false);
    }

    public void update(PCLCard card, AbstractCreature target, boolean draggingCard)
    {
        getActiveMeter().update(card, target, draggingCard);
    }

    public boolean tryUpdate(PCLCard card, AbstractCreature target, boolean draggingCard) {
        if (this.isActive) {
            this.update(card, target, draggingCard);
        }
        return this.isActive;
    }


    public void renderImpl(SpriteBatch sb)
    {
        if (player == null || player.hand == null || AbstractDungeon.overlayMenu.energyPanel.isHidden)
        {
            return;
        }

        getActiveMeter().renderImpl(sb);
    }

    public boolean wouldMatch(AbstractCard card)
    {
        for (OnMatchCheckSubscriber s : CombatManager.onMatchCheck.getSubscribers())
        {
            if (s.onMatchCheck(card, null))
            {
                return true;
            }
        }

        final PCLCard a = EUIUtils.safeCast(card, PCLCard.class);
        if (a != null)
        {
            return getActiveMeter().hasMatch(a);
        }
        return false;
    }
}