package pinacolada.ui.editor;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.helpers.ImageMaster;
import extendedui.EUIRM;
import extendedui.EUIUtils;
import extendedui.ui.EUIBase;
import extendedui.ui.controls.EUIButton;
import extendedui.ui.controls.EUIDropdown;
import extendedui.ui.controls.EUILabel;
import extendedui.ui.hitboxes.EUIHitbox;
import extendedui.ui.hitboxes.RelativeHitbox;
import extendedui.utilities.EUIFontHelper;
import pinacolada.resources.PGR;

public class PCLCustomFormEditor extends EUIBase {
    protected static final float ICON_SIZE = scale(36f);
    protected PCLCustomEditEntityScreen<?, ?> screen;
    protected EUIDropdown<Integer> formValues;
    public EUIHitbox hb;
    public EUILabel header;
    public EUIButton decreaseButton;
    public EUIButton increaseButton;
    public EUIButton add;
    public EUIButton remove;

    public PCLCustomFormEditor(EUIHitbox hb, PCLCustomEditEntityScreen<?, ?> screen) {
        this.hb = hb;
        this.screen = screen;

        this.header = new EUILabel(EUIFontHelper.cardTitleFontSmall,
                new EUIHitbox(hb.x, hb.y + hb.height * 0.8f, hb.width, hb.height))
                .setAlignment(0.5f, 0.0f, false)
                .setFont(EUIFontHelper.cardTitleFontSmall, 0.8f).setColor(Settings.GOLD_COLOR)
                .setLabel(PGR.core.strings.cedit_editForm)
                .setTooltip(PGR.core.strings.cedit_editForm, PGR.core.strings.cetut_primaryForm);

        formValues = new EUIDropdown<Integer>(hb)
                .setOnChange(types -> {
                    if (types.size() > 0) {
                        setValue(types.get(0));
                    }
                })
                .setFontForButton(EUIFontHelper.cardTitleFontSmall, 0.9f);
        formValues.setLabelFunctionForButton((items, strFunc) -> {
                    return items.size() > 0 ? strFunc.invoke(items.get(0)) + "/" + (formValues.size() - 1) : String.valueOf(formValues.size());
                }, false)
                .setItems(EUIUtils.range(0, screen.tempBuilders.size() - 1));
        decreaseButton = new EUIButton(ImageMaster.CF_LEFT_ARROW, new RelativeHitbox(formValues.hb, ICON_SIZE, ICON_SIZE, ICON_SIZE * -0.4f, formValues.hb.height * 0.5f))
                .setOnClick(this::decrease);

        increaseButton = new EUIButton(ImageMaster.CF_RIGHT_ARROW, new RelativeHitbox(formValues.hb, ICON_SIZE, ICON_SIZE, formValues.hb.width + (ICON_SIZE * 0.4f), formValues.hb.height * 0.5f))
                .setOnClick(this::increase);

        add = new EUIButton(EUIRM.images.plus.texture(), new RelativeHitbox(hb, scale(48), scale(48), scale(162), scale(24)))
                .setOnClick(screen::addBuilder)
                .setClickDelay(0.02f)
                .setTooltip(PGR.core.strings.cedit_addForm, "");
        remove = new EUIButton(EUIRM.images.minus.texture(), new RelativeHitbox(hb, scale(48), scale(48), scale(212), scale(24)))
                .setOnClick(screen::removeBuilder)
                .setClickDelay(0.02f)
                .setTooltip(PGR.core.strings.cedit_removeForm, "");
        refresh();
    }

    public void decrease() {
        setValue(screen.currentBuilder - 1);
    }

    public void increase() {
        setValue(screen.currentBuilder + 1);
    }

    public void refresh() {
        formValues.setItems(EUIUtils.range(0, screen.tempBuilders.size() - 1)).setSelection(screen.currentBuilder, false);
        remove.setInteractable(screen.tempBuilders.size() > 1).setActive(!screen.fromInGame);
        add.setActive(!screen.fromInGame);
    }

    @Override
    public void renderImpl(SpriteBatch sb) {
        this.hb.render(sb);
        header.tryRender(sb);
        formValues.tryRender(sb);
        decreaseButton.tryRender(sb);
        increaseButton.tryRender(sb);
        add.tryRender(sb);
        remove.tryRender(sb);
    }

    @Override
    public void updateImpl() {
        this.hb.update();
        header.tryUpdate();
        formValues.tryUpdate();
        decreaseButton.setInteractable(screen.currentBuilder > 0).tryUpdate();
        increaseButton.setInteractable(screen.currentBuilder < formValues.size() - 1).tryUpdate();
        add.tryUpdate();
        remove.tryUpdate();
    }

    public void setValue(int value) {
        screen.setCurrentBuilder(value);
    }
}
