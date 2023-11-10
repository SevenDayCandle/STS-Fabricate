package pinacolada.skills.skills.base.traits;

import com.megacrit.cardcrawl.cards.AbstractCard;
import pinacolada.annotations.VisibleSkill;
import pinacolada.cards.base.PCLCard;
import pinacolada.cards.base.fields.PCLAttackType;
import pinacolada.cards.base.fields.PCLCardTarget;
import pinacolada.resources.pcl.PCLCoreStrings;
import pinacolada.skills.PSkillData;
import pinacolada.skills.PSkillSaveData;
import pinacolada.skills.PTrait;
import pinacolada.skills.fields.PField_AttackType;
import pinacolada.skills.skills.PFacetCond;
import pinacolada.skills.skills.base.primary.PTrigger_Passive;

@VisibleSkill
public class PTrait_AttackType extends PTrait<PField_AttackType> {
    public static final PSkillData<PField_AttackType> DATA = register(PTrait_AttackType.class, PField_AttackType.class)
            .setSourceTypes(PSkillData.SourceType.Card, PSkillData.SourceType.Augment);

    public PTrait_AttackType() {
        this(PCLAttackType.Normal);
    }

    public PTrait_AttackType(PCLAttackType... type) {
        super(DATA);
        fields.setAttackType(type);
    }

    public PTrait_AttackType(PSkillSaveData content) {
        super(DATA, content);
    }

    @Override
    public void applyToCard(AbstractCard c, boolean conditionMet) {
        if (c instanceof PCLCard && fields.attackTypes.size() > 0) {
            ((PCLCard) c).setAttackType(conditionMet ? fields.attackTypes.get(0) : ((PCLCard) c).cardData.attackType);
        }
    }

    @Override
    public String getSubDescText(PCLCardTarget perspective) {
        return fields.attackTypes.size() > 0 ? fields.attackTypes.get(0).getTooltip().getTitleOrIcon() : "";
    }

    @Override
    public String getSubSampleText() {
        return TEXT.cedit_attackType;
    }

    @Override
    public String getSubText(PCLCardTarget perspective, Object requestor) {
        if (hasParentType(PTrigger_Passive.class) && !hasParentType(PFacetCond.class)) {
            return fields.random ? TEXT.act_removeFrom(getSubDescText(perspective), PCLCoreStrings.pluralForce(TEXT.subjects_cardN)) : TEXT.act_zHas(PCLCoreStrings.pluralForce(TEXT.subjects_cardN), getSubDescText(perspective));
        }
        return fields.random ? TEXT.act_remove(getSubDescText(perspective)) : TEXT.act_has(getSubDescText(perspective));
    }
}
