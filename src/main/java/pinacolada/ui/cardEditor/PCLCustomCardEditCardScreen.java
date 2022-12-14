package pinacolada.ui.cardEditor;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.PixmapIO;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.MathUtils;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.helpers.FontHelper;
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
import extendedui.utilities.EUIFontHelper;
import pinacolada.cards.base.PCLCardBuilder;
import pinacolada.cards.base.PCLCustomCardSlot;
import pinacolada.cards.base.PCLDynamicCard;
import pinacolada.effects.PCLEffectWithCallback;
import pinacolada.resources.PGR;
import pinacolada.skills.PSkill;
import pinacolada.skills.PTrigger;

import java.util.ArrayList;

import static extendedui.ui.AbstractScreen.createHexagonalButton;
import static pinacolada.ui.cardEditor.PCLCustomCardEffectPage.MENU_HEIGHT;
import static pinacolada.ui.cardEditor.PCLCustomCardEffectPage.MENU_WIDTH;

public class PCLCustomCardEditCardScreen extends PCLEffectWithCallback<Object>
{

    public static final int EFFECT_COUNT = 2;
    protected static final float CARD_X = Settings.WIDTH * 0.11f;
    protected static final float CARD_Y = Settings.HEIGHT * 0.76f;
    protected static final float START_X = Settings.WIDTH * (0.24f);
    protected static final float START_Y = Settings.HEIGHT * (0.84f);
    public ArrayList<PCLCardBuilder> prevBuilders;
    public ArrayList<PCLCardBuilder> tempBuilders;
    public int currentBuilder;
    protected ActionT0 onSave;
    protected ArrayList<PSkill> currentEffects = new ArrayList<>();
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
    protected PCLDynamicCard previewCard;
    protected PCLCustomCardFormEditor formEditor;
    protected PCLCustomCardSlot currentSlot;
    protected PCLCustomCardImageEffect imageEditor;
    protected int currentPage;

    public PCLCustomCardEditCardScreen(PCLCustomCardSlot slot)
    {
        final float buttonHeight = Settings.HEIGHT * (0.055f);
        final float labelHeight = Settings.HEIGHT * (0.04f);
        final float buttonWidth = Settings.WIDTH * (0.16f);
        final float labelWidth = Settings.WIDTH * (0.20f);
        final float button_cY = buttonHeight * 1.5f;
        currentSlot = slot;
        tempBuilders = EUIUtils.map(currentSlot.builders, PCLCardBuilder::new);

        currentEffects.addAll(getBuilder().moves);
        while (currentEffects.size() < EFFECT_COUNT)
        {
            currentEffects.add(null);
        }
        currentPowers.addAll(getBuilder().pPowers);
        while (currentPowers.size() < EFFECT_COUNT)
        {
            currentPowers.add(null);
        }

        cancelButton = createHexagonalButton(0, 0, buttonWidth, buttonHeight)
                .setPosition(buttonWidth * 0.6f, button_cY)
                .setColor(Color.FIREBRICK)
                .setText(GridCardSelectScreen.TEXT[1])
                .setFont(FontHelper.buttonLabelFont, 0.85f)
                .setOnClick(this::end);

        saveButton = createHexagonalButton(0, 0, buttonWidth, buttonHeight)
                .setPosition(cancelButton.hb.cX, cancelButton.hb.y + cancelButton.hb.height + labelHeight * 0.8f)
                .setColor(Color.FOREST)
                .setText(GridCardSelectScreen.TEXT[0])
                .setFont(FontHelper.buttonLabelFont, 0.85f)
                .setOnClick(this::save);

        undoButton = createHexagonalButton(0, 0, buttonWidth, buttonHeight)
                .setPosition(cancelButton.hb.cX, saveButton.hb.y + saveButton.hb.height + labelHeight * 0.8f)
                .setColor(Color.WHITE)
                .setText(PGR.core.strings.cardEditor.undo)
                .setFont(FontHelper.buttonLabelFont, 0.85f)
                .setOnClick(this::undo);

        imageButton = createHexagonalButton(0, 0, buttonWidth, buttonHeight)
                .setPosition(cancelButton.hb.cX, undoButton.hb.y + undoButton.hb.height + labelHeight * 0.8f)
                .setColor(Color.WHITE)
                .setText(PGR.core.strings.cardEditor.loadImage)
                .setTooltip(PGR.core.strings.cardEditor.loadImage, PGR.core.strings.cardEditorTutorial.primaryImage)
                .setFont(FontHelper.buttonLabelFont, 0.85f)
                .setOnClick(this::editImage);

        formEditor = new PCLCustomCardFormEditor(
                new EUIHitbox(0, 0, Settings.scale * 256f, Settings.scale * 48f)
                        .setCenter(Settings.WIDTH * 0.116f,  imageButton.hb.y + imageButton.hb.height + labelHeight * 3.2f), this);

        upgradeToggle = new EUIToggle(new EUIHitbox(Settings.scale * 256f, Settings.scale * 48f))
                .setPosition(Settings.WIDTH * 0.116f, CARD_Y - labelHeight - AbstractCard.IMG_HEIGHT / 2f)
                .setBackground(EUIRM.images.panel.texture(), Color.DARK_GRAY)
                .setFont(EUIFontHelper.carddescriptionfontLarge, 0.5f)
                .setText(SingleCardViewPopup.TEXT[6])
                .setToggle(SingleCardViewPopup.isViewingUpgrade)
                .setOnToggle(this::toggleViewUpgrades);

        pages.add(new PCLCustomCardPrimaryInfoPage(this));
        pages.add(new PCLCustomCardAttributesPage(this));
        for (int i = 0; i < currentEffects.size(); i++)
        {
            int finalI = i;
            PCLCustomCardEffectPage page = new PCLCustomCardEffectPage(this, currentEffects.get(i), new EUIHitbox(START_X, START_Y, MENU_WIDTH, MENU_HEIGHT)
                    , EUIUtils.format(PGR.core.strings.cardEditor.effectX, i + 1), (be) -> {
                currentEffects.set(finalI, be);
                refreshCard(e -> e.setPSkill(currentEffects, true, true));
            });
            pages.add(page);
            effectPages.add(page);
            page.refresh();
        }
        for (int i = 0; i < currentPowers.size(); i++)
        {
            int finalI = i;
            PCLCustomCardPowerPage page = new PCLCustomCardPowerPage(this, currentPowers.get(i), new EUIHitbox(START_X, START_Y, MENU_WIDTH, MENU_HEIGHT)
                    , EUIUtils.format(PGR.core.strings.cardEditor.powerX, i + 1), (be) -> {
                if (be instanceof PTrigger)
                {
                    currentPowers.set(finalI, (PTrigger) be);
                    refreshCard(e -> e.setPPower(currentPowers, true, true));
                }
            });
            pages.add(page);
            powerPages.add(page);
            page.refresh();
        }

        for (int i = 0; i < pages.size(); i++)
        {
            PCLCustomCardEditorPage pg = pages.get(i);
            String title = pg.getTitle();
            pageButtons.add(new EUIButton(EUIRM.images.squaredButton.texture(), new EUIHitbox(0, 0, buttonHeight, buttonHeight))
                    .setPosition(Settings.WIDTH * (0.45f) + ((i - 1f) * buttonHeight), (buttonHeight * 0.85f))
                    .setText(String.valueOf(title.charAt(0)))
                    .setOnClick(i, (finalI, __) -> {
                        currentPage = finalI;
                    })
                    .setTooltip(title, pg instanceof PCLCustomCardPrimaryInfoPage ? PGR.core.strings.cardEditor.primaryInfoDesc : ""));
        }

        refreshCard(__ -> {});
    }

    private void toggleViewUpgrades(boolean value)
    {
        SingleCardViewPopup.isViewingUpgrade = !SingleCardViewPopup.isViewingUpgrade;
        refreshCard(__ -> {});
    }

    public void addBuilder()
    {
        refreshCard(__ -> {
            tempBuilders.add(new PCLCardBuilder(getBuilder()));
        });
        for (PCLCustomCardEditorPage b : pages)
        {
            b.refresh();
        }
        currentBuilder = tempBuilders.size() - 1;
        formEditor.refresh();
    }

    protected void editImage()
    {
        imageEditor = (PCLCustomCardImageEffect) new PCLCustomCardImageEffect(getBuilder())
                .addCallback(pixmap -> {
                            if (pixmap != null)
                            {
                                PixmapIO.writePNG(currentSlot.getImageHandle(), pixmap);
                                refreshAll(e -> e
                                        .setImagePath(currentSlot.getImagePath()));
                                if (previewCard != null)
                                {
                                    previewCard.loadImage(null, true);
                                }
                            }
                        }
                );
    }

    protected void end()
    {
        complete();
    }

    public PCLCardBuilder getBuilder()
    {
        return tempBuilders.get(currentBuilder);
    }

    protected void rebuildCard()
    {
        previewCard = getBuilder().build().setForms(tempBuilders);
        if (SingleCardViewPopup.isViewingUpgrade)
        {
            previewCard.upgrade();
            previewCard.displayUpgrades();
        }

        previewCard.drawScale = previewCard.targetDrawScale = 1f;
        previewCard.current_x = previewCard.target_x = CARD_X;
        previewCard.current_y = previewCard.target_y = CARD_Y;
    }

    public void refreshAll(ActionT1<PCLCardBuilder> updateFunc)
    {
        prevBuilders = EUIUtils.map(tempBuilders, PCLCardBuilder::new);
        for (PCLCardBuilder b : tempBuilders)
        {
            updateFunc.invoke(b);
        }
        rebuildCard();
    }

    public void refreshCard(ActionT1<PCLCardBuilder> updateFunc)
    {
        prevBuilders = EUIUtils.map(tempBuilders, PCLCardBuilder::new);
        updateFunc.invoke(getBuilder());
        rebuildCard();
    }

    public void removeBuilder()
    {
        currentBuilder = MathUtils.clamp(currentBuilder, 0, tempBuilders.size() - 2);
        refreshCard(__ -> {
            tempBuilders.remove(getBuilder());
        });
        for (PCLCustomCardEditorPage b : pages)
        {
            b.refresh();
        }
        formEditor.refresh();
    }

    protected void save()
    {
        currentSlot.builders = tempBuilders;
        if (this.onSave != null)
        {
            this.onSave.invoke();
        }
        end();
    }

    public void setCurrentBuilder(int index)
    {
        currentBuilder = MathUtils.clamp(index, 0, tempBuilders.size() - 1);
        refreshCard(__ -> {
        });
        for (PCLCustomCardEditorPage b : pages)
        {
            b.refresh();
        }
        formEditor.refresh();
    }

    public PCLCustomCardEditCardScreen setOnSave(ActionT0 onSave)
    {
        this.onSave = onSave;

        return this;
    }

    protected void undo()
    {
        if (prevBuilders != null)
        {
            ArrayList<PCLCardBuilder> backups = EUIUtils.map(prevBuilders, PCLCardBuilder::new);
            currentBuilder = MathUtils.clamp(currentBuilder, 0, backups.size() - 1);
            formEditor.refresh();
            refreshCard(__ -> {
                tempBuilders = backups;
            });
            for (PCLCustomCardEditorPage b : pages)
            {
                b.refresh();
            }
        }
    }

    @Override
    public void render(SpriteBatch sb)
    {
        if (imageEditor != null)
        {
            imageEditor.render(sb);
        }
        else
        {
            cancelButton.tryRender(sb);
            saveButton.tryRender(sb);
            undoButton.tryRender(sb);
            imageButton.tryRender(sb);
            pages.get(currentPage).tryRender(sb);
            formEditor.tryRender(sb);
            upgradeToggle.tryRender(sb);
            for (EUIButton b : pageButtons)
            {
                b.tryRender(sb);
            }
            previewCard.render(sb);
        }
    }

    @Override
    protected void updateInternal(float deltaTime)
    {
        if (imageEditor != null)
        {
            imageEditor.update();
            if (imageEditor.isDone)
            {
                imageEditor = null;
            }
        }
        else
        {
            cancelButton.tryUpdate();
            saveButton.tryUpdate();
            undoButton.tryUpdate();
            imageButton.tryUpdate();
            pages.get(currentPage).tryUpdate();
            formEditor.tryUpdate();
            upgradeToggle.tryUpdate();
            for (EUIButton b : pageButtons)
            {
                b.tryUpdate();
            }
            previewCard.update();
            previewCard.hb.update();
            if (previewCard.hb.hovered)
            {
                EUITooltip.queueTooltips(previewCard);
            }
        }
    }

}
