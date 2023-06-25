package pinacolada.skills.skills.special.traits;

import com.megacrit.cardcrawl.cards.AbstractCard;
import pinacolada.cards.base.PCLCard;
import pinacolada.cards.base.fields.PCLCardTarget;
import pinacolada.skills.PSkillData;
import pinacolada.skills.PSkillSaveData;
import pinacolada.skills.PTrait;
import pinacolada.skills.fields.PField_CardTarget;
import pinacolada.skills.skills.PTrigger;

// Only used for augments
public class PTrait_CardTarget extends PTrait<PField_CardTarget> {
    public static final PSkillData<PField_CardTarget> DATA = register(PTrait_CardTarget.class, PField_CardTarget.class);

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
    public void applyToCard(AbstractCard c, boolean conditionMet) {
        if (c instanceof PCLCard) {
            ((PCLCard) c).setTarget(conditionMet ? newTarget : ((PCLCard) c).cardData.cardTarget);
        }
    }

    @Override
    public String getSubText() {
        return hasParentType(PTrigger.class) ? getSubDescText() :
                fields.random ? TEXT.act_remove(getSubDescText()) : TEXT.act_has(getSubDescText());
    }

    @Override
    public PTrait_CardTarget makeCopy() {
        PTrait_CardTarget other = (PTrait_CardTarget) super.makeCopy();
        other.newTarget = this.newTarget;
        return other;
    }

    @Override
    public String getSubDescText() {
        return newTarget.getTitle();
    }

    @Override
    public String getSubSampleText() {
        return TEXT.cedit_cardTarget;
    }
}
