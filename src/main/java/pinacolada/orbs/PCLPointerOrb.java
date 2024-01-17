package pinacolada.orbs;

import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import extendedui.EUIUtils;
import org.apache.commons.lang3.StringUtils;
import pinacolada.actions.PCLActions;
import pinacolada.dungeon.CombatManager;
import pinacolada.dungeon.PCLUseInfo;
import pinacolada.interfaces.markers.TriggerConnection;
import pinacolada.interfaces.providers.ClickableProvider;
import pinacolada.interfaces.providers.PointerProvider;
import pinacolada.powers.PCLClickableUse;
import pinacolada.skills.PSkill;
import pinacolada.skills.PSkillContainer;
import pinacolada.skills.skills.PTrigger;
import pinacolada.utilities.GameUtilities;

public abstract class PCLPointerOrb extends PCLOrb implements PointerProvider, TriggerConnection, ClickableProvider {
    public PSkillContainer skills;
    public PCLClickableUse triggerCondition;

    public PCLPointerOrb(PCLOrbData data) {
        super(data);
    }

    @Override
    public float atBlockLastModify(PCLUseInfo info, float block) {
        refresh(info);
        for (PSkill<?> effect : getEffects()) {
            block = effect.modifyBlockLast(info, block);
        }
        return block;
    }

    @Override
    public float atBlockLastModify(float block, AbstractCard c) {
        return atBlockLastModify(CombatManager.playerSystem.getInfo(c, AbstractDungeon.player, AbstractDungeon.player), block);
    }

    @Override
    public float atBlockModify(PCLUseInfo info, float block) {
        refresh(info);
        for (PSkill<?> effect : getEffects()) {
            block = effect.modifyBlockFirst(info, block);
        }
        return block;
    }

    @Override
    public float atBlockModify(float block, AbstractCard c) {
        return atBlockModify(CombatManager.playerSystem.getInfo(c, AbstractDungeon.player, AbstractDungeon.player), block);
    }

    @Override
    public int atCostModify(PCLUseInfo info, int block) {
        refresh(info);
        for (PSkill<?> effect : getEffects()) {
            block = effect.modifyCost(info, block);
        }
        return block;
    }

    @Override
    public int atCostModify(int block, AbstractCard c) {
        return atCostModify(CombatManager.playerSystem.getInfo(c, AbstractDungeon.player, AbstractDungeon.player), block);
    }


    @Override
    public float atDamageLastModify(PCLUseInfo info, float damage) {
        refresh(info);
        for (PSkill<?> effect : getEffects()) {
            damage = effect.modifyDamageGiveLast(info, damage);
        }
        return damage;
    }

    @Override
    public float atDamageLastModify(float block, AbstractCard c) {
        return atDamageLastModify(CombatManager.playerSystem.getInfo(c, AbstractDungeon.player, AbstractDungeon.player), block);
    }

    @Override
    public float atDamageModify(PCLUseInfo info, float damage) {
        refresh(info);
        for (PSkill<?> effect : getEffects()) {
            damage = effect.modifyDamageGiveFirst(info, damage);
        }
        return damage;
    }

    @Override
    public float atDamageModify(float block, AbstractCard c) {
        return atDamageModify(CombatManager.playerSystem.getInfo(c, AbstractDungeon.player, AbstractDungeon.player), block);
    }

    @Override
    public float atHealModify(PCLUseInfo info, float damage) {
        refresh(info);
        for (PSkill<?> effect : getEffects()) {
            damage = effect.modifyHeal(info, damage);
        }
        return damage;
    }

    @Override
    public float atHitCountModify(PCLUseInfo info, float damage) {
        refresh(info);
        for (PSkill<?> effect : getEffects()) {
            damage = effect.modifyHitCount(info, damage);
        }
        return damage;
    }

    @Override
    public float atRightCountModify(PCLUseInfo info, float damage) {
        refresh(info);
        for (PSkill<?> effect : getEffects()) {
            damage = effect.modifyRightCount(info, damage);
        }
        return damage;
    }

    @Override
    public float atSkillBonusModify(PCLUseInfo info, float damage) {
        refresh(info);
        for (PSkill<?> effect : getEffects()) {
            damage = effect.modifySkillBonus(info, damage);
        }
        return damage;
    }

    @Override
    public boolean canActivate(PTrigger trigger) {
        return !GameUtilities.isDeadOrEscaped(AbstractDungeon.player);
    }

    @Override
    public PCLClickableUse getClickable() {
        return triggerCondition;
    }

    @Override
    public String getID() {
        return ID;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public PSkillContainer getSkills() {
        return skills;
    }

    @Override
    public AbstractCreature getOwner() {
        return AbstractDungeon.player;
    }

    @Override
    public String getUpdatedDescription() {
        if (skills == null) {
            return EUIUtils.EMPTY_STRING;
        }
        return StringUtils.capitalize(EUIUtils.joinStringsMapNonnull(EUIUtils.SPLIT_LINE, skill -> skill.getPowerTextForDisplay(null), getEffects()));
    }

    @Override
    public void onChannel() {
        super.onChannel();
        for (PSkill<?> effect : getEffects()) {
            effect.subscribeChildren();
            effect.triggerOnCreateGeneric(this);
        }
    }

    @Override
    public void onClick() {
        super.onClick();
        if (triggerCondition != null && triggerCondition.interactable()) {
            triggerCondition.targetToUse(1);
        }
    }

    @Override
    public void onEvoke() {
        super.onEvoke();
        for (PSkill<?> effect : getEffects()) {
            effect.unsubscribeChildren();
            effect.triggerOnRemove(this);
        }
    }

    @Override
    public void passive() {
        final PCLUseInfo info = CombatManager.playerSystem.generateInfo(null, AbstractDungeon.player, target);
        for (PSkill<?> ef : getEffects()) {
            ef.use(info, PCLActions.bottom);
        }
        super.passive();
    }

    // TODO single endpoint for calling refresh
    public void refresh(PCLUseInfo info) {
        for (PSkill<?> effect : getEffects()) {
            effect.refresh(info, true, false);
        }
    }

    @Override
    public void setup() {
        skills = new PSkillContainer();
    }
}
