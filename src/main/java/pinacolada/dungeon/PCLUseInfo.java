package pinacolada.dungeon;

import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import extendedui.EUIUtils;
import pinacolada.cards.base.fields.PCLAffinity;
import pinacolada.interfaces.markers.EditorCard;
import pinacolada.utilities.GameUtilities;
import pinacolada.utilities.RandomizedList;

import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;

public class PCLUseInfo {
    public final HashMap<PCLPlayerMeter, Object> auxiliaryData = new HashMap<>();
    public final RandomizedList<AbstractCreature> targets;
    public final RandomizedList<AbstractCreature> tempTargets = new RandomizedList<>();
    public AbstractCreature source;
    public AbstractCreature target;
    public AbstractCard card;
    public AbstractCard previousCard;
    public boolean canActivateSemiLimited;
    public boolean canActivateLimited;
    public boolean isStarter;
    public Object data;

    public PCLUseInfo(AbstractCard card, AbstractCreature source, AbstractCreature target) {
        this.targets = new RandomizedList<>();

        set(card, source, target);
    }

    public RandomizedList<AbstractCreature> fillWithTargets() {
        this.targets.clear();
        if (card instanceof EditorCard) {
            ((EditorCard) card).pclTarget().getTargets(source, target, targets);
        }
        else if (card != null) {
            switch (card.target) {
                case ALL:
                    GameUtilities.fillWithAllCharacters(true, targets);
                    break;
                case ALL_ENEMY:
                    GameUtilities.fillWithEnemies(true, targets);
                    break;
                case ENEMY:
                    targets.add(target);
                    break;
                case SELF_AND_ENEMY:
                    targets.add(target);
                case SELF:
                case NONE:
                    targets.add(AbstractDungeon.player);
            }
        }

        setTempTargets(targets);
        return targets;
    }

    public <T> T getAux(PCLPlayerMeter meter, Class<T> dataClass) {
        return EUIUtils.safeCast(auxiliaryData.getOrDefault(meter, null), dataClass);
    }

    public <T> T getAuxOrCreate(PCLPlayerMeter meter, Class<T> dataClass) {
        T object = EUIUtils.safeCast(auxiliaryData.getOrDefault(meter, null), dataClass);
        if (object == null) {
            try {
                object = dataClass.newInstance();
            }
            catch (Exception e) {
                throw new RuntimeException("Failed to instantiate PCLUseInfo auxiliary data for meter " + meter.getClass().getName() + ", dataClass " + dataClass.getName());
            }
        }
        auxiliaryData.put(meter, object);
        return object;
    }

    public <T> T getAuxOrCreate(PCLPlayerMeter meter, Constructor<T> dataClass, Object... args) {
        T object = EUIUtils.safeCast(auxiliaryData.getOrDefault(meter, null), dataClass.getDeclaringClass());
        if (object == null) {
            try {
                object = dataClass.newInstance(args);
            }
            catch (Exception e) {
                throw new RuntimeException("Failed to instantiate PCLUseInfo auxiliary data for meter " + meter.getClass().getName() + ", dataClass " + dataClass.getName());
            }
        }
        auxiliaryData.put(meter, object);
        return object;
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

    public PCLUseInfo set(AbstractCard card, AbstractCreature source, AbstractCreature target) {
        this.card = card;
        this.source = source;
        this.target = target;
        this.previousCard = CombatManager.lastCardPlayed;
        fillWithTargets();
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

    public PCLUseInfo setTempTargets(AbstractCreature... creatures) {
        tempTargets.clear();
        tempTargets.addAll(creatures);
        return this;
    }

    public PCLUseInfo setTempTargets(Collection<? extends AbstractCreature> creatures) {
        tempTargets.clear();
        tempTargets.addAll(creatures);
        return this;
    }

    public boolean tryActivateLimited() {
        return CombatManager.tryActivateLimited(card.cardID);
    }

    public boolean tryActivateSemiLimited() {
        return CombatManager.tryActivateSemiLimited(card.cardID);
    }
}
