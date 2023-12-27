package pinacolada.skills.skills.base.moves;

import com.megacrit.cardcrawl.potions.AbstractPotion;
import com.megacrit.cardcrawl.relics.AbstractRelic;
import extendedui.EUIUtils;
import pinacolada.actions.PCLActions;
import pinacolada.annotations.VisibleSkill;
import pinacolada.augments.PCLAugment;
import pinacolada.cards.base.PCLCard;
import pinacolada.cards.base.fields.PCLCardTarget;
import pinacolada.dungeon.PCLUseInfo;
import pinacolada.interfaces.markers.OutOfCombatMove;
import pinacolada.interfaces.providers.PointerProvider;
import pinacolada.potions.PCLPotion;
import pinacolada.powers.PCLDynamicPower;
import pinacolada.relics.PCLRelic;
import pinacolada.resources.PGR;
import pinacolada.resources.pcl.PCLCoreStrings;
import pinacolada.skills.PMove;
import pinacolada.skills.PSkill;
import pinacolada.skills.PSkillData;
import pinacolada.skills.fields.PField_Numeric;
import pinacolada.ui.editor.PCLCustomEffectEditingPane;
import pinacolada.utilities.GameUtilities;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;

@VisibleSkill
public class PMove_ChangeForm extends PMove<PField_Numeric> implements OutOfCombatMove {
    public static final PSkillData<PField_Numeric> DATA = register(PMove_ChangeForm.class, PField_Numeric.class, 1, 1)
            .noTarget();

    public PMove_ChangeForm() {
        super(DATA);
    }

    public PMove_ChangeForm(Collection<Integer> relics) {
        super(DATA);
        fields.indexes.addAll(relics);
    }

    public PMove_ChangeForm(Integer... relics) {
        super(DATA);
        fields.indexes.addAll(Arrays.asList(relics));
    }

    protected void doEffect() {
        // TODO choose form randomly or through choice
        if (!fields.indexes.isEmpty()) {
            int index = fields.indexes.get(0);
            if (source instanceof PCLCard) {
                ((PCLCard) source).changeForm(index, ((PCLCard) source).timesUpgraded);
            }
            else if (source instanceof PCLRelic) {
                ((PCLRelic) source).setForm(index);
            }
            else if (source instanceof PCLPotion) {
                ((PCLPotion) source).setForm(index);
            }
            else if (source instanceof PCLDynamicPower) {
                ((PCLDynamicPower) source).setForm(index);
            }
            else if (source instanceof PCLAugment) {
                ((PCLAugment) source).setForm(index, ((PCLAugment) source).save.timesUpgraded);
            }
        }
    }

    @Override
    public String getSampleText(PSkill<?> callingSkill, PSkill<?> parentSkill) {
        return TEXT.act_changeXToY(TEXT.subjects_this, TEXT.cedit_form);
    }

    protected String getSourceName(int index) {
        if (source instanceof PointerProvider) {
            // Intentially omit upgrade level
            return GameUtilities.getMultiformName(((PointerProvider) source).getName(), index, 1, 2, 1, ((PointerProvider) source).branchFactor());
        }
        return EUIUtils.EMPTY_STRING;
    }

    @Override
    public String getSubText(PCLCardTarget perspective, Object requestor) {
        String forms = PCLCoreStrings.joinWithOr(this::getSourceName, fields.indexes);
        return TEXT.act_changeXToY(TEXT.subjects_this, forms);
    }

    @Override
    public boolean isMetascaling() {
        return source instanceof AbstractRelic || source instanceof AbstractPotion;
    }

    // Indexes should correspond to the available forms
    @Override
    public void setupEditor(PCLCustomEffectEditingPane editor) {
        super.setupEditor(editor);
        List<Integer> range = Arrays.asList(EUIUtils.range(0, editor.editor.screen.getTempBuilders().size() - 1));
        editor.registerDropdown(range, fields.indexes, item -> String.valueOf(item + 1), PGR.core.strings.cedit_form, false, false, false);
    }

    @Override
    public void use(PCLUseInfo info, PCLActions order) {
        order.callback(this::doEffect);
        super.use(info, order);
    }

    @Override
    public void useOutsideOfBattle() {
        super.useOutsideOfBattle();

        doEffect();
    }
}
