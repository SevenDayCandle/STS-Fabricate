package pinacolada.relics;

import com.megacrit.cardcrawl.cards.AbstractCard;
import extendedui.EUIInputManager;
import extendedui.ui.tooltips.EUITooltip;
import org.apache.commons.lang3.StringUtils;
import pinacolada.dungeon.CombatManager;
import pinacolada.dungeon.PCLUseInfo;
import pinacolada.interfaces.providers.ClickableProvider;
import pinacolada.interfaces.providers.PointerProvider;
import pinacolada.powers.PCLClickableUse;
import pinacolada.skills.PSkill;
import pinacolada.skills.Skills;
import pinacolada.utilities.GameUtilities;

public class PCLPointerRelic extends PCLRelic implements PointerProvider, ClickableProvider {
    public Skills skills;
    public PCLClickableUse triggerCondition;

    public PCLPointerRelic(PCLRelicData data) {
        super(data);
    }

    public float atBlockModify(PCLUseInfo info, float block, AbstractCard c) {
        refresh(info);
        for (PSkill<?> effect : getEffects()) {
            block = effect.modifyBlock(info, block);
        }
        return block;
    }

    public float atBlockModify(float block, AbstractCard c) {
        return atBlockModify(CombatManager.playerSystem.generateInfo(c, player, player), block, c);
    }

    public float atDamageModify(PCLUseInfo info, float damage, AbstractCard c) {
        refresh(info);
        for (PSkill<?> effect : getEffects()) {
            damage = effect.modifyDamage(info, damage);
        }
        return damage;
    }

    public float atHealModify(PCLUseInfo info, float damage, AbstractCard c) {
        refresh(info);
        for (PSkill<?> effect : getEffects()) {
            damage = effect.modifyHeal(info, damage);
        }
        return damage;
    }

    public float atHitCountModify(PCLUseInfo info, float damage, AbstractCard c) {
        refresh(info);
        for (PSkill<?> effect : getEffects()) {
            damage = effect.modifyHitCount(info, damage);
        }
        return damage;
    }

    public float atRightCountModify(PCLUseInfo info, float damage, AbstractCard c) {
        refresh(info);
        for (PSkill<?> effect : getEffects()) {
            damage = effect.modifyRightCount(info, damage);
        }
        return damage;
    }

    public float atSkillBonusModify(PCLUseInfo info, float damage, AbstractCard c) {
        refresh(info);
        for (PSkill<?> effect : getEffects()) {
            damage = effect.modifySkillBonus(info, damage);
        }
        return damage;
    }

    // Initialize skills here because this gets called in AbstractRelic's constructor
    @Override
    public String getUpdatedDescription() {
        if (skills == null) {
            skills = new Skills();
            setup();
        }
        try {
            return StringUtils.capitalize(getEffectPowerTextStrings());
        }
        catch (Exception e) {
            return "";
        }
    }

    @Override
    public void atPreBattle() {
        super.atPreBattle();
        subscribe();
    }

    @Override
    public void onVictory() {
        super.onVictory();
        unsubscribe();
    }

    @Override
    public void atBattleStart() {
        super.atBattleStart();
        for (PSkill<?> effect : getEffects()) {
            effect.triggerOnStartOfBattleForRelic();
        }
    }

    @Override
    public void atTurnStart() {
        super.atTurnStart();
        if (triggerCondition != null) {
            triggerCondition.refresh(true, true);
        }
    }

    @Override
    public void onEquip() {
        super.onEquip();
        for (PSkill<?> effect : getEffects()) {
            effect.triggerOnObtain();
        }
        subscribe();
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
    public String getID() {
        return relicId;
    }

    @Override
    public Skills getSkills() {
        return skills;
    }

    @Override
    public int xValue() {
        return counter;
    }

    @Override
    public EUITooltip getTooltip() {
        return super.getTooltip();
    }

    public void refresh(PCLUseInfo info) {
        for (PSkill<?> effect : getEffects()) {
            effect.refresh(info, true);
        }
    }

    public void reset() {
        skills = new Skills();
        setup();
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

        if (triggerCondition != null) {
            triggerCondition.refresh(false, hb.justHovered);
        }

        if (GameUtilities.inBattle() && hb.hovered && EUIInputManager.rightClick.isJustPressed() && triggerCondition != null && triggerCondition.interactable()) {
            triggerCondition.targetToUse(1);
            flash();
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

    public float atDamageModify(float block, AbstractCard c) {
        return atDamageModify(CombatManager.playerSystem.generateInfo(c, player, player), block, c);
    }


}
