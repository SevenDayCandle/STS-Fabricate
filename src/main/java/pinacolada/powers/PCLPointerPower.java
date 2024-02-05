package pinacolada.powers;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.megacrit.cardcrawl.actions.utility.UseCardAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.helpers.FontHelper;
import extendedui.EUIUtils;
import extendedui.utilities.EUIColors;
import org.apache.commons.lang3.StringUtils;
import pinacolada.actions.PCLActions;
import pinacolada.dungeon.CombatManager;
import pinacolada.dungeon.PCLUseInfo;
import pinacolada.interfaces.markers.TriggerConnection;
import pinacolada.interfaces.providers.PointerProvider;
import pinacolada.skills.PSkill;
import pinacolada.skills.PSkillContainer;
import pinacolada.skills.skills.PTrigger;
import pinacolada.skills.skills.base.primary.PTrigger_Interactable;
import pinacolada.utilities.GameUtilities;

public class PCLPointerPower extends PCLClickablePower implements PointerProvider, TriggerConnection {
    public PSkillContainer skills;

    public PCLPointerPower(PCLPowerData data, AbstractCreature owner, AbstractCreature source, int amount) {
        super(data, owner, source, amount);
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
        return damage;
    }

    @Override
    public float atDamageFinalReceive(float damage, DamageInfo.DamageType type, AbstractCard card) {
        PCLUseInfo info = CombatManager.playerSystem.getInfo(card, owner, owner);
        refreshTriggers(info);
        for (PSkill<?> effect : getEffects()) {
            damage = effect.modifyDamageReceiveLast(info, damage, type);
        }
        return damage;
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
        return damage;
    }

    @Override
    public float atDamageReceive(float damage, DamageInfo.DamageType type, AbstractCard card) {
        PCLUseInfo info = CombatManager.playerSystem.getInfo(card, owner, owner);
        refreshTriggers(info);
        for (PSkill<?> effect : getEffects()) {
            damage = effect.modifyDamageReceiveFirst(info, damage, type);
        }
        return damage;
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
            canPlay = effect.canPlay(info, effect, canPlay);
        }
        return canPlay;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public AbstractCreature getOwner() {
        return owner;
    }

    @Override
    public PSkillContainer getSkills() {
        return skills;
    }

    @Override
    public AbstractCreature getSourceCreature() {
        return owner != null ? owner : AbstractDungeon.player;
    }

    @Override
    public String getUpdatedDescription() {
        if (skills == null) {
            return EUIUtils.EMPTY_STRING;
        }
        String base = StringUtils.capitalize(EUIUtils.joinStringsMapNonnull(EUIUtils.SPLIT_LINE, skill -> skill.getPowerTextForDisplay(null), getEffects()));
        String add = data.endTurnBehavior.getAddendum(turns);
        return add != null ? base + " " + add : base;
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

    @Override
    public float modifyBlockLast(PCLUseInfo info, float block, AbstractCard c) {
        refreshTriggers(info);
        for (PSkill<?> effect : getEffects()) {
            block = effect.modifyBlockLast(info, block);
        }
        return block;
    }

    @Override
    public int modifyCost(int block, AbstractCard c) {
        return modifyCost(CombatManager.playerSystem.getInfo(c, owner, owner), block, c);
    }

    @Override
    public int modifyCost(PCLUseInfo info, int cost, AbstractCard c) {
        refreshTriggers(info);
        for (PSkill<?> effect : getEffects()) {
            cost = effect.modifyCost(info, cost);
        }
        return cost;
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

    // Update skill amounts whenever stacks change
    protected void onAmountChanged(int previousAmount, int difference) {
        if (difference != 0) {
            for (PSkill<?> effect : getEffects()) {
                effect.setAmountFromCard();
            }
            updateDescription();
        }
    }

    public void onDeath() {
        super.onDeath();
        for (PSkill<?> effect : getEffects()) {
            effect.unsubscribeChildren();
        }
    }

    public void onInitialApplication() {
        super.onInitialApplication();
        if (data.endTurnBehavior != PCLPowerData.Behavior.Instant) {
            for (PSkill<?> effect : getEffects()) {
                effect.subscribeChildren();
                effect.triggerOnStartOfBattleForRelic();
            }
        }
        for (PSkill<?> effect : getEffects()) {
            effect.triggerOnCreateGeneric(this);
        }
    }

    protected void onInstantRemoval() {
        PCLUseInfo last = CombatManager.getLastInfo();
        useOnInstantRemoval(last != null ? last : CombatManager.playerSystem.generateInfo(null, owner, null), PCLActions.bottom);
    }

    public void onRemove() {
        super.onRemove();
        for (PSkill<?> effect : getEffects()) {
            effect.unsubscribeChildren();
            effect.triggerOnRemove(this);
        }
    }

    public void refreshTriggers(PCLUseInfo info) {
        for (PSkill<?> effect : getEffects()) {
            effect.refresh(info, true, false);
        }
    }

    @Override
    public void renderAmount(SpriteBatch sb, float x, float y, Color c) {
        super.renderAmount(sb, x, y, c);
        renderSecondaryAmount(sb, x, y, c);
    }

    protected void renderSecondaryAmount(SpriteBatch sb, float x, float y, Color c) {
        for (PSkill<?> effect : getEffects()) {
            if (effect instanceof PTrigger) {
                int uses = ((PTrigger) effect).getUses();
                if (!(effect instanceof PTrigger_Interactable) && uses >= 0) {
                    Color color = uses > 0 && (((PTrigger)effect).fields.forced || uses >= effect.amount) ? EUIColors.green(c.a) : EUIColors.gray(c.a);
                    FontHelper.renderFontRightTopAligned(sb, FontHelper.powerAmountFont, String.valueOf(amount), x, y + 15 * Settings.scale, fontScale, color);
                    return;
                }
            }
        }
    }

    @Override
    public void setup() {
        skills = new PSkillContainer();
    }

    @Override
    public int timesUpgraded() {
        return amount;
    }

    public void useOnInstantRemoval(PCLUseInfo info, PCLActions order) {
        for (PSkill<?> effect : getEffects()) {
            effect.use(info, order);
        }
    }
}
