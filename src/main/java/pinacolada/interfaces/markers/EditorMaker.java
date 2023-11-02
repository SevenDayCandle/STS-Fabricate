package pinacolada.interfaces.markers;

import com.badlogic.gdx.graphics.Texture;
import com.megacrit.cardcrawl.cards.AbstractCard;
import extendedui.EUIUtils;
import extendedui.interfaces.delegates.ActionT0;
import pinacolada.skills.PSkill;
import pinacolada.skills.skills.PTrigger;

import java.util.Arrays;
import java.util.List;

public interface EditorMaker<T> {
    default EditorMaker<T> addPPower(PSkill<?> effect) {
        return addPPower(effect, false);
    }

    default EditorMaker<T> addPPower(PSkill<?> effect, boolean makeCopy) {
        if (makeCopy && effect != null) {
            effect = effect.makeCopy();
        }
        getPowers().add(effect);

        return this;
    }

    default EditorMaker<T> addPSkill(PSkill<?> effect) {
        return addPSkill(effect, false);
    }

    default EditorMaker<T> addPSkill(PSkill<?> effect, boolean makeCopy) {
        if (makeCopy && effect != null) {
            effect = effect.makeCopy();
        }
        getMoves().add(effect);

        return this;
    }

    default void clearPowers() {
        getPowers().clear();
    }

    default void clearSkills() {
        getMoves().clear();
    }

    default void safeLoadValue(ActionT0 loadFunc) {
        try {
            loadFunc.invoke();
        }
        catch (Exception e) {
            // Using info since this can be really long
            EUIUtils.logInfoIfDebug(this, "Failed to load field: " + e.getLocalizedMessage());
        }
    }

    default EditorMaker<T> setPPower(PSkill<?>... effect) {
        return setPPower(Arrays.asList(effect));
    }

    default EditorMaker<T> setPPower(Iterable<PSkill<?>> currentEffects) {
        return setPPower(currentEffects, false, true);
    }

    default EditorMaker<T> setPPower(Iterable<PSkill<?>> currentEffects, boolean makeCopy, boolean clear) {
        if (clear) {
            clearPowers();
        }
        for (PSkill<?> be : currentEffects) {
            addPPower(be, makeCopy);
        }
        return this;
    }

    default EditorMaker<T> setPSkill(PSkill<?>... effect) {
        return setPSkill(Arrays.asList(effect));
    }

    default EditorMaker<T> setPSkill(Iterable<PSkill<?>> currentEffects) {
        return setPSkill(currentEffects, false, true);
    }

    default EditorMaker<T> setPSkill(Iterable<PSkill<?>> currentEffects, boolean makeCopy, boolean clear) {
        if (clear) {
            clearSkills();
        }
        for (PSkill<?> be : currentEffects) {
            addPSkill(be, makeCopy);
        }
        return this;
    }

    T create();

    AbstractCard.CardColor getCardColor();

    Texture getImage();

    List<PSkill<?>> getMoves();

    List<PSkill<?>> getPowers();

    <U extends EditorMaker<T>> U makeCopy();
}
