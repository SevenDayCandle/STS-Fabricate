package pinacolada.skills.skills.base.moves;

import extendedui.EUIUtils;
import pinacolada.actions.PCLActions;
import pinacolada.annotations.VisibleSkill;
import pinacolada.cards.base.fields.PCLAffinity;
import pinacolada.cards.base.fields.PCLCardTarget;
import pinacolada.dungeon.PCLUseInfo;
import pinacolada.resources.PGR;
import pinacolada.skills.PMove;
import pinacolada.skills.PSkill;
import pinacolada.skills.PSkillData;
import pinacolada.skills.PSkillSaveData;
import pinacolada.skills.fields.PField_Affinity;

@VisibleSkill
public class PMove_AddLevel extends PMove<PField_Affinity> {
    public static final PSkillData<PField_Affinity> DATA = register(PMove_AddLevel.class, PField_Affinity.class)
            .pclOnly()
            .selfTarget();

    public PMove_AddLevel(PSkillSaveData content) {
        super(DATA, content);
    }

    public PMove_AddLevel(int amount, PCLAffinity... stance) {
        super(DATA, PCLCardTarget.Self, amount);
        fields.setAffinity(stance);
    }


    @Override
    public String getSampleText(PSkill<?> callingSkill) {
        return TEXT.act_gain(PGR.core.tooltips.level);
    }

    @Override
    public String getSubText() {
        String base = TEXT.act_giveTargetAmount(fields.getAffinityChoiceString(), (amount > 0 ? ("+ " + getAmountRawString()) : getAmountRawString()), plural(PGR.core.tooltips.level));
        return fields.random ? TEXT.subjects_randomly(base) : base;
    }

    @Override
    public void use(PCLUseInfo info, PCLActions order) {
        if (fields.affinities.isEmpty()) {
            order.tryChooseAffinitySkill(getName(), amount, info.source, info.target, EUIUtils.map(PCLAffinity.getAvailableAffinities(), a -> PMove.addLevel(amount, a)));
        }
        else if (fields.affinities.size() == 1) {
            order.addAffinityLevel(fields.affinities.get(0), amount);
        }
        else {
            order.tryChooseAffinitySkill(getName(), amount, info.source, info.target, EUIUtils.map(fields.affinities, a -> PMove.addLevel(amount, a)));
        }
        super.use(info, order);
    }
}
