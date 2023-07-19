package pinacolada.skills.skills.base.moves;

import com.megacrit.cardcrawl.cards.AbstractCard;
import extendedui.EUIRM;
import extendedui.interfaces.delegates.ActionT1;
import pinacolada.actions.PCLActions;
import pinacolada.annotations.VisibleSkill;
import pinacolada.cards.base.fields.PCLAffinity;
import pinacolada.cards.base.fields.PCLCardTarget;
import pinacolada.cards.base.tags.PCLCardTag;
import pinacolada.skills.PSkillData;
import pinacolada.skills.PSkillSaveData;
import pinacolada.skills.fields.PField_CardModifyTag;

@VisibleSkill
public class PMove_ModifyTag extends PMove_Modify<PField_CardModifyTag> {
    public static final PSkillData<PField_CardModifyTag> DATA = PMove_Modify.register(PMove_ModifyTag.class, PField_CardModifyTag.class)
            .setExtra(-PCLAffinity.MAX_LEVEL, PCLAffinity.MAX_LEVEL)
            .setExtra(0, DEFAULT_MAX)
            .selfTarget();

    public PMove_ModifyTag() {
        this(1, 1);
    }

    public PMove_ModifyTag(int amount, int extra, PCLCardTag... tags) {
        super(DATA, amount, extra);
        fields.setAddTag(tags);
    }

    public PMove_ModifyTag(PSkillSaveData content) {
        super(DATA, content);
    }

    @Override
    public String getNumericalObjectText() {
        return amount > 1 ? EUIRM.strings.numNoun(getAmountRawString(), getObjectText()) : getObjectText();
    }

    @Override
    public String getObjectSampleText() {
        return TEXT.cedit_tags;
    }

    @Override
    public String getSubText(PCLCardTarget perspective) {
        return getBasicGiveString();
    }

    @Override
    public ActionT1<AbstractCard> getAction(PCLActions order) {
        return (c) -> {
            for (PCLCardTag tag : fields.addTags) {
                order.modifyTag(c, tag, amount, !fields.not);
            }
        };
    }

    @Override
    public String getObjectText() {
        return fields.getAddTagChoiceString();
    }
}
