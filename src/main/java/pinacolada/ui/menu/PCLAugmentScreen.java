package pinacolada.ui.menu;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.evacipated.cardcrawl.modthespire.lib.SpireEnum;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import extendedui.EUI;
import extendedui.EUIUtils;
import extendedui.interfaces.delegates.ActionT2;
import extendedui.interfaces.delegates.FuncT0;
import extendedui.ui.screens.EUIDungeonScreen;
import pinacolada.actions.piles.SelectFromPile;
import pinacolada.augments.PCLAugment;
import pinacolada.augments.PCLAugmentData;
import pinacolada.cards.base.PCLCard;
import pinacolada.effects.PCLEffect;
import pinacolada.effects.utility.ActionCallbackEffect;
import pinacolada.resources.PGR;
import pinacolada.ui.cardView.PCLAugmentList;
import pinacolada.utilities.GameUtilities;

import java.util.HashMap;
import java.util.Map;

public class PCLAugmentScreen extends EUIDungeonScreen {

    @SpireEnum
    public static AbstractDungeon.CurrentScreen AUGMENT_SCREEN;

    protected PCLAugmentList panel;
    protected PCLEffect curEffect;
    protected FuncT0<HashMap<PCLAugmentData, Integer>> getEntries;
    protected ActionT2<PCLAugment, Integer> addItem;
    protected boolean canSelect;

    public PCLAugmentScreen() {
        panel = new PCLAugmentList(this::doAction);
    }

    @Override
    public AbstractDungeon.CurrentScreen curScreen() {
        return AUGMENT_SCREEN;
    }

    @Override
    public void update() {
        if (curEffect != null) {
            curEffect.update();
            if (curEffect.isDone) {
                curEffect = null;
            }
        }
        else {
            panel.updateImpl();
        }
        EUI.countingPanel.tryUpdate();

    }

    @Override
    public void render(SpriteBatch sb) {
        if (curEffect != null) {
            curEffect.render(sb);
        }
        else {
            panel.renderImpl(sb);
        }
        EUI.countingPanel.tryRender(sb);
    }

    public void doAction(PCLAugment augment) {
        if (canSelect && augment != null) {
            curEffect = new ActionCallbackEffect(new SelectFromPile(augment.getName(), 1, AbstractDungeon.player.masterDeck)
                    .cancellableFromPlayer(true)
                    .setFilter(augment::canApply)
                    .addCallback(selection -> {
                        for (AbstractCard c : selection) {
                            PGR.dungeon.addAugment(augment.ID, -1);
                            augment.addToCard((PCLCard) c);
                            refreshAugments();
                        }
                    }));
        }
        else {
            close();
        }
    }

    public void open(FuncT0<HashMap<PCLAugmentData, Integer>> getEntries, int rows, boolean canSelect) {
        super.open();
        this.getEntries = getEntries;
        this.canSelect = canSelect;
        panel = new PCLAugmentList(this::doAction, rows);
        addItem = canSelect ? (a, b) -> panel.addPanelItem(a, b, EUIUtils.any(AbstractDungeon.player.masterDeck.group, a::canApply)) : panel::addListItem;
        refreshAugments();
    }

    @Override
    public void openingSettings() {
        AbstractDungeon.previousScreen = curScreen();
    }

    public void refreshAugments() {
        panel.clear();
        HashMap<PCLAugmentData, Integer> entries = getEntries != null ? getEntries.invoke() : new HashMap<>();
        for (Map.Entry<PCLAugmentData, Integer> params : entries.entrySet()) {
            PCLAugmentData data = params.getKey();
            int amount = params.getValue();
            if (data != null && amount > 0) {
                PCLAugment augment = data.create();
                addItem.invoke(augment, amount);
            }
        }
        EUI.countingPanel.openManual(GameUtilities.augmentStats(entries), null, false);
    }


}
