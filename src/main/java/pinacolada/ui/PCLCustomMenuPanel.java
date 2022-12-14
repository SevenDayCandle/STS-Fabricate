package pinacolada.ui;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.math.MathUtils;
import com.evacipated.cardcrawl.modthespire.lib.SpireEnum;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.helpers.FontHelper;
import com.megacrit.cardcrawl.helpers.Hitbox;
import com.megacrit.cardcrawl.helpers.ImageMaster;
import com.megacrit.cardcrawl.helpers.MathHelper;
import com.megacrit.cardcrawl.helpers.controller.CInputActionSet;
import com.megacrit.cardcrawl.helpers.input.InputHelper;
import com.megacrit.cardcrawl.screens.DoorUnlockScreen;
import com.megacrit.cardcrawl.screens.mainMenu.MainMenuPanelButton;
import com.megacrit.cardcrawl.screens.mainMenu.MainMenuScreen;
import com.megacrit.cardcrawl.screens.mainMenu.MenuPanelScreen;
import pinacolada.resources.PGR;
import pinacolada.ui.cardEditor.PCLCustomCardSelectorScreen;
import pinacolada.utilities.PCLRenderHelpers;

import static com.megacrit.cardcrawl.screens.mainMenu.MainMenuPanelButton.PanelColor.BEIGE;

public class PCLCustomMenuPanel extends MainMenuPanelButton
{

    public static final float START_Y = Settings.HEIGHT / 2.0F;
    public static final int PANEL_H = 800;
    public static final int PANEL_W = 512;
    protected static final int P_H = 206;
    protected static final int P_W = 317;
    @SpireEnum
    public static MainMenuPanelButton.PanelClickResult CUSTOM_CARDS;
    public Hitbox hb;
    public MainMenuPanelButton.PanelColor pColor;
    protected Color cColor;
    protected Color gColor;
    protected Color grColor;
    protected Color wColor;
    protected Color oColor;
    protected MainMenuPanelButton.PanelClickResult result;
    protected String description;
    protected String header;
    protected Texture panelImg;
    protected Texture portraitImg;
    protected float animTime;
    protected float animTimer;
    protected float uiScale;
    protected float yMod;
    protected float w;
    protected float h;

    public PCLCustomMenuPanel(MainMenuPanelButton.PanelClickResult setResult, MainMenuPanelButton.PanelColor setColor, float x, float y)
    {
        this(setResult, setColor, x, y, PANEL_W, PANEL_H);
    }

    public PCLCustomMenuPanel(MainMenuPanelButton.PanelClickResult setResult, MainMenuPanelButton.PanelColor setColor, float x, float y, float w, float h)
    {
        super(setResult, BEIGE, x, y);
        this.hb = new Hitbox(w * Settings.scale * 0.75f, h * Settings.scale * 0.75f);
        this.hb.move(x, y);
        this.gColor = Settings.GOLD_COLOR.cpy();
        this.cColor = Settings.CREAM_COLOR.cpy();
        this.wColor = Color.WHITE.cpy();
        this.grColor = Color.GRAY.cpy();
        this.portraitImg = null;
        this.panelImg = ImageMaster.MENU_PANEL_BG_BLUE;
        this.header = null;
        this.description = null;
        this.uiScale = 1.0F;
        this.result = setResult;
        this.pColor = setColor;
        this.setLabel();
        this.animTime = MathUtils.random(0.2F, 0.35F);
        this.animTimer = this.animTime;
        this.w = w;
        this.h = h;
    }

    public PCLCustomMenuPanel setPanelColor(Color color)
    {
        this.oColor = color.cpy();
        return this;
    }

    private void animatePanelIn()
    {
        this.animTimer -= Gdx.graphics.getDeltaTime();
        if (this.animTimer < 0.0F)
        {
            this.animTimer = 0.0F;
        }

        this.yMod = Interpolation.swingIn.apply(0.0F, START_Y, this.animTimer / this.animTime);
        this.wColor.a = 1.0F - this.animTimer / this.animTime;
        this.cColor.a = this.wColor.a;
        this.gColor.a = this.wColor.a;
        this.grColor.a = this.wColor.a;
        if (this.oColor != null)
        {
            this.oColor.a = this.wColor.a;
        }
    }

    private void buttonEffect()
    {
        if (this.result == CUSTOM_CARDS)
        {
            PGR.core.customCards.open(null, PCLCustomCardSelectorScreen.currentColor, () -> {
            });
        }
        else
        {
            switch (this.result)
            {
                case PLAY_CUSTOM:
                    CardCrawlGame.mainMenuScreen.customModeScreen.open();
                    break;
                case PLAY_DAILY:
                    CardCrawlGame.mainMenuScreen.dailyScreen.open();
                    break;
                case PLAY_NORMAL:
                    CardCrawlGame.mainMenuScreen.charSelectScreen.open(false);
                    break;
                case INFO_CARD:
                    CardCrawlGame.mainMenuScreen.cardLibraryScreen.open();
                    break;
                case INFO_RELIC:
                    CardCrawlGame.mainMenuScreen.relicScreen.open();
                    break;
                case INFO_POTION:
                    CardCrawlGame.mainMenuScreen.potionScreen.open();
                    break;
                case STAT_CHAR:
                    CardCrawlGame.mainMenuScreen.statsScreen.open();
                    break;
                case STAT_HISTORY:
                    CardCrawlGame.mainMenuScreen.runHistoryScreen.open();
                    break;
                case STAT_LEADERBOARDS:
                    CardCrawlGame.mainMenuScreen.leaderboardsScreen.open();
                    break;
                case SETTINGS_CREDITS:
                    DoorUnlockScreen.show = false;
                    CardCrawlGame.mainMenuScreen.creditsScreen.open(false);
                    break;
                case SETTINGS_GAME:
                    CardCrawlGame.sound.play("END_TURN");
                    CardCrawlGame.mainMenuScreen.isSettingsUp = true;
                    InputHelper.pressedEscape = false;
                    CardCrawlGame.mainMenuScreen.statsScreen.hide();
                    CardCrawlGame.mainMenuScreen.cancelButton.hide();
                    CardCrawlGame.cancelButton.show(MainMenuScreen.TEXT[2]);
                    CardCrawlGame.mainMenuScreen.screen = MainMenuScreen.CurScreen.SETTINGS;
                    break;
                case SETTINGS_INPUT:
                    CardCrawlGame.mainMenuScreen.inputSettingsScreen.open();
            }
        }
    }

    private void renderPanel(SpriteBatch sb)
    {
        sb.draw(this.panelImg, this.hb.cX - (w / 2), this.hb.cY + this.yMod - (h / 2), (w / 2), (h / 2), w, h, this.uiScale * Settings.scale, this.uiScale * Settings.scale, 0.0F, 0, 0, 512, 800, false, false);
    }

    private void setLabel()
    {
        this.panelImg = ImageMaster.MENU_PANEL_BG_BEIGE;
        if (this.result == CUSTOM_CARDS)
        {
            this.header = PGR.core.strings.cardEditor.customCards;
            this.description = PGR.core.strings.cardEditor.customCardsDesc;
            this.portraitImg = ImageMaster.P_DAILY;
        }
        switch (this.result)
        {
            case PLAY_CUSTOM:
                this.header = MenuPanelScreen.TEXT[39];
                if (this.pColor == MainMenuPanelButton.PanelColor.GRAY)
                {
                    this.description = MenuPanelScreen.TEXT[37];
                    this.panelImg = ImageMaster.MENU_PANEL_BG_GRAY;
                }
                else
                {
                    this.description = MenuPanelScreen.TEXT[40];
                    this.panelImg = ImageMaster.MENU_PANEL_BG_RED;
                }

                this.portraitImg = ImageMaster.P_LOOP;
                break;
            case PLAY_DAILY:
                this.header = MenuPanelScreen.TEXT[3];
                this.description = MenuPanelScreen.TEXT[5];
                this.portraitImg = ImageMaster.P_DAILY;
                if (this.pColor == MainMenuPanelButton.PanelColor.GRAY)
                {
                    this.panelImg = ImageMaster.MENU_PANEL_BG_GRAY;
                }
                else
                {
                    this.panelImg = ImageMaster.MENU_PANEL_BG_BLUE;
                }
                break;
            case PLAY_NORMAL:
                this.header = MenuPanelScreen.TEXT[0];
                this.description = MenuPanelScreen.TEXT[2];
                this.portraitImg = ImageMaster.P_STANDARD;
                break;
            case INFO_CARD:
                this.header = MenuPanelScreen.TEXT[9];
                this.description = MenuPanelScreen.TEXT[11];
                this.portraitImg = ImageMaster.P_INFO_CARD;
                break;
            case INFO_RELIC:
                this.header = MenuPanelScreen.TEXT[12];
                this.description = MenuPanelScreen.TEXT[14];
                this.portraitImg = ImageMaster.P_INFO_RELIC;
                this.panelImg = ImageMaster.MENU_PANEL_BG_BLUE;
                break;
            case INFO_POTION:
                this.header = MenuPanelScreen.TEXT[43];
                this.description = MenuPanelScreen.TEXT[44];
                this.portraitImg = ImageMaster.P_INFO_POTION;
                this.panelImg = ImageMaster.MENU_PANEL_BG_RED;
                break;
            case STAT_CHAR:
                this.header = MenuPanelScreen.TEXT[18];
                this.description = MenuPanelScreen.TEXT[20];
                this.portraitImg = ImageMaster.P_STAT_CHAR;
                break;
            case STAT_HISTORY:
                this.header = MenuPanelScreen.TEXT[24];
                this.description = MenuPanelScreen.TEXT[26];
                this.portraitImg = ImageMaster.P_STAT_HISTORY;
                this.panelImg = ImageMaster.MENU_PANEL_BG_RED;
                break;
            case STAT_LEADERBOARDS:
                this.header = MenuPanelScreen.TEXT[21];
                this.description = MenuPanelScreen.TEXT[23];
                this.portraitImg = ImageMaster.P_STAT_LEADERBOARD;
                this.panelImg = ImageMaster.MENU_PANEL_BG_BLUE;
                break;
            case SETTINGS_CREDITS:
                this.header = MenuPanelScreen.TEXT[33];
                this.description = MenuPanelScreen.TEXT[35];
                this.portraitImg = ImageMaster.P_SETTING_CREDITS;
                this.panelImg = ImageMaster.MENU_PANEL_BG_RED;
                break;
            case SETTINGS_GAME:
                this.header = MenuPanelScreen.TEXT[27];
                if (!Settings.isConsoleBuild)
                {
                    this.description = MenuPanelScreen.TEXT[29];
                }
                else
                {
                    this.description = MenuPanelScreen.TEXT[42];
                }

                this.portraitImg = ImageMaster.P_SETTING_GAME;
                break;
            case SETTINGS_INPUT:
                this.header = MenuPanelScreen.TEXT[30];
                if (!Settings.isConsoleBuild)
                {
                    this.description = MenuPanelScreen.TEXT[32];
                }
                else
                {
                    this.description = MenuPanelScreen.TEXT[41];
                }

                this.portraitImg = ImageMaster.P_SETTING_INPUT;
                this.panelImg = ImageMaster.MENU_PANEL_BG_BLUE;
        }

    }

    public void update()
    {
        if (this.pColor != MainMenuPanelButton.PanelColor.GRAY)
        {
            this.hb.update();
        }

        if (this.hb.justHovered)
        {
            CardCrawlGame.sound.playV("UI_HOVER", 0.5F);
        }

        if (this.hb.hovered)
        {
            this.uiScale = MathHelper.fadeLerpSnap(this.uiScale, 1.025F);
            if (InputHelper.justClickedLeft)
            {
                this.hb.clickStarted = true;
            }
        }
        else
        {
            this.uiScale = MathHelper.cardScaleLerpSnap(this.uiScale, 1.0F);
        }

        if (this.hb.hovered && CInputActionSet.select.isJustPressed())
        {
            this.hb.clicked = true;
        }

        if (this.hb.clicked)
        {
            this.hb.clicked = false;
            CardCrawlGame.sound.play("DECK_OPEN");
            CardCrawlGame.mainMenuScreen.panelScreen.hide();
            this.buttonEffect();
        }

        this.animatePanelIn();
    }

    public void render(SpriteBatch sb)
    {
        if (this.oColor != null)
        {
            PCLRenderHelpers.drawColorized(sb, this.oColor, this::renderPanel);
        }
        else
        {
            sb.setColor(this.wColor);
            renderPanel(sb);
        }

        if (this.hb.hovered)
        {
            sb.setColor(new Color(1.0F, 1.0F, 1.0F, (this.uiScale - 1.0F) * 16.0F));
            sb.setBlendFunction(770, 1);
            sb.draw(ImageMaster.MENU_PANEL_BG_BLUE, this.hb.cX - 256.0F, this.hb.cY + this.yMod - (h / 2), (w / 2), (h / 2), w, h, this.uiScale * Settings.scale, this.uiScale * Settings.scale, 0.0F, 0, 0, 512, 800, false, false);
            sb.setBlendFunction(770, 771);
        }

        if (this.pColor == MainMenuPanelButton.PanelColor.GRAY)
        {
            sb.setColor(this.grColor);
        }
        else
        {
            sb.setColor(this.wColor);
        }

        sb.draw(this.portraitImg, this.hb.cX - 158.5F, this.hb.cY + this.yMod - 103.0F + 140.0F * Settings.scale, 158.5F, 103.0F, 317.0F, 206.0F, Settings.scale, Settings.scale, 0.0F, 0, 0, 317, 206, false, false);
        if (this.pColor == MainMenuPanelButton.PanelColor.GRAY)
        {
            sb.setColor(this.wColor);
            sb.draw(ImageMaster.P_LOCK, this.hb.cX - 158.5F, this.hb.cY + this.yMod - 103.0F + 140.0F * Settings.scale, 158.5F, 103.0F, 317.0F, 206.0F, Settings.scale, Settings.scale, 0.0F, 0, 0, 317, 206, false, false);
        }

        sb.draw(ImageMaster.MENU_PANEL_FRAME, this.hb.cX - (w / 2), this.hb.cY + this.yMod - (h / 2), (w / 2), (h / 2), w, h, Settings.scale, Settings.scale, 0.0F, 0, 0, 512, 800, false, false);
        if (FontHelper.getWidth(FontHelper.damageNumberFont, this.header, 0.8F) > 310.0F * Settings.scale)
        {
            FontHelper.renderFontCenteredHeight(sb, FontHelper.damageNumberFont, this.header, this.hb.cX - 138.0F * Settings.scale, this.hb.cY + this.yMod + 294.0F * Settings.scale, 280.0F * Settings.scale, this.gColor, 0.7F);
        }
        else
        {
            FontHelper.renderFontCenteredHeight(sb, FontHelper.damageNumberFont, this.header, this.hb.cX - 153.0F * Settings.scale, this.hb.cY + this.yMod + 294.0F * Settings.scale, 310.0F * Settings.scale, this.gColor, 0.8F);
        }

        FontHelper.renderFontCenteredHeight(sb, FontHelper.charDescFont, this.description, this.hb.cX - 153.0F * Settings.scale, this.hb.cY + this.yMod - 130.0F * Settings.scale, 330.0F * Settings.scale, this.cColor);
        this.hb.render(sb);
    }
}
