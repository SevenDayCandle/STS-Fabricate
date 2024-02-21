package pinacolada.effects.screen;

import basemod.abstracts.CustomCard;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.*;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.FrameBuffer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.utils.ScreenUtils;
import com.megacrit.cardcrawl.cards.CardGroup;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.helpers.CardLibrary;
import com.megacrit.cardcrawl.helpers.FontHelper;
import com.megacrit.cardcrawl.helpers.input.InputHelper;
import com.megacrit.cardcrawl.screens.select.GridCardSelectScreen;
import extendedui.EUIGameUtils;
import extendedui.EUIRenderHelpers;
import extendedui.EUIUtils;
import extendedui.interfaces.delegates.ActionT0;
import extendedui.ui.EUIBase;
import extendedui.ui.controls.*;
import extendedui.ui.hitboxes.DraggableHitbox;
import extendedui.ui.hitboxes.EUIHitbox;
import pinacolada.cards.base.PCLCard;
import pinacolada.effects.PCLEffectWithCallback;
import pinacolada.resources.PGR;
import pinacolada.ui.editor.PCLCustomColorEditor;
import pinacolada.utilities.GameUtilities;
import pinacolada.utilities.PCLRenderHelpers;

import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.List;

import static extendedui.ui.controls.EUIButton.createHexagonalButton;

public class PCLCustomImageEffect extends PCLEffectWithCallback<Pixmap> {
    private static final FileNameExtensionFilter EXTENSIONS = new FileNameExtensionFilter("Image files (*.png, *.bmp, *.jpg, *.jpeg)", "png", "bmp", "jpg", "jpeg");
    private static final float OFFSET_BASE_CARD_Y = Settings.scale * -6;
    private static final float OUTLINE_SIZE = Settings.scale * 7;
    private static final int CARD_IMG_WIDTH = 500;
    private static final int CARD_IMG_HEIGHT = 380;
    private static final int ORB_IMG_SIZE = 96;
    private static final int POWER_IMG_SIZE = 84;
    private static final int RELIC_IMG_SIZE = 128;
    private final DraggableHitbox hb;
    private final EUILabel instructionsLabel;
    private final EUIButton cancelButton;
    private final EUIButton loadButton;
    private final EUIButton pasteButton;
    private final EUIButton saveButton;
    private final EUIButton selectExistingButton;
    private final EUIVerticalScrollBar zoomBar;
    private final PCLCustomColorEditor anchor1Editor;
    private final PCLCustomColorEditor anchor2Editor;
    private final PCLCustomColorEditor target1Editor;
    private final PCLCustomColorEditor target2Editor;
    private final EUIDialogColorPicker colorPicker;
    private final EUIToggle tintToggle;
    private final ShapeRenderer renderer;
    private final SpriteBatch sb;
    private final FrameBuffer imageBuffer;
    private final OrthographicCamera camera;
    private boolean enableTint;
    private Pixmap insideImage;
    private Texture baseTexture;
    private TextureRegion insideImageRenderable;
    private TextureRegion outsideImage;
    private PCLEffectWithCallback<?> curEffect;
    protected float scale = 1f;
    protected int targetWidth = CARD_IMG_WIDTH;
    protected int targetHeight = CARD_IMG_HEIGHT;

    public PCLCustomImageEffect(Texture base, int imageWidth, int imageHeight) {
        final float buttonHeight = Settings.HEIGHT * (0.055f);
        final float labelHeight = Settings.HEIGHT * (0.04f);
        final float buttonWidth = Settings.WIDTH * (0.16f);
        final float labelWidth = Settings.WIDTH * (0.20f);
        final float button_cY = buttonHeight * 1.5f;

        hb = new DraggableHitbox(0, 0, Settings.WIDTH * 2, Settings.HEIGHT * 2, true);
        targetWidth = imageWidth;
        targetHeight = imageHeight;
        renderer = new ShapeRenderer();

        instructionsLabel = new EUILabel(FontHelper.topPanelAmountFont,
                new EUIHitbox(Settings.WIDTH * 0.35f, Settings.HEIGHT * 0.1f, buttonWidth * 2f, buttonHeight))
                .setAlignment(0.5f, 0f, true)
                .setFont(FontHelper.topPanelAmountFont, 0.8f)
                .setLabel(EUIUtils.format(PGR.core.strings.cetut_imageSelect, targetWidth, targetHeight));

        cancelButton = createHexagonalButton(0, 0, buttonWidth, buttonHeight)
                .setPosition(buttonWidth * 0.6f, button_cY)
                .setColor(Color.FIREBRICK)
                .setLabel(FontHelper.buttonLabelFont, 0.85f, GridCardSelectScreen.TEXT[1])
                .setOnClick((ActionT0) this::complete);

        saveButton = createHexagonalButton(0, 0, buttonWidth, buttonHeight)
                .setPosition(cancelButton.hb.cX, cancelButton.hb.y + cancelButton.hb.height + labelHeight * 0.8f)
                .setColor(Color.FOREST)
                .setLabel(FontHelper.buttonLabelFont, 0.85f, GridCardSelectScreen.TEXT[0])
                .setInteractable(false)
                .setOnClick(this::commit);

        pasteButton = createHexagonalButton(0, 0, buttonWidth, buttonHeight)
                .setPosition(cancelButton.hb.cX, saveButton.hb.y + saveButton.hb.height + labelHeight * 0.8f)
                .setColor(Color.WHITE)
                .setLabel(FontHelper.buttonLabelFont, 0.85f, PGR.core.strings.cedit_paste)
                .setOnClick(this::getImageFromClipboard);

        selectExistingButton = createHexagonalButton(0, 0, buttonWidth, buttonHeight)
                .setPosition(cancelButton.hb.cX, pasteButton.hb.y + pasteButton.hb.height + labelHeight * 0.8f)
                .setColor(Color.WHITE)
                .setLabel(FontHelper.buttonLabelFont, 0.85f, PGR.core.strings.cedit_loadFromCard)
                .setOnClick(this::selectExistingCards);

        loadButton = createHexagonalButton(0, 0, buttonWidth, buttonHeight)
                .setPosition(cancelButton.hb.cX, selectExistingButton.hb.y + selectExistingButton.hb.height + labelHeight * 0.8f)
                .setColor(Color.WHITE)
                .setLabel(FontHelper.buttonLabelFont, 0.85f, PGR.core.strings.cedit_loadFile)
                .setOnClick(this::getImageFromFileDialog);

        colorPicker = new EUIDialogColorPicker(new EUIHitbox(Settings.WIDTH * 0.7f, (Settings.HEIGHT - EUIBase.scale(800)) / 2f, EUIBase.scale(460), EUIBase.scale(800)), EUIUtils.EMPTY_STRING, EUIUtils.EMPTY_STRING);
        colorPicker
                .setShowDark(false)
                .setActive(false);

        target2Editor = new PCLCustomColorEditor(new EUIHitbox(cancelButton.hb.x + cancelButton.hb.width * 0.2f, selectExistingButton.hb.y + selectExistingButton.hb.height + labelHeight * 2f, EUIGameUtils.scale(160), EUIGameUtils.scale(60))
                , EUIUtils.format(PGR.core.strings.cedit_targetColor, 2),
                this::openTint,
                (__) -> this.tryUpdatePictures());
        target2Editor.setTooltip(target2Editor.header.text, PGR.core.strings.cetut_colorAnchor);
        target1Editor = new PCLCustomColorEditor(new EUIHitbox(target2Editor.hb.x, target2Editor.hb.y + target2Editor.hb.height + labelHeight, EUIGameUtils.scale(160), EUIGameUtils.scale(60))
                , EUIUtils.format(PGR.core.strings.cedit_targetColor, 1),
                this::openTint,
                (__) -> this.tryUpdatePictures());
        target1Editor.setTooltip(target1Editor.header.text, PGR.core.strings.cetut_colorAnchor);
        anchor2Editor = new PCLCustomColorEditor(new EUIHitbox(target1Editor.hb.x, target1Editor.hb.y + target1Editor.hb.height + labelHeight, EUIGameUtils.scale(160), EUIGameUtils.scale(60))
                , EUIUtils.format(PGR.core.strings.cedit_anchorColor, 2),
                this::openTint,
                (__) -> this.tryUpdatePictures());
        anchor2Editor.setTooltip(anchor2Editor.header.text, PGR.core.strings.cetut_colorAnchor);
        anchor1Editor = new PCLCustomColorEditor(new EUIHitbox(anchor2Editor.hb.x, anchor2Editor.hb.y + anchor2Editor.hb.height + labelHeight, EUIGameUtils.scale(160), EUIGameUtils.scale(60))
                , EUIUtils.format(PGR.core.strings.cedit_anchorColor, 1),
                this::openTint,
                (__) -> this.tryUpdatePictures());
        anchor1Editor.setTooltip(anchor1Editor.header.text, PGR.core.strings.cetut_colorAnchor);
        anchor1Editor.setActive(false);
        anchor2Editor.setActive(false);
        target1Editor.setActive(false);
        target2Editor.setActive(false);

        tintToggle = (EUIToggle) new EUIToggle(new EUIHitbox(cancelButton.hb.x + cancelButton.hb.width * 0.2f, anchor1Editor.hb.y + anchor1Editor.hb.height + labelHeight * 2f, buttonWidth, buttonHeight))
                .setFont(FontHelper.cardDescFont_N, 1f)
                .setText(PGR.core.strings.cedit_enableTint)
                .setOnToggle(this::setEnableTint)
                .setTooltip(PGR.core.strings.cedit_enableTint, PGR.core.strings.cedit_tintDesc);


        zoomBar = new EUIVerticalScrollBar(new EUIHitbox(Settings.WIDTH * 0.03f, Settings.HEIGHT * 0.7f))
                .setPosition(Settings.WIDTH * 0.9f, Settings.HEIGHT * 0.5f)
                .setOnScroll(this::updateZoom);
        zoomBar.setActive(false);

        imageBuffer = new FrameBuffer(Pixmap.Format.RGBA8888, Gdx.graphics.getWidth(), Gdx.graphics.getHeight(), true, false);
        camera = new OrthographicCamera((float) Gdx.graphics.getWidth(), (float) Gdx.graphics.getHeight());
        sb = new SpriteBatch();
        sb.setProjectionMatrix(camera.combined.scl(1, -1, 1));

        if (base != null) {
            baseTexture = new Texture(base.getTextureData());
            updatePictures();
        }
    }

    public static PCLCustomImageEffect forCard(Texture texture) {
        return new PCLCustomImageEffect(texture, CARD_IMG_WIDTH, CARD_IMG_HEIGHT);
    }

    public static PCLCustomImageEffect forOrb(Texture texture) {
        return new PCLCustomImageEffect(texture, ORB_IMG_SIZE, ORB_IMG_SIZE);
    }

    public static PCLCustomImageEffect forPower(Texture texture) {
        return new PCLCustomImageEffect(texture, POWER_IMG_SIZE, POWER_IMG_SIZE);
    }

    public static PCLCustomImageEffect forRelic(Texture texture) {
        return new PCLCustomImageEffect(texture, RELIC_IMG_SIZE, RELIC_IMG_SIZE);
    }

    protected void commit() {
        if (baseTexture == null) {
            complete(null);
        }
        else {
            updateBuffer(true);
            complete(insideImage);
        }
    }

    public void complete(Pixmap pixmap) {
        super.complete(pixmap);
        if (pixmap == null) {
            if (insideImage != null) {
                try {
                    insideImage.dispose();
                }
                catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public void complete() {
        super.complete();
        // Ensure textures are unloaded to avoid memory leaks
        if (baseTexture != null) {
            baseTexture.dispose();
        }
        if (insideImageRenderable != null) {
            insideImageRenderable.getTexture().dispose();
        }
    }

    private void getImageFromClipboard() {
        Transferable transferable = Toolkit.getDefaultToolkit().getSystemClipboard().getContents(null);
        if (transferable != null) {
            if (transferable.isDataFlavorSupported(DataFlavor.imageFlavor)) {
                try {
                    BufferedImage image = ((BufferedImage) transferable.getTransferData(DataFlavor.imageFlavor));
                    updateImage(new Texture(PCLRenderHelpers.getPixmapFromBufferedImage(image), true));
                }
                catch (Exception e) {
                    e.printStackTrace();
                }
            }
            else if (transferable.isDataFlavorSupported(DataFlavor.javaFileListFlavor)) {
                try {
                    java.util.List<File> droppedFiles = (List<File>) transferable.getTransferData(DataFlavor.javaFileListFlavor);
                    updateImage(new Texture(new FileHandle(droppedFiles.get(0)), true));
                }
                catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private void getImageFromFileDialog() {
        try {
            File openedFile = EUIUtils.loadFile(EXTENSIONS, PGR.config.lastImagePath);
            updateImage(new Texture(new FileHandle(openedFile), true));
        }
        catch (Exception e) {
            e.printStackTrace();
            EUIUtils.logError(this, "Failed to load card image: " + e.getLocalizedMessage());
        }
    }

    private void openTint(PCLCustomColorEditor editor) {
        Color prev = editor.getColor().cpy();
        colorPicker
                .setOnChange(editor::setColor)
                .setOnComplete((res) -> {
                    colorPicker.setActive(false);
                    if (res == null) {
                        editor.setColor(prev, true);
                    }
                })
                .setHeaderText(editor.header.text)
                .setActive(true);
        colorPicker.open(prev);
    }

    @Override
    public void render(SpriteBatch sb) {
        if (curEffect != null) {
            curEffect.render(sb);
        }
        else {
            hb.render(sb);
            cancelButton.tryRender(sb);
            saveButton.tryRender(sb);
            loadButton.tryRender(sb);
            selectExistingButton.tryRender(sb);
            pasteButton.tryRender(sb);
            instructionsLabel.tryRender(sb);
            anchor1Editor.tryRender(sb);
            anchor2Editor.tryRender(sb);
            target1Editor.tryRender(sb);
            target2Editor.tryRender(sb);
            tintToggle.tryRender(sb);
            zoomBar.tryRender(sb);

            if (outsideImage != null) {
                PCLRenderHelpers.drawCentered(sb, Color.GRAY.cpy(), outsideImage, Settings.WIDTH / 2f, Settings.HEIGHT / 2f, outsideImage.getRegionWidth(), outsideImage.getRegionHeight(), 1, 0);
            }
            if (insideImageRenderable != null) {
                sb.end();
                float renderW = insideImageRenderable.getRegionWidth();
                float renderY = insideImageRenderable.getRegionHeight();
                float boxW = renderW * Settings.scale;
                float boxY = renderY * Settings.scale;
                float boxW2 = boxW + OUTLINE_SIZE;
                float boxY2 = boxY + OUTLINE_SIZE;
                renderer.setProjectionMatrix(sb.getProjectionMatrix());
                renderer.begin(ShapeRenderer.ShapeType.Filled);
                renderer.rect((Settings.WIDTH - boxW2) / 2, (Settings.HEIGHT - boxY2) / 2, boxW2, boxY2, Color.MAGENTA, Color.MAGENTA, Color.MAGENTA, Color.MAGENTA);
                renderer.rect((Settings.WIDTH - boxW) / 2, (Settings.HEIGHT - boxY) / 2, boxW, boxY, Color.BLACK, Color.BLACK, Color.BLACK, Color.BLACK);
                renderer.end();
                sb.begin();
                PCLRenderHelpers.drawCentered(sb, Color.WHITE.cpy(), insideImageRenderable, Settings.WIDTH / 2f, Settings.HEIGHT / 2f, renderW, renderY, 1, 0);
            }

            colorPicker.tryRender(sb);
        }
    }

    private void selectExistingCards() {
        CardGroup group = GameUtilities.createCardGroup(CardLibrary.getAllCards());
        group.sortAlphabetically(true);
        curEffect = new PCLGenericSelectCardEffect(group.group)
                .addCallback(card -> {
                            if (card != null) {
                                if (card instanceof PCLCard) {
                                    // TODO handle EYBCardBase with PCLCard check
                                    updateImage(new Texture(Gdx.files.internal(card.assetUrl), true));
                                }
                                else {
                                    // Zoom in slightly for non-PCLCards because base game card images have weird-ass transparent offsets
                                    updateImage(
                                            card instanceof CustomCard ? CustomCard.getPortraitImage((CustomCard) card)
                                                    : new Texture(Gdx.files.internal(GameUtilities.toInternalAtlasPath(card.assetUrl)), true)
                                            , 0.53f, Settings.WIDTH, Settings.HEIGHT + OFFSET_BASE_CARD_Y);
                                }
                            }
                        }
                );
    }

    private void setEnableTint(boolean val) {
        this.enableTint = val;
        this.anchor1Editor.setActive(val);
        this.anchor2Editor.setActive(val);
        this.target1Editor.setActive(val);
        this.target2Editor.setActive(val);
        if (baseTexture != null) {
            updatePictures();
        }
    }

    private void tryUpdatePictures() {
        if (baseTexture != null) {
            updatePictures();
        }
    }

    private void updateBuffer(boolean forCommit) {
        imageBuffer.begin();
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        Gdx.gl.glClearColor(0f, 0f, 0f, 0f);
        sb.begin();
        sb.setColor(Color.WHITE);

        if (forCommit) {
            // Generate a temporary resized image and capture it into the framebuffer
            Texture resized = new Texture(PCLRenderHelpers.scalrScaleAsPixmap(baseTexture, scale, scale));
            if (enableTint) {
                PCLRenderHelpers.drawBicolor(sb, anchor1Editor.getColor(), anchor2Editor.getColor(), target1Editor.getColor(), target2Editor.getColor(), s -> s.draw(resized, hb.x - resized.getWidth() / 2f, hb.y - resized.getHeight() / 2f, resized.getWidth() / 2f, resized.getHeight() / 2f, resized.getWidth(), resized.getHeight(), 1f, 1f, 0f, 0, 0, resized.getWidth(), resized.getHeight(), false, false));
            }
            else {
                sb.draw(resized, hb.x - resized.getWidth() / 2f, hb.y - resized.getHeight() / 2f, resized.getWidth() / 2f, resized.getHeight() / 2f, resized.getWidth(), resized.getHeight(), 1f, 1f, 0f, 0, 0, resized.getWidth(), resized.getHeight(), false, false);
            }
            updateBufferEnding();
            resized.dispose();
        }
        else {
            if (enableTint) {
                PCLRenderHelpers.drawBicolor(sb, anchor1Editor.getColor(), anchor2Editor.getColor(), target1Editor.getColor(), target2Editor.getColor(), s -> s.draw(baseTexture, hb.x - baseTexture.getWidth() / 2f, hb.y - baseTexture.getHeight() / 2f, baseTexture.getWidth() / 2f, baseTexture.getHeight() / 2f, baseTexture.getWidth(), baseTexture.getHeight(), scale, scale, 0f, 0, 0, baseTexture.getWidth(), baseTexture.getHeight(), false, false));
            }
            else {
                sb.draw(baseTexture, hb.x - baseTexture.getWidth() / 2f, hb.y - baseTexture.getHeight() / 2f, baseTexture.getWidth() / 2f, baseTexture.getHeight() / 2f, baseTexture.getWidth(), baseTexture.getHeight(), scale, scale, 0f, 0, 0, baseTexture.getWidth(), baseTexture.getHeight(), false, false);
            }

            updateBufferEnding();
        }
    }

    private void updateBufferEnding() {
        sb.end();
        // Dispose the existing pixmap
        if (insideImage != null) {
            try {
                insideImage.dispose();
            }
            catch (Exception e) {
                e.printStackTrace();
            }
        }
        insideImage = ScreenUtils.getFrameBufferPixmap((Settings.WIDTH - targetWidth) / 2, (Settings.HEIGHT - targetHeight) / 2, targetWidth, targetHeight);
        imageBuffer.end();
    }

    // Update the images shown on the screen. Textures are explicitly not saved with EUI because we don't want to keep them after this effect is over
    private void updateImage(Texture texture) {
        updateImage(texture, 0.5f, Settings.WIDTH, Settings.HEIGHT);
    }

    private void updateImage(Texture texture, float zoom, float centerX, float centerY) {
        if (texture != null) {
            // Flush the existing texture before dropping it
            if (baseTexture != null) {
                baseTexture.dispose();
            }

            baseTexture = texture;
            hb.setCenter(centerX, centerY);

            baseTexture.setFilter(Texture.TextureFilter.MipMapLinearLinear, Texture.TextureFilter.Linear);
            zoomBar.setActive(true);
            updateZoom(zoom);
            instructionsLabel.setLabel(PGR.core.strings.cetut_imageCrop);
            saveButton.setInteractable(true);
        }
    }

    @Override
    protected void updateInternal(float deltaTime) {
        if (curEffect != null) {
            curEffect.update();
            if (curEffect.isDone) {
                curEffect = null;
            }
        }
        else {
            cancelButton.tryUpdate();
            saveButton.tryUpdate();
            loadButton.tryUpdate();
            selectExistingButton.tryUpdate();
            pasteButton.tryUpdate();
            instructionsLabel.tryUpdate();
            anchor1Editor.tryUpdate();
            anchor2Editor.tryUpdate();
            target1Editor.tryUpdate();
            target2Editor.tryUpdate();
            tintToggle.tryUpdate();
            camera.update();
            if (!colorPicker.tryUpdate()) {
                if (baseTexture != null) {
                    if (!hb.isDragging()) {
                        zoomBar.tryUpdate();
                    }
                    if (!zoomBar.isDragging) {
                        hb.update();
                        if (hb.isDragging()) {
                            updatePictures();
                        }
                    }
                }
            }

            if (InputHelper.isPasteJustPressed()) {
                getImageFromClipboard();
            }
        }
    }

    private void updatePictures() {
        updateBuffer(false);

        // Dispose the inner image texture. Outside image texture is handled by the image buffer
        if (insideImageRenderable != null) {
            insideImageRenderable.getTexture().dispose();
        }

        // Textures are explicitly not saved with EUI because we don't want to keep them after this effect is over
        outsideImage = new TextureRegion(imageBuffer.getColorBufferTexture(), Settings.WIDTH / 3, Settings.HEIGHT / 4, Settings.WIDTH / 3, Settings.HEIGHT / 2);
        insideImageRenderable = new TextureRegion(new Texture(insideImage));
        outsideImage.getTexture().setFilter(Texture.TextureFilter.Linear, Texture.TextureFilter.Linear);
        insideImageRenderable.getTexture().setFilter(Texture.TextureFilter.Linear, Texture.TextureFilter.Linear);
    }

    private void updateZoom(float scrollPercentage) {
        zoomBar.scroll(scrollPercentage, false);
        scale = EUIRenderHelpers.lerp(0f, 2f, scrollPercentage);

        updatePictures();
    }
}
