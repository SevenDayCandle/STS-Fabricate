package pinacolada.skills.skills;

import extendedui.EUIRM;
import extendedui.EUIUtils;
import pinacolada.annotations.VisibleSkill;
import pinacolada.cards.base.fields.PCLCardTarget;
import pinacolada.dungeon.PCLUseInfo;
import pinacolada.resources.pcl.PCLCoreStrings;
import pinacolada.skills.PMod;
import pinacolada.skills.PSkill;
import pinacolada.skills.PSkillData;
import pinacolada.skills.PSkillSaveData;
import pinacolada.skills.fields.PField_Empty;
import pinacolada.skills.skills.base.modifiers.PMod_MaxX;
import pinacolada.skills.skills.base.modifiers.PMod_MinX;
import pinacolada.skills.skills.base.modifiers.PMod_Range;

public abstract class PMathMod extends PMod<PField_Empty> {

    public static PMod_MaxX max(int amount) {
        return new PMod_MaxX(amount);
    }

    public static PMod_MinX min(int amount) {
        return new PMod_MinX(amount);
    }

    public static PMod_Range range(int amount) {
        return new PMod_Range(amount);
    }

    public PMathMod(PSkillData<PField_Empty> data, PSkillSaveData content) {
        super(data, content);
    }

    public PMathMod(PSkillData<PField_Empty> data) {
        super(data);
    }

    public PMathMod(PSkillData<PField_Empty> data, int amount) {
        super(data, PCLCardTarget.None, amount);
    }

    public PMathMod(PSkillData<PField_Empty> data, PCLCardTarget target, int amount) {
        super(data, target, amount);
    }

    @Override
    public String getSubText(PCLCardTarget perspective, Object requestor) {
        return EUIUtils.EMPTY_STRING;
    }

    @Override
    public String getText(PCLCardTarget perspective, Object requestor, boolean addPeriod) {
        return (childEffect != null ? childEffect.getText(perspective, requestor, true) : getSubText(perspective, requestor));
    }

    // Apply the modifier on top of this one
    @Override
    public int refreshChildAmount(PCLUseInfo info, int amount, boolean isUsing) {
        int newAmount = super.refreshChildAmount(info, amount, isUsing);
        return parent != null ? parent.refreshChildAmount(info, newAmount, isUsing) : newAmount;
    }
}
