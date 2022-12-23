package pinacolada.skills;

import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import extendedui.EUIUtils;
import extendedui.utilities.ColoredString;
import pinacolada.cards.base.PCLAffinity;
import pinacolada.cards.base.PCLCardGroupHelper;
import pinacolada.cards.base.PCLCardTarget;
import pinacolada.cards.base.PCLUseInfo;
import pinacolada.interfaces.markers.PMultiBase;
import pinacolada.orbs.PCLOrbHelper;
import pinacolada.powers.PCLPowerHelper;
import pinacolada.resources.pcl.PCLCoreStrings;
import pinacolada.skills.skills.PDelay;
import pinacolada.skills.skills.base.modifiers.*;
import pinacolada.stances.PCLStanceHelper;

import java.util.ArrayList;

public abstract class PMod extends PSkill
{
    public static final int MODIFIER_PRIORITY = 3;
    public int cachedValue;

    public static ArrayList<PMod> getEligibleModifiers(AbstractCard.CardColor co, Integer priority)
    {
        return EUIUtils.mapAsNonnull(getEligibleEffects(co, priority), ef -> EUIUtils.safeCast(ef, PMod.class));
    }

    public static PSkillData register(Class<? extends PSkill> type, PCLEffectType effectType, AbstractCard.CardColor... cardColors)
    {
        return PSkill.register(type, effectType, getDefaultPriority(type), -1, DEFAULT_MAX, cardColors)
                .setExtra(-1, DEFAULT_MAX);
    }

    public static PSkillData register(Class<? extends PSkill> type, PCLEffectType effectType)
    {
        return PSkill.register(type, effectType, getDefaultPriority(type), -1, DEFAULT_MAX)
                .setExtra(-1, DEFAULT_MAX);
    }

    public static PMod bonusInStance(int amount, PCLStanceHelper s)
    {
        return new PMod_BonusInStance(amount, s);
    }

    public static PMod bonusOnDiscarded(int amount)
    {
        return new PMod_BonusOnHasDiscarded(amount);
    }

    public static PMod bonusOnDiscarded(int amount, int count)
    {
        return new PMod_BonusOnHasDiscarded(amount, count);
    }

    public static PMod bonusOnExhausted(int amount)
    {
        return new PMod_BonusOnHasExhausted(amount);
    }

    public static PMod bonusOnExhausted(int amount, int count)
    {
        return new PMod_BonusOnHasExhausted(amount, count);
    }

    public static PMod bonusOnStarter(int amount)
    {
        return new PMod_BonusOnStarter(amount);
    }

    public static PMod bonusPerLevel(int amount, PCLAffinity... aff)
    {
        return new PMod_BonusPerAffinityLevel(amount, aff);
    }

    public static PMod bonusPerOrb(int amount, PCLOrbHelper... aff)
    {
        return new PMod_BonusPerOrb(amount, aff);
    }

    public static PMod cyclePer(int amount)
    {
        return new PMod_CyclePerCard(amount);
    }

    public static PMod cycleRandomPer(int amount)
    {
        return (PMod) new PMod_CyclePerCard(amount).setAlt(true);
    }

    public static PMod discardBranch(int amount, PCLCardGroupHelper... groups)
    {
        return new PMod_DiscardBranch(amount, groups);
    }

    public static PMod discardPer(int amount, PCLCardGroupHelper... groups)
    {
        return new PMod_DiscardPerCard(amount, groups);
    }

    public static PMod discardRandomPer(int amount, PCLCardGroupHelper... groups)
    {
        return (PMod) new PMod_DiscardPerCard(amount, groups).setAlt(true);
    }

    public static PMod drawBranch(int amount)
    {
        return new PMod_DrawBranch(amount);
    }

    public static PMod drawPer(int amount)
    {
        return new PMod_DrawPerCard(amount);
    }

    public static PMod exhaustPer(int amount, PCLCardGroupHelper... groups)
    {
        return new PMod_ExhaustPerCard(amount, groups);
    }

    public static PMod exhaustRandomPer(int amount, PCLCardGroupHelper... groups)
    {
        return (PMod) new PMod_ExhaustPerCard(amount, groups).setAlt(true);
    }

    public static PMod evokePerOrb(int amount, PCLOrbHelper... aff)
    {
        return new PMod_EvokePerOrb(amount, aff);
    }

    public static PMod fetchPer(int amount, PCLCardGroupHelper... groups)
    {
        return new PMod_FetchPerCard(amount, groups);
    }

    public static PMod fetchRandomPer(int amount, PCLCardGroupHelper... groups)
    {
        return (PMod) new PMod_FetchPerCard(amount, groups).setAlt(true);
    }

    public static PMod payXEnergy()
    {
        return (PMod) new PMod_PerEnergy().setAlt(true);
    }

    public static PMod perCard(PCLCardGroupHelper... groups)
    {
        return perCard(1, groups);
    }

    public static PMod perCard(int amount, PCLCardGroupHelper... groups)
    {
        return new PMod_PerCard(amount, groups);
    }

    public static PMod perCardExhausted(int amount)
    {
        return new PMod_PerCardExhausted(amount);
    }

    public static PMod perCardPlayed(int amount)
    {
        return new PMod_PerCardPlayed(amount);
    }

    public static PMod perCardPlayedCombat(int amount)
    {
        return new PMod_PerCardPlayedCombat(amount);
    }

    public static PMod perCreatureAttacking(int amount)
    {
        return new PMod_PerCreatureAttacking(amount);
    }

    public static PMod perCreatureWith(int amount, PCLPowerHelper... powers)
    {
        return new PMod_PerCreatureWith(amount, powers);
    }

    public static PMod perDistinctPower(PCLPowerHelper... powers)
    {
        return perDistinctPower(1, powers);
    }

    public static PMod perDistinctPower(int amount, PCLPowerHelper... powers)
    {
        return new PMod_PerDistinctPower(amount, powers);
    }

    public static PMod perDistinctPower(PCLCardTarget target, int amount, PCLPowerHelper... powers)
    {
        return new PMod_PerDistinctPower(target, amount, powers);
    }

    public static PMod perEnergy(int amount)
    {
        return (PMod) new PMod_PerEnergy(amount);
    }

    public static PMod perLevel(int amount, PCLAffinity... aff)
    {
        return new PMod_PerAffinityLevel(amount, aff);
    }

    public static PMod perOrb(PCLOrbHelper... orbs)
    {
        return perOrb(1, orbs);
    }

    public static PMod perOrb(int amount, PCLOrbHelper... orbs)
    {
        return new PMod_PerOrb(amount, orbs);
    }

    public static PMod perPower(PCLPowerHelper... powers)
    {
        return perPower(1, powers);
    }

    public static PMod perPower(int amount, PCLPowerHelper... powers)
    {
        return new PMod_PerPower(amount, powers);
    }

    public static PMod perPowerAny(PCLPowerHelper... powers)
    {
        return perPowerAny(1, powers);
    }

    public static PMod perPowerAny(int amount, PCLPowerHelper... powers)
    {
        return (PMod) new PMod_PerPower(amount, powers).setTarget(PCLCardTarget.Any);
    }

    public static PMod perPowerAoe(PCLPowerHelper... powers)
    {
        return perPowerAoe(1, powers);
    }

    public static PMod perPowerAoe(int amount, PCLPowerHelper... powers)
    {
        return (PMod) new PMod_PerPower(amount, powers).setTarget(PCLCardTarget.AllEnemy);
    }

    public static PMod perPowerSelf(PCLPowerHelper... powers)
    {
        return perPowerSelf(1, powers);
    }

    public static PMod perPowerSelf(int amount, PCLPowerHelper... powers)
    {
        return (PMod) new PMod_PerPower(amount, powers).setTarget(PCLCardTarget.Self);
    }

    public static PMod perPowerSingle(PCLPowerHelper... powers)
    {
        return perPowerSingle(1, powers);
    }

    public static PMod perPowerSingle(int amount, PCLPowerHelper... powers)
    {
        return (PMod) new PMod_PerPower(amount, powers).setTarget(PCLCardTarget.Single);
    }

    public static PMod purgePer(int amount, PCLCardGroupHelper... groups)
    {
        return new PMod_PurgePerCard(amount, groups);
    }

    public static PMod purgeRandomPer(int amount, PCLCardGroupHelper... groups)
    {
        return (PMod) new PMod_PurgePerCard(amount, groups).setAlt(true);
    }

    public static PMod reshufflePer(int amount, PCLCardGroupHelper... groups)
    {
        return new PMod_ReshufflePerCard(amount, groups);
    }

    public static PMod reshuffleRandomPer(int amount, PCLCardGroupHelper... groups)
    {
        return (PMod) new PMod_ReshufflePerCard(amount, groups).setAlt(true);
    }

    public static PMod scoutBranch(int amount)
    {
        return new PMod_ScoutBranch(amount);
    }

    public static PMod scoutPer(int amount)
    {
        return new PMod_ScoutPerCard(amount);
    }

    public static PMod scryBranch(int amount)
    {
        return new PMod_ScryBranch(amount);
    }

    public static PMod scryPer(int amount)
    {
        return new PMod_ScryPerCard(amount);
    }

    public PMod(PSkillSaveData content)
    {
        super(content);
    }

    public PMod(PSkillData data)
    {
        super(data);
    }

    public PMod(PSkillData data, PCLCardTarget target, int amount)
    {
        super(data, target, amount);
    }

    public PMod(PSkillData data, PCLCardTarget target, int amount, int extra)
    {
        super(data, target, amount, extra);
    }

    public PMod(PSkillData data, PCLCardTarget target, int amount, PCLCardGroupHelper... groups)
    {
        super(data, target, amount, groups);
    }

    public PMod(PSkillData data, PCLCardTarget target, int amount, PSkill effect)
    {
        super(data, target, amount, effect);
    }

    public PMod(PSkillData data, PCLCardTarget target, int amount, PSkill... effect)
    {
        super(data, target, amount, effect);
    }

    public PMod(PSkillData data, PCLCardTarget target, int amount, PCLAffinity... affinities)
    {
        super(data, target, amount, affinities);
    }

    public PMod(PSkillData data, PCLCardTarget target, int amount, PCLOrbHelper... orbs)
    {
        super(data, target, amount, orbs);
    }

    public PMod(PSkillData data, PCLCardTarget target, int amount, PCLPowerHelper... powerHelpers)
    {
        super(data, target, amount, powerHelpers);
    }

    public PMod(PSkillData data, int amount, PCLStanceHelper powerHelpers)
    {
        super(data, amount, powerHelpers);
    }

    public int getModifiedAmount(PSkill be, PCLUseInfo info)
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
    public void refresh(AbstractCreature m, AbstractCard c, boolean conditionMet)
    {
        updateChildAmount(makeInfo(m));
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
    public String getSubText()
    {
        return getFullCardOrString(1);
    }

    @Override
    public String getText(boolean addPeriod)
    {
        String subText = extra > 0 ? getSubText() + " (" + TEXT.subjects.max(extra) + ")" : getSubText();
        return TEXT.conditions.per(childEffect != null ? capital(childEffect.getText(false), addPeriod) : "",
                subText + getXRawString()) + PCLCoreStrings.period(addPeriod);
    }

    @Override
    public void use(PCLUseInfo info)
    {
        if (this.childEffect != null)
        {
            updateChildAmount(info);
            this.childEffect.use(info);
        }
    }

    public void use(PCLUseInfo info, int index)
    {
        if (this.childEffect != null)
        {
            updateChildAmount(info);
            this.childEffect.use(info, index);
        }
    }

    public void use(PCLUseInfo info, boolean isUsing)
    {
        if (this.childEffect != null)
        {
            updateChildAmount(info);
            this.childEffect.use(info, isUsing);
        }
    }

    public final int updateAmount(PSkill be, PCLUseInfo info)
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
        if (this.childEffect != null && !this.childEffect.useParent)
        {
            if (this.childEffect instanceof PMultiBase)
            {
                for (PSkill ce : ((PMultiBase<?>) this.childEffect).getSubEffects())
                {
                    ce.setTemporaryAmount(updateAmount(ce, info));
                }
            }
            else if (this.childEffect instanceof PDelay && this.childEffect.childEffect != null)
            {
                this.childEffect.childEffect.setTemporaryAmount(updateAmount(this.childEffect.childEffect, info));
            }
            else
            {
                this.childEffect.setTemporaryAmount(updateAmount(this.childEffect, info));
            }
        }
    }
}
