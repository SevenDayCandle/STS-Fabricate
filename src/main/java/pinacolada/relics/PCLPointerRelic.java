package pinacolada.relics;

import com.megacrit.cardcrawl.cards.AbstractCard;
import extendedui.EUIInputManager;
import extendedui.interfaces.delegates.ActionT3;
import extendedui.interfaces.delegates.FuncT1;
import extendedui.interfaces.delegates.FuncT2;
import extendedui.ui.tooltips.EUIPreview;
import extendedui.ui.tooltips.EUITooltip;
import extendedui.utilities.RotatingList;
import pinacolada.actions.PCLActions;
import pinacolada.dungeon.CombatManager;
import pinacolada.dungeon.PCLUseInfo;
import pinacolada.interfaces.providers.ClickableProvider;
import pinacolada.interfaces.providers.PointerProvider;
import pinacolada.powers.PCLClickableUse;
import pinacolada.powers.PCLPower;
import pinacolada.skills.PSkill;
import pinacolada.skills.PSkillContainer;
import pinacolada.skills.PSkillPowerContainer;
import pinacolada.skills.skills.PSpecialPowerSkill;
import pinacolada.skills.skills.PSpecialSkill;
import pinacolada.utilities.GameUtilities;

public abstract class PCLPointerRelic extends PCLRelic implements PointerProvider, ClickableProvider {
    public PSkillPowerContainer skills;
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
        return atBlockLastModify(CombatManager.playerSystem.getInfo(c, player, player), block);
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
        return atBlockModify(CombatManager.playerSystem.getInfo(c, player, player), block);
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
        return atCostModify(CombatManager.playerSystem.getInfo(c, player, player), block);
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
        return atDamageLastModify(CombatManager.playerSystem.getInfo(c, player, player), block);
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
        return atDamageModify(CombatManager.playerSystem.getInfo(c, player, player), block);
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
    public PSkillPowerContainer getSkills() {
        return skills;
    }

    protected PSpecialSkill getSpecialMove(String description, ActionT3<PSpecialSkill, PCLUseInfo, PCLActions> onUse, int amount, int extra) {
        return new PSpecialSkill(this.relicId + this.getEffects().size(), description, onUse, amount, extra);
    }

    protected PSpecialSkill getSpecialMove(FuncT1<String, PSpecialSkill> strFunc, ActionT3<PSpecialSkill, PCLUseInfo, PCLActions> onUse, int amount) {
        return getSpecialMove(strFunc, onUse, amount, 0);
    }

    protected PSpecialSkill getSpecialMove(FuncT1<String, PSpecialSkill> strFunc, ActionT3<PSpecialSkill, PCLUseInfo, PCLActions> onUse, int amount, int extra) {
        return new PSpecialSkill(this.relicId + this.getEffects().size(), strFunc, onUse, amount, extra);
    }

    protected PSpecialPowerSkill getSpecialPower(String description, FuncT2<? extends PCLPower, PSpecialPowerSkill, PCLUseInfo> onUse, int amount, int extra) {
        return new PSpecialPowerSkill(this.relicId + this.getEffects().size(), description, onUse, amount, extra);
    }

    protected PSpecialPowerSkill getSpecialPower(FuncT1<String, PSpecialPowerSkill> strFunc, FuncT2<? extends PCLPower, PSpecialPowerSkill, PCLUseInfo> onUse, int amount) {
        return getSpecialPower(strFunc, onUse, amount, 0);
    }

    protected PSpecialPowerSkill getSpecialPower(FuncT1<String, PSpecialPowerSkill> strFunc, FuncT2<? extends PCLPower, PSpecialPowerSkill, PCLUseInfo> onUse, int amount, int extra) {
        return new PSpecialPowerSkill(this.relicId + this.getEffects().size(), strFunc, onUse, amount, extra);
    }

    @Override
    public EUITooltip getTooltip() {
        return super.getTooltip();
    }

    @Override
    public int getXValue() {
        return counter;
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
    }

    @Override
    public void onUnequip() {
        super.onUnequip();
        for (PSkill<?> effect : getEffects()) {
            effect.triggerOnRemoval();
            effect.unsubscribeChildren();
        }
    }

    @Override
    public void onVictory() {
        super.onVictory();
        unsubscribe();
    }

    @Override
    protected void preSetup(PCLRelicData data) {
        skills = new PSkillPowerContainer();
        setup();
    }

    // TODO single endpoint for calling refresh
    public void refresh(PCLUseInfo info) {
        for (PSkill<?> effect : getEffects()) {
            effect.refresh(info, true);
        }
    }

    public void reset() {
        skills = new PSkillPowerContainer();
        setup();
        usedUp = false;
        initializePCLTips();
    }

    public void setup() {
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

            if (GameUtilities.inBattle() && hb.hovered && EUIInputManager.rightClick.isJustPressed() && triggerCondition != null && triggerCondition.interactable()) {
                triggerCondition.targetToUse(1);
                flash();
            }
        }
    }

    @Override
    public PCLPointerRelic upgrade() {
        if (this.canUpgrade()) {
            auxiliaryData.timesUpgraded += 1;
            for (PSkill<?> ef : getEffects()) {
                ef.setAmountFromCard().onUpgrade();
            }
            for (PSkill<?> ef : getPowerEffects()) {
                ef.setAmountFromCard().onUpgrade();
            }
            updateDescription(null);
        }
        return this;
    }

    @Override
    public void usedUp() {
        super.usedUp();
        unsubscribe();
    }
}
