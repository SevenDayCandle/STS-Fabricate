package pinacolada.dungeon;

import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import extendedui.EUIUtils;
import pinacolada.cards.base.fields.PCLAffinity;
import pinacolada.utilities.GameUtilities;
import pinacolada.utilities.RandomizedList;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class PCLUseInfo {
    public final ArrayList<AbstractMonster> enemies;
    public final RandomizedList<AbstractCreature> targetList = new RandomizedList<>();
    public AbstractCreature source;
    public AbstractCreature target;
    public AbstractCard card;
    public AbstractCard previousCard;
    public PCLAffinity currentAffinity;
    public boolean canActivateSemiLimited;
    public boolean canActivateLimited;
    public boolean isStarter;
    public Object data;

    public PCLUseInfo(AbstractCard card, AbstractCreature source, AbstractCreature target) {
        this.enemies = new ArrayList<>();

        set(card, source, target);
    }

    public PCLUseInfo(PCLUseInfo other) {
        this.card = other.card;
        this.source = other.source;
        this.target = other.target;
        this.previousCard = other.previousCard;
        this.currentAffinity = other.currentAffinity;
        this.enemies = new ArrayList<>(other.enemies);
        this.canActivateSemiLimited = other.canActivateSemiLimited;
        this.canActivateLimited = other.canActivateLimited;
        this.isStarter = other.isStarter;
        this.data = other.data;
    }

    public <T> T getData(Class<T> dataClass) {
        return EUIUtils.safeCast(data, dataClass);
    }

    public <T> ArrayList<? extends T> getDataAsArrayList(Class<T> dataClass) {
        ArrayList<?> list = EUIUtils.safeCast(data, ArrayList.class);
        if (list != null && list.size() > 0 && dataClass.isInstance(list.get(0))) {
            return (ArrayList<? extends T>) data;
        }
        return null;
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

    public PCLUseInfo makeCopy() {
        return new PCLUseInfo(this);
    }

    public PCLUseInfo set(AbstractCard card, AbstractCreature source, AbstractCreature target) {
        this.card = card;
        this.source = source;
        this.target = target;
        this.previousCard = CombatManager.playerSystem.getLastCardPlayed();
        this.currentAffinity = CombatManager.playerSystem.getActiveMeter().getCurrentAffinity();
        this.enemies.clear();
        GameUtilities.fillWithEnemies(true, this.enemies);
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
        this.data = null;
        return this;
    }

    public PCLUseInfo setData(Object data) {
        this.data = data;
        return this;
    }

    public PCLUseInfo setTargetList(AbstractCreature... creatures) {
        targetList.clear();
        targetList.addAll(creatures);
        return this;
    }

    public PCLUseInfo setTargetList(Collection<? extends AbstractCreature> creatures) {
        targetList.clear();
        targetList.addAll(creatures);
        return this;
    }

    public boolean tryActivateLimited() {
        return CombatManager.tryActivateLimited(card.cardID);
    }

    public boolean tryActivateSemiLimited() {
        return CombatManager.tryActivateSemiLimited(card.cardID);
    }
}
