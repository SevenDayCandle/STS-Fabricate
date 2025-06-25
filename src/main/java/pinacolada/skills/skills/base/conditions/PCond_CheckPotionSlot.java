package pinacolada.skills.skills.base.conditions;

import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import extendedui.EUIRM;
import pinacolada.annotations.VisibleSkill;
import pinacolada.cards.base.fields.PCLCardTarget;
import pinacolada.dungeon.PCLUseInfo;
import pinacolada.resources.PGR;
import pinacolada.skills.PSkill;
import pinacolada.skills.PSkillData;
import pinacolada.skills.PSkillSaveData;
import pinacolada.skills.fields.PField_Not;
import pinacolada.skills.skills.PPassiveCond;

@VisibleSkill
public class PCond_CheckPotionSlot extends PPassiveCond<PField_Not> {
    public static final PSkillData<PField_Not> DATA = register(PCond_CheckPotionSlot.class, PField_Not.class);

    public PCond_CheckPotionSlot(PSkillSaveData content) {
        super(DATA, content);
    }

    public PCond_CheckPotionSlot() {
        super(DATA, PCLCardTarget.Self, 1);
    }

    public PCond_CheckPotionSlot(int amount) {
        super(DATA, PCLCardTarget.Self, amount);
    }

    @Override
    public boolean checkCondition(PCLUseInfo info, boolean isUsing, PSkill<?> triggerSource) {
        return fields.doesValueMatchThreshold(info, AbstractDungeon.player != null ? AbstractDungeon.player.potionSlots : 0);
    }

    @Override
    public String getSampleText(PSkill<?> callingSkill, PSkill<?> parentSkill) {
        return EUIRM.strings.numNoun(TEXT.subjects_x, PGR.core.tooltips.potionSlot.title);
    }

    @Override
    public String getSubText(PCLCardTarget perspective, Object requestor) {
        return getTargetHasStringPerspective(perspective, fields.getThresholdRawString(PGR.core.tooltips.potionSlot.toString(), requestor));
    }
}
