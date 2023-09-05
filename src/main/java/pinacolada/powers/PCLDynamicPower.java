package pinacolada.powers;

import com.badlogic.gdx.graphics.Color;
import com.megacrit.cardcrawl.actions.utility.UseCardAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.powers.AbstractPower;
import extendedui.EUIUtils;
import extendedui.utilities.ColoredString;
import org.apache.commons.lang3.StringUtils;
import pinacolada.dungeon.CombatManager;
import pinacolada.dungeon.PCLUseInfo;
import pinacolada.interfaces.markers.EditorMaker;
import pinacolada.interfaces.markers.FabricateItem;
import pinacolada.interfaces.markers.TriggerConnection;
import pinacolada.interfaces.providers.PointerProvider;
import pinacolada.interfaces.subscribers.PCLCombatSubscriber;
import pinacolada.skills.PSkill;
import pinacolada.skills.PSkillContainer;
import pinacolada.skills.skills.PTrigger;
import pinacolada.skills.skills.base.primary.PTrigger_Interactable;
import pinacolada.utilities.GameUtilities;

public class PCLDynamicPower extends PCLClickablePower implements PointerProvider, TriggerConnection, FabricateItem {

    public PCLDynamicPowerData builder;
    public PSkillContainer skills = new PSkillContainer();

    public PCLDynamicPower(PCLDynamicPowerData data, AbstractCreature owner, AbstractCreature source, int amount) {
        super(data, owner, source, amount);
        this.builder = data;
    }

    @Override
    public float atDamageFinalGive(PCLUseInfo info, float damage, DamageInfo.DamageType type, AbstractCard c) {
        refreshTriggers(info);
        for (PSkill<?> effect : getEffects()) {
            damage = effect.modifyDamageGiveLast(info, damage);
        }
        return damage;
    }

    @Override
    public float atDamageFinalGive(float damage, DamageInfo.DamageType type) {
        return atDamageFinalGive(CombatManager.playerSystem.getInfo(null, owner, owner), damage, type, null);
    }

    @Override
    public float atDamageFinalGive(float damage, DamageInfo.DamageType type, AbstractCard c) {
        return atDamageFinalGive(CombatManager.playerSystem.getInfo(c, owner, owner), damage, type, c);
    }

    @Override
    public float atDamageFinalReceive(float damage, DamageInfo.DamageType type) {
        PCLUseInfo info = CombatManager.playerSystem.getInfo(null, owner, owner);
        refreshTriggers(info);
        for (PSkill<?> effect : getEffects()) {
            damage = effect.modifyDamageReceiveLast(info, damage, type);
        }
        return super.atDamageFinalReceive(damage, type);
    }

    @Override
    public float atDamageFinalReceive(float damage, DamageInfo.DamageType type, AbstractCard card) {
        PCLUseInfo info = CombatManager.playerSystem.getInfo(card, owner, owner);
        refreshTriggers(info);
        for (PSkill<?> effect : getEffects()) {
            damage = effect.modifyDamageReceiveLast(info, damage, type);
        }
        return super.atDamageFinalReceive(damage, type, card);
    }

    @Override
    public float atDamageGive(PCLUseInfo info, float damage, DamageInfo.DamageType type, AbstractCard c) {
        refreshTriggers(info);
        for (PSkill<?> effect : getEffects()) {
            damage = effect.modifyDamageGiveFirst(info, damage);
        }
        return damage;
    }

    @Override
    public float atDamageGive(float damage, DamageInfo.DamageType type) {
        return atDamageGive(CombatManager.playerSystem.getInfo(null, owner, owner), damage, type, null);
    }

    @Override
    public float atDamageGive(float damage, DamageInfo.DamageType type, AbstractCard c) {
        return atDamageGive(CombatManager.playerSystem.getInfo(c, owner, owner), damage, type, c);
    }

    @Override
    public float atDamageReceive(float damage, DamageInfo.DamageType type) {
        PCLUseInfo info = CombatManager.playerSystem.getInfo(null, owner, owner);
        refreshTriggers(info);
        for (PSkill<?> effect : getEffects()) {
            damage = effect.modifyDamageReceiveFirst(info, damage, type);
        }
        return super.atDamageReceive(damage, type);
    }

    @Override
    public float atDamageReceive(float damage, DamageInfo.DamageType type, AbstractCard card) {
        PCLUseInfo info = CombatManager.playerSystem.getInfo(card, owner, owner);
        refreshTriggers(info);
        for (PSkill<?> effect : getEffects()) {
            damage = effect.modifyDamageReceiveFirst(info, damage, type);
        }
        return super.atDamageReceive(damage, type, card);
    }

    @Override
    public boolean canActivate(PTrigger trigger) {
        return !GameUtilities.isDeadOrEscaped(owner);
    }

    @Override
    public boolean canPlayCard(AbstractCard card) {
        PCLUseInfo info = CombatManager.playerSystem.getInfo(card, owner, owner);
        refreshTriggers(info);
        boolean canPlay = true;
        for (PSkill<?> effect : getEffects()) {
            canPlay = canPlay & effect.canPlay(info, effect);
        }
        return canPlay;
    }

    @Override
    public AbstractCreature getOwner() {
        return owner;
    }

    @Override
    public PCLDynamicPowerData getDynamicData() {
        return builder;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    protected ColoredString getSecondaryAmount(Color c) {
        for (PSkill<?> effect : getEffects()) {
            if (effect instanceof PTrigger) {
                int uses = ((PTrigger) effect).getUses();
                if (!(effect instanceof PTrigger_Interactable) && uses >= 0) {
                    return new ColoredString(uses, uses > 0 && (((PTrigger) effect).fields.forced || uses >= effect.amount) ? Color.GREEN : Color.GRAY, c.a);
                }
            }
        }
        return null;
    }

    @Override
    public PSkillContainer getSkills() {
        return skills;
    }

    @Override
    public String getUpdatedDescription() {
        return StringUtils.capitalize(EUIUtils.joinStringsMapNonnull(EUIUtils.SPLIT_LINE, PSkill::getPowerText, getEffects()));
    }

    public float modifyBlock(PCLUseInfo info, float block, AbstractCard c) {
        refreshTriggers(info);
        for (PSkill<?> effect : getEffects()) {
            block = effect.modifyBlockFirst(info, block);
        }
        return block;
    }

    @Override
    public float modifyBlock(float block, AbstractCard c) {
        return modifyBlock(CombatManager.playerSystem.getInfo(c, owner, owner), block, c);
    }

    @Override
    public float modifyBlockLast(float block) {
        return modifyBlockLast(CombatManager.playerSystem.getInfo(null, owner, owner), block, null);
    }

    public float modifyBlockLast(PCLUseInfo info, float block, AbstractCard c) {
        refreshTriggers(info);
        for (PSkill<?> effect : getEffects()) {
            block = effect.modifyBlockLast(info, block);
        }
        return block;
    }

    public float modifyHeal(PCLUseInfo info, float damage, AbstractCard c) {
        refreshTriggers(info);
        for (PSkill<?> effect : getEffects()) {
            damage = effect.modifyHeal(info, damage);
        }
        return damage;
    }

    public float modifyHitCount(PCLUseInfo info, float damage, AbstractCard c) {
        refreshTriggers(info);
        for (PSkill<?> effect : getEffects()) {
            damage = effect.modifyHitCount(info, damage);
        }
        return damage;
    }

    public float modifyRightCount(PCLUseInfo info, float damage, AbstractCard c) {
        refreshTriggers(info);
        for (PSkill<?> effect : getEffects()) {
            damage = effect.modifyRightCount(info, damage);
        }
        return damage;
    }

    public float modifySkillBonus(PCLUseInfo info, float damage, AbstractCard c) {
        refreshTriggers(info);
        for (PSkill<?> effect : getEffects()) {
            damage = effect.modifySkillBonus(info, damage);
        }
        return damage;
    }

    // Update this power's effects whenever you play a card
    @Override
    public void onAfterUseCard(AbstractCard card, UseCardAction act) {
        refreshTriggers(CombatManager.playerSystem.getInfo(card, owner, owner));
        super.onAfterUseCard(card, act);
    }


    public void onInitialApplication() {
        super.onInitialApplication();
        for (PSkill<?> effect : getEffects()) {
            effect.subscribeChildren();
        }
    }

    public void onRemove() {
        super.onRemove();
        for (PSkill<?> effect : getEffects()) {
            effect.unsubscribeChildren();
        }
    }

    @Override
    protected void onSamePowerApplied(AbstractPower power) {
        PCLDynamicPower po = EUIUtils.safeCast(power, PCLDynamicPower.class);
        if (po != null && this.ID.equals(po.ID)) {
            // The effects of identical powers should always be in the same order
            for (int i = 0; i < Math.min(getEffects().size(), po.getEffects().size()); i++) {
                getEffects().get(i).stack(po.getEffects().get(i));
            }
        }
    }

    public void refreshTriggers(PCLUseInfo info) {
        for (PSkill<?> effect : getEffects()) {
            effect.refresh(info, true);
        }
    }
}
