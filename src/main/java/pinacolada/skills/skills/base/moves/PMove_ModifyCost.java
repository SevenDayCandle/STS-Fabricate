package pinacolada.skills.skills.base.moves;

import com.megacrit.cardcrawl.cards.AbstractCard;
import extendedui.EUIRM;
import extendedui.interfaces.delegates.ActionT1;
import pinacolada.actions.cards.ModifyCost;
import pinacolada.annotations.VisibleSkill;
import pinacolada.cards.base.PCLCardGroupHelper;
import pinacolada.skills.PSkillData;
import pinacolada.skills.PSkillSaveData;
import pinacolada.skills.fields.PField_CardCategory;

@VisibleSkill
public class PMove_ModifyCost extends PMove_Modify<PField_CardCategory> {
    public static final PSkillData<PField_CardCategory> DATA = PMove_Modify.register(PMove_ModifyCost.class, PField_CardCategory.class)
            .setAmounts(-DEFAULT_MAX, DEFAULT_MAX)
            .setExtra(0, DEFAULT_MAX)
            .selfTarget();

    public PMove_ModifyCost() {
        this(1, 1);
    }

    public PMove_ModifyCost(int amount, int extra) {
        super(DATA, amount, extra);
    }

    public PMove_ModifyCost(PSkillSaveData content) {
        super(DATA, content);
    }

    public PMove_ModifyCost(int amount, int extra, PCLCardGroupHelper... groups) {
        super(DATA, amount, extra, groups);
    }

    @Override
    public String getObjectSampleText() {
        return TEXT.subjects_cost;
    }

    @Override
    public String getObjectText() {
        String base = EUIRM.strings.numNoun(getAmountRawString(), TEXT.subjects_cost);
        return fields.forced ? TEXT.subjects_thisTurn(base) : EUIRM.strings.numNoun(getAmountRawString(), TEXT.subjects_cost);
    }

    @Override
    public boolean canCardPass(AbstractCard c) {
        return fields.getFullCardFilter().invoke(c) && ModifyCost.canCardPass(c, amount);
    }

    @Override
    public ActionT1<AbstractCard> getAction() {
        return (c) -> getActions().modifyCost(c, amount, !fields.forced, true);
    }

    @Override
    public boolean isDetrimental() {
        return extra > 0;
    }
}
