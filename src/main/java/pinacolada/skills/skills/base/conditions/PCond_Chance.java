package pinacolada.skills.skills.base.conditions;

import com.badlogic.gdx.graphics.Color;
import com.megacrit.cardcrawl.core.Settings;
import pinacolada.annotations.VisibleSkill;
import pinacolada.cards.base.fields.PCLCardTarget;
import pinacolada.dungeon.PCLUseInfo;
import pinacolada.resources.PGR;
import pinacolada.skills.PSkill;
import pinacolada.skills.PSkillData;
import pinacolada.skills.PSkillSaveData;
import pinacolada.skills.fields.PField_Not;
import pinacolada.skills.skills.PPassiveCond;
import pinacolada.utilities.GameUtilities;

@VisibleSkill
public class PCond_Chance extends PPassiveCond<PField_Not> {
    public static final PSkillData<PField_Not> DATA = register(PCond_Chance.class, PField_Not.class)
            .noTarget();

    public PCond_Chance(PSkillSaveData content) {
        super(DATA, content);
    }

    public PCond_Chance() {
        super(DATA, PCLCardTarget.None, 1);
    }

    public PCond_Chance(int amount) {
        super(DATA, PCLCardTarget.None, amount);
    }

    @Override
    public boolean checkCondition(PCLUseInfo info, boolean isUsing, PSkill<?> triggerSource) {
        // In the case of triggerSource not being null, this will be passed through twice, once with triggerSource, and once without
        // On the actual usage call, we need to reset conditionMetCache so that subsequent triggers immediately afterwards are independent
        if (isUsing) {
            boolean val = conditionMetCache;
            if (conditionMetCache && triggerSource == null) {
                conditionMetCache = GameUtilities.chance(amount);
            }
            return val;
        }
        return GameUtilities.chance(amount);
    }

    @Override
    public Color getConditionColor() {
        return Settings.GOLD_COLOR;
    }

    @Override
    public String getSampleText(PSkill<?> callingSkill, PSkill<?> parentSkill) {
        return TEXT.act_generic2(PGR.core.tooltips.chance.title, "X%");
    }

    @Override
    public String getSubText(PCLCardTarget perspective) {
        return TEXT.cond_ifX(TEXT.act_generic2(PGR.core.tooltips.chance.title, getAmountRawString() + "%"));
    }
}
