package pinacolada.skills.skills.special.moves;

import com.megacrit.cardcrawl.cards.AbstractCard;
import extendedui.EUIRM;
import extendedui.interfaces.delegates.ActionT1;
import pinacolada.resources.PGR;
import pinacolada.skills.PSkillData;
import pinacolada.skills.PSkillSaveData;
import pinacolada.skills.fields.PField_CardCategory;
import pinacolada.skills.skills.base.moves.PMove_Modify;

public class PMove_ModifyMagicNumber extends PMove_Modify<PField_CardCategory> {
    public static final PSkillData<PField_CardCategory> DATA = PMove_Modify.register(PMove_ModifyMagicNumber.class, PField_CardCategory.class)
            .pclOnly();

    public PMove_ModifyMagicNumber() {
        this(1, 1);
    }

    public PMove_ModifyMagicNumber(int amount, int priority) {
        super(DATA, amount, priority);
    }

    public PMove_ModifyMagicNumber(PSkillSaveData content) {
        super(DATA, content);
    }

    @Override
    public String getObjectSampleText() {
        return PGR.core.tooltips.magic.title;
    }

    @Override
    public String getObjectText() {
        return EUIRM.strings.numNoun(getExtraRawString(), PGR.core.tooltips.magic);
    }

    @Override
    public ActionT1<AbstractCard> getAction() {
        return (c) -> getActions().modifyMagicNumber(c, extra, true, true);
    }

    @Override
    public boolean isDetrimental() {
        return extra < 0;
    }
}
