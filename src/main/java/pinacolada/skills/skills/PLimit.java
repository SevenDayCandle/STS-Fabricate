package pinacolada.skills.skills;

import com.badlogic.gdx.graphics.Color;
import com.megacrit.cardcrawl.core.Settings;
import extendedui.utilities.EUIColors;
import pinacolada.actions.PCLActions;
import pinacolada.cards.base.fields.PCLCardTarget;
import pinacolada.dungeon.CombatManager;
import pinacolada.dungeon.PCLUseInfo;
import pinacolada.resources.pcl.PCLCoreStrings;
import pinacolada.skills.*;
import pinacolada.skills.fields.PField_Empty;
import pinacolada.skills.skills.base.primary.PLimit_Limited;
import pinacolada.skills.skills.base.primary.PLimit_SemiLimited;

public abstract class PLimit extends PPrimary<PField_Empty> {
    protected boolean limitCache = false;

    public PLimit(PSkillData<PField_Empty> data, PSkillSaveData content) {
        super(data, content);
    }

    public PLimit(PSkillData<PField_Empty> data) {
        super(data, PCLCardTarget.None, 0);
    }

    public static PLimit_Limited limited() {
        return new PLimit_Limited();
    }

    public static PLimit_SemiLimited semiLimited() {
        return new PLimit_SemiLimited();
    }

    @Override
    public Color getConditionColor() {
        return CombatManager.inBattle() && !limitCache ? EUIColors.gold(0.6f) : Settings.GOLD_COLOR;
    }

    @Override
    public String getText(PCLCardTarget perspective, Object requestor, boolean addPeriod) {
        return getConditionRawString(perspective, requestor, addPeriod) + (childEffect != null ? ((childEffect instanceof PCond ? EFFECT_SEPARATOR : COLON_SEPARATOR) + childEffect.getText(perspective, requestor, addPeriod)) : PCLCoreStrings.period(addPeriod));
    }

    @Override
    public void refresh(PCLUseInfo info, boolean conditionMet, boolean isUsing) {
        limitCache = canActivate(info);
        super.refresh(info, limitCache & conditionMet, isUsing);
    }

    public PLimit setChild(PSkill<?> effect) {
        super.setChild(effect);
        return this;
    }

    public PLimit setChild(PSkill<?>... effects) {
        super.setChild(effects);
        return this;
    }

    public boolean tryPassParent(PSkill<?> source, PCLUseInfo info) {
        return tryActivate(info);
    }

    @Override
    public void use(PCLUseInfo info, PCLActions order) {
        if (tryActivate(info) && childEffect != null) {
            childEffect.use(info, order);
        }
    }

    @Override
    public void use(PCLUseInfo info, PCLActions order, boolean shouldPay) {
        if ((shouldPay ? tryActivate(info) : canActivate(info)) && childEffect != null) {
            childEffect.use(info, order);
        }
    }

    abstract public boolean canActivate(PCLUseInfo info);

    abstract public boolean tryActivate(PCLUseInfo info);
}
