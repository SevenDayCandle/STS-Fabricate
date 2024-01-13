package pinacolada.skills.skills.base.moves;

import com.megacrit.cardcrawl.core.AbstractCreature;
import pinacolada.actions.PCLActions;
import pinacolada.annotations.VisibleSkill;
import pinacolada.cards.base.fields.PCLCardTarget;
import pinacolada.dungeon.PCLUseInfo;
import pinacolada.resources.PGR;
import pinacolada.skills.PSkill;
import pinacolada.skills.PSkillData;
import pinacolada.skills.PSkillSaveData;
import pinacolada.skills.fields.PField_Empty;

import java.util.ArrayList;

@VisibleSkill
public class PMove_GainBlock extends PMove_Gain {
    public static final PSkillData<PField_Empty> DATA = register(PMove_GainBlock.class, PField_Empty.class)
            .setAmounts(-DEFAULT_MAX, DEFAULT_MAX);

    public PMove_GainBlock() {
        this(1);
    }

    public PMove_GainBlock(int amount) {
        super(DATA, PCLCardTarget.Self, amount);
    }

    public PMove_GainBlock(PSkillSaveData content) {
        super(DATA, content);
    }

    public PMove_GainBlock(PCLCardTarget target, int amount) {
        super(DATA, target, amount);
    }

    @Override
    public String gainText() {
        return PGR.core.tooltips.block.toString();
    }

    @Override
    public String getSampleText(PSkill<?> callingSkill, PSkill<?> parentSkill) {
        return TEXT.act_gainAmount(TEXT.subjects_x, PGR.core.tooltips.block.title);
    }

    @Override
    public void use(PCLUseInfo info, PCLActions order) {
        for (AbstractCreature c : getTargetListAsNew(info)) {
            int actualAmount = refreshAmount(info);
            if (actualAmount < 0) {
                order.loseBlock(c, -actualAmount);
            }
            else {
                order.gainBlock(c, actualAmount);
            }
        }
        super.use(info, order);
    }
}
