package pinacolada.skills.skills.base.conditions;

import com.megacrit.cardcrawl.cards.AbstractCard;
import extendedui.EUIUtils;
import pinacolada.annotations.VisibleSkill;
import pinacolada.cards.base.fields.PCLAffinity;
import pinacolada.cards.base.fields.PCLCardTarget;
import pinacolada.cards.base.tags.PCLCardTag;
import pinacolada.misc.PCLUseInfo;
import pinacolada.resources.pcl.PCLCoreStrings;
import pinacolada.skills.PSkill;
import pinacolada.skills.PSkillData;
import pinacolada.skills.PSkillSaveData;
import pinacolada.skills.fields.PField;
import pinacolada.skills.fields.PField_CardCategory;
import pinacolada.skills.skills.PPassiveCond;

import java.util.ArrayList;
@VisibleSkill
public class PCond_IfHasProperty extends PPassiveCond<PField_CardCategory>
{
    public static final PSkillData<PField_CardCategory> DATA = register(PCond_IfHasProperty.class, PField_CardCategory.class)
            .selfTarget();

    public PCond_IfHasProperty(PSkillSaveData content)
    {
        super(DATA, content);
    }

    public PCond_IfHasProperty()
    {
        super(DATA, PCLCardTarget.None, 0);
    }

    public PCond_IfHasProperty(PCLAffinity... affinities)
    {
        super(DATA, PCLCardTarget.None, 0);
        fields.setAffinity(affinities);
    }

    public PCond_IfHasProperty(PCLCardTag... affinities)
    {
        super(DATA, PCLCardTarget.None, 0);
        fields.setTag(affinities);
    }

    public PCond_IfHasProperty(AbstractCard.CardRarity... affinities)
    {
        super(DATA, PCLCardTarget.None, 0);
        fields.setRarity(affinities);
    }

    public PCond_IfHasProperty(AbstractCard.CardType... affinities)
    {
        super(DATA, PCLCardTarget.None, 0);
        fields.setType(affinities);
    }

    @Override
    public String getSampleText()
    {
        return TEXT.cond_ifTargetHas(TEXT.subjects_thisObj, 1, TEXT.subjects_x);
    }

    @Override
    public String getSubText()
    {
        if (isWhenClause())
        {
            return fields.getFullCardAndString();
        }

        ArrayList<String> conditions = new ArrayList<>();
        if (!fields.affinities.isEmpty())
        {
            conditions.add(PField.getAffinityOrString(fields.affinities));
        }
        if (!fields.tags.isEmpty())
        {
            conditions.add(PField.getTagOrString(fields.tags));
        }
        if (!fields.costs.isEmpty())
        {
            conditions.add(PCLCoreStrings.joinWithOr(EUIUtils.map(fields.costs, c -> c.name)));
        }

        return TEXT.cond_ifTargetHas(TEXT.subjects_thisObj, 1, PCLCoreStrings.joinWithOr(conditions));
    }

    @Override
    public String getText(boolean addPeriod)
    {
        return isWhenClause() ? TEXT.act_objectHas(getSubText(), childEffect != null ? childEffect.getText(addPeriod) : PCLCoreStrings.period(addPeriod)) : super.getText(addPeriod);
    }

    @Override
    public boolean checkCondition(PCLUseInfo info, boolean isUsing, PSkill<?> triggerSource)
    {
        if (info != null)
        {
            return fields.getFullCardFilter().invoke(info.card);
        }
        return false;
    }
}
