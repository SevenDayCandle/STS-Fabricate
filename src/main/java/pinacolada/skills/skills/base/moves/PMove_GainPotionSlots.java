package pinacolada.skills.skills.base.moves;

import com.megacrit.cardcrawl.relics.PotionBelt;
import pinacolada.actions.PCLActions;
import pinacolada.annotations.VisibleSkill;
import pinacolada.dungeon.PCLUseInfo;
import pinacolada.resources.PGR;
import pinacolada.skills.PSkill;
import pinacolada.skills.PSkillData;
import pinacolada.skills.PSkillSaveData;
import pinacolada.skills.fields.PField_Empty;
import pinacolada.utilities.GameUtilities;

@VisibleSkill
public class PMove_GainPotionSlots extends PMove_Gain {
    public static final PSkillData<PField_Empty> DATA = register(PMove_GainPotionSlots.class, PField_Empty.class)
            .setAmounts(-DEFAULT_MAX, DEFAULT_MAX)
            .noTarget();

    public PMove_GainPotionSlots() {
        this(1);
    }

    public PMove_GainPotionSlots(int amount) {
        super(DATA, amount);
    }

    public PMove_GainPotionSlots(PSkillSaveData content) {
        super(DATA, content);
    }

    @Override
    public String gainText(Object requestor) {
        return plural(PGR.core.tooltips.potionSlot, requestor);
    }

    @Override
    public String getSampleText(PSkill<?> callingSkill, PSkill<?> parentSkill) {
        return TEXT.act_gainAmount(TEXT.subjects_x, PGR.core.tooltips.potionSlot.title);
    }

    @Override
    public boolean isMetascaling() {
        return true;
    }

    @Override
    public void use(PCLUseInfo info, PCLActions order) {
        int gain = refreshAmount(info);
        order.callback(() -> {
            GameUtilities.addPotionSlots(gain);
        });

        super.use(info, order);
    }
}
