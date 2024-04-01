package pinacolada.skills.skills.base.moves;

import com.megacrit.cardcrawl.cards.AbstractCard;
import extendedui.EUIRM;
import pinacolada.actions.cards.ModifyBlockPercent;
import pinacolada.actions.cards.ModifyCard;
import pinacolada.annotations.VisibleSkill;
import pinacolada.cardmods.PermanentBlockPercentModifier;
import pinacolada.cards.base.PCLCardGroupHelper;
import pinacolada.resources.PGR;
import pinacolada.skills.PSkillData;
import pinacolada.skills.PSkillSaveData;
import pinacolada.skills.fields.PField_CardModify;

@VisibleSkill
public class PMove_PermanentModifyBlockPercent extends PMove_PermanentModify {
    public static final PSkillData<PField_CardModify> DATA = PMove_Modify.register(PMove_PermanentModifyBlockPercent.class, PField_CardModify.class)
            .setAmounts(-DEFAULT_MAX, DEFAULT_MAX)
            .setExtra(0, DEFAULT_MAX)
            .setGroups(PCLCardGroupHelper.getAll())
            .noTarget();

    public PMove_PermanentModifyBlockPercent() {
        this(1, 1);
    }

    public PMove_PermanentModifyBlockPercent(int amount, int extra, PCLCardGroupHelper... groups) {
        super(DATA, amount, extra, groups);
    }

    public PMove_PermanentModifyBlockPercent(PSkillSaveData content) {
        super(DATA, content);
    }

    @Override
    protected void applyModifierOutsideOfCombat(AbstractCard c, int amount) {
        PermanentBlockPercentModifier.apply(c, amount);
    }

    @Override
    protected ModifyCard modifyCard(AbstractCard c, int amount, boolean forced, boolean relative, boolean untilPlayed) {
        return new ModifyBlockPercent(c, amount, forced, relative, untilPlayed);
    }

    @Override
    public String getNumericalObjectText(Object requestor) {
        return EUIRM.strings.numNoun(getAmountRawString(requestor) + "%", getObjectText(requestor));
    }

    @Override
    public String getObjectSampleText() {
        return getObjectText(null) + "%";
    }

    @Override
    public String getObjectText(Object requestor) {
        return PGR.core.tooltips.block.title;
    }
}
