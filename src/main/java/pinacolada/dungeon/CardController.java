package pinacolada.dungeon;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import extendedui.interfaces.delegates.ActionT1;
import extendedui.interfaces.delegates.FuncT1;
import pinacolada.ui.combat.ControllableCardPile;

public class CardController {
    public final AbstractCard card;
    protected boolean enabled;
    protected FuncT1<Boolean, CardController> useCondition;
    protected ActionT1<CardController> onUpdate;
    protected ActionT1<CardController> onSelect;
    protected ActionT1<CardController> onDelete;
    public ControllableCardPile sourcePile;

    public CardController(ControllableCardPile sourcePile, AbstractCard card) {
        this.sourcePile = sourcePile;
        this.card = card;
        this.enabled = true;
    }

    public boolean canUse() {
        return enabled && AbstractDungeon.getCurrMapNode() != null && card != null && (useCondition == null || useCondition.invoke(this));
    }

    public void delete() {
        sourcePile.remove(this);

        if (onDelete != null) {
            onDelete.invoke(this);
        }
    }

    public boolean isEnabled() {
        return enabled;
    }

    public CardController onDelete(ActionT1<CardController> onCompletion) {
        onDelete = onCompletion;

        return this;
    }

    public CardController onSelect(ActionT1<CardController> onCompletion) {
        onSelect = onCompletion;

        return this;
    }

    public CardController onUpdate(ActionT1<CardController> onCompletion) {
        onUpdate = onCompletion;

        return this;
    }

    public void render(SpriteBatch sb) {
        card.render(sb);
    }

    public void select() {
        if (onSelect != null) {
            onSelect.invoke(this);
        }
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public CardController setUseCondition(FuncT1<Boolean, CardController> useCondition) {
        this.useCondition = useCondition;

        return this;
    }

    public void update() {
        if (onUpdate != null) {
            onUpdate.invoke(this);
        }
    }

    public void update(ControllableCardPile pile) {
        card.update();
    }
}
