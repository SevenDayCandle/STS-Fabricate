package pinacolada.skills.skills.base.traits;

import basemod.helpers.CardModifierManager;
import com.megacrit.cardcrawl.cards.AbstractCard;
import extendedui.EUIRM;
import pinacolada.annotations.VisibleSkill;
import pinacolada.cardmods.TagDisplayModifier;
import pinacolada.cards.base.PCLCard;
import pinacolada.cards.base.fields.PCLCardTarget;
import pinacolada.cards.base.tags.PCLCardTag;
import pinacolada.dungeon.PCLUseInfo;
import pinacolada.resources.pcl.PCLCoreStrings;
import pinacolada.skills.PMod;
import pinacolada.skills.PSkillData;
import pinacolada.skills.PSkillSaveData;
import pinacolada.skills.PTrait;
import pinacolada.skills.fields.PField;
import pinacolada.skills.fields.PField_Tag;
import pinacolada.skills.skills.PFacetCond;
import pinacolada.skills.skills.base.primary.PTrigger_Passive;
import pinacolada.ui.editor.PCLCustomEffectEditingPane;

@VisibleSkill
public class PTrait_Tag extends PTrait<PField_Tag> {
    public static final PSkillData<PField_Tag> DATA = register(PTrait_Tag.class, PField_Tag.class)
            .setSourceTypes(PSkillData.SourceType.Card, PSkillData.SourceType.Power);

    public PTrait_Tag() {
        this(1);
    }

    public PTrait_Tag(int amount, PCLCardTag... tags) {
        super(DATA, amount);
        fields.setTag(tags);
    }

    public PTrait_Tag(PSkillSaveData content) {
        super(DATA, content);
    }

    public PTrait_Tag(PCLCardTag... tags) {
        this(1, tags);
    }

    @Override
    public void applyToCard(PCLUseInfo info, AbstractCard c, boolean conditionMet) {
        int actualAmount = refreshAmount(info);
        if (fields.not) {
            if (conditionMet ^ fields.random) {
                for (PCLCardTag tag : fields.tags) {
                    tag.set(c, actualAmount);
                }
            }
            else {
                for (PCLCardTag tag : fields.tags) {
                    tag.set(c, 0);
                }
            }
        }
        else {
            for (PCLCardTag tag : fields.tags) {
                tag.add(c, (fields.random ^ conditionMet ? 1 : -1) * actualAmount);
            }
        }
        checkTagModifier(c);
    }

    private void checkTagModifier(AbstractCard card) {
        if (!(card instanceof PCLCard)) {
            TagDisplayModifier mod = TagDisplayModifier.get(card);
            if (mod == null) {
                mod = new TagDisplayModifier();
                CardModifierManager.addModifier(card, mod);
            }
        }
    }

    @Override
    public String getSubDescText(PCLCardTarget perspective) {
        String base = PField.getTagAndString(fields.tags);
        if (fields.not) {
            if (amount == 1) {
                return base;
            }
            if (amount < 0) {
                return EUIRM.strings.numNoun(TEXT.subjects_infinite, base);
            }
        }
        return EUIRM.strings.numNoun(getAmountRawString(), base);
    }

    @Override
    public String getSubSampleText() {
        return TEXT.cedit_tags;
    }

    @Override
    public String getSubText(PCLCardTarget perspective, Object requestor) {
        if (hasParentType(PTrigger_Passive.class) && !hasParentType(PFacetCond.class)) {
            return fields.random && !fields.not ? TEXT.act_removeFrom(getSubDescText(perspective), PCLCoreStrings.pluralForce(TEXT.subjects_cardN)) : TEXT.act_zHas(PCLCoreStrings.pluralForce(TEXT.subjects_cardN), getSubDescText(perspective));
        }
        return fields.random && !fields.not ? TEXT.act_remove(getSubDescText(perspective)) : TEXT.act_has(getSubDescText(perspective));
    }

    @Override
    public void setupEditor(PCLCustomEffectEditingPane editor) {
        super.setupEditor(editor);
        fields.registerNotBoolean(editor, TEXT.cedit_exact, null);
    }

    @Override
    public String wrapTextAmountSelf(int input) {
        return input >= 0 && !fields.not ? "+" + input : String.valueOf(input);
    }
}
