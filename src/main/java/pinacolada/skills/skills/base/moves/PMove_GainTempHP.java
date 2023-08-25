package pinacolada.skills.skills.base.moves;

import com.megacrit.cardcrawl.core.AbstractCreature;
import pinacolada.actions.PCLActions;
import pinacolada.annotations.VisibleSkill;
import pinacolada.cards.base.fields.PCLCardTarget;
import pinacolada.dungeon.PCLUseInfo;
import pinacolada.resources.PGR;
import pinacolada.skills.PMove;
import pinacolada.skills.PSkill;
import pinacolada.skills.PSkillData;
import pinacolada.skills.PSkillSaveData;
import pinacolada.skills.fields.PField_Empty;

@VisibleSkill
public class PMove_GainTempHP extends PMove<PField_Empty> {
    public static final PSkillData<PField_Empty> DATA = register(PMove_GainTempHP.class, PField_Empty.class);

    public PMove_GainTempHP() {
        this(1);
    }

    public PMove_GainTempHP(int amount) {
        super(DATA, PCLCardTarget.None, amount);
    }

    public PMove_GainTempHP(PSkillSaveData content) {
        super(DATA, content);
    }

    public PMove_GainTempHP(PCLCardTarget target, int amount) {
        super(DATA, target, amount);
    }

    @Override
    public String getSampleText(PSkill<?> callingSkill, PSkill<?> parentSkill) {
        return TEXT.act_gainAmount(TEXT.subjects_x, PGR.core.tooltips.tempHP.title);
    }

    @Override
    public String getSubText(PCLCardTarget perspective) {
        if (isSelfOnlyTarget(perspective)) {
            return TEXT.act_gainAmount(getAmountRawString(), PGR.core.tooltips.tempHP);
        }
        return TEXT.act_giveTargetAmount(getTargetStringPerspective(perspective), getAmountRawString(), PGR.core.tooltips.tempHP);
    }

    @Override
    public void use(PCLUseInfo info, PCLActions order) {
        for (AbstractCreature c : getTargetList(info)) {
            order.gainTemporaryHP(c, c, amount);
        }
        super.use(info, order);
    }
}
