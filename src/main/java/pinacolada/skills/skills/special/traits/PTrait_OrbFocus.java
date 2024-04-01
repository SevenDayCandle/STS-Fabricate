package pinacolada.skills.skills.special.traits;

import com.megacrit.cardcrawl.orbs.AbstractOrb;
import pinacolada.cards.base.fields.PCLCardTarget;
import pinacolada.interfaces.subscribers.OnOrbApplyFocusSubscriber;
import pinacolada.resources.PGR;
import pinacolada.skills.PSkillData;
import pinacolada.skills.PSkillSaveData;
import pinacolada.skills.PTrait;
import pinacolada.skills.fields.PField_Orb;
import pinacolada.utilities.GameUtilities;

public class PTrait_OrbFocus extends PTrait<PField_Orb> implements OnOrbApplyFocusSubscriber {

    public static final PSkillData<PField_Orb> DATA = register(PTrait_OrbFocus.class, PField_Orb.class);

    public PTrait_OrbFocus() {
        this(1);
    }

    public PTrait_OrbFocus(int amount) {
        super(DATA, amount);
    }

    public PTrait_OrbFocus(PSkillSaveData content) {
        super(DATA, content);
    }

    @Override
    public String getSubDescText(PCLCardTarget perspective, Object requestor) {
        return PGR.core.tooltips.focus.title;
    }

    @Override
    public String getSubSampleText() {
        return PGR.core.tooltips.focus.title;
    }

    @Override
    public void onApplyFocus(AbstractOrb orb) {
        GameUtilities.modifyOrbTemporaryFocus(orb, amount, true, false);
    }
}
