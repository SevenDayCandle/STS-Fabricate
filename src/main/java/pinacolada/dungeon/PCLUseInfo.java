package pinacolada.dungeon;

import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import extendedui.EUIUtils;
import pinacolada.cards.base.fields.PCLAffinity;
import pinacolada.utilities.GameUtilities;

import java.util.ArrayList;
import java.util.List;

public class PCLUseInfo {
    public final AbstractCreature source;
    public final AbstractCreature target;
    public final AbstractCard card;
    public final AbstractCard previousCard;
    public final PCLAffinity currentAffinity;
    public final ArrayList<AbstractMonster> enemies;
    public final boolean canActivateSemiLimited;
    public final boolean canActivateLimited;
    public final boolean isStarter;
    public Object data;

    public PCLUseInfo(AbstractCard card, AbstractCreature source, AbstractCreature target) {
        this.card = card;
        this.source = source;
        this.target = target;
        this.previousCard = CombatManager.playerSystem.getLastCardPlayed();
        this.currentAffinity = CombatManager.playerSystem.getActiveMeter().getCurrentAffinity();
        this.enemies = GameUtilities.getEnemies(true);
        if (card != null) {
            this.canActivateSemiLimited = CombatManager.canActivateSemiLimited(card.cardID);
            this.canActivateLimited = CombatManager.canActivateLimited(card.cardID);
            this.isStarter = GameUtilities.isStarter(card);
        }
        else {
            this.canActivateSemiLimited = false;
            this.canActivateLimited = false;
            this.isStarter = false;
        }
    }

    public <T> T getData(Class<T> dataClass) {
        return EUIUtils.safeCast(data, dataClass);
    }

    public <T> List<? extends T> getDataAsList(Class<T> dataClass) {
        List<?> list = EUIUtils.safeCast(data, List.class);
        if (list != null && list.size() > 0 && dataClass.isInstance(list.get(0))) {
            return (List<? extends T>) data;
        }
        return null;
    }

    public String getPreviousCardID() {
        return previousCard != null ? previousCard.cardID : "";
    }

    public PCLUseInfo setData(Object data) {
        this.data = data;
        return this;
    }

    public boolean tryActivateLimited() {
        return CombatManager.tryActivateLimited(card.cardID);
    }

    public boolean tryActivateSemiLimited() {
        return CombatManager.tryActivateSemiLimited(card.cardID);
    }
}
