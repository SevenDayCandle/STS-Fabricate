package pinacolada.interfaces.markers;

import com.megacrit.cardcrawl.cards.AbstractCard;
import pinacolada.skills.PSkill;
import pinacolada.skills.skills.PTrigger;

import java.util.ArrayList;
import java.util.Arrays;

public interface EditorMaker {
    default public EditorMaker addPPower(PTrigger effect) {
        return addPPower(effect, false);
    }

    default public EditorMaker addPPower(PTrigger effect, boolean makeCopy) {
        if (makeCopy && effect != null) {
            effect = effect.makeCopy();
        }
        getPowers().add(effect);

        return this;
    }

    default public EditorMaker addPSkill(PSkill<?> effect) {
        return addPSkill(effect, false);
    }

    default public EditorMaker addPSkill(PSkill<?> effect, boolean makeCopy) {
        if (makeCopy && effect != null) {
            effect = effect.makeCopy();
        }
        getMoves().add(effect);

        return this;
    }

    AbstractCard.CardColor getCardColor();

    ArrayList<PSkill<?>> getMoves();

    ArrayList<PTrigger> getPowers();

    <T extends EditorMaker> T makeCopy();

    default public EditorMaker setPPower(PTrigger... effect) {
        return setPPower(Arrays.asList(effect));
    }

    default public EditorMaker setPPower(Iterable<PTrigger> currentEffects) {
        return setPPower(currentEffects, false, true);
    }

    default public EditorMaker setPPower(Iterable<PTrigger> currentEffects, boolean makeCopy, boolean clear) {
        if (clear) {
            getPowers().clear();
        }
        for (PTrigger be : currentEffects) {
            addPPower(be, makeCopy);
        }
        return this;
    }

    default public EditorMaker setPSkill(PSkill<?>... effect) {
        return setPSkill(Arrays.asList(effect));
    }

    default public EditorMaker setPSkill(Iterable<PSkill<?>> currentEffects) {
        return setPSkill(currentEffects, false, true);
    }

    default public EditorMaker setPSkill(Iterable<PSkill<?>> currentEffects, boolean makeCopy, boolean clear) {
        if (clear) {
            getMoves().clear();
        }
        for (PSkill<?> be : currentEffects) {
            addPSkill(be, makeCopy);
        }
        return this;
    }
}
