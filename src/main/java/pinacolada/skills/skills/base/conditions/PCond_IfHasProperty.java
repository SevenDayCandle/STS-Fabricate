package pinacolada.skills.skills.base.conditions;

import com.megacrit.cardcrawl.cards.AbstractCard;
import extendedui.EUIGameUtils;
import extendedui.utilities.CostFilter;
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
import pinacolada.ui.editor.PCLCustomEffectEditingPane;
import pinacolada.utilities.GameUtilities;

import java.util.ArrayList;
import java.util.List;

@VisibleSkill
public class PCond_IfHasProperty extends PFacetCond<PField_CardCategory> {
    public static final PSkillData<PField_CardCategory> DATA = register(PCond_IfHasProperty.class, PField_CardCategory.class, 1, 1)
            .setSourceTypes(PSkillData.SourceType.Card, PSkillData.SourceType.Power)
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
        if (info != null) {
            if (useParent) {
                List<? extends AbstractCard> cards = info.getDataAsList(AbstractCard.class);
                if (cards != null) {
                    return fields.allOrAnyR(cards, card -> fields.getFullCardFilter().invoke(card));
                }
                return false;
            }
            else if (info.card != null) {
                return fields.getFullCardFilter().invoke(info.card);
            }
        }
        return false;
    }

    @Override
    public String getSampleText(PSkill<?> callingSkill, PSkill<?> parentSkill) {
        return TEXT.cond_ifX(TEXT.subjects_card);
    }

    @Override
    public String getSubText(PCLCardTarget perspective, Object requestor) {
        if (isWhenClause() || isPassiveClause()) {
            // Force plural
            return !fields.cardIDs.isEmpty() ? fields.getCardIDAndString() : fields.getCardAndString(2);
        }

        ArrayList<String> conditions = new ArrayList<>();
        if (!fields.affinities.isEmpty()) {
            conditions.add(fields.forced ? PField.getAffinityAndString(fields.affinities) : PField.getAffinityOrString(fields.affinities));
        }
        if (!fields.tags.isEmpty()) {
            conditions.add(PField.getTagOrString(fields.tags));
        }
        if (!fields.costs.isEmpty()) {
            conditions.add(PCLCoreStrings.joinWithOr(CostFilter.getCostRangeStrings(fields.costs)));
        }
        if (!fields.loadouts.isEmpty()) {
            conditions.add(PCLCoreStrings.joinWithOr(PField_CardCategory::getLoadoutName, fields.loadouts));
        }
        if (!fields.flags.isEmpty()) {
            conditions.add(PCLCoreStrings.joinWithOr(PField_CardCategory::getFlagName, fields.flags));
        }
        if (!fields.colors.isEmpty()) {
            conditions.add(PCLCoreStrings.joinWithOr(EUIGameUtils::getColorName, fields.colors));
        }
        if (!fields.rarities.isEmpty()) {
            conditions.add(PCLCoreStrings.joinWithOr(EUIGameUtils::textForRarity, fields.rarities));
        }
        if (!fields.types.isEmpty()) {
            conditions.add(PCLCoreStrings.joinWithOr(EUIGameUtils::textForType, fields.types));
        }
        if (!fields.cardIDs.isEmpty()) {
            conditions.add(PCLCoreStrings.joinWithOr(c -> GameUtilities.getCardNameForID(c, extra2, 0), fields.cardIDs));
        }

        return TEXT.cond_ifTargetIs(useParent ? getTheyString() : TEXT.subjects_thisCard(), 1, PCLCoreStrings.joinWithOr(conditions));
    }

    @Override
    public void setupEditor(PCLCustomEffectEditingPane editor) {
        super.setupEditor(editor);
        registerUseParentBoolean(editor);
        // TODO any/or toggle
    }
}
