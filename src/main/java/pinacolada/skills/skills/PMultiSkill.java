package pinacolada.skills.skills;

import com.badlogic.gdx.graphics.Color;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import extendedui.EUIUtils;
import extendedui.interfaces.delegates.ActionT1;
import extendedui.ui.tooltips.EUICardPreview;
import extendedui.utilities.RotatingList;
import pinacolada.actions.PCLActions;
import pinacolada.annotations.VisibleSkill;
import pinacolada.cards.base.PCLCard;
import pinacolada.cards.base.fields.PCLCardTarget;
import pinacolada.cards.pcl.special.QuestionMark;
import pinacolada.dungeon.PCLUseInfo;
import pinacolada.interfaces.markers.PMultiBase;
import pinacolada.interfaces.providers.PointerProvider;
import pinacolada.resources.pcl.PCLCoreStrings;
import pinacolada.skills.PSkill;
import pinacolada.skills.PSkillData;
import pinacolada.skills.PSkillSaveData;
import pinacolada.skills.fields.PField_Empty;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@VisibleSkill
public class PMultiSkill extends PSkill<PField_Empty> implements PMultiBase<PSkill<?>> {
    public static final PSkillData<PField_Empty> DATA = register(PMultiSkill.class, PField_Empty.class, 0, DEFAULT_MAX)
            .selfTarget();
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
    public PSkill<PField_Empty> addAmountForCombat(int amount) {
        for (PSkill<?> effect : effects) {
            effect.addAmountForCombat(amount);
        }
        return this;
    }

    @Override
    public PSkill<PField_Empty> addExtraForCombat(int extra) {
        for (PSkill<?> effect : effects) {
            effect.addExtraForCombat(extra);
        }
        return this;
    }

    @Override
    public boolean canPlay(PCLUseInfo info) {
        boolean canPlay = true;
        for (PSkill<?> be : effects) {
            canPlay = canPlay & be.canPlay(info);
        }
        return canPlay;
    }

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
    public String getSampleText(PSkill<?> callingSkill) {
        return null;
    }

    public String getSpecialData() {
        return PSkill.joinDataAsJson(effects, PSkill::serialize);
    }

    @Override
    public String getSubText() {
        return null;
    }

    @Override
    public String getText(int index, boolean addPeriod) {
        return effects.size() > index ? effects.get(index).getText(addPeriod) : "";
    }

    @Override
    public String getText(boolean addPeriod) {
        return amount > 0 ? (capital(TEXT.act_choose(amount), addPeriod) + ": " +
                (generated ? joinEffectTexts(effects) : PCLCoreStrings.joinWithOr(getEffectTextsWithoutPeriod(effects, addPeriod)))) :
                generated ? joinEffectTexts(effects) : PCLCoreStrings.joinWithAnd(getEffectTextsWithoutPeriod(effects, addPeriod)) + PCLCoreStrings.period(addPeriod);
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
    public boolean isBlank() {
        return effects.size() == 0 && !(childEffect != null && !childEffect.isBlank());
    }

    @Override
    public boolean isDetrimental() {
        return EUIUtils.any(effects, PSkill::isDetrimental);
    }

    @Override
    public PMultiSkill makeCopy() {
        PMultiSkill copy = (PMultiSkill) super.makeCopy();
        for (PSkill<?> effect : effects) {
            copy.addEffect(effect.makeCopy());
        }
        return copy;
    }

    public PMultiSkill makePreviews(RotatingList<EUICardPreview> previews) {
        for (PSkill<?> effect : effects) {
            effect.makePreviews(previews);
        }
        super.makePreviews(previews);
        return this;
    }

    @Override
    public float modifyBlock(PCLUseInfo info, float amount) {
        for (PSkill<?> be : effects) {
            amount = be.modifyBlock(info, amount);
        }
        return amount;
    }

    @Override
    public float modifyDamage(PCLUseInfo info, float amount) {
        for (PSkill<?> be : effects) {
            amount = be.modifyDamage(info, amount);
        }
        return amount;
    }

    @Override
    public float modifyDamageIncoming(PCLUseInfo info, float amount, DamageInfo.DamageType type) {
        for (PSkill<?> be : effects) {
            amount = be.modifyDamageIncoming(info, amount, type);
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
    public float modifySkillBonus(PCLUseInfo info, float amount) {
        for (PSkill<?> be : effects) {
            amount = be.modifySkillBonus(info, amount);
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
    public void refresh(PCLUseInfo info, boolean conditionMet) {
        for (PSkill<?> effect : effects) {
            effect.refresh(info, conditionMet);
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

    public PMultiSkill stack(PSkill<?> other) {
        super.stack(other);
        if (other instanceof PMultiBase) {
            stackMulti((PMultiBase<?>) other);
        }
        return this;
    }

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
            chooseEffect(info, order);
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
    public void use(PCLUseInfo info, PCLActions order, boolean isUsing) {
        use(info, order);
    }

    public PMultiSkill useParent(boolean value) {
        this.useParent = value;
        for (PSkill<?> effect : effects) {
            effect.useParent(value);
        }
        return this;
    }

    public PMultiSkill addEffect(PSkill<?> newEffect) {
        this.effects.add(newEffect);
        setParentsForChildren();
        return this;
    }

    public PSkill<?> getSubEffect(int index) {
        return index < effects.size() ? effects.get(index) : null;
    }

    public List<PSkill<?>> getSubEffects() {
        return effects;
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

    public void chooseEffect(PCLUseInfo info, PCLActions order) {
        PCLCard choiceCard = EUIUtils.safeCast(sourceCard, PCLCard.class);
        if (choiceCard == null) {
            choiceCard = new QuestionMark();
        }

        order.tryChooseSkill(choiceCard.cardData, amount, info.source, info.target, effects);
    }

    public PMultiSkill setGenerated(boolean val) {
        generated = val;
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
