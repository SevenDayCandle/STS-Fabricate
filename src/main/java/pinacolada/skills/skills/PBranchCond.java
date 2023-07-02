package pinacolada.skills.skills;

import com.badlogic.gdx.graphics.Color;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import extendedui.EUIUtils;
import extendedui.interfaces.delegates.ActionT1;
import extendedui.ui.tooltips.EUICardPreview;
import extendedui.utilities.RotatingList;
import org.apache.commons.lang3.StringUtils;
import pinacolada.actions.PCLActions;
import pinacolada.annotations.VisibleSkill;
import pinacolada.cards.base.fields.PCLCardTarget;
import pinacolada.dungeon.PCLUseInfo;
import pinacolada.interfaces.markers.PMultiBase;
import pinacolada.interfaces.providers.PointerProvider;
import pinacolada.skills.PCond;
import pinacolada.skills.PSkill;
import pinacolada.skills.PSkillData;
import pinacolada.skills.PSkillSaveData;
import pinacolada.skills.fields.PField_Not;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@VisibleSkill
public class PBranchCond extends PCond<PField_Not> implements PMultiBase<PSkill<?>> {
    public static final PSkillData<PField_Not> DATA = register(PBranchCond.class, PField_Not.class, 0, DEFAULT_MAX)
            .selfTarget();
    protected ArrayList<PSkill<?>> effects = new ArrayList<>();

    public PBranchCond() {
        super(DATA);
    }

    public PBranchCond(PSkillSaveData content) {
        super(DATA, content);
        effects = EUIUtils.mapAsNonnull(splitJson(content.special), PSkill::get);
        setParentsForChildren();
    }

    public PBranchCond(PCLCardTarget target, int amount) {
        super(DATA, target, amount);
    }

    public PBranchCond(PCLCardTarget target, int amount, int extra) {
        super(DATA, target, amount, extra);
    }

    public static PBranchCond branch(PCond<?> cond, PSkill<?>... effs) {
        return (PBranchCond) new PBranchCond().setEffects(effs).setChild(cond);
    }

    public static PBranchCond discard(int amount, PSkill<?>... effs) {
        return (PBranchCond) new PBranchCond().setEffects(effs).setChild(PCond.discard(amount));
    }

    public PBranchCond addEffect(PCond<?> effect) {
        this.effects.add(effect);
        setParentsForChildren();
        return this;
    }

    @Override
    public void displayUpgrades(boolean value) {
        super.displayUpgrades(value);
        displayChildUpgrades(value);
    }

    @Override
    public Color getGlowColor() {
        Color c = super.getGlowColor();
        for (PSkill<?> effect : effects) {
            Color c2 = effect.getGlowColor();
            if (c2 != null) {
                c = c2;
            }
        }
        return c;
    }

    @Override
    public AbstractMonster.Intent getIntent() {
        AbstractMonster.Intent c = super.getIntent();
        for (PSkill<?> effect : effects) {
            AbstractMonster.Intent c2 = effect.getIntent();
            if (c2 != null) {
                c = c2;
            }
        }
        return c;
    }

    @Override
    public String getSampleText(PSkill<?> caller) {
        return TEXT.cedit_branchCondition;
    }

    @Override
    public String getSpecialData() {
        return PSkill.joinDataAsJson(effects, PSkill::serialize);
    }

    @Override
    public String getSubText() {
        return this.childEffect != null ? this.childEffect.getSubText() : "";
    }

    @Override
    public boolean hasChildType(Class<?> childType) {
        return super.hasChildType(childType) || EUIUtils.any(effects, child -> childType.isInstance(child) || (child != null && child.hasChildType(childType)));
    }

    @Override
    public boolean isBlank() {
        return effects.size() == 0 && !(childEffect != null && !childEffect.isBlank());
    }

    @Override
    public PBranchCond makeCopy() {
        PBranchCond copy = (PBranchCond) super.makeCopy();
        for (PSkill<?> effect : effects) {
            if (effect != null) {
                copy.addEffect(effect.makeCopy());
            }
        }
        return copy;
    }

    @Override
    public PBranchCond makePreviews(RotatingList<EUICardPreview> previews) {
        for (PSkill<?> effect : effects) {
            effect.makePreviews(previews);
        }
        super.makePreviews(previews);
        return this;
    }

    @Override
    public PBranchCond onAddToCard(AbstractCard card) {
        for (PSkill<?> effect : effects) {
            effect.onAddToCard(card);
        }
        super.onAddToCard(card);
        return this;
    }

    @Override
    public void onDrag(AbstractMonster m) {
        for (PSkill<?> effect : effects) {
            effect.onDrag(m);
        }
    }

    @Override
    public PBranchCond onRemoveFromCard(AbstractCard card) {
        removeSubs(card);
        super.onRemoveFromCard(card);
        return this;
    }

    @Override
    public void recurse(ActionT1<PSkill<?>> onRecurse) {
        onRecurse.invoke(this);
        for (PSkill<?> effect : effects) {
            effect.recurse(onRecurse);
        }
        if (this.childEffect != null) {
            this.childEffect.recurse(onRecurse);
        }
    }

    @Override
    public boolean requiresTarget() {
        return target == PCLCardTarget.Single || EUIUtils.any(effects, PSkill::requiresTarget);
    }

    @Override
    public PBranchCond setTemporaryAmount(int amount) {
        if (childEffect != null) {
            childEffect.setTemporaryAmount(amount);
        }
        return this;
    }

    @Override
    public PBranchCond setTemporaryExtra(int extra) {
        if (childEffect != null) {
            childEffect.setTemporaryExtra(extra);
        }
        return this;
    }

    @Override
    public PBranchCond stack(PSkill<?> other) {
        super.stack(other);
        if (other instanceof PMultiBase) {
            stackMulti((PMultiBase<?>) other);
        }
        return this;
    }

    @Override
    public void subscribeChildren() {
        for (PSkill<?> effect : effects) {
            effect.subscribeChildren();
        }
        if (this.childEffect != null) {
            this.childEffect.subscribeChildren();
        }
    }

    @Override
    public void unsubscribeChildren() {
        for (PSkill<?> effect : effects) {
            effect.unsubscribeChildren();
        }
        if (this.childEffect != null) {
            this.childEffect.unsubscribeChildren();
        }
    }

    protected String getEffectTexts(boolean addPeriod) {
        switch (effects.size()) {
            case 0:
                return getSubText();
            case 1:
                return super.getText(addPeriod);
            case 2:
                if (childEffect instanceof PCond && this.childEffect.getQualifierRange() < this.effects.size()) {
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

    public PSkill<?> getSubEffect(int index) {
        return index < effects.size() ? effects.get(index) : null;
    }

    @Override
    public List<PSkill<?>> getSubEffects() {
        return effects;
    }

    public PBranchCond setEffects(PSkill<?>... effects) {
        return setEffects(Arrays.asList(effects));
    }

    public PBranchCond setEffects(List<PSkill<?>> effects) {
        this.effects.clear();
        this.effects.addAll(effects);
        setParentsForChildren();
        return this;
    }

    @Override
    public String getText(boolean addPeriod) {
        if (this.childEffect != null) {
            return getEffectTexts(addPeriod);
        }
        return getSubText();
    }

    @Override
    public void refresh(PCLUseInfo info, boolean conditionMet) {
        conditionMetCache = checkCondition(info, false, null);
        boolean refreshVal = conditionMetCache & conditionMet;
        for (PSkill<?> effect : effects) {
            effect.refresh(info, refreshVal);
        }
        if (this.childEffect != null) {
            this.childEffect.refresh(info, refreshVal);
        }
    }

    @Override
    public PBranchCond setAmountFromCard() {
        super.setAmountFromCard();
        for (PSkill<?> effect : effects) {
            effect.setAmountFromCard();
        }
        return this;
    }

    @Override
    public PBranchCond setSource(PointerProvider card) {
        super.setSource(card);
        for (PSkill<?> effect : effects) {
            effect.setSource(card);
        }
        return this;
    }

    public boolean tryPassParent(PSkill<?> source, PCLUseInfo info) {
        return source == childEffect ? (parent == null || parent.tryPassParent(source, info)) : super.tryPassParent(source, info);
    }

    @Override
    public void use(PCLUseInfo info, PCLActions order) {
        if (childEffect instanceof PActiveCond) {
            ((PActiveCond<?>) childEffect).useImpl(info, order, (i) -> useSubEffect(i, order, childEffect.getQualifiers(i)), (i) -> {
            });
        }
        else if (childEffect instanceof PCallbackMove) {
            ((PCallbackMove<?>) childEffect).use(info, order, (i) -> useSubEffect(i, order, childEffect.getQualifiers(i)));
        }
        else if (childEffect != null) {
            useSubEffect(info, order, childEffect.getQualifiers(info));
        }
    }

    @Override
    public boolean checkCondition(PCLUseInfo info, boolean isUsing, PSkill<?> triggerSource) {
        if (this.childEffect instanceof PCond && this.effects.size() < 2) {
            return ((PCond<?>) this.childEffect).checkCondition(info, isUsing, triggerSource);
        }
        return false;
    }

    public void useSubEffect(PCLUseInfo info, PCLActions order, ArrayList<Integer> qualifiers) {
        if (this.effects.size() > 0) {
            boolean canGoOver = this.childEffect.getQualifierRange() < this.effects.size();
            for (int i : qualifiers) {
                this.effects.get(i).use(info, order);
            }
            if (qualifiers.isEmpty() && canGoOver) {
                this.effects.get(this.childEffect.getQualifierRange()).use(info, order);
            }
        }
    }
}
