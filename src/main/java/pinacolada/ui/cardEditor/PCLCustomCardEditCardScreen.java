package pinacolada.ui.cardEditor;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.screens.SingleCardViewPopup;
import extendedui.EUIRM;
import extendedui.EUIUtils;
import extendedui.ui.controls.EUIButton;
import extendedui.ui.controls.EUIToggle;
import extendedui.ui.hitboxes.EUIHitbox;
import extendedui.ui.tooltips.EUITooltip;
import extendedui.utilities.ColoredTexture;
import extendedui.utilities.EUIFontHelper;
import pinacolada.cards.base.PCLCustomCardSlot;
import pinacolada.cards.base.PCLDynamicCard;
import pinacolada.cards.base.PCLDynamicData;
import pinacolada.effects.screen.PCLCustomCardImageEffect;
import pinacolada.resources.PGR;
import pinacolada.skills.skills.special.primary.PCardPrimary_DealDamage;
import pinacolada.skills.skills.special.primary.PCardPrimary_GainBlock;

import static extendedui.ui.AbstractScreen.createHexagonalButton;
import static pinacolada.ui.cardEditor.PCLCustomEffectEditingPane.invalidateCards;
import static pinacolada.ui.cardEditor.PCLCustomEffectPage.MENU_HEIGHT;
import static pinacolada.ui.cardEditor.PCLCustomEffectPage.MENU_WIDTH;

public class PCLCustomCardEditCardScreen extends PCLCustomEditEntityScreen<PCLCustomCardSlot, PCLDynamicData> {

    protected EUIToggle upgradeToggle;
    protected PCardPrimary_DealDamage currentDamage;
    protected PCardPrimary_GainBlock currentBlock;
    protected PCLDynamicCard previewCard;
    protected PCLCustomCardFormEditor formEditor;
    protected PCLCustomCardImageEffect imageEditor;
    protected EUIButton imageButton;
    protected Texture loadedImage;

    public PCLCustomCardEditCardScreen(PCLCustomCardSlot slot) {
        this(slot, false);
    }

    public PCLCustomCardEditCardScreen(PCLCustomCardSlot slot, boolean fromInGame) {
        super(slot);
    }

    public void preInitialize(PCLCustomCardSlot slot)
    {
        super.preInitialize(slot);
        imageButton = createHexagonalButton(0, 0, BUTTON_WIDTH, BUTTON_HEIGHT)
                .setPosition(cancelButton.hb.cX, undoButton.hb.y + undoButton.hb.height + LABEL_HEIGHT * 0.8f)
                .setColor(Color.WHITE)
                .setText(PGR.core.strings.cedit_loadImage)
                .setTooltip(PGR.core.strings.cedit_loadImage, PGR.core.strings.cetut_primaryImage)
                .setFont(EUIFontHelper.buttonFont, 0.85f)
                .setOnClick(this::editImage);

        formEditor = new PCLCustomCardFormEditor(
                new EUIHitbox(0, 0, Settings.scale * 256f, Settings.scale * 48f)
                        .setCenter(Settings.WIDTH * 0.116f, imageButton.hb.y + imageButton.hb.height + LABEL_HEIGHT * 3.2f), this);

        upgradeToggle = new EUIToggle(new EUIHitbox(Settings.scale * 256f, Settings.scale * 48f))
                .setPosition(Settings.WIDTH * 0.116f, CARD_Y - LABEL_HEIGHT - AbstractCard.IMG_HEIGHT / 2f)
                .setBackground(EUIRM.images.panel.texture(), Color.DARK_GRAY)
                .setFont(EUIFontHelper.carddescriptionfontLarge, 0.5f)
                .setText(SingleCardViewPopup.TEXT[6])
                .setToggle(SingleCardViewPopup.isViewingUpgrade)
                .setOnToggle(this::toggleViewUpgrades);

        upgradeToggle.setActive(slot.maxUpgradeLevel != 0);
        invalidateCards();
    }

    protected void editImage() {
        imageEditor = (PCLCustomCardImageEffect) new PCLCustomCardImageEffect(getBuilder())
                .addCallback(pixmap -> {
                            if (pixmap != null) {
                                setLoadedImage(new Texture(pixmap));
                            }
                        }
                );
    }

    protected void rebuildItem() {
        previewCard = getBuilder().createImplWithForms(false);
        if (SingleCardViewPopup.isViewingUpgrade) {
            //previewCard.upgrade();
            previewCard.displayUpgrades();
        }
        else {
            previewCard.displayUpgradesForSkills(false);
        }

        previewCard.drawScale = previewCard.targetDrawScale = 1f;
        previewCard.current_x = previewCard.target_x = CARD_X;
        previewCard.current_y = previewCard.target_y = CARD_Y;
    }

    public void updateInnerElements()
    {
        super.updateInnerElements();
        imageButton.tryUpdate();
        formEditor.tryUpdate();
        upgradeToggle.tryUpdate();
        previewCard.update();
        previewCard.hb.update();
        if (previewCard.hb.hovered) {
            EUITooltip.queueTooltips(previewCard);
        }
    }

    public void renderInnerElements(SpriteBatch sb)
    {
        super.renderInnerElements(sb);
        imageButton.tryRender(sb);
        formEditor.tryRender(sb);
        upgradeToggle.tryRender(sb);
        previewCard.render(sb);
    }

    @Override
    public void render(SpriteBatch sb) {
        if (imageEditor != null) {
            imageEditor.render(sb);
        }
        else {
            super.render(sb);
        }
    }

    @Override
    protected void updateInternal(float deltaTime) {
        if (imageEditor != null) {
            imageEditor.update();
            if (imageEditor.isDone) {
                imageEditor = null;
            }
        }
        else {
            super.updateInternal(deltaTime);
        }
    }

    protected void complete() {
        super.complete();
        invalidateCards();
        if (loadedImage != null) {
            loadedImage.dispose();
        }
    }

    public void setLoadedImage(Texture texture) {
        loadedImage = texture;
        modifyAllBuilders(e -> e
                .setImagePath(currentSlot.getImagePath())
                .setImage(new ColoredTexture(loadedImage)));
    }

    protected void clearPages() {
        super.clearPages();

        currentDamage = getBuilder().attackSkill;
        currentBlock = getBuilder().blockSkill;
    }

    protected void addSkillPages()
    {
        if (!fromInGame) {
            pages.add(new PCLCustomCardPrimaryInfoPage(this));
        }
        pages.add(new PCLCustomCardAttributesPage(this));
        pages.add(new PCLCustomAttackEffectPage(this, new EUIHitbox(START_X, START_Y, MENU_WIDTH, MENU_HEIGHT), 0, PGR.core.strings.cedit_damage, be -> {
            currentDamage = EUIUtils.safeCast(be, PCardPrimary_DealDamage.class);
            modifyBuilder(e -> e.setAttackSkill(currentDamage));
        }));
        pages.add(new PCLCustomBlockEffectPage(this, new EUIHitbox(START_X, START_Y, MENU_WIDTH, MENU_HEIGHT), 0, PGR.core.strings.cedit_block, be -> {
            currentBlock = EUIUtils.safeCast(be, PCardPrimary_GainBlock.class);
            modifyBuilder(e -> e.setBlockSkill(currentBlock));
        }));
        super.addSkillPages();
    }

    protected EUITooltip getPageTooltip(PCLCustomGenericPage page)
    {
        return new EUITooltip(page.getTitle(), page instanceof PCLCustomCardPrimaryInfoPage ? PGR.core.strings.cedit_primaryInfoDesc : "");
    }

    private void toggleViewUpgrades(boolean value) {
        SingleCardViewPopup.isViewingUpgrade = !SingleCardViewPopup.isViewingUpgrade;
        modifyBuilder(__ -> {
        });
    }

    protected void updateVariant() {
        formEditor.refresh();
    }

}
