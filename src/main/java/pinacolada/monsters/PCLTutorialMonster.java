package pinacolada.monsters;

import basemod.BaseMod;
import com.megacrit.cardcrawl.actions.common.HealAction;
import com.megacrit.cardcrawl.actions.common.SetMoveAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.monsters.MonsterGroup;
import com.megacrit.cardcrawl.powers.AbstractPower;
import com.megacrit.cardcrawl.relics.AbstractRelic;
import com.megacrit.cardcrawl.relics.NeowsLament;
import extendedui.EUIUtils;
import extendedui.configuration.STSConfigItem;
import extendedui.interfaces.delegates.FuncT0;
import extendedui.interfaces.delegates.FuncT1;
import extendedui.interfaces.markers.TourProvider;
import extendedui.ui.tooltips.EUITourTooltip;
import pinacolada.actions.PCLActions;
import pinacolada.effects.PCLEffects;
import pinacolada.effects.PCLSFX;
import pinacolada.resources.PGR;
import pinacolada.resources.pcl.PCLCoreImages;
import pinacolada.resources.pcl.PCLCoreStrings;
import pinacolada.utilities.GameUtilities;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;

public abstract class PCLTutorialMonster extends PCLCreature implements TourProvider {
    public static ArrayList<TutorialInfo> tutorials = new ArrayList<>();
    public static STSConfigItem<Boolean> currentConfig;
    protected EUITourTooltip waitingTip;
    public ArrayList<FuncT0<EUITourTooltip>> steps = new ArrayList<>();
    public int current;

    public PCLTutorialMonster(PCLCreatureData data) {
        super(data);
    }

    public PCLTutorialMonster(PCLCreatureData data, float offsetX, float offsetY) {
        super(data, offsetX, offsetY);
    }

    public static void register(PCLCreatureData data, STSConfigItem<Boolean> config, FuncT1<Boolean, AbstractPlayer> shouldShow) {
        tutorials.add(new TutorialInfo(data, config, shouldShow));
        BaseMod.addMonster(data.ID, data::create);
    }

    public static MonsterGroup tryStart() {
        for (PCLTutorialMonster.TutorialInfo tutorialInfo : PCLTutorialMonster.tutorials) {
            if (tutorialInfo.shouldShow.invoke(AbstractDungeon.player) && !tutorialInfo.config.get()) {
                MonsterGroup group = tutorialInfo.data.getEncounter();
                if (group != null) {
                    currentConfig = tutorialInfo.config;
                    return group;
                }
            }
        }
        return null;
    }

    @SafeVarargs
    public final PCLTutorialMonster addSteps(FuncT0<EUITourTooltip>... steps) {
        this.steps.addAll(Arrays.asList(steps));
        return this;
    }

    public void addToHand(AbstractCard... cards) {
        AbstractDungeon.player.hand.group.addAll(Arrays.asList(cards));
    }

    public void clearLists() {
        AbstractDungeon.player.drawPile.group = new ArrayList<>();
        AbstractDungeon.player.hand.group = new ArrayList<>();
        AbstractDungeon.player.discardPile.group = new ArrayList<>();
        AbstractDungeon.player.limbo.group = new ArrayList<>();
    }

    public void damage(DamageInfo info) {
        super.damage(info);
        if (this.currentHealth <= 0 && !this.halfDead) {
            this.halfDead = true;
            this.powers.clear();
        }
    }

    public void die() {
        if (!AbstractDungeon.getCurrRoom().cannotLose) {
            super.die();
        }
    }

    public void finishTutorial() {
        AbstractDungeon.getCurrRoom().cannotLose = false;
        this.halfDead = false;
        EUITourTooltip.clearTutorialQueue();
        super.die();
        if (currentConfig != null) {
            currentConfig.set(true);
        }
    }

    @Override
    protected void getMove(int i) {
        this.setMove("", (byte) -1, Intent.NONE);
    }

    @Override
    public void usePreBattleAction() {
        AbstractDungeon.getCurrRoom().cannotLose = true;
        performActions(false);
    }

    public void onComplete() {
        PCLSFX.play(PCLSFX.TINGSHA);
        PCLEffects.Queue.particle(PCLCoreImages.Menu.check.texture(), Settings.WIDTH * 0.75f, Settings.HEIGHT * 0.6f)
                .setTargetPosition(Settings.WIDTH * 0.75f, Settings.HEIGHT * 0.68f)
                .setDuration(1.8f, true);
        if (current < steps.size() - 1) {
            EUITourTooltip.queueTutorial(new EUITourTooltip(this.hb,
                    PGR.core.strings.tutorial_tutorialStepHeader, PGR.core.strings.tutorial_tutorialNextStep).setCanDismiss(true));
        }
        else {
            EUITourTooltip.queueTutorial(new EUITourTooltip(this.hb,
                    PGR.core.strings.tutorial_tutorialCompleteHeader, PGR.core.strings.tutorial_tutorialComplete).setCanDismiss(true));
        }
    }

    @Override
    public void performActions(boolean manual) {
        if (waitingTip != null && !EUITourTooltip.hasTutorial(waitingTip)) {
            waitingTip = null;
            current += 1;
        }
        if (current < steps.size()) {
            if (currentHealth <= 0 || halfDead) {
                PCLActions.bottom.add(new HealAction(this, this, this.maxHealth));
                this.halfDead = false;
            }
            EUITourTooltip.clearTutorialQueue();
            waitingTip = steps.get(current).invoke();
            waitingTip.setWaitOnProvider(this)
                    .setDescription(waitingTip.description + EUIUtils.DOUBLE_SPLIT_LINE + PCLCoreStrings.colorString("p", PGR.core.strings.tutorial_tutorialReplay));
            EUITourTooltip.queueTutorial(waitingTip);
        }
        else {
            finishTutorial();
        }
    }

    public void replaceHandWith(AbstractCard... cards) {
        clearLists();
        addToHand(cards);
    }

    public void talk(String message) {
        PCLEffects.Queue.talk(this, message);
    }

    public static class TutorialInfo {
        public final PCLCreatureData data;
        public final STSConfigItem<Boolean> config;
        public final FuncT1<Boolean, AbstractPlayer> shouldShow;

        public TutorialInfo(PCLCreatureData data, STSConfigItem<Boolean> config, FuncT1<Boolean, AbstractPlayer> shouldShow) {
            this.data = data;
            this.config = config;
            this.shouldShow = shouldShow;
        }
    }
}
