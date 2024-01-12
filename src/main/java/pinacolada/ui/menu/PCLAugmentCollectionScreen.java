package pinacolada.ui.menu;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.evacipated.cardcrawl.modthespire.lib.SpireEnum;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import extendedui.EUI;
import extendedui.interfaces.delegates.ActionT1;
import extendedui.interfaces.delegates.FuncT0;
import extendedui.patches.game.AbstractDungeonPatches;
import extendedui.ui.screens.EUIPoolScreen;
import pinacolada.augments.PCLAugment;
import pinacolada.cards.base.PCLCard;
import pinacolada.effects.PCLEffect;
import pinacolada.effects.card.ChooseCardForAugmentEffect;
import pinacolada.resources.PGR;
import pinacolada.ui.cardView.PCLAugmentList;
import pinacolada.utilities.GameUtilities;

import java.util.ArrayList;

public class PCLAugmentCollectionScreen extends EUIPoolScreen {

    @SpireEnum
    public static AbstractDungeon.CurrentScreen AUGMENT_SCREEN;

    protected PCLAugmentList panel;
    protected PCLEffect curEffect;
    protected FuncT0<ArrayList<PCLAugment>> getEntries;
    protected ActionT1<PCLAugment> addItem;
    protected boolean canSelect;

    public PCLAugmentCollectionScreen() {
        panel = new PCLAugmentList(this::doAction, this::doRemove).enableCancel(false);
    }

    // To prevent the user from accessing the augment screen from the pop-up view while the augment selection is open
    public boolean allowOpenDeck() {
        return curEffect != null;
    }

    public boolean allowOpenMap() {
        return curEffect != null;
    }

    @Override
    public AbstractDungeon.CurrentScreen curScreen() {
        return AUGMENT_SCREEN;
    }

    public void doAction(PCLAugment augment) {
        if (canSelect && augment != null) {
            AbstractDungeon.CurrentScreen wayBack = AbstractDungeonPatches.coolerPreviousScreen;
            AbstractDungeonPatches.coolerPreviousScreen = AUGMENT_SCREEN; // So that the effect will return to this screen once we are done
            curEffect = new ChooseCardForAugmentEffect(augment)
                    .setOnCancel(__ -> {
                        AbstractDungeonPatches.coolerPreviousScreen = wayBack; // Prevent previous screen before this from going poof
                    })
                    .addCallback(__ -> {
                                AbstractDungeonPatches.coolerPreviousScreen = wayBack;
                                refreshAugments();
                            }
                    );
        }
        else {
            close();
        }
    }

    public void doRemove(PCLAugment augment) {
        if (canSelect && augment != null && augment.card instanceof PCLCard) {
               PCLAugment retrieved = ((PCLCard) augment.card).removeAugment(augment);
               if (retrieved != null) {
                   PGR.dungeon.addAugment(retrieved.save);
               }
               refreshAugments();
        }
    }

    public void openScreen(FuncT0<ArrayList<PCLAugment>> getEntries, int rows, boolean canSelect) {
        super.reopen();
        this.getEntries = getEntries;
        this.canSelect = canSelect;
        panel = new PCLAugmentList(this::doAction, this::doRemove, rows).enableCancel(false);
        addItem = canSelect ? (a) -> panel.addPanelItem(a) : panel::addListItem;
        refreshAugments();
    }

    @Override
    public void openingSettings() {
        AbstractDungeon.previousScreen = curScreen();
    }

    public void refreshAugments() {
        panel.clear();
        ArrayList<PCLAugment> entries = getEntries != null ? getEntries.invoke() : new ArrayList<>();
        for (PCLAugment aug : entries) {
            addItem.invoke(aug);
        }
        EUI.countingPanel.openManual(GameUtilities.augmentStats(entries), __ -> {}, false);
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

    @Override
    public void update() {
        if (curEffect != null) {
            curEffect.update();
            if (curEffect.isDone) {
                curEffect = null;
                refreshAugments();
            }
        }
        else {
            panel.updateImpl();
        }
        EUI.countingPanel.tryUpdate();

    }


}
