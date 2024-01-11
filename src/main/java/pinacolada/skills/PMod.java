package pinacolada.skills;

import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.core.Settings;
import extendedui.EUIUtils;
import extendedui.interfaces.delegates.ActionT1;
import extendedui.utilities.ColoredString;
import pinacolada.cards.base.PCLCardGroupHelper;
import pinacolada.cards.base.cardText.PointerToken;
import pinacolada.cards.base.fields.PCLCardSelection;
import pinacolada.cards.base.fields.PCLCardTarget;
import pinacolada.dungeon.CombatManager;
import pinacolada.dungeon.PCLUseInfo;
import pinacolada.interfaces.markers.PMultiBase;
import pinacolada.interfaces.providers.ValueProvider;
import pinacolada.orbs.PCLOrbData;
import pinacolada.powers.PCLPowerData;
import pinacolada.resources.pcl.PCLCoreStrings;
import pinacolada.skills.fields.PField;
import pinacolada.skills.skills.base.modifiers.*;
import pinacolada.skills.skills.base.primary.PTrigger_When;
import pinacolada.stances.PCLStanceHelper;

public abstract class PMod<T extends PField> extends PSkill<T> {
    public static final int MODIFIER_PRIORITY = 3;
    private int cachedValue;

    public PMod(PSkillData<T> data, PSkillSaveData content) {
        super(data, content);
    }

    public PMod(PSkillData<T> data) {
        super(data);
    }

    public PMod(PSkillData<T> data, PCLCardTarget target, int amount) {
        super(data, target, amount);
    }

    public PMod(PSkillData<T> data, PCLCardTarget target, int amount, int extra) {
        super(data, target, amount, extra);
    }

    public static PMod_BonusInStance bonusInStance(int amount, PCLStanceHelper s) {
        return new PMod_BonusInStance(amount, s);
    }

    public static PMod_BonusOnHasDiscarded bonusOnDiscarded(int amount) {
        return new PMod_BonusOnHasDiscarded(amount);
    }

    public static PMod_BonusOnHasDiscarded bonusOnDiscarded(int amount, int count) {
        return new PMod_BonusOnHasDiscarded(amount, count);
    }

    public static PMod_BonusOnHasExhausted bonusOnExhausted(int amount) {
        return new PMod_BonusOnHasExhausted(amount);
    }

    public static PMod_BonusOnHasExhausted bonusOnExhausted(int amount, int count) {
        return new PMod_BonusOnHasExhausted(amount, count);
    }

    public static PMod_BonusOnHasPlayed bonusOnPlayed(int amount) {
        return new PMod_BonusOnHasPlayed(amount);
    }

    public static PMod_BonusOnHasPlayed bonusOnPlayed(int amount, int count) {
        return new PMod_BonusOnHasPlayed(amount, count);
    }

    public static PMod_BonusOnStarter bonusOnStarter(int amount) {
        return new PMod_BonusOnStarter(amount);
    }

    public static PMod_PerCreature bonusPerCreature(PCLCardTarget target, int amount) {
        return (PMod_PerCreature) new PMod_PerCreature(target, amount).edit(f -> f.setNot(true));
    }

    public static PMod_PerCardDamage bonusPerDamage(int amount) {
        return (PMod_PerCardDamage) new PMod_PerCardDamage(amount).edit(f -> f.setNot(true));
    }

    public static PMod_PerOrb bonusPerOrb(int amount, PCLOrbData... aff) {
        return new PMod_PerOrb(amount, aff);
    }

    public static PMod_PerPower bonusPerPower(PCLPowerData... powers) {
        return bonusPerPower(1, powers);
    }

    public static PMod_PerPower bonusPerPower(int amount, PCLPowerData... powers) {
        return (PMod_PerPower) new PMod_PerPower(amount, powers).edit(f -> f.setNot(true));
    }

    public static PMod_PerPower bonusPerPowerAny(PCLPowerData... powers) {
        return bonusPerPowerAny(1, powers);
    }

    public static PMod_PerPower bonusPerPowerAny(int amount, PCLPowerData... powers) {
        return (PMod_PerPower) new PMod_PerPower(amount, powers).setTarget(PCLCardTarget.Any).edit(f -> f.setNot(true));
    }

    public static PMod_PerPower bonusPerPowerAoe(PCLPowerData... powers) {
        return bonusPerPowerAoe(1, powers);
    }

    public static PMod_PerPower bonusPerPowerAoe(int amount, PCLPowerData... powers) {
        return (PMod_PerPower) new PMod_PerPower(amount, powers).setTarget(PCLCardTarget.AllEnemy).edit(f -> f.setNot(true));
    }

    public static PMod_PerPower bonusPerPowerSelf(PCLPowerData... powers) {
        return bonusPerPowerSelf(1, powers);
    }

    public static PMod_PerPower bonusPerPowerSelf(int amount, PCLPowerData... powers) {
        return (PMod_PerPower) new PMod_PerPower(amount, powers).setTarget(PCLCardTarget.Self).edit(f -> f.setNot(true));
    }

    public static PMod_PerPower bonusPerPowerSingle(PCLPowerData... powers) {
        return bonusPerPowerSingle(1, powers);
    }

    public static PMod_PerPower bonusPerPowerSingle(int amount, PCLPowerData... powers) {
        return (PMod_PerPower) new PMod_PerPower(amount, powers).setTarget(PCLCardTarget.Single).edit(f -> f.setNot(true));
    }

    public static PMod_CyclePerCard cyclePer(int amount) {
        return new PMod_CyclePerCard(amount);
    }

    public static PMod_CyclePerCard cycleRandomPer(int amount) {
        return (PMod_CyclePerCard) new PMod_CyclePerCard(amount).edit(f -> f.setOrigin(PCLCardSelection.Random));
    }

    public static PMod_DiscardPerCard discardPer(int amount) {
        return new PMod_DiscardPerCard(amount, PCLCardGroupHelper.Hand);
    }

    public static PMod_DiscardPerCard discardPer(int amount, PCLCardGroupHelper... groups) {
        return new PMod_DiscardPerCard(amount, groups);
    }

    public static PMod_DiscardPerCard discardRandomPer(int amount, PCLCardGroupHelper... groups) {
        return (PMod_DiscardPerCard) new PMod_DiscardPerCard(amount, groups).edit(f -> f.setOrigin(PCLCardSelection.Random));
    }

    public static PMod_DrawPerCard drawPer(int amount) {
        return new PMod_DrawPerCard(amount);
    }

    public static PMod_EvokePerOrb evokePerOrb(int amount, PCLOrbData... aff) {
        return new PMod_EvokePerOrb(amount, aff);
    }

    public static PMod_ExhaustPerCard exhaustPer(int amount) {
        return new PMod_ExhaustPerCard(amount, PCLCardGroupHelper.Hand);
    }

    public static PMod_ExhaustPerCard exhaustPer(int amount, PCLCardGroupHelper... groups) {
        return new PMod_ExhaustPerCard(amount, groups);
    }

    public static PMod_ExhaustPerCard exhaustRandomPer(int amount, PCLCardGroupHelper... groups) {
        return (PMod_ExhaustPerCard) new PMod_ExhaustPerCard(amount, groups).edit(f -> f.setOrigin(PCLCardSelection.Random));
    }

    public static PMod_FetchPerCard fetchPer(int amount, PCLCardGroupHelper... groups) {
        return new PMod_FetchPerCard(amount, groups);
    }

    public static PMod_FetchPerCard fetchRandomPer(int amount, PCLCardGroupHelper... groups) {
        return (PMod_FetchPerCard) new PMod_FetchPerCard(amount, groups).edit(f -> f.setOrigin(PCLCardSelection.Random));
    }

    public static PMod_IncreaseOnUse increaseOnUse(int amount) {
        return new PMod_IncreaseOnUse(amount);
    }

    public static PMod_PayPerPower payPerPower(PCLPowerData... powers) {
        return payPerPower(1, powers);
    }

    public static PMod_PayPerPower payPerPower(int amount, PCLPowerData... powers) {
        return new PMod_PayPerPower(amount, powers);
    }

    public static PMod_PerCard perCard(PCLCardGroupHelper... groups) {
        return perCard(1, groups);
    }

    public static PMod_PerCard perCard(int amount, PCLCardGroupHelper... groups) {
        return new PMod_PerCard(amount, groups);
    }

    public static PMod_PerCardDiscarded perCardDiscarded(int amount) {
        return new PMod_PerCardDiscarded(amount);
    }

    public static PMod_PerCardDiscarded perCardDiscardedCombat(int amount) {
        return (PMod_PerCardDiscarded) new PMod_PerCardDiscarded(amount).edit(f -> f.setForced(true));
    }

    public static PMod_PerCardExhausted perCardExhausted(int amount) {
        return new PMod_PerCardExhausted(amount);
    }

    public static PMod_PerCardExhausted perCardExhaustedCombat(int amount) {
        return (PMod_PerCardExhausted) new PMod_PerCardExhausted(amount).edit(f -> f.setForced(true));
    }

    public static PMod_PerCardPlayed perCardPlayed(int amount) {
        return new PMod_PerCardPlayed(amount);
    }

    public static PMod_PerCardPlayed perCardPlayedCombat(int amount) {
        return (PMod_PerCardPlayed) new PMod_PerCardPlayed(amount).edit(f -> f.setForced(true));
    }

    public static PMod_PerCreature perCreature(int amount) {
        return new PMod_PerCreature(amount);
    }

    public static PMod_PerCreature perCreature(PCLCardTarget target, int amount) {
        return new PMod_PerCreature(target, amount);
    }

    public static PMod_PerCreatureIntent perCreatureAttacking(int amount) {
        return new PMod_PerCreatureIntent(amount);
    }

    public static PMod_PerCreatureBlock perCreatureBlock(int amount) {
        return new PMod_PerCreatureBlock(amount);
    }

    public static PMod_PerCreatureBlock perCreatureBlock(PCLCardTarget target, int amount) {
        return new PMod_PerCreatureBlock(target, amount);
    }

    public static PMod_PerCreatureHP perCreatureHP(int amount) {
        return new PMod_PerCreatureHP(amount);
    }

    public static PMod_PerCreatureHP perCreatureHP(PCLCardTarget target, int amount) {
        return new PMod_PerCreatureHP(target, amount);
    }

    public static PMod_PerCreatureWith perCreatureWith(int amount, PCLPowerData... powers) {
        return new PMod_PerCreatureWith(amount, powers);
    }

    public static PMod_PerDistinctPower perDistinctDebuff() {
        return perDistinctDebuff(1);
    }

    public static PMod_PerDistinctPower perDistinctDebuff(int amount) {
        return (PMod_PerDistinctPower) perDistinctPower(amount).edit(f -> f.debuff = true);
    }

    public static PMod_PerDistinctPower perDistinctDebuff(PCLCardTarget target, int amount) {
        return (PMod_PerDistinctPower) perDistinctPower(target, amount).edit(f -> f.debuff = true);
    }

    public static PMod_PerDistinctPower perDistinctPower(PCLPowerData... powers) {
        return perDistinctPower(1, powers);
    }

    public static PMod_PerDistinctPower perDistinctPower(int amount, PCLPowerData... powers) {
        return new PMod_PerDistinctPower(amount, powers);
    }

    public static PMod_PerDistinctPower perDistinctPower(PCLCardTarget target, int amount, PCLPowerData... powers) {
        return new PMod_PerDistinctPower(target, amount, powers);
    }

    public static PMod_PerEnergy perEnergy(int amount) {
        return new PMod_PerEnergy(amount);
    }

    public static PMod_PerOrb perOrb(PCLOrbData... orbs) {
        return perOrb(1, orbs);
    }

    public static PMod_PerOrb perOrb(int amount, PCLOrbData... orbs) {
        return new PMod_PerOrb(amount, orbs);
    }

    public static PMod_PerParentAmount perParentAmount() {
        return new PMod_PerParentAmount();
    }

    public static PMod_PerPower perPower(PCLPowerData... powers) {
        return perPower(1, powers);
    }

    public static PMod_PerPower perPower(int amount, PCLPowerData... powers) {
        return new PMod_PerPower(amount, powers);
    }

    public static PMod_PerPower perPowerAny(PCLPowerData... powers) {
        return perPowerAny(1, powers);
    }

    public static PMod_PerPower perPowerAny(int amount, PCLPowerData... powers) {
        return (PMod_PerPower) new PMod_PerPower(amount, powers).setTarget(PCLCardTarget.Any);
    }

    public static PMod_PerPower perPowerAoe(PCLPowerData... powers) {
        return perPowerAoe(1, powers);
    }

    public static PMod_PerPower perPowerAoe(int amount, PCLPowerData... powers) {
        return (PMod_PerPower) new PMod_PerPower(amount, powers).setTarget(PCLCardTarget.AllEnemy);
    }

    public static PMod_PerPower perPowerSelf(PCLPowerData... powers) {
        return perPowerSelf(1, powers);
    }

    public static PMod_PerPower perPowerSelf(int amount, PCLPowerData... powers) {
        return (PMod_PerPower) new PMod_PerPower(amount, powers).setTarget(PCLCardTarget.Self);
    }

    public static PMod_PerPower perPowerSingle(PCLPowerData... powers) {
        return perPowerSingle(1, powers);
    }

    public static PMod_PerPower perPowerSingle(int amount, PCLPowerData... powers) {
        return (PMod_PerPower) new PMod_PerPower(amount, powers).setTarget(PCLCardTarget.Single);
    }

    public static PMod_PurgePerCard purgePer(int amount) {
        return new PMod_PurgePerCard(amount, PCLCardGroupHelper.Hand);
    }

    public static PMod_PurgePerCard purgePer(int amount, PCLCardGroupHelper... groups) {
        return new PMod_PurgePerCard(amount, groups);
    }

    public static PMod_PurgePerCard purgeRandomPer(int amount, PCLCardGroupHelper... groups) {
        return (PMod_PurgePerCard) new PMod_PurgePerCard(amount, groups).edit(f -> f.setOrigin(PCLCardSelection.Random));
    }

    public static <T extends PField> PSkillData<T> register(Class<? extends PSkill<T>> type, Class<T> effectType, AbstractCard.CardColor... cardColors) {
        return PSkill.register(type, effectType, -1, DEFAULT_MAX, cardColors)
                .setExtra(-1, DEFAULT_MAX);
    }

    public static <T extends PField> PSkillData<T> register(Class<? extends PSkill<T>> type, Class<T> effectType) {
        return PSkill.register(type, effectType, -1, DEFAULT_MAX)
                .setExtra(-1, DEFAULT_MAX);
    }

    public static PMod_Repeat repeat(int amount) {
        return new PMod_Repeat(amount);
    }

    public static PMod_RepeatForTarget repeatTarget(PCLCardTarget target) {
        return new PMod_RepeatForTarget(target);
    }

    public static PMod_ReshufflePerCard reshufflePer(int amount, PCLCardGroupHelper... groups) {
        return new PMod_ReshufflePerCard(amount, groups);
    }

    public static PMod_ReshufflePerCard reshuffleRandomPer(int amount, PCLCardGroupHelper... groups) {
        return (PMod_ReshufflePerCard) new PMod_ReshufflePerCard(amount, groups).edit(f -> f.setOrigin(PCLCardSelection.Random));
    }

    public static PMod_ScoutPerCard scoutPer(int amount) {
        return new PMod_ScoutPerCard(amount);
    }

    public static PMod_ScryPerCard scryPer(int amount) {
        return new PMod_ScryPerCard(amount);
    }

    public static PMod_SelectPerCard selectPer(int amount) {
        return new PMod_SelectPerCard(amount);
    }

    public static PMod_SelectPerCard selectPer(int amount, PCLCardGroupHelper... groups) {
        return new PMod_SelectPerCard(amount, groups);
    }

    public static PMod_XEnergy xEnergy() {
        return new PMod_XEnergy();
    }

    public static PMod_XEnergy xEnergy(int value) {
        return new PMod_XEnergy(value);
    }

    @Override
    public PMod<T> edit(ActionT1<T> editFunc) {
        editFunc.invoke(fields);
        return this;
    }

    public String getAdditionalWidthString() {
        return !hasParentType(PTrigger_When.class) ? PointerToken.DUMMY : EUIUtils.EMPTY_STRING;
    }

    protected String getMaxExtraString() {
        return " (" + TEXT.subjects_max(extra) + ")";
    }

    public int getModifiedAmount(PCLUseInfo info, int baseAmount, boolean isUsing) {
        return baseAmount;
    }

    @Override
    public String getText(PCLCardTarget perspective, Object requestor, boolean addPeriod) {
        String subText = extra > 0 ? getSubText(perspective, requestor) + getMaxExtraString() : getSubText(perspective, requestor);
        return TEXT.cond_xPerY(childEffect != null ? capital(childEffect.getText(perspective, requestor, false), addPeriod) : EUIUtils.EMPTY_STRING,
                subText + getXRawString()) + PCLCoreStrings.period(addPeriod);
    }

    @Override
    public String getXString() {
        // Do not show the x value for when powers
        if (CombatManager.inBattle() && source instanceof ValueProvider && !hasParentType(PTrigger_When.class)) {
            return " (" + getXValue() + ")";
        }
        return EUIUtils.EMPTY_STRING;
    }

    @Override
    public int getXValue() {
        return cachedValue;
    }

    @Override
    public boolean hasChildWarning() {
        return childEffect == null;
    }

    protected int limitPer(int val) {
        return extra > 0 ? Math.min(extra, val) : val;
    }

    @Override
    public int refreshChildAmount(PCLUseInfo info, int amount, boolean isUsing) {
        cachedValue = limitPer(getModifiedAmount(info, amount, isUsing));
        return cachedValue;
    }

    @Override
    public PMod<T> setExtra(int amount, int upgrade) {
        super.setExtra(amount, upgrade);
        return this;
    }

    @Override
    public PMod<T> setExtra(int amount) {
        super.setExtra(amount);
        return this;
    }

    @Override
    public PMod<T> setUpgrade(int... upgrade) {
        super.setUpgrade(upgrade);
        return this;
    }

    @Override
    public PMod<T> setUpgradeExtra(int... upgrade) {
        super.setUpgradeExtra(upgrade);
        return this;
    }
}
