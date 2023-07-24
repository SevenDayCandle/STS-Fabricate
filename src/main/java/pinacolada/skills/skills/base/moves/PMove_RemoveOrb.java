package pinacolada.skills.skills.base.moves;

import com.megacrit.cardcrawl.actions.common.RemoveAllBlockAction;
import com.megacrit.cardcrawl.core.AbstractCreature;
import pinacolada.actions.PCLActions;
import pinacolada.annotations.VisibleSkill;
import pinacolada.cards.base.fields.PCLCardTarget;
import pinacolada.dungeon.PCLUseInfo;
import pinacolada.orbs.PCLOrbHelper;
import pinacolada.resources.PGR;
import pinacolada.skills.PMove;
import pinacolada.skills.PSkill;
import pinacolada.skills.PSkillData;
import pinacolada.skills.PSkillSaveData;
import pinacolada.skills.fields.PField_Empty;
import pinacolada.skills.fields.PField_Orb;
import pinacolada.utilities.GameUtilities;

@VisibleSkill
public class PMove_RemoveOrb extends PMove<PField_Orb> {
    public static final PSkillData<PField_Orb> DATA = register(PMove_RemoveOrb.class, PField_Orb.class).selfTarget();

    public PMove_RemoveOrb(PSkillSaveData content) {
        super(DATA, content);
    }

    public PMove_RemoveOrb() {
        super(DATA, PCLCardTarget.Self, 1);
    }

    public PMove_RemoveOrb(PCLOrbHelper... orb) {
        super(DATA, PCLCardTarget.Self, 1);
        fields.setOrb(orb);
    }

    @Override
    public String getSampleText(PSkill<?> callingSkill, PSkill<?> parentSkill) {
        return TEXT.act_remove(PGR.core.tooltips.orb.title);
    }

    @Override
    public boolean isDetrimental() {
        return true;
    }

    @Override
    public String getSubText(PCLCardTarget perspective) {
        return TEXT.act_remove(fields.getOrbAmountString());
    }

    @Override
    public void use(PCLUseInfo info, PCLActions order) {
        order.removeOrb(amount <= 0 ? GameUtilities.getOrbCount() : amount, fields.random).setFilter(fields.orbs.isEmpty() ? null : fields.getOrbFilter());
        super.use(info, order);
    }
}
