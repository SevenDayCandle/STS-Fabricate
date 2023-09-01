package pinacolada.skills.skills.base.conditions;

import com.megacrit.cardcrawl.cards.AbstractCard;
import pinacolada.annotations.VisibleSkill;
import pinacolada.cards.base.fields.PCLAffinity;
import pinacolada.cards.base.fields.PCLCardTarget;
import pinacolada.cards.base.tags.PCLCardTag;
import pinacolada.dungeon.PCLUseInfo;
import pinacolada.resources.pcl.PCLCoreStrings;
import pinacolada.skills.PSkill;
import pinacolada.skills.PSkillData;
import pinacolada.skills.PSkillSaveData;
import pinacolada.skills.fields.PField;
import pinacolada.skills.fields.PField_CardCategory;
import pinacolada.skills.skills.PFacetCond;

import java.util.ArrayList;

@VisibleSkill
public class PCond_IfHasProperty extends PFacetCond<PField_CardCategory> {
    public static final PSkillData<PField_CardCategory> DATA = register(PCond_IfHasProperty.class, PField_CardCategory.class, 1, 1)
            .noTarget();

    public PCond_IfHasProperty(PSkillSaveData content) {
        super(DATA, content);
    }

    public PCond_IfHasProperty() {
        super(DATA, PCLCardTarget.None, 0);
    }

    public PCond_IfHasProperty(PCLAffinity... affinities) {
        super(DATA, PCLCardTarget.None, 0);
        fields.setAffinity(affinities);
    }

    public PCond_IfHasProperty(PCLCardTag... affinities) {
        super(DATA, PCLCardTarget.None, 0);
        fields.setTag(affinities);
    }

    public PCond_IfHasProperty(AbstractCard.CardRarity... affinities) {
        super(DATA, PCLCardTarget.None, 0);
        fields.setRarity(affinities);
    }

    public PCond_IfHasProperty(AbstractCard.CardType... affinities) {
        super(DATA, PCLCardTarget.None, 0);
        fields.setType(affinities);
    }

    @Override
    public boolean checkCondition(PCLUseInfo info, boolean isUsing, PSkill<?> triggerSource) {
        if (info != null && info.card != null) {
            return fields.getFullCardFilter().invoke(info.card);
        }
        return false;
    }

    @Override
    public String getSampleText(PSkill<?> callingSkill, PSkill<?> parentSkill) {
        return TEXT.cond_ifX(TEXT.subjects_card);
    }

    @Override
    public String getSubText(PCLCardTarget perspective) {
        if (isWhenClause() || isPassiveClause()) {
            return !fields.cardIDs.isEmpty() ? fields.getCardIDAndString() : fields.getCardAndString();
        }

        ArrayList<String> conditions = new ArrayList<>();
        if (!fields.affinities.isEmpty()) {
            conditions.add(fields.forced ? PField.getAffinityAndString(fields.affinities) : PField.getAffinityOrString(fields.affinities));
        }
        if (!fields.tags.isEmpty()) {
            conditions.add(PField.getTagOrString(fields.tags));
        }
        if (!fields.costs.isEmpty()) {
            conditions.add(PCLCoreStrings.joinWithOr(c -> c.name, fields.costs));
        }

        return TEXT.cond_ifTargetHas(TEXT.subjects_thisCard, 1, PCLCoreStrings.joinWithOr(conditions));
    }
}
