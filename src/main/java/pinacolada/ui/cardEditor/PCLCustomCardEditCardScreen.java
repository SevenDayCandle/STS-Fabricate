package pinacolada.ui.cardEditor;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.MathUtils;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.screens.SingleCardViewPopup;
import com.megacrit.cardcrawl.screens.select.GridCardSelectScreen;
import extendedui.EUIRM;
import extendedui.EUIUtils;
import extendedui.interfaces.delegates.ActionT0;
import extendedui.interfaces.delegates.ActionT1;
import extendedui.ui.controls.EUIButton;
import extendedui.ui.controls.EUIToggle;
import extendedui.ui.hitboxes.EUIHitbox;
import extendedui.ui.tooltips.EUITooltip;
import extendedui.utilities.ColoredTexture;
import extendedui.utilities.EUIFontHelper;
import pinacolada.cards.base.PCLCustomCardSlot;
import pinacolada.cards.base.PCLDynamicCard;
import pinacolada.cards.base.PCLDynamicData;
import pinacolada.effects.PCLEffectWithCallback;
import pinacolada.effects.screen.PCLCustomCardImageEffect;
import pinacolada.resources.PGR;
import pinacolada.skills.PSkill;
import pinacolada.skills.skills.PTrigger;
import pinacolada.skills.skills.base.primary.PTrigger_When;
import pinacolada.skills.skills.special.primary.PCardPrimary_DealDamage;
import pinacolada.skills.skills.special.primary.PCardPrimary_GainBlock;

import java.util.ArrayList;

import static extendedui.ui.AbstractScreen.createHexagonalButton;
import static pinacolada.ui.cardEditor.PCLCustomCardEffectEditor.invalidateCards;
import static pinacolada.ui.cardEditor.PCLCustomCardEffectPage.MENU_HEIGHT;
import static pinacolada.ui.cardEditor.PCLCustomCardEffectPage.MENU_WIDTH;

public class PCLCustomCardEditCardScreen extends PCLEffectWithCallback<Object> {
    public static final int EFFECT_COUNT = 2;
    protected static final float BUTTON_HEIGHT = Settings.HEIGHT * (0.055f);
    protected static final float CARD_X = Settings.WIDTH * 0.11f;
    protected static final float CARD_Y = Settings.HEIGHT * 0.76f;
    protected static final float START_X = Settings.WIDTH * (0.24f);
    protected static final float START_Y = Settings.HEIGHT * (0.93f);
    protected final PCLCustomCardSlot currentSlot;
    protected final boolean fromInGame;
    public ArrayList<PCLDynamicData> prevBuilders;
    public ArrayList<PCLDynamicData> tempBuilders;
    public int currentBuilder;
    protected ActionT0 onSave;
    protected ArrayList<PSkill<?>> currentEffects = new ArrayList<>();
    protected ArrayList<PTrigger> currentPowers = new ArrayList<>();
    protected ArrayList<EUIButton> pageButtons = new ArrayList<>();
    protected ArrayList<PCLCustomCardEffectPage> effectPages = new ArrayList<>();
    protected ArrayList<PCLCustomCardPowerPage> powerPages = new ArrayList<>();
    protected ArrayList<PCLCustomCardEditorPage> pages = new ArrayList<>();
    protected EUIButton cancelButton;
    protected EUIButton imageButton;
    protected EUIButton saveButton;
    protected EUIButton undoButton;
    protected EUIToggle upgradeToggle;
    protected PCardPrimary_DealDamage currentDamage;
    protected PCardPrimary_GainBlock currentBlock;
    protected PCLDynamicCard previewCard;
    protected PCLCustomCardFormEditor formEditor;
    protected PCLCustomCardImageEffect imageEditor;
    protected Texture loadedImage;
    protected int currentPage;

    public PCLCustomCardEditCardScreen(PCLCustomCardSlot slot) {
        this(slot, false);
    }

    public PCLCustomCardEditCardScreen(PCLCustomCardSlot slot, boolean fromInGame) {
        final float labelHeight = Settings.HEIGHT * (0.04f);
        final float buttonWidth = Settings.WIDTH * (0.16f);
        final float labelWidth = Settings.WIDTH * (0.20f);
        final float button_cY = BUTTON_HEIGHT * 1.5f;
        this.currentSlot = slot;
        this.fromInGame = fromInGame;
        tempBuilders = EUIUtils.map(currentSlot.builders, PCLDynamicData::new);

        cancelButton = createHexagonalButton(0, 0, buttonWidth, BUTTON_HEIGHT)
                .setPosition(buttonWidth * 0.6f, button_cY)
                .setColor(Color.FIREBRICK)
                .setText(GridCardSelectScreen.TEXT[1])
                .setFont(EUIFontHelper.buttonFont, 0.85f)
                .setOnClick(this::end);

        saveButton = createHexagonalButton(0, 0, buttonWidth, BUTTON_HEIGHT)
                .setPosition(cancelButton.hb.cX, cancelButton.hb.y + cancelButton.hb.height + labelHeight * 0.8f)
                .setColor(Color.FOREST)
                .setText(GridCardSelectScreen.TEXT[0])
                .setFont(EUIFontHelper.buttonFont, 0.85f)
                .setOnClick(this::save);

        undoButton = createHexagonalButton(0, 0, buttonWidth, BUTTON_HEIGHT)
                .setPosition(cancelButton.hb.cX, saveButton.hb.y + saveButton.hb.height + labelHeight * 0.8f)
                .setColor(Color.WHITE)
                .setText(PGR.core.strings.cedit_undo)
                .setFont(EUIFontHelper.buttonFont, 0.85f)
                .setOnClick(this::undo);

        imageButton = createHexagonalButton(0, 0, buttonWidth, BUTTON_HEIGHT)
                .setPosition(cancelButton.hb.cX, undoButton.hb.y + undoButton.hb.height + labelHeight * 0.8f)
                .setColor(Color.WHITE)
                .setText(PGR.core.strings.cedit_loadImage)
                .setTooltip(PGR.core.strings.cedit_loadImage, PGR.core.strings.cetut_primaryImage)
                .setFont(EUIFontHelper.buttonFont, 0.85f)
                .setOnClick(this::editImage);

        formEditor = new PCLCustomCardFormEditor(
                new EUIHitbox(0, 0, Settings.scale * 256f, Settings.scale * 48f)
                        .setCenter(Settings.WIDTH * 0.116f, imageButton.hb.y + imageButton.hb.height + labelHeight * 3.2f), this);

        upgradeToggle = new EUIToggle(new EUIHitbox(Settings.scale * 256f, Settings.scale * 48f))
                .setPosition(Settings.WIDTH * 0.116f, CARD_Y - labelHeight - AbstractCard.IMG_HEIGHT / 2f)
                .setBackground(EUIRM.images.panel.texture(), Color.DARK_GRAY)
                .setFont(EUIFontHelper.carddescriptionfontLarge, 0.5f)
                .setText(SingleCardViewPopup.TEXT[6])
                .setToggle(SingleCardViewPopup.isViewingUpgrade)
                .setOnToggle(this::toggleViewUpgrades);

        upgradeToggle.setActive(slot.maxUpgradeLevel != 0);

        invalidateCards();
        setupPages();
    }

    public void addBuilder() {
        tempBuilders.add(new PCLDynamicData(getBuilder()));
        setCurrentBuilder(tempBuilders.size() - 1);
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

    protected void end() {
        complete(null);
    }

    public PCLDynamicData getBuilder() {
        return tempBuilders.get(currentBuilder);
    }

    public int getPowerCount() {
        return powerPages.size();
    }

    public void modifyAllBuilders(ActionT1<PCLDynamicData> updateFunc) {
        prevBuilders = EUIUtils.map(tempBuilders, PCLDynamicData::new);
        for (PCLDynamicData b : tempBuilders) {
            updateFunc.invoke(b);
        }
        rebuildCard();
    }

    public void modifyBuilder(ActionT1<PCLDynamicData> updateFunc) {
        prevBuilders = EUIUtils.map(tempBuilders, PCLDynamicData::new);
        updateFunc.invoke(getBuilder());
        rebuildCard();
    }

    public void openPageAtIndex(int index) {
        currentPage = index;
        for (int j = 0; j < pageButtons.size(); j++) {
            pageButtons.get(j).setColor(j == index ? Color.WHITE : Color.GRAY);
        }
    }

    protected void rebuildCard() {
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

    public void refreshPages() {
        for (PCLCustomCardEditorPage b : pages) {
            b.refresh();
        }
    }

    public void removeBuilder() {
        tempBuilders.remove(getBuilder());
        setCurrentBuilder(currentBuilder);
    }

    @Override
    public void render(SpriteBatch sb) {
        if (imageEditor != null) {
            imageEditor.render(sb);
        }
        else {
            cancelButton.tryRender(sb);
            saveButton.tryRender(sb);
            undoButton.tryRender(sb);
            imageButton.tryRender(sb);
            pages.get(currentPage).tryRender(sb);
            formEditor.tryRender(sb);
            upgradeToggle.tryRender(sb);
            for (EUIButton b : pageButtons) {
                b.tryRender(sb);
            }
            previewCard.render(sb);
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
            cancelButton.tryUpdate();
            saveButton.tryUpdate();
            undoButton.tryUpdate();
            imageButton.tryUpdate();
            pages.get(currentPage).tryUpdate();
            formEditor.tryUpdate();
            upgradeToggle.tryUpdate();
            for (EUIButton b : pageButtons) {
                b.tryUpdate();
            }
            previewCard.update();
            previewCard.hb.update();
            if (previewCard.hb.hovered) {
                EUITooltip.queueTooltips(previewCard);
            }
        }
    }

    protected void complete() {
        super.complete();
        invalidateCards();
        if (loadedImage != null) {
            loadedImage.dispose();
        }
    }

    protected void save() {
        currentSlot.builders = tempBuilders;
        if (this.onSave != null) {
            this.onSave.invoke();
        }
        end();
    }

    public void setCurrentBuilder(int index) {
        currentBuilder = MathUtils.clamp(index, 0, tempBuilders.size() - 1);
        modifyBuilder(__ -> {
        });
        setupPages();
        formEditor.refresh();
    }

    public void setLoadedImage(Texture texture) {
        loadedImage = texture;
        modifyAllBuilders(e -> e
                .setImagePath(currentSlot.getImagePath())
                .setImage(new ColoredTexture(loadedImage)));
    }

    public PCLCustomCardEditCardScreen setOnSave(ActionT0 onSave) {
        this.onSave = onSave;

        return this;
    }

    protected void setupPages() {
        currentDamage = getBuilder().attackSkill;
        currentBlock = getBuilder().blockSkill;
        currentEffects.clear();
        currentEffects.addAll(getBuilder().moves);
        while (currentEffects.size() < EFFECT_COUNT) {
            currentEffects.add(null);
        }
        currentPowers.clear();
        currentPowers.addAll(getBuilder().powers);
        while (currentPowers.size() < EFFECT_COUNT) {
            currentPowers.add(null);
        }

        pages.clear();
        effectPages.clear();
        powerPages.clear();
        pageButtons.clear();
        if (!fromInGame) {
            pages.add(new PCLCustomCardPrimaryInfoPage(this));
        }
        pages.add(new PCLCustomCardAttributesPage(this));
        pages.add(new PCLCustomCardAttackPage(this, new EUIHitbox(START_X, START_Y, MENU_WIDTH, MENU_HEIGHT), 0, PGR.core.strings.cedit_damage, be -> {
            currentDamage = EUIUtils.safeCast(be, PCardPrimary_DealDamage.class);
            modifyBuilder(e -> e.setAttackSkill(currentDamage));
        }));
        pages.add(new PCLCustomCardBlockPage(this, new EUIHitbox(START_X, START_Y, MENU_WIDTH, MENU_HEIGHT), 0, PGR.core.strings.cedit_block, be -> {
            currentBlock = EUIUtils.safeCast(be, PCardPrimary_GainBlock.class);
            modifyBuilder(e -> e.setBlockSkill(currentBlock));
        }));
        for (int i = 0; i < currentEffects.size(); i++) {
            int finalI = i;
            PCLCustomCardEffectPage page = new PCLCustomCardEffectPage(this, new EUIHitbox(START_X, START_Y, MENU_WIDTH, MENU_HEIGHT), i
                    , EUIUtils.format(PGR.core.strings.cedit_effectX, i + 1), (be) -> {
                currentEffects.set(finalI, be);
                modifyBuilder(e -> e.setPSkill(currentEffects, true, true));
            });
            pages.add(page);
            effectPages.add(page);
            page.refresh();
        }
        for (int i = 0; i < currentPowers.size(); i++) {
            int finalI = i;
            PCLCustomCardPowerPage page = new PCLCustomCardPowerPage(this, new EUIHitbox(START_X, START_Y, MENU_WIDTH, MENU_HEIGHT), i
                    , EUIUtils.format(PGR.core.strings.cedit_powerX, i + 1), (be) -> {
                if (be instanceof PTrigger) {
                    currentPowers.set(finalI, (PTrigger) be);
                }
                else {
                    currentPowers.set(finalI, new PTrigger_When());
                }
                modifyBuilder(e -> e.setPPower(currentPowers, true, true));
            });
            pages.add(page);
            powerPages.add(page);
            page.refresh();
        }

        for (int i = 0; i < pages.size(); i++) {
            PCLCustomCardEditorPage pg = pages.get(i);
            String title = pg.getTitle();
            pageButtons.add(new EUIButton(pg.getTextureCache().texture(), new EUIHitbox(0, 0, BUTTON_HEIGHT, BUTTON_HEIGHT))
                    .setPosition(Settings.WIDTH * (0.45f) + ((i - 1f) * BUTTON_HEIGHT), (BUTTON_HEIGHT * 0.85f))
                    .setColor(i == 0 ? Color.WHITE : Color.GRAY)
                    .setOnClick(i, this::openPageAtIndex)
                    .setTooltip(title, pg instanceof PCLCustomCardPrimaryInfoPage ? PGR.core.strings.cedit_primaryInfoDesc : ""));
        }

        modifyBuilder(__ -> {
        });
    }

    private void toggleViewUpgrades(boolean value) {
        SingleCardViewPopup.isViewingUpgrade = !SingleCardViewPopup.isViewingUpgrade;
        modifyBuilder(__ -> {
        });
    }

    protected void undo() {
        if (prevBuilders != null) {
            ArrayList<PCLDynamicData> backups = EUIUtils.map(prevBuilders, PCLDynamicData::new);
            currentBuilder = MathUtils.clamp(currentBuilder, 0, backups.size() - 1);
            formEditor.refresh();
            modifyBuilder(__ -> {
                tempBuilders = backups;
            });
            refreshPages();
        }
    }

}
