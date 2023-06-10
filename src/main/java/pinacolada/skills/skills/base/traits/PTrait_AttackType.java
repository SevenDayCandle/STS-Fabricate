package pinacolada.skills.skills.base.traits;

import com.megacrit.cardcrawl.cards.AbstractCard;
import pinacolada.annotations.VisibleSkill;
import pinacolada.cards.base.PCLCard;
import pinacolada.cards.base.fields.PCLAttackType;
import pinacolada.skills.PSkillData;
import pinacolada.skills.PSkillSaveData;
import pinacolada.skills.fields.PField_AttackType;
import pinacolada.skills.skills.PTrait;
import pinacolada.skills.skills.PTrigger;

@VisibleSkill
public class PTrait_AttackType extends PTrait<PField_AttackType> {
    public static final PSkillData<PField_AttackType> DATA = register(PTrait_AttackType.class, PField_AttackType.class);

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
    public String getSubText() {
        return hasParentType(PTrigger.class) ? getSubDescText() :
                fields.random ? TEXT.act_remove(getSubDescText()) : TEXT.act_has(getSubDescText());
    }

    @Override
    public String getSubDescText() {
        return fields.attackTypes.size() > 0 ? fields.attackTypes.get(0).getTooltip().getTitleOrIcon() : "";
    }

    @Override
    public String getSubSampleText() {
        return TEXT.cedit_attackType;
    }
}
