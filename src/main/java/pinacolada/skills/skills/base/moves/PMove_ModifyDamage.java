package pinacolada.skills.skills.base.moves;

import com.megacrit.cardcrawl.cards.AbstractCard;
import extendedui.EUIRM;
import extendedui.interfaces.delegates.ActionT1;
import pinacolada.annotations.VisibleSkill;
import pinacolada.cards.base.PCLCardGroupHelper;
import pinacolada.skills.PSkillData;
import pinacolada.skills.PSkillSaveData;
import pinacolada.skills.fields.PField_CardCategory;

@VisibleSkill
public class PMove_ModifyDamage extends PMove_Modify<PField_CardCategory> {
    public static final PSkillData<PField_CardCategory> DATA = PMove_Modify.register(PMove_ModifyDamage.class, PField_CardCategory.class)
            .setAmounts(-DEFAULT_MAX, DEFAULT_MAX)
            .setExtra(0, DEFAULT_MAX)
            .selfTarget();

    public PMove_ModifyDamage() {
        this(1, 1);
    }

    public PMove_ModifyDamage(int amount, int extra, PCLCardGroupHelper... groups) {
        super(DATA, amount, extra, groups);
    }

    public PMove_ModifyDamage(PSkillSaveData content) {
        super(DATA, content);
    }

    @Override
    public String getObjectSampleText() {
        return TEXT.subjects_damage;
    }

    @Override
    public String getObjectText() {
        return EUIRM.strings.numNoun(getAmountRawString(), TEXT.subjects_damage);
    }

    @Override
    public ActionT1<AbstractCard> getAction() {
        return (c) -> getActions().modifyDamage(c, amount, true, true);
    }

    @Override
    public boolean isDetrimental() {
        return extra < 0;
    }
}
