package pinacolada.ui.characterSelection;

import basemod.BaseMod;
import basemod.animations.AbstractAnimation;
import basemod.animations.SpineAnimation;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.utils.Array;
import com.esotericsoftware.spine.*;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.helpers.ImageMaster;
import com.megacrit.cardcrawl.screens.select.GridCardSelectScreen;
import extendedui.EUIUtils;
import extendedui.ui.controls.*;
import extendedui.ui.hitboxes.EUIHitbox;
import extendedui.ui.hitboxes.RelativeHitbox;
import extendedui.utilities.EUIFontHelper;
import pinacolada.cards.base.PCLCard;
import pinacolada.cards.base.fields.PCLCustomFlagInfo;
import pinacolada.characters.PCLCharacter;
import pinacolada.characters.PCLCharacterAnimation;
import pinacolada.characters.PCLCharacterSpineAnimation;
import pinacolada.monsters.animations.PCLAnimation;

import java.util.Collection;
import java.util.HashMap;

public class PCLSkinDialog extends EUIDialog<String> {
    private static final float ICON_SIZE = 60f * Settings.scale;
    private final EUISearchableDropdown<String> skins;
    private final EUIButton next;
    private final EUIButton prev;
    private int page;
    private String selection;
    private AbstractAnimation current;
    private Skeleton skeleton;
    private AnimationState state;
    private AnimationStateData stateData;
    private TextureAtlas atlas;
    private Texture img;

    public PCLSkinDialog(String headerText, Collection<String> skins) {
        this(headerText, "", skins);
    }

    public PCLSkinDialog(String headerText, String descriptionText, Collection<String> skins) {
        this(headerText, descriptionText, scale(500), scale(780), skins);
    }

    public PCLSkinDialog(String headerText, String descriptionText, float w, float h, Collection<String> skins) {
        this(new EUIHitbox(Settings.WIDTH / 2.0F - w / 2f, Settings.HEIGHT / 2.0F - h / 2f, w, h), ImageMaster.OPTION_CONFIRM, headerText, descriptionText, skins);
    }

    public PCLSkinDialog(EUIHitbox hb, Texture backgroundTexture, String headerText, String descriptionText, Collection<String> skins) {
        super(hb, backgroundTexture, headerText, descriptionText);
        this.skins = (EUISearchableDropdown<String>) new EUISearchableDropdown<String>(new RelativeHitbox(hb, hb.width * 0.5f, scale(48), hb.width * 0.5f, hb.height * 0.75f), this::sanitizeName)
                .setOnOpenOrClose(isOpen -> {
                    CardCrawlGame.isPopupOpen = isOpen;
                })
                .setCanAutosize(false, true);
        this.skins.setOnChange(selectedSeries -> {
                    this.skins.forceClose();
                    if (selectedSeries.size() > 0) {
                        setPage(selectedSeries.get(0));
                    }
                }
        );
        this.description = new EUILabel(EUIFontHelper.cardTooltipFont,
                new RelativeHitbox(hb, hb.width * 0.8f, hb.height, hb.width * 0.1f, hb.height - scale(200)))
                .setAlignment(0.5f, 0.5f, true)
                .setSmartText(true, false);

        this.skins.setItems(skins);
        this.skins.sortByLabel();
        changePage(0);

        this.next = new EUIButton(ImageMaster.POPUP_ARROW,
                new RelativeHitbox(hb, ICON_SIZE, ICON_SIZE, hb.width * 0.9f, hb.height * 0.5f))
                .setOnClick(() -> changePage(page >= this.skins.size() - 1 ? 0 : page + 1));
        this.next.background.setFlipping(true, false);

        this.prev = new EUIButton(ImageMaster.POPUP_ARROW,
                new RelativeHitbox(hb, ICON_SIZE, ICON_SIZE, hb.width * 0.1f, hb.height * 0.5f))
                .setOnClick(() -> changePage(page <= 0 ? this.skins.size() - 1 : page - 1));

        this.next.setActive(this.skins.size() > 1);
        this.prev.setActive(this.skins.size() > 1);
    }

    protected void changePage(int newPage) {
        this.page = newPage;
        this.skins.setSelectedIndex(newPage);
    }

    public void close() {
        this.skins.forceClose();
    }

    protected EUIButton getCancelButton() {
        return new EUIButton(ImageMaster.OPTION_NO,
                new RelativeHitbox(hb, scale(175), scale(80), hb.width * 0.85f, hb.height * 0.15f))
                .setLabel(EUIFontHelper.cardTitleFontNormal, 0.8f, GridCardSelectScreen.TEXT[1])
                .setOnClick(() -> {
                    if (onComplete != null) {
                        onComplete.invoke(getCancelValue());
                    }
                });
    }

    @Override
    public String getCancelValue() {
        return null;
    }

    protected EUIButton getConfirmButton() {
        return new EUIButton(ImageMaster.OPTION_YES,
                new RelativeHitbox(hb, scale(175), scale(80), hb.width * 0.15f, hb.height * 0.15f))
                .setLabel(EUIFontHelper.cardTitleFontNormal, 0.8f, GridCardSelectScreen.TEXT[0])
                .setOnClick(() -> {
                    if (onComplete != null) {
                        onComplete.invoke(getConfirmValue());
                    }
                });
    }

    protected EUILabel getHeader(String headerText) {
        return new EUILabel(EUIFontHelper.buttonFont,
                new RelativeHitbox(hb, hb.width, hb.height, hb.width * 0.5f, hb.height * 0.9f))
                .setAlignment(0.5f, 0.5f, false)
                .setLabel(headerText);
    }

    @Override
    public String getConfirmValue() {
        return selection;
    }

    private void loadAnimation(String atlasUrl, String skeletonUrl, float scale) {
        try {
            this.atlas = new TextureAtlas(Gdx.files.internal(atlasUrl));
            SkeletonJson json = new SkeletonJson(this.atlas);
            json.setScale(Settings.renderScale * scale);
            SkeletonData skeletonData = json.readSkeletonData(Gdx.files.internal(skeletonUrl));
            this.skeleton = new Skeleton(skeletonData);
            this.skeleton.setColor(Color.WHITE);
            this.stateData = new AnimationStateData(skeletonData);
            this.state = new AnimationState(this.stateData);
            Array<Animation> animations = this.stateData.getSkeletonData().getAnimations();
            if (animations.size > 0) {
                this.state.setAnimation(0, animations.get(0), true);
            }

        }
        catch (Exception e) {
            EUIUtils.logError(this, "Failed to reload animation with atlas " + atlasUrl + " and skeleton " + skeletonUrl);
        }
    }

    public void open(String initial) {
        setActive(true);
        this.skins.setSelection(initial, true);
    }

    private void renderAnimation(SpriteBatch sb, AbstractAnimation animation) {
        switch (animation.type()) {
            case NONE:
                if (this.atlas != null) {
                    this.state.update(Gdx.graphics.getDeltaTime());
                    this.state.apply(this.skeleton);
                    this.skeleton.updateWorldTransform();
                    this.skeleton.setPosition(hb.cX, hb.y + hb.height * 0.25f);
                    sb.end();
                    CardCrawlGame.psb.begin();
                    PCLCharacter.sr.draw(CardCrawlGame.psb, this.skeleton);
                    CardCrawlGame.psb.end();
                    sb.begin();
                }
                else if (this.img != null) {
                    sb.setColor(Color.WHITE);
                    sb.draw(this.img, hb.cX - (float) this.img.getWidth() * Settings.scale / 2.0F, hb.y + hb.height * 0.25f, (float) this.img.getWidth() * Settings.scale, (float) this.img.getHeight() * Settings.scale, 0, 0, this.img.getWidth(), this.img.getHeight(), false, false);
                }
                break;
            case MODEL:
                BaseMod.publishAnimationRender(sb);
                break;
            case SPRITE:
                animation.renderSprite(sb, hb.cX, hb.y + hb.height * 0.25f);
        }
    }

    @Override
    public void renderImpl(SpriteBatch sb) {
        super.renderImpl(sb);
        this.skins.tryRender(sb);
        this.next.tryRender(sb);
        this.prev.tryRender(sb);
        if (current != null) {
            renderAnimation(sb, current);
        }
    }

    private String sanitizeName(String val) {
        String[] splits = EUIUtils.splitString(".", val);
        return splits.length > 0 ? splits[splits.length - 1] : EUIUtils.EMPTY_STRING;
    }

    public void setPage(String page) {
        int index = this.skins.indexOf(page);
        if (index >= 0) {
            this.page = index;
            this.selection = page;
            if (this.img != null) {
                this.img.dispose();
                this.img = null;
            }
            if (this.atlas != null) {
                this.atlas.dispose();
                this.atlas = null;
            }
            current = PCLCharacterAnimation.getAnimationForID(page);
            if (current instanceof SpineAnimation) {
                loadAnimation(((SpineAnimation) current).atlasUrl, ((SpineAnimation) current).skeletonUrl, ((SpineAnimation) current).scale);
            }
            else if (current instanceof PCLCharacterAnimation) {
                loadAnimation(((PCLCharacterAnimation) current).atlasUrl, ((PCLCharacterAnimation) current).skeletonUrl, ((PCLCharacterAnimation) current).scale);
            }
        }
    }

    @Override
    public void updateImpl() {
        super.updateImpl();
        this.skins.tryUpdate();
        this.next.tryUpdate();
        this.prev.tryUpdate();
    }
}
