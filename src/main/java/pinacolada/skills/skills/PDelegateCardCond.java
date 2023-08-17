package pinacolada.skills.skills;

import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.core.AbstractCreature;
import extendedui.ui.tooltips.EUIKeywordTooltip;
import pinacolada.actions.PCLActions;
import pinacolada.cards.base.fields.PCLCardTarget;
import pinacolada.dungeon.PCLUseInfo;
import pinacolada.skills.PSkill;
import pinacolada.skills.PSkillData;
import pinacolada.skills.PSkillSaveData;
import pinacolada.skills.fields.PField_CardCategory;
import pinacolada.ui.editor.PCLCustomEffectEditingPane;

import java.util.Collections;

public abstract class PDelegateCardCond extends PDelegateCond<PField_CardCategory> {
    public PDelegateCardCond(PSkillData<PField_CardCategory> data) {
        super(data, PCLCardTarget.None, 0);
    }

    public PDelegateCardCond(PSkillData<PField_CardCategory> data, PSkillSaveData content) {
        super(data, content);
    }

    @Override
    public boolean canPlay(PCLUseInfo info, PSkill<?> triggerSource) {
        return true;
    }

    public String getDelegatePastText() {
        return getDelegateTooltip().past();
    }

    // This should not activate the child effect when played normally

    public String getDelegateSampleText() {
        return getDelegateText();
    }

    public String getDelegateText() {
        return getDelegateTooltip().title;
    }

    @Override
    public String getSampleText(PSkill<?> callingSkill, PSkill<?> parentSkill) {
        return isUnderWhen(callingSkill, parentSkill) ? TEXT.cond_aObjectIs(TEXT.subjects_x, getDelegateSampleText()) : TEXT.cond_onGeneric(getDelegateSampleText());
    }

    @Override
    public String getSubText(PCLCardTarget perspective) {
        if (isBranch()) {
            return getWheneverYouString(getDelegateText());
        }
        if (isWhenClause()) {
            return TEXT.cond_aObjectIs(fields.getFullCardStringSingular(), getDelegatePastText());
        }
        return TEXT.cond_onGeneric(getDelegateText());
    }

    @Override
    public void setupEditor(PCLCustomEffectEditingPane editor) {
        if (isWhenClause() || isBranch()) {
            fields.setupEditor(editor);
        }
    }

    public void triggerOnCard(AbstractCard c) {
        if (fields.getFullCardFilter().invoke(c)) {
            useFromTrigger(generateInfo(null).setData(Collections.singletonList(c)));
        }
    }

    public void triggerOnCard(AbstractCard c, AbstractCreature target) {
        if (fields.getFullCardFilter().invoke(c)) {
            useFromTrigger(generateInfo(target).setData(Collections.singletonList(c)));
        }
    }

    @Override
    public void use(PCLUseInfo info, PCLActions order) {
    }

    public abstract EUIKeywordTooltip getDelegateTooltip();
}
