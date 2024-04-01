package pinacolada.skills.skills.base.conditions;

import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import extendedui.EUIRM;
import extendedui.EUIUtils;
import pinacolada.annotations.VisibleSkill;
import pinacolada.cards.base.fields.PCLCardTarget;
import pinacolada.dungeon.PCLUseInfo;
import pinacolada.skills.PSkill;
import pinacolada.skills.PSkillData;
import pinacolada.skills.PSkillSaveData;
import pinacolada.skills.fields.PField_Blight;
import pinacolada.skills.skills.PPassiveCond;

@VisibleSkill
public class PCond_CheckBlight extends PPassiveCond<PField_Blight> {
    public static final PSkillData<PField_Blight> DATA = register(PCond_CheckBlight.class, PField_Blight.class)
            .noTarget();

    public PCond_CheckBlight() {
        super(DATA, PCLCardTarget.None, 1);
    }

    public PCond_CheckBlight(PSkillSaveData content) {
        super(DATA, content);
    }

    public PCond_CheckBlight(int amount, String... blight) {
        super(DATA, PCLCardTarget.None, amount);
        fields.setBlightID(blight);
    }

    @Override
    public boolean checkCondition(PCLUseInfo info, boolean isUsing, PSkill<?> triggerSource) {
        int count = EUIUtils.count(AbstractDungeon.player.blights, c -> fields.getFullBlightFilter().invoke(c));
        return amount == 0 ? count == 0 : fields.random ^ count >= refreshAmount(info);
    }

    @Override
    public String getSampleText(PSkill<?> callingSkill, PSkill<?> parentSkill) {
        return EUIRM.strings.numNoun(TEXT.subjects_x, TEXT.subjects_blight);
    }

    @Override
    public String getSubText(PCLCardTarget perspective, Object requestor) {
        return getTargetHasStringPerspective(perspective, EUIRM.strings.numNoun(getAmountRawString(requestor), fields.getFullBlightString(requestor)));

    }
}
