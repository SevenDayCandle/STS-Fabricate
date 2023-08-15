package pinacolada.skills.skills.special.traits;

import com.megacrit.cardcrawl.cards.AbstractCard;
import pinacolada.cards.base.PCLCard;
import pinacolada.cards.base.fields.PCLAffinity;
import pinacolada.cards.base.fields.PCLCardTarget;
import pinacolada.resources.PGR;
import pinacolada.resources.pcl.PCLCoreStrings;
import pinacolada.skills.PSkillData;
import pinacolada.skills.PSkillSaveData;
import pinacolada.skills.PTrait;
import pinacolada.skills.fields.PField_Affinity;
import pinacolada.skills.skills.PFacetCond;
import pinacolada.skills.skills.base.primary.PTrigger_Passive;
import pinacolada.utilities.GameUtilities;

// Only used for augments
public class PTrait_Affinity extends PTrait<PField_Affinity> {

    public static final PSkillData<PField_Affinity> DATA = register(PTrait_Affinity.class, PField_Affinity.class);

    public PTrait_Affinity() {
        this(1);
    }

    public PTrait_Affinity(int amount, PCLAffinity... affinities) {
        super(DATA, amount);
        fields.setAffinity(affinities);
    }

    public PTrait_Affinity(PSkillSaveData content) {
        super(DATA, content);
    }

    public PTrait_Affinity(PCLAffinity... affinities) {
        this(1, affinities);
    }

    @Override
    public void applyToCard(AbstractCard c, boolean conditionMet) {
        for (PCLAffinity af : fields.affinities) {
            GameUtilities.modifyAffinityLevel(c, af, conditionMet ? amount : -amount, true);
        }
        if (c instanceof PCLCard) {
            ((PCLCard) c).affinities.updateSortedList();
        }
    }

    @Override
    public String getSubDescText(PCLCardTarget perspective) {
        return fields.getAffinityAndString();
    }

    @Override
    public String getSubSampleText() {
        return PGR.core.tooltips.affinityGeneral.title;
    }

    @Override
    public String getSubText(PCLCardTarget perspective) {
        if (hasParentType(PTrigger_Passive.class) && !hasParentType(PFacetCond.class)) {
            return fields.random ? TEXT.act_removeFrom(getSubDescText(perspective), PCLCoreStrings.pluralForce(TEXT.subjects_cardN)) : TEXT.act_zHas(PCLCoreStrings.pluralForce(TEXT.subjects_cardN), getSubDescText(perspective));
        }
        return fields.random ? TEXT.act_remove(getSubDescText(perspective)) : TEXT.act_has(getSubDescText(perspective));
    }
}
