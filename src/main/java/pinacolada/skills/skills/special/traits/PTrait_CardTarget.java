package pinacolada.skills.skills.special.traits;

import com.megacrit.cardcrawl.cards.AbstractCard;
import pinacolada.cards.base.PCLCard;
import pinacolada.cards.base.fields.PCLCardTarget;
import pinacolada.dungeon.PCLUseInfo;
import pinacolada.resources.pcl.PCLCoreStrings;
import pinacolada.skills.PSkillData;
import pinacolada.skills.PSkillSaveData;
import pinacolada.skills.PTrait;
import pinacolada.skills.fields.PField_CardTarget;
import pinacolada.skills.skills.PFacetCond;
import pinacolada.skills.skills.base.primary.PTrigger_Passive;

// Only used for augments
public class PTrait_CardTarget extends PTrait<PField_CardTarget> {
    public static final PSkillData<PField_CardTarget> DATA = register(PTrait_CardTarget.class, PField_CardTarget.class)
            .setSourceTypes(PSkillData.SourceType.Card, PSkillData.SourceType.Power);

    protected PCLCardTarget newTarget = PCLCardTarget.Single;

    public PTrait_CardTarget() {
        this(PCLCardTarget.Single);
    }

    public PTrait_CardTarget(PCLCardTarget type) {
        super(DATA);
        this.newTarget = type;
    }

    public PTrait_CardTarget(PSkillSaveData content) {
        super(DATA, content);
    }

    @Override
    public void applyToCard(PCLUseInfo info, AbstractCard c, boolean conditionMet) {
        if (c instanceof PCLCard) {
            ((PCLCard) c).setTarget(conditionMet ? newTarget : ((PCLCard) c).cardData.cardTarget);
        }
    }

    @Override
    public String getSubDescText(PCLCardTarget perspective, Object requestor) {
        return newTarget.getTitle();
    }

    @Override
    public String getSubSampleText() {
        return TEXT.cedit_cardTarget;
    }

    @Override
    public String getSubText(PCLCardTarget perspective, Object requestor) {
        if (hasParentType(PTrigger_Passive.class) && !hasParentType(PFacetCond.class)) {
            return fields.random ? TEXT.act_removeFrom(getSubDescText(perspective, requestor), PCLCoreStrings.pluralForce(TEXT.subjects_cardN)) : TEXT.act_zHas(PCLCoreStrings.pluralForce(TEXT.subjects_cardN), getSubDescText(perspective, requestor));
        }
        return fields.random ? TEXT.act_remove(getSubDescText(perspective, requestor)) : TEXT.act_has(getSubDescText(perspective, requestor));
    }

    @Override
    public PTrait_CardTarget makeCopy() {
        PTrait_CardTarget other = (PTrait_CardTarget) super.makeCopy();
        other.newTarget = this.newTarget;
        return other;
    }
}
