package pinacolada.skills.skills.base.moves;

import com.megacrit.cardcrawl.cards.AbstractCard;
import pinacolada.actions.PCLActions;
import pinacolada.annotations.VisibleSkill;
import pinacolada.cards.base.fields.PCLCardTarget;
import pinacolada.dungeon.PCLUseInfo;
import pinacolada.orbs.PCLOrbHelper;
import pinacolada.skills.PMove;
import pinacolada.skills.PSkill;
import pinacolada.skills.PSkillData;
import pinacolada.skills.PSkillSaveData;
import pinacolada.skills.fields.PField_Orb;
import pinacolada.utilities.GameUtilities;

@VisibleSkill
public class PMove_EvokeOrb extends PMove<PField_Orb> {
    public static final PSkillData<PField_Orb> DATA = register(PMove_EvokeOrb.class, PField_Orb.class)
            .setExtra(0, Integer.MAX_VALUE)
            .noTarget();

    public PMove_EvokeOrb() {
        this(1, 1);
    }

    public PMove_EvokeOrb(int amount, int orbs, PCLOrbHelper... orb) {
        super(DATA, PCLCardTarget.None, amount, orbs);
        fields.setOrb(orb);
    }

    public PMove_EvokeOrb(PSkillSaveData content) {
        super(DATA, content);
    }

    public PMove_EvokeOrb(int amount, PCLOrbHelper... orb) {
        this(amount, 1, orb);
    }

    @Override
    public String getSampleText(PSkill<?> callingSkill, PSkill<?> parentSkill) {
        return TEXT.act_evoke(TEXT.subjects_x);
    }

    @Override
    public String getSubText(PCLCardTarget perspective, Object requestor) {
        String orbStr = fields.getOrbExtraString();
        return amount == 1 ? TEXT.act_evoke(orbStr) : TEXT.act_evokeXTimes(orbStr, getAmountRawString());
    }

    @Override
    public PMove_EvokeOrb onAddToCard(AbstractCard card) {
        super.onAddToCard(card);
        card.showEvokeValue = amount > 0;
        card.showEvokeOrbCount = amount;
        return this;
    }

    @Override
    public void use(PCLUseInfo info, PCLActions order) {
        order.evokeOrb(amount, extra <= 0 ? GameUtilities.getOrbCount() : extra, fields.random).setFilter(fields.orbs.isEmpty() ? null : fields.getOrbFilter());
        super.use(info, order);
    }
}
