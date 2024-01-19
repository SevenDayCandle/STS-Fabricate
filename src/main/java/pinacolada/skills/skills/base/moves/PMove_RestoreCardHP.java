package pinacolada.skills.skills.base.moves;

import com.megacrit.cardcrawl.cards.AbstractCard;
import extendedui.EUIRM;
import extendedui.interfaces.delegates.ActionT1;
import pinacolada.actions.PCLActions;
import pinacolada.annotations.VisibleSkill;
import pinacolada.cards.base.fields.PCLCardTarget;
import pinacolada.dungeon.PCLUseInfo;
import pinacolada.resources.PGR;
import pinacolada.skills.PSkillData;
import pinacolada.skills.PSkillSaveData;
import pinacolada.skills.fields.PField_CardCategory;

@VisibleSkill
public class PMove_RestoreCardHP extends PMove_Modify<PField_CardCategory> {
    public static final PSkillData<PField_CardCategory> DATA = PMove_Modify.register(PMove_RestoreCardHP.class, PField_CardCategory.class);

    public PMove_RestoreCardHP() {
        this(1, 1);
    }

    public PMove_RestoreCardHP(int amount) {
        this(amount, 1);
    }

    public PMove_RestoreCardHP(int amount, int block) {
        super(DATA, amount, block);
    }

    public PMove_RestoreCardHP(PSkillSaveData content) {
        super(DATA, content);
    }

    @Override
    public ActionT1<AbstractCard> getAction(PCLUseInfo info, PCLActions order) {
        return (c) -> order.modifyCardHp(c, refreshAmount(info), false, true);
    }

    @Override
    public String getObjectSampleText() {
        return PGR.core.tooltips.hp.title;
    }

    @Override
    public String getObjectText() {
        return EUIRM.strings.numNoun(getAmountRawString(), PGR.core.tooltips.hp);
    }

    @Override
    public String getSubText(PCLCardTarget perspective, Object requestor) {
        return TEXT.act_healOn(getAmountRawString(),
                useParent ? getInheritedThemString() :
                        fields.hasGroups() ? fields.getFullCardString(getExtraRawString()) : TEXT.subjects_this);
    }

    @Override
    public boolean isDetrimental() {
        return amount < 0;
    }

    @Override
    public String wrapTextAmountSelf(int input) {
        return String.valueOf(input);
    }
}
