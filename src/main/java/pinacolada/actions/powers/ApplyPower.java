package pinacolada.actions.powers;

import com.megacrit.cardcrawl.actions.common.ApplyPowerAction;
import com.megacrit.cardcrawl.actions.utility.TextAboveCreatureAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.CardGroup;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.powers.*;
import com.megacrit.cardcrawl.relics.*;
import com.megacrit.cardcrawl.unlock.UnlockTracker;
import com.megacrit.cardcrawl.vfx.combat.FlashAtkImgEffect;
import com.megacrit.cardcrawl.vfx.combat.PowerBuffEffect;
import com.megacrit.cardcrawl.vfx.combat.PowerDebuffEffect;
import extendedui.EUIUtils;
import pinacolada.actions.PCLActionWithCallback;
import pinacolada.actions.PCLActions;
import pinacolada.effects.PCLEffects;
import pinacolada.misc.CombatManager;
import pinacolada.patches.actions.ApplyPowerActionPatches;
import pinacolada.powers.PCLPower;
import pinacolada.powers.common.ResistancePower;
import pinacolada.utilities.GameUtilities;

import java.util.Collections;

// Copied and modified from STS-AnimatorMod
public class ApplyPower extends PCLActionWithCallback<AbstractPower>
{
    public static final String[] TEXT = ApplyPowerAction.TEXT;

    public AbstractPower callbackResult;
    public AbstractPower powerToApply;
    public boolean chooseRandomTarget;
    public boolean allowDuplicates;
    public boolean ignoreArtifact;
    public boolean showEffect = true;
    public boolean skipIfZero = false;
    public boolean skipIfNull = true;
    public boolean allowNegative = true;
    public boolean canStack = true;
    public boolean faster;

    public ApplyPower(AbstractCreature source, AbstractCreature target, AbstractPower power)
    {
        this(source, target, power, power.amount);
    }

    public ApplyPower(AbstractCreature source, AbstractCreature target, AbstractPower power, int amount)
    {
        this(source, target, power, amount, false, AttackEffect.NONE);
    }

    public ApplyPower(AbstractCreature source, AbstractCreature target, AbstractPower power, int amount, AttackEffect effect)
    {
        this(source, target, power, amount, false, effect);
    }

    public ApplyPower(AbstractCreature source, AbstractCreature target, AbstractPower power, int amount, boolean isFast)
    {
        this(source, target, power, amount, isFast, AttackEffect.NONE);
    }

    public ApplyPower(AbstractCreature source, AbstractCreature target, AbstractPower power, int amount, boolean isFast, AttackEffect effect)
    {
        super(ActionType.POWER, Settings.FAST_MODE ? Settings.ACTION_DUR_XFAST : Settings.ACTION_DUR_FASTER);

        powerToApply = ApplyPowerActionPatches.getReplacement(power, target, source);
        attackEffect = effect;
        faster = isFast;

        initialize(source, target, amount);

        hardcodedstuffSneckoskull();

        if (AbstractDungeon.getMonsters().areMonstersBasicallyDead())
        {
            complete();
            return;
        }

        if (powerToApply.ID.equals(CorruptionPower.POWER_ID))
        {
            hardcodedstuffCorruption(player.hand);
            hardcodedstuffCorruption(player.drawPile);
            hardcodedstuffCorruption(player.discardPile);
            hardcodedstuffCorruption(player.exhaustPile);
        }
    }

    public static boolean canGoNegative(AbstractPower power)
    {
        return StrengthPower.POWER_ID.equals(power.ID) || DexterityPower.POWER_ID.equals(power.ID) || FocusPower.POWER_ID.equals(power.ID) || ResistancePower.POWER_ID.equals(power.ID);
    }

    private void addPower()
    {
        callbackResult = powerToApply;
        target.powers.add(powerToApply);

        Collections.sort(target.powers);

        powerToApply.onInitialApplication();

        if (showEffect)
        {
            powerToApply.flash();

            if (amount <= 0 && powerToApply.canGoNegative)
            {
                PCLEffects.List.add(new PowerDebuffEffect(target.hb.cX - target.animX,
                        target.hb.cY + target.hb.height / 2f, powerToApply.name + TEXT[3]));
            }
            else if (powerToApply.type == AbstractPower.PowerType.BUFF)
            {
                PCLEffects.List.add(new PowerBuffEffect(target.hb.cX - target.animX, target.hb.cY + target.hb.height / 2f, powerToApply.name));
            }
            else
            {
                PCLEffects.List.add(new PowerDebuffEffect(target.hb.cX - target.animX, target.hb.cY + target.hb.height / 2f, powerToApply.name));
            }
        }

        CombatManager.onApplyPower(source, target, powerToApply);
        AbstractDungeon.onModifyPower();
        if (target.isPlayer)
        {
            int buffCount = 0;

            for (AbstractPower pw : target.powers)
            {
                if (pw.type == AbstractPower.PowerType.BUFF)
                {
                    buffCount += 1;
                }
            }

            if (buffCount >= 10)
            {
                UnlockTracker.unlockAchievement("POWERFUL");
            }
        }
    }

    public ApplyPower allowDuplicates(boolean allowDuplicates)
    {
        this.allowDuplicates = allowDuplicates;

        return this;
    }

    public ApplyPower canStack(boolean canStack)
    {
        this.canStack = canStack;

        return this;
    }

    public ApplyPower chooseRandomTarget(boolean value)
    {
        this.chooseRandomTarget = value;

        return this;
    }

    @Override
    protected void complete(AbstractPower result)
    {
        if (!skipIfNull || (result != null && result.owner != null))
        {
            super.complete(result);
        }
    }

    @Override
    protected void firstUpdate()
    {
        if (chooseRandomTarget)
        {
            target = GameUtilities.getRandomEnemy(true);
            powerToApply.owner = target;
        }

        if (amount == 0 && skipIfZero)
        {
            complete();
            return;
        }

        if (shouldCancelAction() || hardcodedstuffNodraw() || !GameUtilities.canApplyPower(source, target, powerToApply, this))
        {
            complete(callbackResult);
            return;
        }

        if (source != null)
        {
            for (AbstractPower power : source.powers)
            {
                power.onApplyPower(powerToApply, target, source);
            }

            hardcodedstuffChampionbelt();
        }

        if (target.isPlayer && hardcodedstuffTurnipandgingercheck())
        {
            tickDuration();
            return;
        }

        if (powerToApply instanceof PCLPower && !((PCLPower) powerToApply).enabled)
        {
            tickDuration();
            return;
        }

        AbstractPower existingPower = null;
        for (AbstractPower power : target.powers)
        {
            if (power.ID.equals(powerToApply.ID))
            {
                if (canStack)
                {
                    existingPower = power;
                    break;
                }
                else if (!allowDuplicates)
                {
                    return;
                }
            }
        }

        if (powerToApply.type == AbstractPower.PowerType.DEBUFF)
        {
            if (hardcodedstuffArtifact())
            {
                tickDuration();
                return;
            }
            else if (showEffect)
            {
                target.useFastShakeAnimation(0.5f);
            }
        }

        if (showEffect)
        {
            PCLEffects.List.add(new FlashAtkImgEffect(target.hb.cX, target.hb.cY, attackEffect));
        }


        if (existingPower != null)
        {
            stackPower(existingPower);
        }
        else if (!allowNegative && amount < 0 && !powerToApply.canGoNegative) {
            complete();
        }
        else
        {
            addPower();
        }
    }

    @Override
    protected void updateInternal(float deltaTime)
    {
        if (tickDuration(deltaTime))
        {
            complete(callbackResult);
        }
    }

    private boolean hardcodedstuffArtifact()
    {
        if (ignoreArtifact)
        {
            return false;
        }

        AbstractPower artifact = target.getPower(ArtifactPower.POWER_ID);
        if (artifact != null)
        {
            PCLActions.top.add(new TextAboveCreatureAction(target, TEXT[0]));

            CardCrawlGame.sound.play("NULLIFY_SFX");
            artifact.flashWithoutSound();
            artifact.onSpecificTrigger();

            return true;
        }

        return false;
    }

    private void hardcodedstuffChampionbelt()
    {
        if (source.isPlayer && target != source && powerToApply.ID.equals(VulnerablePower.POWER_ID) && !target.hasPower(ArtifactPower.POWER_ID))
        {
            AbstractRelic belt = player.getRelic(ChampionsBelt.ID);
            if (belt != null)
            {
                belt.onTrigger(target);
            }
        }
    }

    private void hardcodedstuffCorruption(CardGroup group)
    {
        for (AbstractCard c : group.group)
        {
            if (c.type == AbstractCard.CardType.SKILL)
            {
                c.modifyCostForCombat(-99);
            }
        }
    }

    private boolean hardcodedstuffNodraw()
    {
        return powerToApply instanceof NoDrawPower && target.hasPower(powerToApply.ID);
    }

    private void hardcodedstuffSneckoskull()
    {
        if (source != null && source.isPlayer && target != source && player.hasRelic(SneckoSkull.ID) && powerToApply.ID.equals(PoisonPower.POWER_ID))
        {
            player.getRelic(SneckoSkull.ID).flash();
            powerToApply.amount += 1;
            amount += 1;
        }
    }

    private boolean hardcodedstuffTurnipandgingercheck()
    {
        if (powerToApply.ID.equals(WeakPower.POWER_ID))
        {
            AbstractRelic relic = player.getRelic(Ginger.ID);
            if (relic != null)
            {
                PCLActions.top.add(new TextAboveCreatureAction(target, TEXT[1]));
                relic.flash();

                return true;
            }
        }
        else if (powerToApply.ID.equals(FrailPower.POWER_ID))
        {
            AbstractRelic relic = player.getRelic(Turnip.ID);
            if (relic != null)
            {
                PCLActions.top.add(new TextAboveCreatureAction(target, TEXT[1]));
                relic.flash();

                return true;
            }
        }

        return false;
    }

    public ApplyPower ignoreArtifact(boolean ignoreArtifact)
    {
        this.ignoreArtifact = ignoreArtifact;

        return this;
    }

    public ApplyPower showEffect(boolean showEffect, boolean isFast)
    {
        this.showEffect = showEffect;
        this.faster = isFast;

        return this;
    }

    public ApplyPower allowNegative(boolean skipIfNegative)
    {
        this.allowNegative = skipIfNegative;

        return this;
    }

    public ApplyPower skipIfNull(boolean skipIfNull)
    {
        this.skipIfNull = skipIfNull;

        return this;
    }

    public ApplyPower skipIfZero(boolean skipIfZero)
    {
        this.skipIfZero = skipIfZero;

        return this;
    }

    private void stackPower(AbstractPower power)
    {
        callbackResult = power;
        if (allowNegative || amount >= 0 || power.canGoNegative) {
            power.stackPower(amount);
        }
        else {
            power.reducePower(-amount);
            final PCLPower p = EUIUtils.safeCast(power, PCLPower.class);
            if (power.amount == 0 && (p == null || !p.canBeZero))
            {
                PCLActions.top.removePower(source, target, power);
            }
        }


        if (showEffect)
        {
            power.flash();

            if (amount <= 0 && power.canGoNegative)
            {
                PCLEffects.List.add(new PowerDebuffEffect(target.hb.cX - target.animX,
                        target.hb.cY + target.hb.height / 2f, powerToApply.name + TEXT[3]));
            }
            else if (amount > 0)
            {
                if (power.type != AbstractPower.PowerType.BUFF && !power.canGoNegative)
                {
                    PCLEffects.List.add(new PowerDebuffEffect(target.hb.cX - target.animX,
                            target.hb.cY + target.hb.height / 2f, "+" + amount + " " + powerToApply.name));
                }
                else
                {
                    PCLEffects.List.add(new PowerBuffEffect(target.hb.cX - target.animX,
                            target.hb.cY + target.hb.height / 2f, "+" + amount + " " + powerToApply.name));
                }
            }
            else if (power.type == AbstractPower.PowerType.BUFF)
            {
                PCLEffects.List.add(new PowerBuffEffect(target.hb.cX - target.animX,
                        target.hb.cY + target.hb.height / 2f, powerToApply.name + TEXT[3]));
            }
            else
            {
                PCLEffects.List.add(new PowerDebuffEffect(target.hb.cX - target.animX,
                        target.hb.cY + target.hb.height / 2f, powerToApply.name + TEXT[3]));
            }
        }

        power.updateDescription();
        CombatManager.onApplyPower(source, target, powerToApply);
        AbstractDungeon.onModifyPower();
    }
}