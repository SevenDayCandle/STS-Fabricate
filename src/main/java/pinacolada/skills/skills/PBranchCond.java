package pinacolada.skills.skills;

import extendedui.EUIUtils;
import org.apache.commons.lang3.StringUtils;
import pinacolada.annotations.VisibleSkill;
import pinacolada.cards.base.fields.PCLCardTarget;
import pinacolada.dungeon.PCLUseInfo;
import pinacolada.interfaces.markers.PMultiBase;
import pinacolada.skills.PCond;
import pinacolada.skills.PSkill;
import pinacolada.skills.PSkillData;
import pinacolada.skills.PSkillSaveData;
import pinacolada.skills.fields.PField_Not;

import java.util.ArrayList;
import java.util.List;

@VisibleSkill
public class PBranchCond extends PCond<PField_Not> implements PMultiBase<PSkill<?>> {
    public static final PSkillData<PField_Not> DATA = register(PBranchCond.class, PField_Not.class, 0, DEFAULT_MAX);
    protected ArrayList<PSkill<?>> effects = new ArrayList<>();

    public PBranchCond(PSkillSaveData content) {
        super(DATA, content);
    }

    public PBranchCond() {
        super(DATA);
    }

    public PBranchCond(PCLCardTarget target, int amount) {
        super(DATA, target, amount);
    }

    public PBranchCond(PCLCardTarget target, int amount, int extra) {
        super(DATA, target, amount, extra);
    }

    @Override
    public String getSubText() {
        return this.childEffect != null ? this.childEffect.getSubText() : "";
    }

    @Override
    public List<PSkill<?>> getSubEffects() {
        return effects;
    }

    @Override
    public boolean checkCondition(PCLUseInfo info, boolean isUsing, PSkill<?> triggerSource) {
        if (this.childEffect instanceof PCond && this.effects.size() < 2)
        {
            return ((PCond<?>) this.childEffect).checkCondition(info, isUsing, triggerSource);
        }
        return false;
    }

    @Override
    public void use(PCLUseInfo info) {
        if (childEffect instanceof PActiveCond) {
            ((PActiveCond<?>) childEffect).useImpl(info, (i) -> useSubEffect(i, childEffect.getQualifiers(i)), (i) -> {});
        }
        else if (childEffect instanceof PCallbackMove) {
            ((PCallbackMove<?>) childEffect).use(info, (i) -> useSubEffect(i, childEffect.getQualifiers(i)));
        }
        else if (childEffect != null) {
            useSubEffect(info, childEffect.getQualifiers(info));
        }
    }

    @Override
    public String getText(boolean addPeriod) {
        if (this.childEffect != null)
        {
            return getEffectTexts(addPeriod);
        }
        return "";
    }

    public void useSubEffect(PCLUseInfo info, ArrayList<Integer> qualifiers)
    {
        if (this.effects.size() > 0)
        {
            boolean canGoOver = this.childEffect.getQualifierRange() < this.effects.size();
            for (int i : qualifiers)
            {
                this.effects.get(i).use(info);
            }
            if (qualifiers.isEmpty() && canGoOver)
            {
                this.effects.get(this.childEffect.getQualifierRange()).use(info);
            }
        }
    }

    protected String getEffectTexts(boolean addPeriod) {
        switch (effects.size())
        {
            case 0:
                return "";
            case 1:
                return this.effects.get(0).getText(addPeriod);
            case 2:
                if (childEffect instanceof PCond && this.childEffect.getQualifierRange() < this.effects.size())
                {
                    return getCapitalSubText(addPeriod) + ": " + this.effects.get(0).getText(addPeriod) + " " +
                            StringUtils.capitalize(TEXT.cond_otherwise(this.effects.get(1).getText(addPeriod)));
                }
            default:
                ArrayList<String> effectTexts = new ArrayList<>();
                for (int i = 0; i < effects.size(); i++) {
                    effectTexts.add(this.childEffect.getQualifierText(i) + " -> " + this.effects.get(i).getText(addPeriod));
                }
                return getSubText() + ": | " + EUIUtils.joinStrings(EUIUtils.SPLIT_LINE, effectTexts);
        }
    }
}
