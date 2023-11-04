package pinacolada.skills.skills;

import com.badlogic.gdx.graphics.Color;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import extendedui.EUIUtils;
import extendedui.interfaces.delegates.ActionT1;
import extendedui.ui.tooltips.EUIPreview;
import extendedui.utilities.RotatingList;
import pinacolada.annotations.VisibleSkill;
import pinacolada.cards.base.fields.PCLCardTarget;
import pinacolada.dungeon.PCLUseInfo;
import pinacolada.interfaces.markers.PMultiBase;
import pinacolada.interfaces.providers.PointerProvider;
import pinacolada.skills.PSkill;
import pinacolada.skills.PSkillData;
import pinacolada.skills.PSkillSaveData;
import pinacolada.skills.PTrait;
import pinacolada.skills.fields.PField_Empty;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@VisibleSkill
public class PMultiTrait extends PTrait<PField_Empty> implements PMultiBase<PTrait<?>> {
    public static final PSkillData<PField_Empty> DATA = register(PMultiTrait.class, PField_Empty.class, 0, DEFAULT_MAX)
            .noTarget();
    protected ArrayList<PTrait<?>> effects = new ArrayList<>();

    public PMultiTrait() {
        super(DATA, 0);
    }

    public PMultiTrait(PSkillSaveData content) {
        super(DATA, content);
        effects = EUIUtils.mapAsNonnull(splitJson(content.special), e -> (PTrait<?>) PSkill.get(e));
        setParentsForChildren();
    }

    public PMultiTrait(PTrait<?>... effects) {
        super(DATA, 0);
        if (target == null) {
            target = PCLCardTarget.None;
        }
        this.effects.addAll(Arrays.asList(effects));
        setParentsForChildren();
    }

    public static PMultiTrait join(PTrait<?>... effects) {
        return new PMultiTrait(effects);
    }

    @Override
    public PSkill<PField_Empty> setAmountForCombat(int amount) {
        for (PSkill<?> effect : effects) {
            effect.setAmountForCombat(amount);
        }
        return this;
    }

    public PMultiTrait addEffect(PTrait<?> newEffect) {
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
    public String getSampleText(PSkill<?> callingSkill, PSkill<?> parentSkill) {
        return null;
    }

    public String getSpecialData() {
        return PSkill.joinDataAsJson(effects, PSkill::serialize);
    }

    @Override
    public String getSubDescText(PCLCardTarget perspective) {
        return null;
    }

    public PTrait<?> getSubEffect(int index) {
        return index < effects.size() ? effects.get(index) : null;
    }

    public List<PTrait<?>> getSubEffects() {
        return effects;
    }

    @Override
    public String getSubSampleText() {
        return null;
    }

    @Override
    public String getSubText(PCLCardTarget perspective, Object requestor) {
        return null;
    }

    @Override
    public String getText(int index, PCLCardTarget perspective, Object requestor, boolean addPeriod) {
        return effects.size() > index ? effects.get(index).getText(perspective, requestor, addPeriod) : "";
    }

    @Override
    public String getText(PCLCardTarget perspective, Object requestor, boolean addPeriod) {
        return EUIUtils.joinStrings(" ", getEffectTexts(effects, perspective, requestor, addPeriod));
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
    public PMultiTrait makeCopy() {
        PMultiTrait copy = (PMultiTrait) super.makeCopy();
        for (PTrait<?> effect : effects) {
            if (effect != null) {
                copy.addEffect(effect.makeCopy());
            }
        }
        return copy;
    }

    public PMultiTrait makePreviews(RotatingList<EUIPreview> previews) {
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
    public PMultiTrait onAddToCard(AbstractCard card) {
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
    public PMultiTrait onRemoveFromCard(AbstractCard card) {
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

    @Override
    public boolean removable() {
        return effects.isEmpty() || EUIUtils.all(effects, PSkill::removable);
    }

    @Override
    public PMultiTrait setAmountFromCard() {
        super.setAmountFromCard();
        for (PSkill<?> effect : effects) {
            effect.setAmountFromCard();
        }
        return this;
    }

    public PMultiTrait setEffects(PTrait<?>... effects) {
        return setEffects(Arrays.asList(effects));
    }

    public PMultiTrait setEffects(List<PTrait<?>> effects) {
        this.effects.clear();
        this.effects.addAll(effects);
        setParentsForChildren();

        return this;
    }

    @Override
    public PMultiTrait setSource(PointerProvider card) {
        super.setSource(card);
        for (PSkill<?> effect : effects) {
            effect.setSource(card);
        }
        return this;
    }

    @Override
    public PMultiTrait setTemporaryAmount(int amount) {
        for (PSkill<?> effect : effects) {
            effect.setTemporaryAmount(amount);
        }
        return this;
    }

    @Override
    public PMultiTrait setTemporaryExtra(int extra) {
        for (PSkill<?> effect : effects) {
            effect.setTemporaryExtra(extra);
        }
        return this;
    }

    public PMultiTrait stack(PSkill<?> other) {
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
    public void useOutsideOfBattle() {
        super.useOutsideOfBattle();
        for (PSkill<?> effect : effects) {
            effect.useOutsideOfBattle();
        }
    }

    public PMultiTrait useParent(boolean value) {
        this.useParent = value;
        for (PSkill<?> effect : effects) {
            effect.useParent(value);
        }
        return this;
    }
}
