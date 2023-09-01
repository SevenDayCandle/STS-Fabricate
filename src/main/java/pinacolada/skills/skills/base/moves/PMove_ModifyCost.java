package pinacolada.skills.skills.base.moves;

import com.megacrit.cardcrawl.cards.AbstractCard;
import extendedui.interfaces.delegates.ActionT1;
import pinacolada.actions.PCLActions;
import pinacolada.actions.cards.ModifyCost;
import pinacolada.annotations.VisibleSkill;
import pinacolada.cards.base.PCLCardGroupHelper;
import pinacolada.cards.base.fields.PCLCardTarget;
import pinacolada.resources.PGR;
import pinacolada.skills.PSkillData;
import pinacolada.skills.PSkillSaveData;
import pinacolada.skills.fields.PField_CardModify;
import pinacolada.ui.editor.PCLCustomEffectEditingPane;

@VisibleSkill
public class PMove_ModifyCost extends PMove_Modify<PField_CardModify> {
    public static final PSkillData<PField_CardModify> DATA = PMove_Modify.register(PMove_ModifyCost.class, PField_CardModify.class)
            .setAmounts(-DEFAULT_MAX, DEFAULT_MAX)
            .setExtra(0, DEFAULT_MAX)
            .noTarget();

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
    public boolean canCardPass(AbstractCard c) {
        return fields.getFullCardFilter().invoke(c) && ModifyCost.canCardPass(c, amount);
    }

    @Override
    public ActionT1<AbstractCard> getAction(PCLActions order) {
        return (c) -> order.modifyCost(c, amount, fields.forced, !fields.not, fields.or);
    }

    @Override
    public String getObjectText() {
        return TEXT.subjects_cost;
    }

    @Override
    public String getSubText(PCLCardTarget perspective) {
        String base = useParent ? TEXT.act_zCosts(getInheritedTheyString(), parent != null ? parent.baseAmount : 1, getAmountRawString()) : super.getSubText(perspective);
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
        return !fields.not && extra > 0;
    }

    @Override
    public void setupEditor(PCLCustomEffectEditingPane editor) {
        super.setupEditor(editor);
        fields.registerFBoolean(editor, TEXT.cedit_combat, null);
        fields.registerOrBoolean(editor, getUntilPlayedString(), null);
    }
}
