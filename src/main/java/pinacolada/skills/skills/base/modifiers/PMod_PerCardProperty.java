package pinacolada.skills.skills.base.modifiers;

import com.megacrit.cardcrawl.cards.AbstractCard;
import extendedui.EUIRM;
import extendedui.EUIUtils;
import pinacolada.cards.base.fields.PCLCardTarget;
import pinacolada.dungeon.PCLUseInfo;
import pinacolada.resources.PGR;
import pinacolada.skills.PSkillData;
import pinacolada.skills.PSkillSaveData;
import pinacolada.skills.fields.PField_CardCategory;
import pinacolada.ui.editor.PCLCustomEffectEditingPane;

public abstract class PMod_PerCardProperty<T extends PField_CardCategory> extends PMod_Per<T> {

    public PMod_PerCardProperty(PSkillData<T> data, PSkillSaveData content) {
        super(data, content);
    }

    public PMod_PerCardProperty(PSkillData<T> data) {
        this(data, 1);
    }

    public PMod_PerCardProperty(PSkillData<T> data, int amount) {
        super(data, amount);
    }

    @Override
    public int getMultiplier(PCLUseInfo info, boolean isUsing) {
        return EUIUtils.sumInt(fields.getCardGroup(info), g -> EUIUtils.sumInt(g.group, this::getCardProperty)) / PGR.dungeon.getDivisor();
    }

    @Override
    public String getSubSampleText() {
        return EUIRM.strings.adjNoun(TEXT.subjects_card, getCardPropertyString());
    }

    @Override
    public String getSubText(PCLCardTarget perspective, Object requestor) {
        String subjectString = getCardPropertyString();
        if (useParent) {
            return TEXT.subjects_onTarget(subjectString, getInheritedThemString());
        }
        else if (fields.hasGroups()) {
            return TEXT.subjects_xOnYInZ(subjectString, fields.getFullCardString(requestor), fields.getGroupString());
        }
        return TEXT.subjects_onTarget(subjectString, TEXT.subjects_thisCard());
    }

    @Override
    public void setupEditor(PCLCustomEffectEditingPane editor) {
        super.setupEditor(editor);
        registerUseParentBoolean(editor);
    }

    protected abstract int getCardProperty(AbstractCard c);

    protected abstract String getCardPropertyString();
}
