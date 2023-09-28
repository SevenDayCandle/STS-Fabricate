package pinacolada.interfaces.markers;

import com.badlogic.gdx.graphics.Texture;
import com.megacrit.cardcrawl.cards.AbstractCard;
import extendedui.EUIUtils;
import extendedui.interfaces.delegates.ActionT0;
import extendedui.utilities.ColoredTexture;
import pinacolada.skills.PSkill;
import pinacolada.skills.skills.PTrigger;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public interface EditorMaker {
    default EditorMaker addPPower(PTrigger effect) {
        return addPPower(effect, false);
    }

    default EditorMaker addPPower(PTrigger effect, boolean makeCopy) {
        if (makeCopy && effect != null) {
            effect = effect.makeCopy();
        }
        getPowers().add(effect);

        return this;
    }

    default EditorMaker addPSkill(PSkill<?> effect) {
        return addPSkill(effect, false);
    }

    default EditorMaker addPSkill(PSkill<?> effect, boolean makeCopy) {
        if (makeCopy && effect != null) {
            effect = effect.makeCopy();
        }
        getMoves().add(effect);

        return this;
    }

    default void safeLoadValue(ActionT0 loadFunc) {
        try {
            loadFunc.invoke();
        }
        catch (Exception e) {
            EUIUtils.logError(this, "Failed to load field: " + e.getMessage());
        }
    }

    default EditorMaker setPPower(PTrigger... effect) {
        return setPPower(Arrays.asList(effect));
    }

    default EditorMaker setPPower(Iterable<PTrigger> currentEffects) {
        return setPPower(currentEffects, false, true);
    }

    default EditorMaker setPPower(Iterable<PTrigger> currentEffects, boolean makeCopy, boolean clear) {
        if (clear) {
            getPowers().clear();
        }
        for (PTrigger be : currentEffects) {
            addPPower(be, makeCopy);
        }
        return this;
    }

    default EditorMaker setPSkill(PSkill<?>... effect) {
        return setPSkill(Arrays.asList(effect));
    }

    default EditorMaker setPSkill(Iterable<PSkill<?>> currentEffects) {
        return setPSkill(currentEffects, false, true);
    }

    default EditorMaker setPSkill(Iterable<PSkill<?>> currentEffects, boolean makeCopy, boolean clear) {
        if (clear) {
            getMoves().clear();
        }
        for (PSkill<?> be : currentEffects) {
            addPSkill(be, makeCopy);
        }
        return this;
    }

    AbstractCard.CardColor getCardColor();

    Texture getImage();

    List<PSkill<?>> getMoves();

    List<PTrigger> getPowers();

    <T extends EditorMaker> T makeCopy();
}
