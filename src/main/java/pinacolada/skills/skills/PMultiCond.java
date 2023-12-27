package pinacolada.skills.skills;

import com.badlogic.gdx.graphics.Color;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.CardGroup;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import extendedui.EUIUtils;
import extendedui.interfaces.delegates.ActionT1;
import extendedui.ui.tooltips.EUIPreview;
import extendedui.utilities.RotatingList;
import extendedui.utilities.TupleT2;
import pinacolada.actions.PCLActions;
import pinacolada.annotations.VisibleSkill;
import pinacolada.cards.base.ChoiceCard;
import pinacolada.cards.base.PCLCard;
import pinacolada.cards.base.fields.PCLCardTarget;
import pinacolada.dungeon.PCLUseInfo;
import pinacolada.interfaces.markers.PMultiBase;
import pinacolada.monsters.PCLCardAlly;
import pinacolada.resources.PGR;
import pinacolada.resources.pcl.PCLCoreStrings;
import pinacolada.skills.PCond;
import pinacolada.skills.PSkill;
import pinacolada.skills.PSkillData;
import pinacolada.skills.PSkillSaveData;
import pinacolada.skills.fields.PField_Not;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@VisibleSkill
public class PMultiCond extends PCond<PField_Not> implements PMultiBase<PCond<?>> {
    public static final PSkillData<PField_Not> DATA = register(PMultiCond.class, PField_Not.class, 0, 1)
            .noTarget();
    protected ArrayList<PCond<?>> effects = new ArrayList<>();

    public PMultiCond() {
        super(DATA, PCLCardTarget.None, 0);
    }

    public PMultiCond(PSkillSaveData content) {
        super(DATA, content);
        effects = EUIUtils.mapAsNonnull(splitJson(content.special), e -> (PCond<?>) PSkill.get(e));
        setParentsForChildren();
    }

    public PMultiCond(PCond<?>... effects) {
        super(DATA, EUIUtils.max(effects, effect -> effect.target), 0);
        if (target == null) {
            target = PCLCardTarget.None;
        }
        this.effects.addAll(Arrays.asList(effects));
        setParentsForChildren();
    }

    public static PMultiCond or(PCond<?>... effects) {
        return new PMultiCond(effects);
    }

    public PMultiCond addEffect(PCond<?> effect) {
        this.effects.add(effect);
        setParentsForChildren();
        return this;
    }

    @Override
    public boolean checkCondition(PCLUseInfo info, boolean isUsing, PSkill<?> triggerSource) {
        if (triggerSource != null && EUIUtils.any(effects, ef -> ef == triggerSource)) {
            return true;
        }
        return effects.isEmpty() || (fields.not ^ EUIUtils.any(effects, c -> c.checkCondition(info, isUsing, triggerSource)));
    }

    protected void chooseCond(PCLUseInfo info, PCLActions order) {
        chooseEffect(info, order, getEligibleConds(info)).addCallback(choices -> {
            ChoiceCard<PSkill<?>> chosen = choices.get(0);
            if (chosen.value instanceof PActiveCond) {
                ((PActiveCond<?>) chosen.value).useImpl(info, order, (i) -> useDirectly(info, order), (i) -> {});
            }
            else {
                useDirectly(info, order);
            }
        });
    }

    @Override
    public void displayUpgrades(boolean value) {
        super.displayUpgrades(value);
        displayChildUpgrades(value);
    }

    @Override
    public TupleT2<PSkill<?>, Integer> getEffectAtIndex(int ind) {
        if (ind == 0) {
            return new TupleT2<>(this, ind);
        }
        for (PSkill<?> effect : effects) {
            ind -= 1;
            TupleT2<PSkill<?>, Integer> res = effect.getEffectAtIndex(ind);
            if (res.v1 != null) {
                return res;
            }
            ind = res.v2;
        }
        if (childEffect == null) {
            return null;
        }
        return childEffect.getEffectAtIndex(ind - 1);
    }

    protected ArrayList<PCond<?>> getEligibleConds(PCLUseInfo info) {
        return EUIUtils.filter(effects, c -> c.checkCondition(info, false, null));
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
    public String getHeaderTextForAmount() {
        return PGR.core.strings.cedit_choices;
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
        return TEXT.cedit_or;
    }

    @Override
    public String getSpecialData() {
        return PSkill.joinDataAsJson(effects, PSkill::serialize);
    }

    public PCond<?> getSubEffect(int index) {
        return index < effects.size() ? effects.get(index) : null;
    }

    public List<PCond<?>> getSubEffects() {
        return effects;
    }

    @Override
    public String getSubText(PCLCardTarget perspective, Object requestor) {
        return fields.not ? TEXT.cond_not(PCLCoreStrings.joinWithOr(getEffectTextsWithoutPeriod(effects, perspective, requestor, false))) : PCLCoreStrings.joinWithOr(getEffectTextsWithoutPeriod(effects, perspective, requestor, false));
    }

    @Override
    public String getText(int index, PCLCardTarget perspective, Object requestor, boolean addPeriod) {
        return effects.size() > index ? effects.get(index).getText(index, perspective, requestor, addPeriod) : getText(perspective, requestor, addPeriod);
    }

    @Override
    public String getText(PCLCardTarget perspective, Object requestor, boolean addPeriod) {
        String base = effects.isEmpty() ? (childEffect != null ? childEffect.getText(perspective, requestor, addPeriod) : "")
                : getCapitalSubText(perspective, requestor, addPeriod) + (childEffect != null ? ((childEffect instanceof PCond ? EFFECT_SEPARATOR : COMMA_SEPARATOR) + childEffect.getText(perspective, requestor, addPeriod)) : PCLCoreStrings.period(addPeriod));
        return amount > 0 ? (capital(TEXT.act_choose(getAmountRawString()), addPeriod) + COLON_SEPARATOR + base) : base;
    }

    @Override
    public boolean hasChildType(Class<?> childType) {
        return super.hasChildType(childType) || EUIUtils.any(effects, child -> childType.isInstance(child) || (child != null && child.hasChildType(childType)));
    }

    @Override
    public <U> void invokeCastChildren(Class<U> targetClass, ActionT1<U> onUse) {
        for (PSkill<?> effect : effects) {
            effect.invokeCastChildren(targetClass, onUse);
        }
        if (this.childEffect != null) {
            this.childEffect.invokeCastChildren(targetClass, onUse);
        }
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
    public PMultiCond makeCopy() {
        PMultiCond copy = (PMultiCond) super.makeCopy();
        for (PCond<?> effect : effects) {
            if (effect != null) {
                copy.addEffect((PCond<?>) effect.makeCopy());
            }
        }
        return copy;
    }

    @Override
    public PMultiCond makePreviews(RotatingList<EUIPreview> previews) {
        for (PSkill<?> effect : effects) {
            effect.makePreviews(previews);
        }
        super.makePreviews(previews);
        return this;
    }

    @Override
    public PMultiCond onAddToCard(AbstractCard card) {
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
    public PMultiCond onRemoveFromCard(AbstractCard card) {
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
    public PMultiCond setAmountFromCard() {
        super.setAmountFromCard();
        for (PSkill<?> effect : effects) {
            effect.setAmountFromCard();
        }
        return this;
    }

    public PMultiCond setEffects(PCond<?>... effects) {
        return setEffects(Arrays.asList(effects));
    }

    public PMultiCond setEffects(List<PCond<?>> effects) {
        this.effects.clear();
        this.effects.addAll(effects);
        setParentsForChildren();
        return this;
    }

    @Override
    public PMultiCond setSource(Object card) {
        super.setSource(card);
        for (PSkill<?> effect : effects) {
            effect.setSource(card);
        }
        return this;
    }

    @Override
    public PMultiCond setTemporaryAmount(int amount) {
        if (childEffect != null) {
            childEffect.setTemporaryAmount(amount);
        }
        return this;
    }

    @Override
    public PMultiCond setTemporaryExtra(int extra) {
        if (childEffect != null) {
            childEffect.setTemporaryExtra(extra);
        }
        return this;
    }

    @Override
    public boolean shouldUseWhenText() {
        return EUIUtils.all(getSubEffects(), PSkill::shouldUseWhenText);
    }

    @Override
    public PMultiCond stack(PSkill<?> other) {
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

    public void triggerOnAllyDeath(PCLCard c, PCLCardAlly ally) {
        for (PSkill<?> effect : effects) {
            effect.triggerOnAllyDeath(c, ally);
        }
    }

    public void triggerOnAllySummon(PCLCard c, PCLCardAlly ally) {
        for (PSkill<?> effect : effects) {
            effect.triggerOnAllySummon(c, ally);
        }
    }

    public void triggerOnAllyTrigger(PCLCard c, AbstractCreature target, PCLCardAlly ally, PCLCardAlly caller) {
        for (PSkill<?> effect : effects) {
            effect.triggerOnAllyTrigger(c, target, ally, caller);
        }
    }

    public void triggerOnAllyWithdraw(PCLCard c, PCLCardAlly ally) {
        for (PSkill<?> effect : effects) {
            effect.triggerOnAllyWithdraw(c, ally);
        }
    }

    public void triggerOnCreate(AbstractCard c, boolean startOfBattle) {
        for (PSkill<?> effect : effects) {
            effect.triggerOnCreate(c, startOfBattle);
        }
    }

    public void triggerOnCreateGeneric(Object c) {
        for (PSkill<?> effect : effects) {
            effect.triggerOnCreateGeneric(c);
        }
    }

    public void triggerOnDiscard(AbstractCard c) {
        for (PSkill<?> effect : effects) {
            effect.triggerOnDiscard(c);
        }
    }

    public void triggerOnDraw(AbstractCard c) {
        for (PSkill<?> effect : effects) {
            effect.triggerOnDraw(c);
        }
    }

    public boolean triggerOnEndOfTurn(boolean isUsing) {
        boolean trigger = false;
        for (PSkill<?> effect : effects) {
            trigger = trigger | effect.triggerOnEndOfTurn(isUsing);
        }
        return trigger;
    }

    public void triggerOnExhaust(AbstractCard c) {
        for (PSkill<?> effect : effects) {
            effect.triggerOnExhaust(c);
        }
    }

    public void triggerOnOtherCardPlayed(AbstractCard c) {
        for (PSkill<?> effect : effects) {
            effect.triggerOnOtherCardPlayed(c);
        }
    }

    public void triggerOnPurge(AbstractCard c) {
        for (PSkill<?> effect : effects) {
            effect.triggerOnPurge(c);
        }
    }

    public void triggerOnReshuffle(AbstractCard c, CardGroup sourcePile) {
        for (PSkill<?> effect : effects) {
            effect.triggerOnReshuffle(c, sourcePile);
        }
    }

    public void triggerOnRetain(AbstractCard c) {
        for (PSkill<?> effect : effects) {
            effect.triggerOnRetain(c);
        }
    }

    public void triggerOnRemove(Object c) {
        for (PSkill<?> effect : effects) {
            effect.triggerOnRemove(c);
        }
    }

    public void triggerOnShuffle() {
        for (PSkill<?> effect : effects) {
            effect.triggerOnShuffle();
        }
    }

    public boolean triggerOnStartOfTurn() {
        boolean trigger = false;
        for (PSkill<?> effect : effects) {
            trigger = trigger | effect.triggerOnStartOfTurn();
        }
        return trigger;
    }

    // When a delegate (e.g. on draw) is triggered from an and multicond, it should only execute the effect if the other conditions would pass
    @Override
    public boolean tryPassParent(PSkill<?> source, PCLUseInfo info) {
        return checkCondition(info, true, source);
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
        if (amount > 0) {
            chooseCond(info, order);
        }
        else if (EUIUtils.any(effects, e -> e instanceof PActiveCond)) {
            useIndividually(info, order, 0);
        }
        else {
            super.use(info, order);
        }
    }

    @Override
    public void use(PCLUseInfo info, PCLActions order, boolean shouldPay) {
        if (shouldPay) {
            if (amount > 0) {
                chooseCond(info, order);
            }
            else if (EUIUtils.any(effects, e -> e instanceof PActiveCond)) {
                useIndividually(info, order, 0);
            }
            else {
                super.use(info, order, true);
            }
        }
        else {
            super.use(info, order, false);
        }
    }

    public void useDirectly(PCLUseInfo info, PCLActions order) {
        if (this.childEffect != null) {
            this.childEffect.use(info, order);
        }
    }

    protected void useIndividually(PCLUseInfo info, PCLActions order, int index) {
        if (index < effects.size()) {
            PCond<?> cond = effects.get(index);
            if (cond instanceof PActiveCond) {
                ((PActiveCond<?>) cond).useImpl(info, order, (i) -> useDirectly(info, order), (i) -> useIndividually(info, order, index + 1));
            }
            else if (cond.checkCondition(info, true, null)) {
                useDirectly(info, order);
            }
            else {
                useIndividually(info, order, index + 1);
            }
        }
    }

    @Override
    public void useOutsideOfBattle() {
        super.useOutsideOfBattle();
        for (PSkill<?> effect : effects) {
            effect.useOutsideOfBattle();
        }
    }

    @Override
    public PMultiCond useParent(boolean value) {
        this.useParent = value;
        for (PSkill<?> effect : effects) {
            effect.useParent(value);
        }
        return this;
    }
}
