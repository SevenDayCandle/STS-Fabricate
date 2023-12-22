package pinacolada.ui.editor;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.helpers.FontHelper;
import com.megacrit.cardcrawl.helpers.ImageMaster;
import com.megacrit.cardcrawl.localization.CardStrings;
import com.megacrit.cardcrawl.screens.leaderboards.LeaderboardScreen;
import com.megacrit.cardcrawl.screens.options.OptionsPanel;
import com.megacrit.cardcrawl.screens.select.GridCardSelectScreen;
import extendedui.EUIRM;
import extendedui.EUIRenderHelpers;
import extendedui.EUIUtils;
import extendedui.text.EUITextHelper;
import extendedui.ui.controls.*;
import extendedui.ui.hitboxes.EUIHitbox;
import extendedui.ui.hitboxes.RelativeHitbox;
import extendedui.utilities.EUIFontHelper;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.mutable.MutableInt;
import pinacolada.cards.base.PCLCard;
import pinacolada.cards.base.PCLCustomCardSlot;
import pinacolada.cards.base.PCLDynamicCardData;
import pinacolada.cards.base.fields.PCLCardTarget;
import pinacolada.interfaces.markers.EditorMaker;
import pinacolada.misc.LoadoutStrings;
import pinacolada.resources.PGR;
import pinacolada.resources.loadout.PCLCustomLoadout;
import pinacolada.resources.loadout.PCLCustomLoadoutInfo;
import pinacolada.skills.PSkill;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.StringJoiner;

import static pinacolada.skills.PSkill.CASCADE_CHAR;
import static pinacolada.skills.PSkill.CHAR_OFFSET;

public class PCLCustomDescriptionDialog extends EUIDialog<PCLCustomDescriptionDialog> {
    protected EUITextBoxInput textInput;
    protected EUITextBox preview;
    protected EUISearchableDropdown<Settings.GameLanguage> languageDropdown;
    protected Settings.GameLanguage activeLanguage = Settings.language;
    protected PSkill<?> skillAt;
    protected String textList = EUIUtils.EMPTY_STRING;
    protected int index;
    public HashMap<Settings.GameLanguage, String[]> currentLanguageMap;

    public PCLCustomDescriptionDialog(String headerText) {
        this(headerText, "");
    }

    public PCLCustomDescriptionDialog(String headerText, String descriptionText) {
        this(headerText, descriptionText, scale(500), scale(600));
    }

    public PCLCustomDescriptionDialog(String headerText, String descriptionText, float w, float h) {
        this(new EUIHitbox(Settings.WIDTH / 2.0F - w / 2f, Settings.HEIGHT / 2.0F - h / 2f, w, h), ImageMaster.OPTION_CONFIRM, headerText, descriptionText);
    }

    public PCLCustomDescriptionDialog(EUIHitbox hb, Texture backgroundTexture, String headerText, String descriptionText) {
        super(hb, backgroundTexture, headerText, descriptionText);
        textInput = (EUITextBoxInput) new EUITextBoxInput(EUIRM.images.darkSquare.texture(),
                new EUIHitbox(hb.x + hb.width * 0.23f, hb.y + hb.height * 0.29f, hb.width * 0.54f, scale(250)))
                .setOnComplete(this::updateText)
                .setColors(new Color(0, 0, 0, 0.85f), Settings.CREAM_COLOR)
                .setAlignment(0.9f, 0.1f, false)
                .setFont(EUIFontHelper.cardTitleFontNormal, 0.7f);
        preview = new EUITextBox(EUIRM.images.greySquare.texture(), new EUIHitbox(Settings.WIDTH * 0.08f, textInput.hb.y, Settings.scale * 256f, Settings.scale * 256f))
                .setColors(Color.DARK_GRAY, Settings.CREAM_COLOR)
                .setFont(EUIFontHelper.cardTipBodyFont, 1f);
        preview.label.setSmartText(true);
        languageDropdown = (EUISearchableDropdown<Settings.GameLanguage>) new EUISearchableDropdown<Settings.GameLanguage>(new EUIHitbox(hb.x + hb.width / 4, textInput.hb.y + textInput.hb.height + scale(15), scale(95), scale(32))
                , item -> StringUtils.capitalize(item.toString().toLowerCase()))
                .setOnChange(languages -> {
                    if (!languages.isEmpty()) {
                        this.updateLanguage(languages.get(0));
                    }
                })
                .setHeader(EUIFontHelper.cardTitleFontSmall, 0.8f, Settings.GOLD_COLOR, OptionsPanel.TEXT[13].replace(":", ""))
                .setItems(Settings.GameLanguage.values())
                .setCanAutosizeButton(true)
                .setSelection(activeLanguage, false)
                .setTooltip(LeaderboardScreen.TEXT[7], PGR.core.strings.cetut_nameLanguage);
        textInput.label.setWrap(true);
    }

    protected EUIButton getCancelButton() {
        return new EUIButton(ImageMaster.OPTION_NO,
                new RelativeHitbox(hb, scale(135), scale(70), hb.width * 0.85f, hb.height * 0.15f))
                .setLabel(EUIFontHelper.cardTitleFontNormal, 0.8f, GridCardSelectScreen.TEXT[1])
                .setOnClick(() -> {
                    if (onComplete != null) {
                        onComplete.invoke(getCancelValue());
                    }
                });
    }

    @Override
    public PCLCustomDescriptionDialog getCancelValue() {
        return null;
    }

    protected EUIButton getConfirmButton() {
        return new EUIButton(ImageMaster.OPTION_YES,
                new RelativeHitbox(hb, scale(135), scale(70), hb.width * 0.15f, hb.height * 0.15f))
                .setLabel(EUIFontHelper.cardTitleFontNormal, 0.8f, GridCardSelectScreen.TEXT[0])
                .setOnClick(() -> {
                    if (onComplete != null) {
                        onComplete.invoke(getConfirmValue());
                    }
                });
    }

    @Override
    public PCLCustomDescriptionDialog getConfirmValue() {
        return this;
    }

    @Override
    protected EUILabel getHeader(String headerText) {
        return new EUILabel(EUIFontHelper.buttonFont,
                new RelativeHitbox(hb, hb.width, hb.height, hb.width * 0.5f, hb.height * 0.89f))
                .setAlignment(0.5f, 0.5f, false)
                .setLabel(headerText);
    }

    protected String[] getStringsForLanguage(Settings.GameLanguage language) {
        return currentLanguageMap.getOrDefault(language,
                currentLanguageMap.getOrDefault(Settings.GameLanguage.ENG,
                        !currentLanguageMap.isEmpty() ? currentLanguageMap.entrySet().iterator().next().getValue() : new String[]{}));
    }

    public void open(EditorMaker<?,?> data, int index) {
        setActive(true);
        currentLanguageMap = data.createDescMap();
        this.index = index;

        skillAt = data.getEffectAtIndex(index);
        if (skillAt != null) {
            StringJoiner sb = new StringJoiner(EUIUtils.SPLIT_LINE);
            MutableInt mint = new MutableInt(0);
            skillAt.recurse(sk -> {
                String res = sk.getSubText(PCLCardTarget.Self, null);
                sb.add(mint + ": " + StringUtils.capitalize(StringUtils.isEmpty(res) ? sk.getSampleText(null, null) : res));
                mint.add(1);
            });
            textList = sb.toString();
        }
        else {
            textList = EUIUtils.EMPTY_STRING;
        }

        updateLanguage(activeLanguage);
    }

    @Override
    public void renderImpl(SpriteBatch sb) {
        super.renderImpl(sb);
        EUITextHelper.renderFont(sb, EUIFontHelper.cardTooltipTitleFontNormal, PGR.core.strings.cedit_preview, this.preview.hb.x, this.preview.hb.y + this.preview.hb.height * 1.1f, Settings.GOLD_COLOR);
        this.preview.renderImpl(sb);
        this.textInput.tryRender(sb);
        this.languageDropdown.tryRender(sb);
        // Must render this text manually without smart text in order to properly display these symbols
        float x = Settings.WIDTH * 0.665f;
        EUITextHelper.renderFont(sb, EUIFontHelper.cardDescriptionFontNormal, PGR.core.strings.cetut_legendDesc, x, Settings.HEIGHT * 0.8f, Settings.GOLD_COLOR);
        EUITextHelper.renderFont(sb, EUIFontHelper.cardDescriptionFontNormal, PGR.core.strings.cedit_effects, x, Settings.HEIGHT * 0.48f, Settings.GOLD_COLOR);
        EUIFontHelper.cardDescriptionFontNormal.getData().setScale(0.75f);
        EUITextHelper.renderFont(sb, EUIFontHelper.cardDescriptionFontNormal, PGR.core.strings.cetut_legendLineBreak, x, Settings.HEIGHT * 0.75f, Color.WHITE);
        EUITextHelper.renderFont(sb, EUIFontHelper.cardDescriptionFontNormal, PGR.core.strings.cetut_legendColored, x, Settings.HEIGHT * 0.73f, Color.WHITE);
        EUITextHelper.renderFont(sb, EUIFontHelper.cardDescriptionFontNormal, PGR.core.strings.cetut_legendDamage, x, Settings.HEIGHT * 0.71f, Color.WHITE);
        EUITextHelper.renderFont(sb, EUIFontHelper.cardDescriptionFontNormal, PGR.core.strings.cetut_legendBlock, x, Settings.HEIGHT * 0.69f, Color.WHITE);
        EUITextHelper.renderFont(sb, EUIFontHelper.cardDescriptionFontNormal, PGR.core.strings.cetut_legendIcon, x, Settings.HEIGHT * 0.67f, Color.WHITE);
        EUITextHelper.renderFont(sb, EUIFontHelper.cardDescriptionFontNormal, PGR.core.strings.cetut_legendAmount, x, Settings.HEIGHT * 0.65f, Color.WHITE);
        EUITextHelper.renderFont(sb, EUIFontHelper.cardDescriptionFontNormal, PGR.core.strings.cetut_legendExtra, x, Settings.HEIGHT * 0.63f, Color.WHITE);
        EUITextHelper.renderFont(sb, EUIFontHelper.cardDescriptionFontNormal, PGR.core.strings.cetut_legendExtra2, x, Settings.HEIGHT * 0.61f, Color.WHITE);
        EUITextHelper.renderFont(sb, EUIFontHelper.cardDescriptionFontNormal, PGR.core.strings.cetut_legendScope, x, Settings.HEIGHT * 0.59f, Color.WHITE);
        EUITextHelper.renderSmart(sb, EUIFontHelper.cardDescriptionFontNormal, textList, x, Settings.HEIGHT * 0.43f, Settings.WIDTH * 0.25f, Color.WHITE);
        EUIRenderHelpers.resetFont(EUIFontHelper.cardDescriptionFontNormal);
    }

    @Override
    public void updateImpl() {
        super.updateImpl();
        this.preview.updateImpl();
        this.textInput.tryUpdate();
        this.languageDropdown.tryUpdate();
    }

    private void updateLanguage(Settings.GameLanguage language) {
        activeLanguage = language;
        textInput.setFont(language == Settings.language ? EUIFontHelper.cardTitleFontNormal : EUIFontHelper.createBoldFont(language, true, 27.0F, 2f, PCLCard.CARD_TYPE_COLOR, 3f, PCLCard.SHADOW_COLOR), 0.7f);
        String[] strings = getStringsForLanguage(activeLanguage);
        if (strings.length <= index) {
            textInput.setLabel(EUIUtils.EMPTY_STRING);
        }
        else {
            textInput.setLabel(strings[index] != null ? strings[index] : EUIUtils.EMPTY_STRING);
        }
        updatePreview(textInput.label.text);
    }

    private void updatePreview(String overrideDesc) {
        if (skillAt == null) {
            preview.setLabel(overrideDesc);
            return;
        }

        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < overrideDesc.length(); i++) {
            char c = overrideDesc.charAt(i);
            if (c == CASCADE_CHAR) {
                if (EUIRenderHelpers.isCharAt(overrideDesc, i + 3, CASCADE_CHAR)) {
                    PSkill<?> move = skillAt.getEffectAtIndex(overrideDesc.charAt(i + 2) - CHAR_OFFSET).v1;
                    if (move != null) {
                        sb.append(move.getRawString(overrideDesc.charAt(i + 1)));
                    }
                    i += 3;
                }
            }
            else {
                sb.append(c);
            }
        }

        preview.setLabel(sb.toString());
    }

    private void updateText(String name) {
        String[] strings = getStringsForLanguage(activeLanguage);
        if (strings.length <= index) {
            strings = Arrays.copyOf(strings, index + 1);
        }
        strings[index] = name;
        currentLanguageMap.put(activeLanguage, strings);
        updatePreview(name);
    }
}
