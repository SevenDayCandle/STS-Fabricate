package pinacolada.skills.skills;

import com.badlogic.gdx.graphics.Color;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import extendedui.EUIUtils;
import extendedui.interfaces.delegates.ActionT1;
import extendedui.ui.tooltips.EUIPreview;
import extendedui.utilities.RotatingList;
import extendedui.utilities.TupleT2;
import pinacolada.actions.PCLActions;
import pinacolada.annotations.VisibleSkill;
import pinacolada.cards.base.fields.PCLCardTarget;
import pinacolada.dungeon.PCLUseInfo;
import pinacolada.interfaces.markers.PMultiBase;
import pinacolada.interfaces.providers.PointerProvider;
import pinacolada.resources.PGR;
import pinacolada.resources.pcl.PCLCoreStrings;
import pinacolada.skills.PSkill;
import pinacolada.skills.PSkillData;
import pinacolada.skills.PSkillSaveData;
import pinacolada.skills.fields.PField_Empty;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

@VisibleSkill
public class PMultiSkill extends PSkill<PField_Empty> implements PMultiBase<PSkill<?>> {
    public static final PSkillData<PField_Empty> DATA = register(PMultiSkill.class, PField_Empty.class, 0, DEFAULT_MAX)
            .noTarget();
    protected ArrayList<PSkill<?>> effects = new ArrayList<>();
    public boolean generated = false;

    public PMultiSkill() {
        super(DATA, PCLCardTarget.None, 0);
    }

    public PMultiSkill(PSkillSaveData content) {
        super(DATA, content);
        effects = EUIUtils.mapAsNonnull(splitJson(content.special), PSkill::get);
        setParentsForChildren();
    }

    public PMultiSkill(PSkill<?>... effects) {
        super(DATA, EUIUtils.max(effects, effect -> effect.target), 0);
        this.effects.addAll(Arrays.asList(effects));
        setParentsForChildren();
    }

    public PMultiSkill(Collection<? extends PSkill<?>> effects) {
        super(DATA, EUIUtils.max(effects, effect -> effect.target), 0);
        this.effects.addAll(effects);
        setParentsForChildren();
    }

    public static PMultiSkill choose(PSkill<?>... effects) {
        return choose(1, effects);
    }

    public static PMultiSkill choose(int amount, PSkill<?>... effects) {
        return (PMultiSkill) new PMultiSkill(effects).setAmount(amount);
    }

    public static PMultiSkill join(PSkill<?>... effects) {
        return new PMultiSkill(effects);
    }

    public static PMultiSkill joinGen(PSkill<?>... effects) {
        return new PMultiSkill(effects).setGenerated(true);
    }

    @Override
    public PSkill<PField_Empty> setAmountForCombat(int amount) {
        for (PSkill<?> effect : effects) {
            effect.setAmountForCombat(amount);
        }
        return this;
    }

    public PMultiSkill addEffect(PSkill<?> newEffect) {
        this.effects.add(newEffect);
        setParentsForChildren();
        return this;
    }

    @Override
    public PSkill<PField_Empty> setExtraForCombat(int extra) {
        for (PSkill<?> effect : effects) {
            effect.setExtraForCombat(extra);
        }
        return this;
    }

    @Override
    public boolean canPlay(PCLUseInfo info, PSkill<?> triggerSource) {
        boolean canPlay = true;
        for (PSkill<?> be : effects) {
            canPlay = canPlay & be.canPlay(info, triggerSource);
        }
        return canPlay;
    }

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
    public String getSampleText(PSkill<?> callingSkill, PSkill<?> parentSkill) {
        return amount > 0 ? TEXT.act_choose(TEXT.subjects_x) : TEXT.cedit_multiEffect;
    }

    public String getSpecialData() {
        return PSkill.joinDataAsJson(effects, PSkill::serialize);
    }

    public PSkill<?> getSubEffect(int index) {
        return index < effects.size() ? effects.get(index) : null;
    }

    public List<PSkill<?>> getSubEffects() {
        return effects;
    }

    @Override
    public String getSubText(PCLCardTarget perspective, Object requestor) {
        return null;
    }

    @Override
    public String getText(int index, PCLCardTarget perspective, Object requestor, boolean addPeriod) {
        return effects.size() > index && !effects.get(index).shouldHideText() ? effects.get(index).getText(perspective, requestor, addPeriod) : EUIUtils.EMPTY_STRING;
    }

    @Override
    public String getText(PCLCardTarget perspective, Object requestor, boolean addPeriod) {
        return amount > 0 ? (capital(TEXT.act_choose(getAmountRawString()), addPeriod) + COLON_SEPARATOR +
                capital((generated ? joinEffectTexts(effects) : PCLCoreStrings.joinWithOr(getEffectTextsWithoutPeriod(EUIUtils.filter(effects, ef -> !ef.shouldHideText()), perspective, requestor, addPeriod))), true)) :
                generated ? joinEffectTexts(effects) : PCLCoreStrings.joinWithAnd(getEffectTextsWithoutPeriod(EUIUtils.filter(effects, ef -> !ef.shouldHideText()), perspective, requestor, addPeriod)) + PCLCoreStrings.period(addPeriod);
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
    public boolean isDetrimental() {
        return EUIUtils.any(effects, PSkill::isDetrimental);
    }

    @Override
    public boolean isMetascaling() {
        return EUIUtils.any(effects, PSkill::isMetascaling);
    }

    @Override
    public PMultiSkill makeCopy() {
        PMultiSkill copy = (PMultiSkill) super.makeCopy();
        for (PSkill<?> effect : effects) {
            if (effect != null) {
                copy.addEffect(effect.makeCopy());
            }
        }
        return copy;
    }

    public PMultiSkill makePreviews(RotatingList<EUIPreview> previews) {
        for (PSkill<?> effect : effects) {
            effect.makePreviews(previews);
        }
        super.makePreviews(previews);
        return this;
    }

    @Override
    public float modifyBlockFirst(PCLUseInfo info, float amount) {
        for (PSkill<?> be : effects) {
            amount = be.modifyBlockFirst(info, amount);
        }
        return amount;
    }

    @Override
    public float modifyBlockLast(PCLUseInfo info, float amount) {
        for (PSkill<?> be : effects) {
            amount = be.modifyBlockLast(info, amount);
        }
        return amount;
    }

    @Override
    public int modifyCost(PCLUseInfo info, int amount) {
        for (PSkill<?> be : effects) {
            amount = be.modifyCost(info, amount);
        }
        return amount;
    }

    @Override
    public float modifyDamageGiveFirst(PCLUseInfo info, float amount) {
        for (PSkill<?> be : effects) {
            amount = be.modifyDamageGiveFirst(info, amount);
        }
        return amount;
    }

    @Override
    public float modifyDamageGiveLast(PCLUseInfo info, float amount) {
        for (PSkill<?> be : effects) {
            amount = be.modifyDamageGiveLast(info, amount);
        }
        return amount;
    }

    @Override
    public float modifyDamageReceiveFirst(PCLUseInfo info, float amount, DamageInfo.DamageType type) {
        for (PSkill<?> be : effects) {
            amount = be.modifyDamageReceiveFirst(info, amount, type);
        }
        return amount;
    }

    @Override
    public float modifyDamageReceiveLast(PCLUseInfo info, float amount, DamageInfo.DamageType type) {
        for (PSkill<?> be : effects) {
            amount = be.modifyDamageReceiveLast(info, amount, type);
        }
        return amount;
    }

    @Override
    public float modifyHeal(PCLUseInfo info, float amount) {
        for (PSkill<?> be : effects) {
            amount = be.modifyHeal(info, amount);
        }
        return amount;
    }

    @Override
    public float modifyHitCount(PCLUseInfo info, float amount) {
        for (PSkill<?> be : effects) {
            amount = be.modifyHitCount(info, amount);
        }
        return amount;
    }

    @Override
    public float modifyOrbIncoming(PCLUseInfo info, float amount) {
        for (PSkill<?> be : effects) {
            amount = be.modifyOrbIncoming(info, amount);
        }
        return amount;
    }

    @Override
    public float modifyOrbOutgoing(PCLUseInfo info, float amount) {
        for (PSkill<?> be : effects) {
            amount = be.modifyOrbOutgoing(info, amount);
        }
        return amount;
    }

    @Override
    public float modifyRightCount(PCLUseInfo info, float amount) {
        for (PSkill<?> be : effects) {
            amount = be.modifyRightCount(info, amount);
        }
        return amount;
    }

    @Override
    public float modifySkillBonus(PCLUseInfo info, float amount) {
        for (PSkill<?> be : effects) {
            amount = be.modifySkillBonus(info, amount);
        }
        return amount;
    }

    @Override
    public PMultiSkill onAddToCard(AbstractCard card) {
        addSubs(card);
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
    public PMultiSkill onRemoveFromCard(AbstractCard card) {
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
        for (PSkill<?> effect : effects) {
            effect.refresh(info, conditionMet, false);
        }
    }

    public boolean removable() {
        return effects.isEmpty() || EUIUtils.all(effects, PSkill::removable);
    }

    public boolean requiresTarget() {
        return target == PCLCardTarget.Single || EUIUtils.any(effects, PSkill::requiresTarget);
    }

    @Override
    public PMultiSkill setAmountFromCard() {
        super.setAmountFromCard();
        for (PSkill<?> effect : effects) {
            effect.setAmountFromCard();
        }
        return this;
    }

    public PMultiSkill setEffects(PSkill<?>... effects) {
        return setEffects(Arrays.asList(effects));
    }

    public PMultiSkill setEffects(List<PSkill<?>> effects) {
        this.effects.clear();
        this.effects.addAll(effects);
        setParentsForChildren();

        return this;
    }

    public PMultiSkill setGenerated(boolean val) {
        generated = val;
        return this;
    }

    @Override
    public PMultiSkill setSource(PointerProvider card) {
        super.setSource(card);
        for (PSkill<?> effect : effects) {
            effect.setSource(card);
        }
        return this;
    }

    @Override
    public PMultiSkill setTemporaryAmount(int amount) {
        for (PSkill<?> effect : effects) {
            effect.setTemporaryAmount(amount);
        }
        return this;
    }

    @Override
    public PMultiSkill setTemporaryExtra(int extra) {
        for (PSkill<?> effect : effects) {
            effect.setTemporaryExtra(extra);
        }
        return this;
    }

    @Override
    public boolean shouldUseWhenText() {
        return EUIUtils.all(getSubEffects(), PSkill::shouldUseWhenText);
    }

    @Override
    public PMultiSkill stack(PSkill<?> other) {
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
            chooseEffect(info, order, effects);
        }
        else {
            if (useParent) {
                for (PSkill<?> effect : effects) {
                    effect.use(info, order);
                }
            }
            else {
                useSkill(info, order, 0);
            }
        }
    }

    @Override
    public void use(PCLUseInfo info, PCLActions order, boolean shouldPay) {
        use(info, order);
    }

    @Override
    public void useOutsideOfBattle() {
        super.useOutsideOfBattle();
        for (PSkill<?> effect : effects) {
            effect.useOutsideOfBattle();
        }
    }

    public PMultiSkill useParent(boolean value) {
        this.useParent = value;
        for (PSkill<?> effect : effects) {
            effect.useParent(value);
        }
        return this;
    }

    public void useSkill(PCLUseInfo info, PCLActions order, int index) {
        PSkill<?> skill = getSubEffect(index);
        if (skill instanceof PCallbackMove) {
            ((PCallbackMove<?>) skill).use(info, order, i -> useSkill(i, order, index + 1));
        }
        else {
            if (skill != null) {
                skill.use(info, order);
            }
            if (index < effects.size() - 1) {
                useSkill(info, order, index + 1);
            }
        }
    }
}
