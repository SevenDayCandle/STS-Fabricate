package pinacolada.interfaces.markers;

import com.megacrit.cardcrawl.cards.AbstractCard;
import pinacolada.skills.PSkill;

import java.util.Arrays;
import java.util.List;

// Interface to denote that a baseeffect is comprised of multiple effects
public interface PMultiBase<T extends PSkill<?>> {
    default PMultiBase<T> addEffect(T newEffect) {
        getSubEffects().add(newEffect);
        setParentsForChildren();
        return this;
    }

    default void addSubs(AbstractCard c) {
        for (PSkill<?> be : getSubEffects()) {
            be.onAddToCard(c);
        }
    }

    default void displayChildUpgrades(boolean value) {
        for (PSkill<?> be : getSubEffects()) {
            be.displayUpgrades(value);
        }
    }

    default T getSubEffect(int index) {
        return index < getSubEffects().size() ? getSubEffects().get(index) : null;
    }

    List<T> getSubEffects();

    default void removeSubs(AbstractCard c) {
        for (PSkill<?> be : getSubEffects()) {
            be.onRemoveFromCard(c);
        }
    }

    default PMultiBase<T> setEffects(T... effects) {
        return setEffects(Arrays.asList(effects));
    }

    default PMultiBase<T> setEffects(List<T> effects) {
        getSubEffects().clear();
        getSubEffects().addAll(effects);
        setParentsForChildren();

        return this;
    }

    default void setParentsForChildren() {
        for (PSkill<?> be : getSubEffects()) {
            if (be != null) {
                be.parent = (PSkill<?>) this;
            }
        }
    }

    default PMultiBase<T> stackMulti(PMultiBase<?> other) {
        for (int i = 0; i < Math.min(getSubEffects().size(), other.getSubEffects().size()); i++) {
            getSubEffect(i).stack(other.getSubEffect(i));
        }
        return this;
    }
}
