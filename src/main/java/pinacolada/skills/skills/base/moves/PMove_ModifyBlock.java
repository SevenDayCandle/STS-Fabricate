package pinacolada.skills.skills.base.moves;

import com.megacrit.cardcrawl.cards.AbstractCard;
import extendedui.EUIRM;
import extendedui.interfaces.delegates.ActionT1;
import pinacolada.annotations.VisibleSkill;
import pinacolada.cards.base.PCLCardGroupHelper;
import pinacolada.resources.PGR;
import pinacolada.skills.PSkillData;
import pinacolada.skills.PSkillSaveData;
import pinacolada.skills.fields.PField_CardCategory;

@VisibleSkill
public class PMove_ModifyBlock extends PMove_Modify<PField_CardCategory> {
    public static final PSkillData<PField_CardCategory> DATA = PMove_Modify.register(PMove_ModifyBlock.class, PField_CardCategory.class)
            .setAmounts(-DEFAULT_MAX, DEFAULT_MAX)
            .setExtra(0, DEFAULT_MAX)
            .selfTarget();

    public PMove_ModifyBlock() {
        this(1, 1);
    }

    public PMove_ModifyBlock(int amount, int extra) {
        super(DATA, amount, extra);
    }

    public PMove_ModifyBlock(PSkillSaveData content) {
        super(DATA, content);
    }

    public PMove_ModifyBlock(int amount, int extra, PCLCardGroupHelper... groups) {
        super(DATA, amount, extra, groups);
    }

    @Override
    public String getObjectSampleText() {
        return PGR.core.tooltips.block.title;
    }

    @Override
    public String getObjectText() {
        return EUIRM.strings.numNoun(getAmountRawString(), PGR.core.tooltips.block);
    }

    @Override
    public ActionT1<AbstractCard> getAction() {
        return (c) -> getActions().modifyBlock(c, amount, true, true);
    }

    @Override
    public boolean isDetrimental() {
        return amount < 0;
    }
}
