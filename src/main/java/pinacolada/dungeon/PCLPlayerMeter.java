package pinacolada.dungeon;

import basemod.abstracts.AbstractCardModifier;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.orbs.AbstractOrb;
import extendedui.EUI;
import extendedui.EUIRM;
import extendedui.EUIUtils;
import extendedui.configuration.STSConfigItem;
import extendedui.ui.controls.EUIButton;
import extendedui.ui.controls.EUITutorial;
import extendedui.ui.controls.EUITutorialImagePage;
import extendedui.ui.controls.EUITutorialPage;
import extendedui.ui.hitboxes.DraggableHitbox;
import extendedui.ui.hitboxes.RelativeHitbox;
import extendedui.utilities.EUIColors;
import pinacolada.cards.base.PCLCard;
import pinacolada.resources.PCLEnum;
import pinacolada.resources.PCLResources;
import pinacolada.resources.PGR;
import pinacolada.resources.pcl.PCLCoreImages;
import pinacolada.ui.EUICardDraggable;

public abstract class PCLPlayerMeter extends EUICardDraggable<PCLCard> {
    public final String ID;
    public final PCLResources<?, ?, ?, ?> resources;
    protected EUIButton infoIcon;
    protected PCLUseInfo info;
    protected int currentScore;
    protected int highestScore;

    public PCLPlayerMeter(String id, STSConfigItem<Vector2> config, PCLResources<?, ?, ?, ?> resources, float iconSize) {
        super(config, new DraggableHitbox(screenW(0.0366f), screenH(0.425f), iconSize, iconSize, true), iconSize);
        this.ID = id;
        this.resources = resources;
        infoIcon = new EUIButton(EUIRM.images.info.texture(), new RelativeHitbox(hb, scale(40f), scale(40f), scale(25), scale(-40f)))
                .setTooltip(getInfoTitle(), getInfoMainDescrption() + EUIUtils.DOUBLE_SPLIT_LINE + PGR.core.strings.tutorial_learnMore)
                .setOnClick(() -> EUI.ftueScreen.openScreen(new EUITutorial(getInfoPages())))
                .setColor(EUIColors.white(0.5f));
    }

    public static String createFullID(PCLResources<?, ?, ?, ?> resources, Class<? extends PCLPlayerMeter> type) {
        return resources.createID(type.getSimpleName());
    }

    public static EUITutorialPage[] getAugmentTutorialPages() {
        return EUIUtils.array(
                new EUITutorialImagePage(makeTitle(PGR.core.strings.misc_fabricate, PGR.core.strings.misc_viewAugments, 1), PGR.core.strings.tutorial_augmentTutorial1, PCLCoreImages.Tutorial.augTut01.texture()),
                new EUITutorialImagePage(makeTitle(PGR.core.strings.misc_fabricate, PGR.core.strings.misc_viewAugments, 2), PGR.core.strings.tutorial_augmentTutorial2, PCLCoreImages.Tutorial.augTut02.texture()),
                new EUITutorialImagePage(makeTitle(PGR.core.strings.misc_fabricate, PGR.core.strings.misc_viewAugments, 3), PGR.core.strings.tutorial_augmentTutorial3, PCLCoreImages.Tutorial.augTut03.texture()),
                new EUITutorialImagePage(makeTitle(PGR.core.strings.misc_fabricate, PGR.core.strings.misc_viewAugments, 4), PGR.core.strings.tutorial_augmentTutorial4, PCLCoreImages.Tutorial.augTut03.texture()),
                new EUITutorialImagePage(makeTitle(PGR.core.strings.misc_fabricate, PGR.core.strings.misc_viewAugments, 5), PGR.core.strings.tutorial_augmentTutorial5, PCLCoreImages.Tutorial.augTut04.texture()),
                new EUITutorialImagePage(makeTitle(PGR.core.strings.misc_fabricate, PGR.core.strings.misc_viewAugments, 6), PGR.core.strings.tutorial_augmentTutorial6, PCLCoreImages.Tutorial.augTut05.texture())
        );
    }

    public static EUITutorialPage[] getSummonTutorialPages() {
        return EUIUtils.array(
                new EUITutorialImagePage(makeTitle(PGR.core.strings.misc_fabricate, PGR.core.tooltips.summon.title, 1), PGR.core.strings.tutorial_summonTutorial1, PCLCoreImages.Tutorial.sumTut01.texture()),
                new EUITutorialImagePage(makeTitle(PGR.core.strings.misc_fabricate, PGR.core.tooltips.summon.title, 2), PGR.core.strings.tutorial_summonTutorial2, PCLCoreImages.Tutorial.sumTut02.texture()),
                new EUITutorialImagePage(makeTitle(PGR.core.strings.misc_fabricate, PGR.core.tooltips.summon.title, 3), PGR.core.strings.tutorial_summonTutorial3, PCLCoreImages.Tutorial.sumTut03.texture()),
                new EUITutorialImagePage(makeTitle(PGR.core.strings.misc_fabricate, PGR.core.tooltips.summon.title, 4), PGR.core.strings.tutorial_summonTutorial4, PCLCoreImages.Tutorial.sumTut03.texture()),
                new EUITutorialImagePage(makeTitle(PGR.core.strings.misc_fabricate, PGR.core.tooltips.summon.title, 5), PGR.core.strings.tutorial_summonTutorial5, PCLCoreImages.Tutorial.sumTut03.texture()),
                new EUITutorialImagePage(makeTitle(PGR.core.strings.misc_fabricate, PGR.core.tooltips.summon.title, 6), PGR.core.strings.tutorial_summonTutorial6, PCLCoreImages.Tutorial.sumTut04.texture()),
                new EUITutorialImagePage(makeTitle(PGR.core.strings.misc_fabricate, PGR.core.tooltips.summon.title, 7), PGR.core.strings.tutorial_summonTutorial7, PCLCoreImages.Tutorial.sumTut05.texture()),
                new EUITutorialImagePage(makeTitle(PGR.core.strings.misc_fabricate, PGR.core.tooltips.summon.title, 8), PGR.core.strings.tutorial_summonTutorial8, PCLCoreImages.Tutorial.sumTut06.texture())
        );
    }

    public static boolean isSwapIntended(PCLCard incoming, PCLCard other) {
        return incoming.type == PCLEnum.CardType.SUMMON && other != null && other != incoming;
    }

    public static String makeTitle(String category, String addendum) {
        return category + ": " + addendum;
    }

    public static String makeTitle(String category, String addendum, int index) {
        return category + ": " + EUIRM.strings.generic2(addendum, index);
    }

    public boolean canGlow(AbstractCard c) {
        return false;
    }

    public int getCurrentScore() {
        return currentScore;
    }

    public int getHighestScore() {
        return highestScore;
    }

    public void increaseScore(int amount) {
        currentScore += amount;
        highestScore = Math.max(highestScore, currentScore);
    }

    public void initialize() {
        super.initialize();
        currentScore = 0;
        highestScore = 0;
    }

    public boolean isHovered() {
        return super.isHovered() || infoIcon.hb.hovered;
    }

    public float modifyBlock(float block, PCLUseInfo info, PCLCard source, PCLCard card) {
        return block;
    }

    public float modifyDamage(float damage, PCLUseInfo info, PCLCard source, PCLCard card) {
        return damage;
    }

    public float modifyOrbOutput(float initial, PCLUseInfo info, AbstractOrb orb) {
        return initial;
    }

    public void onCardCreated(AbstractCard card, boolean startOfBattle) {

    }

    public AbstractCardModifier onCardModified(AbstractCard card, AbstractCardModifier modifier) {
        return modifier;
    }

    public void onCardPlayed(PCLCard card, PCLUseInfo info, boolean fromSummon) {
    }

    public void onEndOfTurn() {
    }

    public void onStartOfTurn() {
    }

    @Override
    public void renderImpl(SpriteBatch sb) {
        super.renderImpl(sb);
        infoIcon.renderImpl(sb);
    }

    public int size() {
        return 0;
    }

    @Override
    public void updateImpl(PCLCard card, PCLCard originalCard, AbstractCreature target, AbstractCreature originalTarget, boolean draggingCard, boolean shouldUpdateForCard, boolean shouldUpdateForTarget) {
        infoIcon.setColor(EUIColors.white(isHovered() ? 1f : 0.5f));
        infoIcon.tryUpdate();
    }

    public abstract String getInfoMainDescrption();

    public abstract EUITutorialPage[] getInfoPages();

    public abstract String getInfoTitle();

    public abstract void setupInfo(PCLUseInfo newInfo);
}
