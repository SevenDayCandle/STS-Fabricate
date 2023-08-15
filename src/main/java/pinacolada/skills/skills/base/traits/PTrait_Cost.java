package pinacolada.skills.skills.base.traits;

import com.megacrit.cardcrawl.cards.AbstractCard;
import pinacolada.annotations.VisibleSkill;
import pinacolada.cards.base.fields.PCLCardTarget;
import pinacolada.resources.pcl.PCLCoreStrings;
import pinacolada.skills.PSkill;
import pinacolada.skills.PSkillData;
import pinacolada.skills.PSkillSaveData;
import pinacolada.skills.PTrait;
import pinacolada.skills.fields.PField_Not;
import pinacolada.skills.skills.PFacetCond;
import pinacolada.skills.skills.base.primary.PTrigger_Passive;
import pinacolada.utilities.GameUtilities;

@VisibleSkill
public class PTrait_Cost extends PTrait<PField_Not> {
    public static final PSkillData<PField_Not> DATA = register(PTrait_Cost.class, PField_Not.class)
            .setSourceTypes(PSkillData.SourceType.Card, PSkillData.SourceType.Power);

    public PTrait_Cost() {
        this(1);
    }

    public PTrait_Cost(int amount) {
        super(DATA, amount);
    }

    public PTrait_Cost(PSkillSaveData content) {
        super(DATA, content);
    }

    @Override
    public void applyToCard(AbstractCard c, boolean conditionMet) {
        GameUtilities.modifyCostForCombat(c, conditionMet ? amount : -amount, true);
    }

    @Override
    public String getSampleText(PSkill<?> callingSkill, PSkill<?> parentSkill) {
        return TEXT.act_costs("+X");
    }

    @Override
    public String getSubDescText(PCLCardTarget perspective) {
        return getAmountRawString();
    }

    @Override
    public String getSubSampleText() {
        return TEXT.subjects_cost;
    }

    @Override
    public String getSubText(PCLCardTarget perspective) {
        if (hasParentType(PTrigger_Passive.class) && !hasParentType(PFacetCond.class)) {
            return TEXT.act_zCosts(PCLCoreStrings.pluralForce(TEXT.subjects_cardN), 2, getSubDescText(perspective));
        }
        return TEXT.act_costs(getSubDescText(perspective));
    }

    @Override
    public boolean isDetrimental() {
        return amount > 0;
    }

    @Override
    public String wrapAmount(int input) {
        return input > 0 && !fields.not ? "+" + input : String.valueOf(input);
    }
}
