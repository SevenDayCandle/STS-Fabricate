package pinacolada.skills.skills.base.moves;

import com.megacrit.cardcrawl.cards.AbstractCard;
import extendedui.interfaces.delegates.ActionT1;
import pinacolada.actions.PCLActions;
import pinacolada.annotations.VisibleSkill;
import pinacolada.cards.base.PCLCardGroupHelper;
import pinacolada.cards.base.fields.PCLCardTarget;
import pinacolada.dungeon.PCLUseInfo;
import pinacolada.resources.PGR;
import pinacolada.skills.PSkillData;
import pinacolada.skills.PSkillSaveData;
import pinacolada.skills.fields.PField_CardModify;
import pinacolada.ui.editor.PCLCustomEffectEditingPane;

@VisibleSkill
public class PMove_ModifyBlock extends PMove_Modify<PField_CardModify> {
    public static final PSkillData<PField_CardModify> DATA = PMove_Modify.register(PMove_ModifyBlock.class, PField_CardModify.class)
            .setAmounts(-DEFAULT_MAX, DEFAULT_MAX)
            .setExtra(0, DEFAULT_MAX)
            .noTarget();

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
    public ActionT1<AbstractCard> getAction(PCLUseInfo info, PCLActions order) {
        return (c) -> order.modifyBlock(c, refreshAmount(info), fields.forced, !fields.not, fields.or);
    }

    @Override
    public String getObjectText(Object requestor) {
        return PGR.core.tooltips.block.title;
    }

    @Override
    public String getSubText(PCLCardTarget perspective, Object requestor) {
        String base = super.getSubText(perspective, requestor);
        if (!fields.forced) {
            base = TEXT.subjects_thisTurn(base);
        }
        if (fields.or) {
            base = TEXT.subjects_untilX(base, PGR.core.tooltips.play.past());
        }
        return base;
    }

    @Override
    public boolean isDetrimental() {
        return amount < 0;
    }

    @Override
    public void setupEditor(PCLCustomEffectEditingPane editor) {
        super.setupEditor(editor);
        fields.registerFBoolean(editor, TEXT.cedit_combat, null);
        fields.registerOrBoolean(editor, getUntilPlayedString(), null);
    }
}
