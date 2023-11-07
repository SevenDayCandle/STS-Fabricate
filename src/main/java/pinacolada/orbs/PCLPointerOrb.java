package pinacolada.orbs;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
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
import pinacolada.skills.delay.DelayTiming;
import pinacolada.skills.skills.PTrigger;
import pinacolada.utilities.GameUtilities;

public abstract class PCLPointerOrb extends PCLOrb implements PointerProvider, TriggerConnection, ClickableProvider {
    public PSkillContainer skills;
    public PCLClickableUse triggerCondition;

    public PCLPointerOrb(PCLOrbData data) {
        super(data);
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
        return StringUtils.capitalize(EUIUtils.joinStringsMapNonnull(EUIUtils.SPLIT_LINE, skill -> skill.getPowerText(null), getEffects()));
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
    public void onEndOfTurn() {
        if (timing == DelayTiming.EndOfTurnFirst) {
            passive();
        }
        for (PSkill<?> effect : getEffects()) {
            effect.triggerOnEndOfTurn(true);
        }
        if (timing == DelayTiming.EndOfTurnLast) {
            passive();
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
    public void onStartOfTurn() {
        if (timing == DelayTiming.StartOfTurnFirst) {
            passive();
        }
        for (PSkill<?> effect : getEffects()) {
            effect.triggerOnStartOfTurn();
        }
        if (timing == DelayTiming.StartOfTurnLast) {
            passive();
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

    @Override
    public void setup() {
        skills = new PSkillContainer();
    }
}
