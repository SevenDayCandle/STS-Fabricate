package pinacolada.skills.skills.base.moves;

import com.megacrit.cardcrawl.cards.AbstractCard;
import extendedui.EUIRM;
import extendedui.EUIUtils;
import extendedui.interfaces.delegates.ActionT1;
import pinacolada.actions.PCLActions;
import pinacolada.annotations.VisibleSkill;
import pinacolada.cards.base.fields.PCLCardTarget;
import pinacolada.cards.base.tags.PCLCardTag;
import pinacolada.dungeon.PCLUseInfo;
import pinacolada.skills.PMove;
import pinacolada.skills.PSkillData;
import pinacolada.skills.PSkillSaveData;
import pinacolada.skills.fields.PField_CardModifyTag;
import pinacolada.ui.editor.PCLCustomEffectEditingPane;

import java.util.List;

@VisibleSkill
public class PMove_ModifyTag extends PMove_Modify<PField_CardModifyTag> {
    public static final PSkillData<PField_CardModifyTag> DATA = PMove_Modify.register(PMove_ModifyTag.class, PField_CardModifyTag.class)
            .setAmounts(DEFAULT_EXTRA_MIN, DEFAULT_MAX)
            .setExtra(0, DEFAULT_MAX)
            .noTarget();

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
    public void cardAction(List<AbstractCard> cards, PCLUseInfo info, PCLActions order) {
        if (fields.or && fields.addTags.size() > 1) {
            chooseEffect(getSourceCreature(), null, order, EUIUtils.map(fields.addTags, a -> PMove.modifyTag(refreshAmount(info), extra, a)));
        }
        else {
            super.cardAction(cards, info, order);
        }
    }

    @Override
    public ActionT1<AbstractCard> getAction(PCLUseInfo info, PCLActions order) {
        return (c) -> {
            for (PCLCardTag tag : fields.addTags) {
                order.modifyTag(c, tag, refreshAmount(info), !fields.not);
            }
        };
    }

    @Override
    public String getNumericalObjectText(Object requestor) {
        return amount > 1 ? EUIRM.strings.numNoun(getAmountRawString(requestor), getObjectText(requestor)) : getObjectText(requestor);
    }

    @Override
    public String getObjectSampleText() {
        return TEXT.cedit_tags;
    }

    @Override
    public String getObjectText(Object requestor) {
        return fields.getAddTagChoiceString();
    }

    @Override
    public String getSubText(PCLCardTarget perspective, Object requestor) {
        return amount < 0 ? getBasicRemoveString(requestor) : getBasicGiveString(requestor);
    }

    @Override
    public void setupEditor(PCLCustomEffectEditingPane editor) {
        super.setupEditor(editor);
        fields.registerOrBoolean(editor);
    }

    @Override
    public String wrapTextAmountSelf(int input) {
        return String.valueOf(Math.abs(input));
    }
}
