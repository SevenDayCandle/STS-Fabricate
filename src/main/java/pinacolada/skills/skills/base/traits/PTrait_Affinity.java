package pinacolada.skills.skills.base.traits;

import com.megacrit.cardcrawl.cards.AbstractCard;
import pinacolada.annotations.VisibleSkill;
import pinacolada.cards.base.PCLCard;
import pinacolada.cards.base.fields.PCLAffinity;
import pinacolada.cards.base.fields.PCLCardTarget;
import pinacolada.dungeon.PCLUseInfo;
import pinacolada.resources.PGR;
import pinacolada.resources.pcl.PCLCoreStrings;
import pinacolada.skills.PSkillData;
import pinacolada.skills.PSkillSaveData;
import pinacolada.skills.PTrait;
import pinacolada.skills.fields.PField_Affinity;
import pinacolada.skills.skills.PFacetCond;
import pinacolada.skills.skills.base.primary.PTrigger_Passive;
import pinacolada.ui.editor.PCLCustomEffectEditingPane;
import pinacolada.utilities.GameUtilities;

@VisibleSkill
public class PTrait_Affinity extends PTrait<PField_Affinity> {

    public static final PSkillData<PField_Affinity> DATA = register(PTrait_Affinity.class, PField_Affinity.class)
            .setSourceTypes(PSkillData.SourceType.Card, PSkillData.SourceType.Power);

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
    public void applyToCard(PCLUseInfo info, AbstractCard c, boolean conditionMet) {
        int actualAmount = refreshAmount(info);
        if (fields.not) {
            for (PCLAffinity af : fields.affinities) {
                GameUtilities.modifyAffinityLevel(c, af, conditionMet ? actualAmount : 0, false);
            }
        }
        else {
            for (PCLAffinity af : fields.affinities) {
                GameUtilities.modifyAffinityLevel(c, af, conditionMet ? actualAmount : -actualAmount, true);
            }
        }
        if (c instanceof PCLCard) {
            ((PCLCard) c).affinities.updateSortedList();
        }
    }

    @Override
    public String getSubDescText(PCLCardTarget perspective, Object requestor) {
        return fields.getAffinityAndString();
    }

    @Override
    public String getSubSampleText() {
        return PGR.core.tooltips.affinityGeneral.title;
    }

    @Override
    public String getSubText(PCLCardTarget perspective, Object requestor) {
        if (hasParentType(PTrigger_Passive.class)) {
            return fields.random && !fields.not ? TEXT.act_removeFrom(getSubDescText(perspective, requestor), getParentCardString(perspective, requestor)) : TEXT.act_zHas(getParentCardString(perspective, requestor), getSubDescText(perspective, requestor));
        }
        return fields.random && !fields.not ? TEXT.act_remove(getSubDescText(perspective, requestor)) : TEXT.act_has(getSubDescText(perspective, requestor));
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
