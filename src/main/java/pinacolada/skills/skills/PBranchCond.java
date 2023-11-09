package pinacolada.skills.skills;

import com.badlogic.gdx.graphics.Color;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import extendedui.EUIUtils;
import extendedui.interfaces.delegates.ActionT1;
import extendedui.interfaces.delegates.FuncT1;
import extendedui.interfaces.delegates.FuncT2;
import extendedui.ui.tooltips.EUIPreview;
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

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@VisibleSkill
public class PBranchCond extends PCond<PField_Not> implements PMultiBase<PSkill<?>> {
    public static final PSkillData<PField_Not> DATA = register(PBranchCond.class, PField_Not.class, 0, DEFAULT_MAX)
            .noTarget();
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
    public boolean checkCondition(PCLUseInfo info, boolean isUsing, PSkill<?> triggerSource) {
        if (this.childEffect instanceof PCond && this.effects.size() < 2) {
            return ((PCond<?>) this.childEffect).checkCondition(info, isUsing, triggerSource);
        }
        return false;
    }

    @Override
    public void displayUpgrades(boolean value) {
        super.displayUpgrades(value);
        displayChildUpgrades(value);
    }

    private String getEffectTexts(PCLCardTarget perspective, Object requestor, boolean addPeriod) {
        switch (effects.size()) {
            case 0:
                return EUIUtils.EMPTY_STRING;
            case 1:
                return this.effects.get(0).getText(perspective, requestor, addPeriod);
            case 2:
                if (childEffect instanceof PCond && this.childEffect.getQualifierRange() < this.effects.size()) {
                    return getCapitalSubText(perspective, requestor, addPeriod) + COMMA_SEPARATOR + this.effects.get(0).getText(perspective, requestor, false) + EFFECT_SEPARATOR +
                            StringUtils.capitalize(TEXT.cond_otherwise(this.effects.get(1).getText(perspective, requestor, addPeriod)));
                }
            default:
                ArrayList<String> effectTexts = new ArrayList<>();
                for (int i = 0; i < effects.size(); i++) {
                    effectTexts.add(this.childEffect.getQualifierText(i) + " -> " + this.effects.get(i).getText(perspective, requestor, addPeriod));
                }
                return getSubText(perspective, requestor) + ": | " + EUIUtils.joinStrings(EUIUtils.SPLIT_LINE, effectTexts);
        }
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
    public String getSampleText(PSkill<?> caller, PSkill<?> parentSkill) {
        return TEXT.cedit_branchCondition;
    }

    @Override
    public String getSpecialData() {
        return PSkill.joinDataAsJson(effects, PSkill::serialize);
    }

    public PSkill<?> getSubEffect(int index) {
        return index < effects.size() ? effects.get(index) : null;
    }

    @Override
    public List<PSkill<?>> getSubEffects() {
        return effects;
    }

    @Override
    public String getSubText(PCLCardTarget perspective, Object requestor) {
        return this.childEffect != null ? this.childEffect.getSubText(perspective, requestor) : EUIUtils.EMPTY_STRING;
    }

    @Override
    public String getText(PCLCardTarget perspective, Object requestor, boolean addPeriod) {
        if (this.childEffect != null) {
            return getEffectTexts(perspective, requestor, addPeriod);
        }
        return getSubText(perspective, requestor);
    }

    @Override
    public boolean hasChildType(Class<?> childType) {
        return super.hasChildType(childType) || EUIUtils.any(effects, child -> childType.isInstance(child) || (child != null && child.hasChildType(childType)));
    }

    @Override
    public boolean hasChildWarning() {
        return childEffect == null || effects.isEmpty();
    }

    @Override
    public boolean isAffectedByMods() {
        return false;
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
    public PBranchCond makePreviews(RotatingList<EUIPreview> previews) {
        for (PSkill<?> effect : effects) {
            effect.makePreviews(previews);
        }
        super.makePreviews(previews);
        return this;
    }

    @Override
    public float modifyBlockFirst(PCLUseInfo info, float amount) {
        return useModify(amount, childEffect.getQualifiers(info, true), (sk, a) -> sk.modifyBlockFirst(info, a));
    }

    @Override
    public float modifyBlockLast(PCLUseInfo info, float amount) {
        return useModify(amount, childEffect.getQualifiers(info, true), (sk, a) -> sk.modifyBlockLast(info, a));
    }

    @Override
    public int modifyCost(PCLUseInfo info, int amount) {
        return useModify(amount, childEffect.getQualifiers(info, true), (sk, a) -> sk.modifyCost(info, a));
    }

    @Override
    public float modifyDamageGiveFirst(PCLUseInfo info, float amount) {
        return useModify(amount, childEffect.getQualifiers(info, true), (sk, a) -> sk.modifyDamageGiveFirst(info, a));
    }

    @Override
    public float modifyDamageGiveLast(PCLUseInfo info, float amount) {
        return useModify(amount, childEffect.getQualifiers(info, true), (sk, a) -> sk.modifyDamageGiveLast(info, a));
    }

    @Override
    public float modifyDamageReceiveFirst(PCLUseInfo info, float amount, DamageInfo.DamageType type) {
        return useModify(amount, childEffect.getQualifiers(info, true), (sk, a) -> sk.modifyDamageReceiveFirst(info, a, type));
    }

    @Override
    public float modifyDamageReceiveLast(PCLUseInfo info, float amount, DamageInfo.DamageType type) {
        return useModify(amount, childEffect.getQualifiers(info, true), (sk, a) -> sk.modifyDamageReceiveLast(info, a, type));
    }

    @Override
    public float modifyHeal(PCLUseInfo info, float amount) {
        return useModify(amount, childEffect.getQualifiers(info, true), (sk, a) -> sk.modifyHeal(info, a));
    }

    @Override
    public float modifyHitCount(PCLUseInfo info, float amount) {
        return useModify(amount, childEffect.getQualifiers(info, true), (sk, a) -> sk.modifyHitCount(info, a));
    }

    @Override
    public float modifyOrbIncoming(PCLUseInfo info, float amount) {
        return useModify(amount, childEffect.getQualifiers(info, true), (sk, a) -> sk.modifyOrbIncoming(info, a));
    }

    @Override
    public float modifyOrbOutgoing(PCLUseInfo info, float amount) {
        return useModify(amount, childEffect.getQualifiers(info, true), (sk, a) -> sk.modifyOrbOutgoing(info, a));
    }

    @Override
    public float modifyRightCount(PCLUseInfo info, float amount) {
        return useModify(amount, childEffect.getQualifiers(info, true), (sk, a) -> sk.modifyRightCount(info, a));
    }

    @Override
    public float modifySkillBonus(PCLUseInfo info, float amount) {
        return useModify(amount, childEffect.getQualifiers(info, true), (sk, a) -> sk.modifySkillBonus(info, a));
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
    public void refresh(PCLUseInfo info, boolean conditionMet, boolean isUsing) {
        conditionMetCache = checkCondition(info, false, null);
        boolean refreshVal = conditionMetCache & conditionMet;
        for (PSkill<?> effect : effects) {
            effect.refresh(info, refreshVal, false);
        }
        if (this.childEffect != null) {
            this.childEffect.refresh(info, refreshVal, false);
        }
    }

    @Override
    public boolean requiresTarget() {
        return target == PCLCardTarget.Single || EUIUtils.any(effects, PSkill::requiresTarget);
    }

    @Override
    public PBranchCond setAmountFromCard() {
        super.setAmountFromCard();
        for (PSkill<?> effect : effects) {
            effect.setAmountFromCard();
        }
        return this;
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
    public PBranchCond setSource(PointerProvider card) {
        super.setSource(card);
        for (PSkill<?> effect : effects) {
            effect.setSource(card);
        }
        return this;
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
    public boolean tryPassParent(PSkill<?> source, PCLUseInfo info) {
        return source == childEffect ? (parent == null || parent.tryPassParent(source, info)) : super.tryPassParent(source, info);
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

    @Override
    public void use(PCLUseInfo info, PCLActions order) {
        if (childEffect instanceof PActiveCond) {
            ((PActiveCond<?>) childEffect).useImpl(info, order,
                    (i) -> useSubEffect(childEffect.getQualifiers(i, true), sk -> sk.use(i, order)),
                    (i) -> useSubEffect(childEffect.getQualifiers(i, false), sk -> sk.use(i, order))
            );
        }
        else if (childEffect instanceof PCallbackMove) {
            ((PCallbackMove<?>) childEffect).use(info, order, (i) -> useSubEffect(childEffect.getQualifiers(i, true), sk -> sk.use(i, order)));
        }
        else if (childEffect != null) {
            useSubEffect(childEffect.getQualifiers(info, true), sk -> sk.use(info, order));
        }
    }

    private <T> T useModify(T input, List<Integer> qualifiers, FuncT2<T, PSkill<?>, T> onDo) {
        if (this.effects.size() > 0) {
            int qr = this.childEffect.getQualifierRange();
            boolean canGoOver = qr < this.effects.size();
            for (int i : qualifiers) {
                input = onDo.invoke(this.effects.get(i), input);
            }
            if (qualifiers.isEmpty() && canGoOver) {
                input = onDo.invoke(this.effects.get(qr), input);
            }
        }
        return input;
    }

    private void useSubEffect(List<Integer> qualifiers, ActionT1<PSkill<?>> onDo) {
        if (this.effects.size() > 0) {
            int qr = this.childEffect.getQualifierRange();
            boolean canGoOver = qr < this.effects.size();
            for (int i : qualifiers) {
                onDo.invoke(this.effects.get(i));
            }
            if (qualifiers.isEmpty() && canGoOver) {
                onDo.invoke(this.effects.get(qr));
            }
        }
    }
}
