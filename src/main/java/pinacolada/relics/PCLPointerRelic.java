package pinacolada.relics;

import basemod.abstracts.CustomSavable;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.powers.AbstractPower;
import extendedui.EUIInputManager;
import extendedui.interfaces.delegates.ActionT3;
import extendedui.interfaces.delegates.FuncT1;
import extendedui.interfaces.delegates.FuncT3;
import extendedui.ui.tooltips.EUIKeywordTooltip;
import extendedui.ui.tooltips.EUIPreview;
import extendedui.utilities.RotatingList;
import pinacolada.actions.PCLActions;
import pinacolada.cards.base.fields.PCLCardTarget;
import pinacolada.dungeon.CombatManager;
import pinacolada.dungeon.PCLUseInfo;
import pinacolada.interfaces.markers.TriggerConnection;
import pinacolada.interfaces.providers.ClickableProvider;
import pinacolada.interfaces.providers.PointerProvider;
import pinacolada.misc.PCLCollectibleSaveData;
import pinacolada.powers.PCLClickableUse;
import pinacolada.skills.PSkill;
import pinacolada.skills.PSkillContainer;
import pinacolada.skills.skills.PSpecialPowerSkill;
import pinacolada.skills.skills.PSpecialSkill;
import pinacolada.skills.skills.PTrigger;
import pinacolada.utilities.GameUtilities;

public abstract class PCLPointerRelic extends PCLRelic implements PointerProvider, ClickableProvider, TriggerConnection, CustomSavable<PCLCollectibleSaveData> {
    public PSkillContainer skills;
    public PCLClickableUse triggerCondition;

    public PCLPointerRelic(PCLRelicData data) {
        super(data);
    }

    @Override
    public void atBattleStart() {
        super.atBattleStart();
        if (!usedUp) {
            for (PSkill<?> effect : getEffects()) {
                effect.triggerOnStartOfBattleForRelic();
            }
        }
    }

    @Override
    public float atBlockLastModify(PCLUseInfo info, float block) {
        if (!usedUp) {
            refresh(info);
            for (PSkill<?> effect : getEffects()) {
                block = effect.modifyBlockLast(info, block);
            }
        }
        return block;
    }

    @Override
    public float atBlockLastModify(float block, AbstractCard c) {
        return atBlockLastModify(CombatManager.playerSystem.getInfo(c, AbstractDungeon.player, AbstractDungeon.player), block);
    }

    @Override
    public float atBlockModify(PCLUseInfo info, float block) {
        if (!usedUp) {
            refresh(info);
            for (PSkill<?> effect : getEffects()) {
                block = effect.modifyBlockFirst(info, block);
            }
        }
        return block;
    }

    @Override
    public float atBlockModify(float block, AbstractCard c) {
        return atBlockModify(CombatManager.playerSystem.getInfo(c, AbstractDungeon.player, AbstractDungeon.player), block);
    }

    @Override
    public int atCostModify(PCLUseInfo info, int block) {
        if (!usedUp) {
            refresh(info);
            for (PSkill<?> effect : getEffects()) {
                block = effect.modifyCost(info, block);
            }
        }
        return block;
    }

    @Override
    public int atCostModify(int block, AbstractCard c) {
        return atCostModify(CombatManager.playerSystem.getInfo(c, AbstractDungeon.player, AbstractDungeon.player), block);
    }


    @Override
    public float atDamageLastModify(PCLUseInfo info, float damage) {
        if (!usedUp) {
            refresh(info);
            for (PSkill<?> effect : getEffects()) {
                damage = effect.modifyDamageGiveLast(info, damage);
            }
        }
        return damage;
    }

    @Override
    public float atDamageLastModify(float block, AbstractCard c) {
        return atDamageLastModify(CombatManager.playerSystem.getInfo(c, AbstractDungeon.player, AbstractDungeon.player), block);
    }

    @Override
    public float atDamageModify(PCLUseInfo info, float damage) {
        if (!usedUp) {
            refresh(info);
            for (PSkill<?> effect : getEffects()) {
                damage = effect.modifyDamageGiveFirst(info, damage);
            }
        }
        return damage;
    }

    @Override
    public float atDamageModify(float block, AbstractCard c) {
        return atDamageModify(CombatManager.playerSystem.getInfo(c, AbstractDungeon.player, AbstractDungeon.player), block);
    }

    @Override
    public float atHealModify(PCLUseInfo info, float damage) {
        if (!usedUp) {
            refresh(info);
            for (PSkill<?> effect : getEffects()) {
                damage = effect.modifyHeal(info, damage);
            }
        }
        return damage;
    }

    @Override
    public float atHitCountModify(PCLUseInfo info, float damage) {
        if (!usedUp) {
            refresh(info);
            for (PSkill<?> effect : getEffects()) {
                damage = effect.modifyHitCount(info, damage);
            }
        }
        return damage;
    }

    @Override
    public void atPreBattle() {
        super.atPreBattle();
        if (!usedUp) {
            subscribe();
        }
        for (PSkill<?> effect : getEffects()) {
            effect.resetUses();
        }
    }

    @Override
    public float atRightCountModify(PCLUseInfo info, float damage) {
        if (!usedUp) {
            refresh(info);
            for (PSkill<?> effect : getEffects()) {
                damage = effect.modifyRightCount(info, damage);
            }
        }
        return damage;
    }

    @Override
    public float atSkillBonusModify(PCLUseInfo info, float damage) {
        if (!usedUp) {
            refresh(info);
            for (PSkill<?> effect : getEffects()) {
                damage = effect.modifySkillBonus(info, damage);
            }
        }
        return damage;
    }

    @Override
    public void atTurnStart() {
        super.atTurnStart();
        if (triggerCondition != null) {
            triggerCondition.refresh(true, true);
        }
        for (PSkill<?> effect : getEffects()) {
            effect.resetUses();
        }
    }

    @Override
    public int branchFactor() {
        return relicData.branchFactor;
    }

    @Override
    public boolean canActivate(PTrigger trigger) {
        return true;
    }

    @Override
    public AbstractCreature getOwner() {
        return AbstractDungeon.player;
    }

    public void fillPreviews(RotatingList<EUIPreview> list) {
        PointerProvider.fillPreviewsForKeywordProvider(this, list);
    }

    @Override
    public PCLClickableUse getClickable() {
        return triggerCondition;
    }

    @Override
    public String getDescriptionImpl() {
        return getEffectPowerTextStrings();
    }

    @Override
    public String getID() {
        return relicId;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public PSkillContainer getSkills() {
        return skills;
    }

    protected PSpecialSkill getSpecialMove(String description, ActionT3<PSpecialSkill, PCLUseInfo, PCLActions> onUse, int amount, int extra) {
        return new PSpecialSkill(this.relicId + this.getEffects().size(), description, onUse, amount, extra);
    }

    protected PSpecialSkill getSpecialMove(int descIndex, ActionT3<PSpecialSkill, PCLUseInfo, PCLActions> onUse) {
        return getSpecialMove(relicData.strings.DESCRIPTIONS[descIndex], onUse, 1, 0);
    }

    protected PSpecialSkill getSpecialMove(int descIndex, ActionT3<PSpecialSkill, PCLUseInfo, PCLActions> onUse, int amount) {
        return getSpecialMove(relicData.strings.DESCRIPTIONS[descIndex], onUse, amount, 0);
    }

    protected PSpecialSkill getSpecialMove(int descIndex, ActionT3<PSpecialSkill, PCLUseInfo, PCLActions> onUse, int amount, int extra) {
        return getSpecialMove(relicData.strings.DESCRIPTIONS[descIndex], onUse, amount, extra);
    }

    protected PSpecialSkill getSpecialMove(FuncT1<String, PSpecialSkill> strFunc, ActionT3<PSpecialSkill, PCLUseInfo, PCLActions> onUse, int amount) {
        return getSpecialMove(strFunc, onUse, amount, 0);
    }

    protected PSpecialSkill getSpecialMove(FuncT1<String, PSpecialSkill> strFunc, ActionT3<PSpecialSkill, PCLUseInfo, PCLActions> onUse, int amount, int extra) {
        return new PSpecialSkill(this.relicId + this.getEffects().size(), strFunc, onUse, amount, extra);
    }

    protected PSpecialPowerSkill getSpecialPower(String description, FuncT3<? extends AbstractPower, AbstractCreature, AbstractCreature, PSpecialPowerSkill> onUse, int amount, int extra) {
        return getSpecialPower(description, PCLCardTarget.Self, onUse, amount, extra);
    }

    protected PSpecialPowerSkill getSpecialPower(String description, PCLCardTarget target, FuncT3<? extends AbstractPower, AbstractCreature, AbstractCreature, PSpecialPowerSkill> onUse, int amount, int extra) {
        return new PSpecialPowerSkill(this.relicId + this.getEffects().size(), description, target, onUse, amount, extra);
    }

    protected PSpecialPowerSkill getSpecialPower(int descIndex, FuncT3<? extends AbstractPower, AbstractCreature, AbstractCreature, PSpecialPowerSkill> onUse) {
        return getSpecialPower(relicData.strings.DESCRIPTIONS[descIndex], onUse, 1, 0);
    }

    protected PSpecialPowerSkill getSpecialPower(int descIndex, FuncT3<? extends AbstractPower, AbstractCreature, AbstractCreature, PSpecialPowerSkill> onUse, int amount) {
        return getSpecialPower(relicData.strings.DESCRIPTIONS[descIndex], onUse, amount, 0);
    }

    protected PSpecialPowerSkill getSpecialPower(int descIndex, FuncT3<? extends AbstractPower, AbstractCreature, AbstractCreature, PSpecialPowerSkill> onUse, int amount, int extra) {
        return getSpecialPower(relicData.strings.DESCRIPTIONS[descIndex], onUse, amount, extra);
    }

    @Override
    public EUIKeywordTooltip getTooltip() {
        return super.getTooltip();
    }

    @Override
    public int getXValue() {
        return counter;
    }

    @Override
    public int maxForms() {
        return relicData.maxForms;
    }

    @Override
    public int maxUpgrades() {
        return relicData.maxUpgradeLevel;
    }

    @Override
    public void onCounterManualChange() {
        for (PSkill<?> ef : getEffects()) {
            if (ef instanceof PTrigger) {
                ((PTrigger) ef).setAmount(counter);
            }
        }
    }

    @Override
    public void onEquip() {
        super.onEquip();
        for (PSkill<?> effect : getEffects()) {
            effect.triggerOnObtain();
        }
        if (!usedUp) {
            subscribe();
        }
        for (PSkill<?> effect : getEffects()) {
            effect.resetUses();
        }
    }

    @Override
    public void onReceiveUpdate() {
        updateDescription(null);
    }

    @Override
    public void onUnequip() {
        super.onUnequip();
        for (PSkill<?> effect : getEffects()) {
            effect.triggerOnRemoveFromInventory();
            effect.unsubscribeChildren();
        }
    }

    @Override
    public void onUpdateUsesPerTurn(int val) {
        counter = val;
    }

    @Override
    public void onUpgrade() {
        for (PSkill<?> ef : getEffects()) {
            ef.setAmountFromCard().onUpgrade();
        }
        for (PSkill<?> ef : getPowerEffects()) {
            ef.setAmountFromCard().onUpgrade();
        }
    }

    @Override
    public void onVictory() {
        super.onVictory();
        unsubscribe();
    }

    @Override
    protected void preSetup(PCLRelicData data) {
        skills = new PSkillContainer();
        setup();
    }

    // TODO single endpoint for calling refresh
    public void refresh(PCLUseInfo info) {
        for (PSkill<?> effect : getEffects()) {
            effect.refresh(info, true, false);
        }
    }

    public void reset() {
        skills = new PSkillContainer();
        setup();
        usedUp = false;
        initializePCLTips();
    }

    public void setTimesUpgraded(int times) {
        auxiliaryData.timesUpgraded = times;
        for (PSkill<?> ef : getEffects()) {
            ef.setAmountFromCard().onUpgrade();
        }
        for (PSkill<?> ef : getPowerEffects()) {
            ef.setAmountFromCard().onUpgrade();
        }
        updateDescription(null);
    }

    public void setup() {
    }

    protected void subscribe() {
        for (PSkill<?> effect : getEffects()) {
            effect.subscribeChildren();
            PCLClickableUse use = effect.getClickable(this);
            if (use != null) {
                triggerCondition = use;
            }
        }
    }

    @Override
    public int timesUpgraded() {
        return auxiliaryData != null ? auxiliaryData.timesUpgraded : 0;
    }

    protected void unsubscribe() {
        if (triggerCondition != null) {
            triggerCondition = null;
            mainTooltip.subHeader = null;
            mainTooltip.invalidateHeight();
        }
    }

    @Override
    public void update() {
        super.update();

        if (!usedUp) {
            if (triggerCondition != null) {
                triggerCondition.refresh(false, hb.justHovered);
            }

            if (CombatManager.inBattle() && hb.hovered && EUIInputManager.rightClick.isJustPressed() && triggerCondition != null && triggerCondition.interactable()) {
                triggerCondition.targetToUse(1);
                flash();
            }
        }
    }

    @Override
    public void usedUp() {
        super.usedUp();
        unsubscribe();
    }
}
