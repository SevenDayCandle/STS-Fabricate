package pinacolada.skills.skills.base.traits;

import org.apache.commons.lang3.StringUtils;
import pinacolada.actions.PCLActions;
import pinacolada.annotations.VisibleSkill;
import pinacolada.cards.base.fields.PCLCardTarget;
import pinacolada.dungeon.PCLUseInfo;
import pinacolada.resources.PGR;
import pinacolada.skills.PSkill;
import pinacolada.skills.PSkillData;
import pinacolada.skills.PSkillSaveData;
import pinacolada.skills.PTrait;
import pinacolada.skills.fields.PField_Empty;
import pinacolada.skills.skills.base.primary.PTrigger_Passive;

@VisibleSkill
public class PTrait_Unplayable extends PTrait<PField_Empty> {
    public static final PSkillData<PField_Empty> DATA = register(PTrait_Unplayable.class, PField_Empty.class)
            .setAmounts(1, 1);

    public PTrait_Unplayable() {
        this(1);
    }

    public PTrait_Unplayable(int amount) {
        super(DATA, amount);
    }

    public PTrait_Unplayable(PSkillSaveData content) {
        super(DATA, content);
    }

    @Override
    public boolean canPlay(PCLUseInfo info, PSkill<?> triggerSource, boolean origValue) {
        return triggerSource == null && info.card != source;
    }

    @Override
    public String getSampleText(PSkill<?> callingSkill, PSkill<?> parentSkill) {
        return PGR.core.tooltips.unplayable.title;
    }

    @Override
    public String getSubDescText(PCLCardTarget perspective, Object requestor) {
        return PGR.core.tooltips.unplayable.title;
    }

    @Override
    public String getSubSampleText() {
        return PGR.core.tooltips.unplayable.title;
    }

    @Override
    public String getSubText(PCLCardTarget perspective, Object requestor) {
        if (hasParentType(PTrigger_Passive.class)) {
            return TEXT.act_zCannot(TEXT.subjects_you, StringUtils.lowerCase(PGR.core.tooltips.play.title), getParentCardString(perspective, requestor));
        }
        return getSubDescText(perspective, requestor);
    }

    @Override
    public boolean isDetrimental() {
        return true;
    }

    @Override
    public void use(PCLUseInfo info, PCLActions order) {

    }
}
