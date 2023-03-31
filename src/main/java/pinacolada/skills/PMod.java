package pinacolada.skills;

import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import extendedui.utilities.ColoredString;
import pinacolada.cards.base.PCLCardGroupHelper;
import pinacolada.cards.base.fields.PCLAffinity;
import pinacolada.cards.base.fields.PCLCardTarget;
import pinacolada.interfaces.markers.PMultiBase;
import pinacolada.misc.PCLUseInfo;
import pinacolada.orbs.PCLOrbHelper;
import pinacolada.powers.PCLPowerHelper;
import pinacolada.resources.pcl.PCLCoreStrings;
import pinacolada.skills.fields.PField;
import pinacolada.skills.fields.PField_CardGeneric;
import pinacolada.skills.skills.base.modifiers.*;
import pinacolada.stances.PCLStanceHelper;

public abstract class PMod<T extends PField> extends PSkill<T>
{
    public static final int MODIFIER_PRIORITY = 3;
    public int cachedValue;

    public static <T extends PField> PSkillData<T> register(Class<? extends PSkill<T>> type, Class<T> effectType, AbstractCard.CardColor... cardColors)
    {
        return PSkill.register(type, effectType, -1, DEFAULT_MAX, cardColors)
                .setExtra(-1, DEFAULT_MAX);
    }

    public static <T extends PField> PSkillData<T> register(Class<? extends PSkill<T>> type, Class<T> effectType)
    {
        return PSkill.register(type, effectType, -1, DEFAULT_MAX)
                .setExtra(-1, DEFAULT_MAX);
    }

    public static PMod_BonusInStance bonusInStance(int amount, PCLStanceHelper s)
    {
        return new PMod_BonusInStance(amount, s);
    }

    public static PMod_BonusOnHasDiscarded bonusOnDiscarded(int amount)
    {
        return new PMod_BonusOnHasDiscarded(amount);
    }

    public static PMod_BonusOnHasDiscarded bonusOnDiscarded(int amount, int count)
    {
        return new PMod_BonusOnHasDiscarded(amount, count);
    }

    public static PMod_BonusOnHasExhausted bonusOnExhausted(int amount)
    {
        return new PMod_BonusOnHasExhausted(amount);
    }

    public static PMod_BonusOnHasExhausted bonusOnExhausted(int amount, int count)
    {
        return new PMod_BonusOnHasExhausted(amount, count);
    }

    public static PMod_BonusOnStarter bonusOnStarter(int amount)
    {
        return new PMod_BonusOnStarter(amount);
    }

    public static PMod_BonusPerAffinityLevel bonusPerLevel(int amount, PCLAffinity... aff)
    {
        return new PMod_BonusPerAffinityLevel(amount, aff);
    }

    public static PMod_BonusPerOrb bonusPerOrb(int amount, PCLOrbHelper... aff)
    {
        return new PMod_BonusPerOrb(amount, aff);
    }

    public static PMod_CyclePerCard cyclePer(int amount)
    {
        return new PMod_CyclePerCard(amount);
    }

    public static PMod_CyclePerCard cycleRandomPer(int amount)
    {
        return (PMod_CyclePerCard) new PMod_CyclePerCard(amount).edit(PField_CardGeneric::setRandom);
    }

    public static PMod_DiscardPerCard discardPer(int amount)
    {
        return new PMod_DiscardPerCard(amount, PCLCardGroupHelper.Hand);
    }

    public static PMod_DiscardPerCard discardPer(int amount, PCLCardGroupHelper... groups)
    {
        return new PMod_DiscardPerCard(amount, groups);
    }

    public static PMod_DiscardPerCard discardRandomPer(int amount, PCLCardGroupHelper... groups)
    {
        return (PMod_DiscardPerCard) new PMod_DiscardPerCard(amount, groups).edit(PField_CardGeneric::setRandom);
    }

    public static PMod_DrawPerCard drawPer(int amount)
    {
        return new PMod_DrawPerCard(amount);
    }

    public static PMod_ExhaustPerCard exhaustPer(int amount)
    {
        return new PMod_ExhaustPerCard(amount, PCLCardGroupHelper.Hand);
    }

    public static PMod_ExhaustPerCard exhaustPer(int amount, PCLCardGroupHelper... groups)
    {
        return new PMod_ExhaustPerCard(amount, groups);
    }

    public static PMod_ExhaustPerCard exhaustRandomPer(int amount, PCLCardGroupHelper... groups)
    {
        return (PMod_ExhaustPerCard) new PMod_ExhaustPerCard(amount, groups).edit(PField_CardGeneric::setRandom);
    }

    public static PMod_EvokePerOrb evokePerOrb(int amount, PCLOrbHelper... aff)
    {
        return new PMod_EvokePerOrb(amount, aff);
    }

    public static PMod_FetchPerCard fetchPer(int amount, PCLCardGroupHelper... groups)
    {
        return new PMod_FetchPerCard(amount, groups);
    }

    public static PMod_FetchPerCard fetchRandomPer(int amount, PCLCardGroupHelper... groups)
    {
        return (PMod_FetchPerCard) new PMod_FetchPerCard(amount, groups).edit(PField_CardGeneric::setRandom);
    }

    public static PMod_IncreaseOnUse increaseOnUse(int amount)
    {
        return new PMod_IncreaseOnUse(amount);
    }

    public static PMod_PerCard perCard(PCLCardGroupHelper... groups)
    {
        return perCard(1, groups);
    }

    public static PMod_PerCard perCard(int amount, PCLCardGroupHelper... groups)
    {
        return new PMod_PerCard(amount, groups);
    }

    public static PMod_PerCardDiscarded perCardDiscarded(int amount)
    {
        return new PMod_PerCardDiscarded(amount);
    }

    public static PMod_PerCardDiscarded perCardDiscardedCombat(int amount)
    {
        return (PMod_PerCardDiscarded) new PMod_PerCardDiscarded(amount).edit(f -> f.setForced(true));
    }

    public static PMod_PerCardExhausted perCardExhausted(int amount)
    {
        return new PMod_PerCardExhausted(amount);
    }

    public static PMod_PerCardExhausted perCardExhaustedCombat(int amount)
    {
        return (PMod_PerCardExhausted) new PMod_PerCardExhausted(amount).edit(f -> f.setForced(true));
    }

    public static PMod_PerCardPlayed perCardPlayed(int amount)
    {
        return new PMod_PerCardPlayed(amount);
    }

    public static PMod_PerCardPlayed perCardPlayedCombat(int amount)
    {
        return (PMod_PerCardPlayed) new PMod_PerCardPlayed(amount).edit(f -> f.setForced(true));
    }

    public static PMod_PerCreature perCreature(int amount)
    {
        return new PMod_PerCreature(amount);
    }

    public static PMod_PerCreature perCreature(PCLCardTarget target, int amount)
    {
        return new PMod_PerCreature(target, amount);
    }

    public static PMod_PerCreatureAttacking perCreatureAttacking(int amount)
    {
        return new PMod_PerCreatureAttacking(amount);
    }

    public static PMod_PerCreatureBlock perCreatureBlock(int amount)
    {
        return new PMod_PerCreatureBlock(amount);
    }

    public static PMod_PerCreatureBlock perCreatureBlock(PCLCardTarget target, int amount)
    {
        return new PMod_PerCreatureBlock(target, amount);
    }

    public static PMod_PerCreatureWith perCreatureWith(int amount, PCLPowerHelper... powers)
    {
        return new PMod_PerCreatureWith(amount, powers);
    }

    public static PMod_PerDistinctPower perDistinctPower(PCLPowerHelper... powers)
    {
        return perDistinctPower(1, powers);
    }

    public static PMod_PerDistinctPower perDistinctPower(int amount, PCLPowerHelper... powers)
    {
        return new PMod_PerDistinctPower(amount, powers);
    }

    public static PMod_PerDistinctPower perDistinctPower(PCLCardTarget target, int amount, PCLPowerHelper... powers)
    {
        return new PMod_PerDistinctPower(target, amount, powers);
    }

    public static PMod_PerEnergy perEnergy(int amount)
    {
        return new PMod_PerEnergy(amount);
    }

    public static PMod_PerAffinityLevel perLevel(int amount, PCLAffinity... aff)
    {
        return new PMod_PerAffinityLevel(amount, aff);
    }

    public static PMod_PerOrb perOrb(PCLOrbHelper... orbs)
    {
        return perOrb(1, orbs);
    }

    public static PMod_PerOrb perOrb(int amount, PCLOrbHelper... orbs)
    {
        return new PMod_PerOrb(amount, orbs);
    }

    public static PMod_PerPower perPower(PCLPowerHelper... powers)
    {
        return perPower(1, powers);
    }

    public static PMod_PerPower perPower(int amount, PCLPowerHelper... powers)
    {
        return new PMod_PerPower(amount, powers);
    }

    public static PMod_PerPower perPowerAny(PCLPowerHelper... powers)
    {
        return perPowerAny(1, powers);
    }

    public static PMod_PerPower perPowerAny(int amount, PCLPowerHelper... powers)
    {
        return (PMod_PerPower) new PMod_PerPower(amount, powers).setTarget(PCLCardTarget.Any);
    }

    public static PMod_PerPower perPowerAoe(PCLPowerHelper... powers)
    {
        return perPowerAoe(1, powers);
    }

    public static PMod_PerPower perPowerAoe(int amount, PCLPowerHelper... powers)
    {
        return (PMod_PerPower) new PMod_PerPower(amount, powers).setTarget(PCLCardTarget.AllEnemy);
    }

    public static PMod_PerPower perPowerSelf(PCLPowerHelper... powers)
    {
        return perPowerSelf(1, powers);
    }

    public static PMod_PerPower perPowerSelf(int amount, PCLPowerHelper... powers)
    {
        return (PMod_PerPower) new PMod_PerPower(amount, powers).setTarget(PCLCardTarget.Self);
    }

    public static PMod_PerPower perPowerSingle(PCLPowerHelper... powers)
    {
        return perPowerSingle(1, powers);
    }

    public static PMod_PerPower perPowerSingle(int amount, PCLPowerHelper... powers)
    {
        return (PMod_PerPower) new PMod_PerPower(amount, powers).setTarget(PCLCardTarget.Single);
    }

    public static PMod_PurgePerCard purgePer(int amount)
    {
        return new PMod_PurgePerCard(amount, PCLCardGroupHelper.Hand);
    }

    public static PMod_PurgePerCard purgePer(int amount, PCLCardGroupHelper... groups)
    {
        return new PMod_PurgePerCard(amount, groups);
    }

    public static PMod_PurgePerCard purgeRandomPer(int amount, PCLCardGroupHelper... groups)
    {
        return (PMod_PurgePerCard) new PMod_PurgePerCard(amount, groups).edit(PField_CardGeneric::setRandom);
    }

    public static PMod_ReshufflePerCard reshufflePer(int amount, PCLCardGroupHelper... groups)
    {
        return new PMod_ReshufflePerCard(amount, groups);
    }

    public static PMod_ReshufflePerCard reshuffleRandomPer(int amount, PCLCardGroupHelper... groups)
    {
        return (PMod_ReshufflePerCard) new PMod_ReshufflePerCard(amount, groups).edit(PField_CardGeneric::setRandom);
    }

    public static PMod_ScoutPerCard scoutPer(int amount)
    {
        return new PMod_ScoutPerCard(amount);
    }

    public static PMod_ScryPerCard scryPer(int amount)
    {
        return new PMod_ScryPerCard(amount);
    }

    public static PMod_SelectPerCard selectPer(int amount)
    {
        return new PMod_SelectPerCard(amount);
    }

    public static PMod_SelectPerCard selectPer(int amount, PCLCardGroupHelper... groups)
    {
        return new PMod_SelectPerCard(amount, groups);
    }

    public static PMod_XEnergy xEnergy()
    {
        return new PMod_XEnergy();
    }

    public static PMod_XEnergy xEnergy(int value)
    {
        return new PMod_XEnergy(value);
    }

    public PMod(PSkillData<T> data, PSkillSaveData content)
    {
        super(data, content);
    }

    public PMod(PSkillData<T> data)
    {
        super(data);
    }

    public PMod(PSkillData<T> data, PCLCardTarget target, int amount)
    {
        super(data, target, amount);
    }

    public PMod(PSkillData<T> data, PCLCardTarget target, int amount, int extra)
    {
        super(data, target, amount, extra);
    }

    public int getModifiedAmount(PSkill<?> be, PCLUseInfo info)
    {
        return 0;
    }

    @Override
    public int getXValue(AbstractCard card)
    {
        return cachedValue;
    }

    @Override
    public void onDrag(AbstractMonster m)
    {
        updateChildAmount(makeInfo(m));
    }

    @Override
    public void refresh(PCLUseInfo info, boolean conditionMet)
    {
        updateChildAmount(info);
    }

    public ColoredString getColoredValueString()
    {
        if (baseAmount != amount)
        {
            return new ColoredString(amount, amount >= baseAmount ? Settings.GREEN_TEXT_COLOR : Settings.RED_TEXT_COLOR);
        }

        return new ColoredString(amount, Settings.CREAM_COLOR);
    }

    @Override
    public String getText(boolean addPeriod)
    {
        String subText = extra > 0 ? getSubText() + " (" + TEXT.subjects_max(extra) + ")" : getSubText();
        return TEXT.cond_per(childEffect != null ? capital(childEffect.getText(false), addPeriod) : "",
                subText + getXRawString()) + PCLCoreStrings.period(addPeriod);
    }

    @Override
    public void use(PCLUseInfo info)
    {
        getActions().callback(() -> {
            if (this.childEffect != null)
            {
                updateChildAmount(info);
                this.childEffect.use(info);
            }
        });
    }

    @Override
    public void use(PCLUseInfo info, int index)
    {
        getActions().callback(() -> {
            if (this.childEffect != null)
            {
                updateChildAmount(info);
                this.childEffect.use(info, index);
            }
        });
    }

    @Override
    public void use(PCLUseInfo info, boolean isUsing)
    {
        getActions().callback(() -> {
            if (this.childEffect != null)
            {
                updateChildAmount(info);
                this.childEffect.use(info, isUsing);
            }
        });
    }

    public final int updateAmount(PSkill<?> be, PCLUseInfo info)
    {
        cachedValue = getModifiedAmount(be, info);
        if (extra > 0)
        {
            cachedValue = Math.min(extra, cachedValue);
        }
        return cachedValue;
    }

    protected void updateChildAmount(PCLUseInfo info)
    {
        if (this.childEffect != null)
        {
            if (this.childEffect instanceof PMultiBase)
            {
                for (PSkill<?> ce : ((PMultiBase<?>) this.childEffect).getSubEffects())
                {
                    if (!ce.useParent)
                    {
                        ce.setTemporaryAmount(updateAmount(ce, info));
                    }
                }
            }
            // PDelays should be ignored. PMods will directly affect their children instead
            else if (this.childEffect instanceof PDelay && this.childEffect.childEffect != null && !this.childEffect.childEffect.useParent)
            {
                this.childEffect.childEffect.setTemporaryAmount(updateAmount(this.childEffect.childEffect, info));
            }
            else if (!this.childEffect.useParent)
            {
                this.childEffect.setTemporaryAmount(updateAmount(this.childEffect, info));
            }
        }
    }
}
