package pinacolada.skills.skills.base.conditions;

import com.badlogic.gdx.graphics.Color;
import com.megacrit.cardcrawl.core.Settings;
import extendedui.utilities.EUIColors;
import pinacolada.actions.PCLActions;
import pinacolada.annotations.VisibleSkill;
import pinacolada.cards.base.fields.PCLCardTarget;
import pinacolada.dungeon.CombatManager;
import pinacolada.dungeon.PCLUseInfo;
import pinacolada.resources.PGR;
import pinacolada.resources.pcl.PCLCoreStrings;
import pinacolada.skills.PCond;
import pinacolada.skills.PSkill;
import pinacolada.skills.PSkillData;
import pinacolada.skills.PSkillSaveData;
import pinacolada.skills.fields.PField_Empty;

@VisibleSkill
public class PLimit_Limited extends PCond<PField_Empty> {
    public static final PSkillData<PField_Empty> DATA = register(PLimit_Limited.class, PField_Empty.class, 1, 1)
            .setSourceTypes(PSkillData.SourceType.Card)
            .noTarget();

    public PLimit_Limited() {
        super(DATA);
    }

    public PLimit_Limited(PSkillSaveData content) {
        super(DATA, content);
    }

    @Override
    public boolean checkCondition(PCLUseInfo info, boolean isUsing, PSkill<?> triggerSource) {
        return info.canActivateLimited;
    }

    @Override
    public Color getConditionColor() {
        return CombatManager.inBattle() && !conditionMetCache ? EUIColors.gold(0.6f) : Settings.GOLD_COLOR;
    }

    @Override
    public String getSubText(PCLCardTarget perspective, Object requestor) {
        return PGR.core.tooltips.limited.title;
    }

    @Override
    public String getText(PCLCardTarget perspective, Object requestor, boolean addPeriod) {
        return getConditionRawString(perspective, requestor, addPeriod) + (childEffect != null ? ((childEffect instanceof PCond ? EFFECT_SEPARATOR : COLON_SEPARATOR) + childEffect.getText(perspective, requestor, addPeriod)) : PCLCoreStrings.period(addPeriod));
    }

    public boolean tryPassParent(PSkill<?> source, PCLUseInfo info) {
        return info.tryActivateLimited() && super.tryPassParent(source, info);
    }

    @Override
    public void use(PCLUseInfo info, PCLActions order) {
        if (info.tryActivateLimited() && childEffect != null) {
            childEffect.use(info, order);
        }
    }

    @Override
    public void use(PCLUseInfo info, PCLActions order, boolean shouldPay) {
        if ((shouldPay ? info.tryActivateLimited() : checkCondition(info, true, null)) && childEffect != null) {
            childEffect.use(info, order);
        }
    }
}
