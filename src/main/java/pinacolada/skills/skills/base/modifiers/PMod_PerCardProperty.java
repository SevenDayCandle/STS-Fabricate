package pinacolada.skills.skills.base.modifiers;

import com.megacrit.cardcrawl.cards.AbstractCard;
import extendedui.EUIRM;
import extendedui.EUIUtils;
import pinacolada.annotations.VisibleSkill;
import pinacolada.cards.base.PCLCardGroupHelper;
import pinacolada.cards.base.fields.PCLCardTarget;
import pinacolada.dungeon.PCLUseInfo;
import pinacolada.resources.PGR;
import pinacolada.skills.PSkillData;
import pinacolada.skills.PSkillSaveData;
import pinacolada.skills.fields.PField_CardCategory;
import pinacolada.ui.editor.PCLCustomEffectEditingPane;

public abstract class PMod_PerCardProperty extends PMod_Per<PField_CardCategory> {

    public PMod_PerCardProperty(PSkillData<PField_CardCategory> data, PSkillSaveData content) {
        super(data, content);
    }

    public PMod_PerCardProperty(PSkillData<PField_CardCategory> data) {
        this(data, 1);
    }

    public PMod_PerCardProperty(PSkillData<PField_CardCategory> data, int amount) {
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
    public String getSubText(PCLCardTarget perspective) {
        String subjectString = amount > 1 ? EUIRM.strings.numNoun(getAmountRawString(), getCardPropertyString()) : getCardPropertyString();
        if (useParent) {
            return TEXT.subjects_onTarget(subjectString, getInheritedThemString());
        }
        else if (fields.hasGroups()) {
            return TEXT.subjects_xOnYInZ(subjectString, fields.getFullCardString(), fields.getGroupString());
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
