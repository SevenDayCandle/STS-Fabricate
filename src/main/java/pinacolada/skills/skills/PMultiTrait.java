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
            .selfTarget();
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

    public String getSpecialData() {
        return PSkill.joinDataAsJson(effects, PSkill::serialize);
    }

    @Override
    public String getText(int index, boolean addPeriod) {
        return effects.size() > index ? effects.get(index).getText(addPeriod) : "";
    }

    @Override
    public String getText(boolean addPeriod) {
        return EUIUtils.joinStrings(" ", getEffectTexts(effects, addPeriod));
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

    public PMultiTrait makePreviews(RotatingList<EUIPreview> previews) {
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
    public void onDrag(AbstractMonster m) {
        for (PSkill<?> effect : effects) {
            effect.onDrag(m);
        }
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

    @Override
    public PMultiTrait setSource(PointerProvider card) {
        super.setSource(card);
        for (PSkill<?> effect : effects) {
            effect.setSource(card);
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

    public PMultiTrait useParent(boolean value) {
        this.useParent = value;
        for (PSkill<?> effect : effects) {
            effect.useParent(value);
        }
        return this;
    }

    public PMultiTrait addEffect(PTrait<?> newEffect) {
        this.effects.add(newEffect);
        setParentsForChildren();
        return this;
    }

    public PTrait<?> getSubEffect(int index) {
        return index < effects.size() ? effects.get(index) : null;
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

    public List<PTrait<?>> getSubEffects() {
        return effects;
    }

    @Override
    public String getSampleText(PSkill<?> callingSkill, PSkill<?> parentSkill) {
        return null;
    }

    @Override
    public String getSubText() {
        return null;
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

    @Override
    public PMultiTrait onAddToCard(AbstractCard card) {
        addSubs(card);
        super.onAddToCard(card);
        return this;
    }

    @Override
    public PMultiTrait onRemoveFromCard(AbstractCard card) {
        removeSubs(card);
        super.onRemoveFromCard(card);
        return this;
    }

    @Override
    public void refresh(PCLUseInfo info, boolean conditionMet) {
        for (PSkill<?> effect : effects) {
            effect.refresh(info, conditionMet);
        }
    }

    @Override
    public PMultiTrait setTemporaryAmount(int amount) {
        for (PSkill<?> effect : effects) {
            effect.setTemporaryAmount(amount);
        }
        return this;
    }

    @Override
    public String getSubDescText() {
        return null;
    }

    @Override
    public String getSubSampleText() {
        return null;
    }
}
