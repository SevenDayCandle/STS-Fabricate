package pinacolada.ui.menu;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.helpers.FontHelper;
import extendedui.EUI;
import extendedui.EUIUtils;
import extendedui.ui.controls.EUITutorial;
import extendedui.ui.controls.EUITutorialImagePage;
import extendedui.ui.panelitems.PCLTopPanelItem;
import extendedui.ui.tooltips.EUITooltip;
import extendedui.utilities.EUIColors;
import extendedui.utilities.EUIFontHelper;
import pinacolada.augments.PCLAugment;
import pinacolada.cards.base.PCLCard;
import pinacolada.resources.PCLHotkeys;
import pinacolada.resources.PGR;
import pinacolada.resources.pcl.PCLCoreImages;
import pinacolada.ui.cardView.PCLAugmentList;

import java.util.ArrayList;

import static pinacolada.dungeon.PCLPlayerMeter.makeTitle;

public class PCLAugmentPanelItem extends PCLTopPanelItem {
    public static final String ID = createFullID(PCLAugmentPanelItem.class);
    protected Color currentColor = Color.WHITE.cpy();
    protected float lerpAmount = 1;

    public PCLAugmentPanelItem() {
        super(PCLCoreImages.Menu.augmentPanel, ID);
        this.setTooltip(new EUITooltip(PGR.core.strings.misc_viewAugments, EUIUtils.format(PGR.core.strings.misc_viewAugmentsDescription, "")));
    }

    public void flash() {
        currentColor.set(Settings.GREEN_TEXT_COLOR);
        lerpAmount = 0;
    }

    private ArrayList<PCLAugment> getAugments() {
        ArrayList<PCLAugment> augs = EUIUtils.map(PGR.dungeon.augmentList, PCLAugment.SaveData::create);
        for (AbstractCard c : AbstractDungeon.player.masterDeck.group) {
            if (c instanceof PCLCard) {
                for (PCLAugment augment : ((PCLCard) c).augments) {
                    if (augment != null && augment.canRemove()) {
                        augs.add(augment);
                    }
                }
            }
        }
        return augs;
    }

    protected void onClick() {
        super.onClick();
        PGR.augmentScreen.openScreen(this::getAugments, PCLAugmentList.DEFAULT, true);
    }

    protected void onRightClick() {
        super.onRightClick();
        this.getHitbox().unhover();
        EUITutorial tutorial = new EUITutorial(
                new EUITutorialImagePage(makeTitle(PGR.core.strings.misc_fabricate, PGR.core.strings.misc_viewAugments, 1), PGR.core.strings.tutorial_augmentTutorial1, PCLCoreImages.Tutorial.augTut01.texture()),
                new EUITutorialImagePage(makeTitle(PGR.core.strings.misc_fabricate, PGR.core.strings.misc_viewAugments, 2), PGR.core.strings.tutorial_augmentTutorial2, PCLCoreImages.Tutorial.augTut02.texture()),
                new EUITutorialImagePage(makeTitle(PGR.core.strings.misc_fabricate, PGR.core.strings.misc_viewAugments, 3), PGR.core.strings.tutorial_augmentTutorial3, PCLCoreImages.Tutorial.augTut03.texture()),
                new EUITutorialImagePage(makeTitle(PGR.core.strings.misc_fabricate, PGR.core.strings.misc_viewAugments, 4), PGR.core.strings.tutorial_augmentTutorial4, PCLCoreImages.Tutorial.augTut03.texture()),
                new EUITutorialImagePage(makeTitle(PGR.core.strings.misc_fabricate, PGR.core.strings.misc_viewAugments, 5), PGR.core.strings.tutorial_augmentTutorial5, PCLCoreImages.Tutorial.augTut04.texture()),
                new EUITutorialImagePage(makeTitle(PGR.core.strings.misc_fabricate, PGR.core.strings.misc_viewAugments, 6), PGR.core.strings.tutorial_augmentTutorial6, PCLCoreImages.Tutorial.augTut05.texture())
        );
        EUI.ftueScreen.openScreen(tutorial);
    }

    @Override
    public void render(SpriteBatch sb) {
        super.render(sb);
        FontHelper.renderFontCentered(sb, EUIFontHelper.cardTitleFontNormal, String.valueOf(PGR.dungeon.getAugmentTotal()), this.x + (this.hb_w * 0.75f), this.y + 16f * Settings.scale, currentColor);
    }

    public void update() {
        super.update();
        if (this.tooltip != null && this.getHitbox().hovered) {
            this.tooltip.setText(
                    PGR.core.strings.misc_viewAugments + " (" + PCLHotkeys.viewAugmentScreen.getKeyString() + ")",
                    EUIUtils.joinStrings(EUIUtils.DOUBLE_SPLIT_LINE, PGR.core.strings.misc_viewAugmentsDescription, PGR.core.strings.misc_rightClickLearnMore)
            );
            EUITooltip.queueTooltip(this.tooltip);
        }
        if (PCLHotkeys.viewAugmentScreen.isJustPressed() && AbstractDungeon.screen != PCLAugmentCollectionScreen.AUGMENT_SCREEN) {
            onClick();
        }
        if (lerpAmount < 1) {
            EUIColors.lerp(currentColor, Settings.GREEN_TEXT_COLOR, Color.WHITE, lerpAmount);
            lerpAmount += Gdx.graphics.getDeltaTime();
        }
    }
}
