package pinacolada.skills.skills.base.moves;

import com.megacrit.cardcrawl.cards.AbstractCard;
import extendedui.interfaces.delegates.ActionT1;
import pinacolada.actions.PCLActions;
import pinacolada.annotations.VisibleSkill;
import pinacolada.dungeon.PCLUseInfo;
import pinacolada.resources.PGR;
import pinacolada.skills.PSkillData;
import pinacolada.skills.PSkillSaveData;
import pinacolada.skills.fields.PField_CardCategory;

@VisibleSkill
public class PMove_ModifyCounter extends PMove_Modify<PField_CardCategory> {
    public static final PSkillData<PField_CardCategory> DATA = PMove_Modify.register(PMove_ModifyCounter.class, PField_CardCategory.class)
            .setAmounts(-DEFAULT_MAX, DEFAULT_MAX)
            .setExtra(0, DEFAULT_MAX)
            .noTarget();

    public PMove_ModifyCounter() {
        this(1, 1);
    }

    public PMove_ModifyCounter(int amount, int priority) {
        super(DATA, amount, priority);
    }

    public PMove_ModifyCounter(PSkillSaveData content) {
        super(DATA, content);
    }

    @Override
    public ActionT1<AbstractCard> getAction(PCLUseInfo info, PCLActions order) {
        return (c) -> order.modifyMagicNumber(c, refreshAmount(info), true, true);
    }

    @Override
    public String getObjectText() {
        return PGR.core.tooltips.counter.title;
    }
}
