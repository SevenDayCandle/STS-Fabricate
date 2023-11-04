package pinacolada.skills.skills.base.traits;

import com.megacrit.cardcrawl.cards.AbstractCard;
import extendedui.EUIRM;
import pinacolada.annotations.VisibleSkill;
import pinacolada.cards.base.fields.PCLCardTarget;
import pinacolada.cards.base.tags.PCLCardTag;
import pinacolada.resources.pcl.PCLCoreStrings;
import pinacolada.skills.PSkillData;
import pinacolada.skills.PSkillSaveData;
import pinacolada.skills.PTrait;
import pinacolada.skills.fields.PField;
import pinacolada.skills.fields.PField_Tag;
import pinacolada.skills.skills.PFacetCond;
import pinacolada.skills.skills.base.primary.PTrigger_Passive;

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
    public void applyToCard(AbstractCard c, boolean conditionMet) {
        for (PCLCardTag tag : fields.tags) {
            tag.set(sourceCard, (fields.random ^ conditionMet ? 1 : 0) * amount);
        }
    }

    @Override
    public String getSubDescText(PCLCardTarget perspective) {
        String base = PField.getTagAndString(fields.tags);
        return amount > 1 ? EUIRM.strings.numNoun(getAmountRawString(), base) : amount < 0 ? EUIRM.strings.numNoun(TEXT.subjects_infinite, base) : base;
    }

    @Override
    public String getSubSampleText() {
        return TEXT.cedit_tags;
    }

    @Override
    public String getSubText(PCLCardTarget perspective, Object requestor) {
        if (hasParentType(PTrigger_Passive.class) && !hasParentType(PFacetCond.class)) {
            return fields.random ? TEXT.act_removeFrom(getSubDescText(perspective), PCLCoreStrings.pluralForce(TEXT.subjects_cardN)) : TEXT.act_zHas(PCLCoreStrings.pluralForce(TEXT.subjects_cardN), getSubDescText(perspective));
        }
        return fields.random ? TEXT.act_remove(getSubDescText(perspective)) : TEXT.act_has(getSubDescText(perspective));
    }
}
