package pinacolada.skills.skills.base.conditions;

import extendedui.EUIRM;
import extendedui.EUIUtils;
import pinacolada.annotations.VisibleSkill;
import pinacolada.cards.base.PCLCardGroupHelper;
import pinacolada.cards.base.fields.PCLCardTarget;
import pinacolada.dungeon.PCLUseInfo;
import pinacolada.skills.PSkill;
import pinacolada.skills.PSkillData;
import pinacolada.skills.PSkillSaveData;
import pinacolada.skills.fields.PField_CardCategory;
import pinacolada.skills.skills.PPassiveCond;

@VisibleSkill
public class PCond_PileHas extends PPassiveCond<PField_CardCategory> {
    public static final PSkillData<PField_CardCategory> DATA = register(PCond_PileHas.class, PField_CardCategory.class)
            .setGroups(PCLCardGroupHelper.getAll())
            .noTarget();

    public PCond_PileHas() {
        this(1);
    }

    public PCond_PileHas(int amount) {
        super(DATA, PCLCardTarget.None, amount);
    }

    public PCond_PileHas(PSkillSaveData content) {
        super(DATA, content);
    }

    public PCond_PileHas(int amount, PCLCardGroupHelper... groups) {
        super(DATA, PCLCardTarget.None, amount);
        fields.setCardGroup(groups);
    }

    @Override
    public boolean checkCondition(PCLUseInfo info, boolean isUsing, PSkill<?> triggerSource) {
        int count = EUIUtils.sumInt(fields.groupTypes, g -> EUIUtils.count(g.getCards(),
                c -> fields.getFullCardFilter().invoke(c) && c != info.card));
        return amount == 0 ? count == 0 : fields.not ^ count >= refreshAmount(info);
    }

    @Override
    public String getSampleText(PSkill<?> callingSkill, PSkill<?> parentSkill) {
        return TEXT.cond_ifX(EUIRM.strings.adjNoun(TEXT.subjects_card, TEXT.cpile_pile));
    }

    @Override
    public String getSubText(PCLCardTarget perspective, Object requestor) {
        // Set ordinal to 1 to treat as a singular target
        return TEXT.cond_ifTargetHas(fields.getGroupString(), 1, fields.getThresholdRawString(fields.getFullCardString(requestor), requestor));
    }
}
