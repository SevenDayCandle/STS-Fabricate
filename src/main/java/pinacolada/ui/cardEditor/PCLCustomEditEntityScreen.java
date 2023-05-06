package pinacolada.ui.cardEditor;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.MathUtils;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.screens.select.GridCardSelectScreen;
import extendedui.EUIUtils;
import extendedui.interfaces.delegates.ActionT0;
import extendedui.interfaces.delegates.ActionT1;
import extendedui.ui.controls.EUIButton;
import extendedui.ui.hitboxes.EUIHitbox;
import extendedui.ui.tooltips.EUITooltip;
import extendedui.utilities.EUIFontHelper;
import pinacolada.effects.PCLEffectWithCallback;
import pinacolada.interfaces.markers.EditorMaker;
import pinacolada.misc.PCLCustomEditorLoadable;
import pinacolada.resources.PGR;
import pinacolada.skills.PSkill;
import pinacolada.skills.skills.PTrigger;
import pinacolada.skills.skills.base.primary.PTrigger_When;

import java.util.ArrayList;

import static extendedui.ui.AbstractScreen.createHexagonalButton;
import static pinacolada.ui.cardEditor.PCLCustomEffectPage.MENU_HEIGHT;
import static pinacolada.ui.cardEditor.PCLCustomEffectPage.MENU_WIDTH;

public abstract class PCLCustomEditEntityScreen<T extends PCLCustomEditorLoadable<U>, U extends EditorMaker> extends PCLEffectWithCallback<Object> {
    protected static final float BUTTON_HEIGHT = Settings.HEIGHT * (0.055f);
    protected static final float CARD_X = Settings.WIDTH * 0.11f;
    protected static final float CARD_Y = Settings.HEIGHT * 0.76f;
    protected static final float START_X = Settings.WIDTH * (0.24f);
    protected static final float START_Y = Settings.HEIGHT * (0.93f);
    protected static final float LABEL_HEIGHT = Settings.HEIGHT * (0.04f);
    protected static final float BUTTON_WIDTH = Settings.WIDTH * (0.16f);
    protected static final float LABEL_WIDTH = Settings.WIDTH * (0.20f);
    protected static final float BUTTON_CY = BUTTON_HEIGHT * 1.5f;
    public static final int EFFECT_COUNT = 2;

    protected ArrayList<PSkill<?>> currentEffects = new ArrayList<>();
    protected ArrayList<PTrigger> currentPowers = new ArrayList<>();
    protected ArrayList<EUIButton> pageButtons = new ArrayList<>();
    protected ArrayList<PCLCustomGenericPage> pages = new ArrayList<>();
    protected ArrayList<PCLCustomEffectPage> effectPages = new ArrayList<>();
    protected ArrayList<PCLCustomPowerEffectPage> powerPages = new ArrayList<>();
    protected EUIButton cancelButton;
    protected EUIButton saveButton;
    protected EUIButton undoButton;
    protected ActionT0 onSave;
    public ArrayList<U> prevBuilders;
    public ArrayList<U> tempBuilders;
    protected final T currentSlot;
    protected final boolean fromInGame;
    public int currentBuilder;
    protected int currentPage;

    public PCLCustomEditEntityScreen(T slot) {
        this(slot, false);
    }

    public PCLCustomEditEntityScreen(T currentSlot, boolean fromInGame) {
        this.currentSlot = currentSlot;
        this.fromInGame = fromInGame;
        tempBuilders = EUIUtils.map(currentSlot.builders, EditorMaker::makeCopy);

        preInitialize(currentSlot);
        setupPages();
    }

    public void preInitialize(T currentSlot)
    {
        cancelButton = createHexagonalButton(0, 0, BUTTON_WIDTH, BUTTON_HEIGHT)
                .setPosition(BUTTON_WIDTH * 0.6f, BUTTON_CY)
                .setColor(Color.FIREBRICK)
                .setText(GridCardSelectScreen.TEXT[1])
                .setFont(EUIFontHelper.buttonFont, 0.85f)
                .setOnClick(this::end);

        saveButton = createHexagonalButton(0, 0, BUTTON_WIDTH, BUTTON_HEIGHT)
                .setPosition(cancelButton.hb.cX, cancelButton.hb.y + cancelButton.hb.height + LABEL_HEIGHT * 0.8f)
                .setColor(Color.FOREST)
                .setText(GridCardSelectScreen.TEXT[0])
                .setFont(EUIFontHelper.buttonFont, 0.85f)
                .setOnClick(this::save);

        undoButton = createHexagonalButton(0, 0, BUTTON_WIDTH, BUTTON_HEIGHT)
                .setPosition(cancelButton.hb.cX, saveButton.hb.y + saveButton.hb.height + LABEL_HEIGHT * 0.8f)
                .setColor(Color.WHITE)
                .setText(PGR.core.strings.cedit_undo)
                .setFont(EUIFontHelper.buttonFont, 0.85f)
                .setOnClick(this::undo);
    }

    public PCLCustomEditEntityScreen<T, U> setOnSave(ActionT0 onSave) {
        this.onSave = onSave;

        return this;
    }

    public void addBuilder() {
        tempBuilders.add(getBuilder().makeCopy());
        setCurrentBuilder(tempBuilders.size() - 1);
    }

    public U getBuilder() {
        return tempBuilders.get(currentBuilder);
    }

    public int getPowerCount() {
        return powerPages.size();
    }

    public void modifyAllBuilders(ActionT1<U> updateFunc) {
        prevBuilders = EUIUtils.map(tempBuilders, EditorMaker::makeCopy);
        for (U b : tempBuilders) {
            updateFunc.invoke(b);
        }
        rebuildItem();
    }

    public void modifyBuilder(ActionT1<U> updateFunc) {
        prevBuilders = EUIUtils.map(tempBuilders, EditorMaker::makeCopy);
        updateFunc.invoke(getBuilder());
        rebuildItem();
    }

    public void openPageAtIndex(int index) {
        currentPage = index;
        for (int j = 0; j < pageButtons.size(); j++) {
            pageButtons.get(j).setColor(j == index ? Color.WHITE : Color.GRAY);
        }
    }

    public void setCurrentBuilder(int index) {
        currentBuilder = MathUtils.clamp(index, 0, tempBuilders.size() - 1);
        modifyBuilder(__ -> {
        });
        setupPages();
        updateVariant();
    }

    public void refreshPages() {
        for (PCLCustomGenericPage b : pages) {
            b.refresh();
        }
    }

    public void removeBuilder() {
        tempBuilders.remove(getBuilder());
        setCurrentBuilder(currentBuilder);
    }

    protected void undo() {
        if (prevBuilders != null) {
            ArrayList<U> backups = EUIUtils.map(prevBuilders, EditorMaker::makeCopy);
            currentBuilder = MathUtils.clamp(currentBuilder, 0, backups.size() - 1);
            updateVariant();
            modifyBuilder(__ -> {
                tempBuilders = backups;
            });
            refreshPages();
        }
    }

    protected void save() {
        currentSlot.builders = tempBuilders;
        if (this.onSave != null) {
            this.onSave.invoke();
        }
        end();
    }

    protected void end() {
        complete(null);
    }

    protected void updateVariant() {

    }

    protected void clearPages()
    {
        currentEffects.clear();
        currentEffects.addAll(getBuilder().getMoves());
        while (currentEffects.size() < EFFECT_COUNT) {
            currentEffects.add(null);
        }
        currentPowers.clear();
        currentPowers.addAll(getBuilder().getPowers());
        while (currentPowers.size() < EFFECT_COUNT) {
            currentPowers.add(null);
        }

        pages.clear();
        effectPages.clear();
        powerPages.clear();
        pageButtons.clear();
    }

    protected void addSkillPages()
    {
        for (int i = 0; i < currentEffects.size(); i++) {
            int finalI = i;
            PCLCustomEffectPage page = new PCLCustomEffectPage(this, new EUIHitbox(START_X, START_Y, MENU_WIDTH, MENU_HEIGHT), i
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
            PCLCustomPowerEffectPage page = new PCLCustomPowerEffectPage(this, new EUIHitbox(START_X, START_Y, MENU_WIDTH, MENU_HEIGHT), i
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
    }

    protected void setupPageButtons()
    {
        for (int i = 0; i < pages.size(); i++) {
            PCLCustomGenericPage pg = pages.get(i);
            pageButtons.add(new EUIButton(pg.getTextureCache().texture(), new EUIHitbox(0, 0, BUTTON_HEIGHT, BUTTON_HEIGHT))
                    .setPosition(Settings.WIDTH * (0.45f) + ((i - 1f) * BUTTON_HEIGHT), (BUTTON_HEIGHT * 0.85f))
                    .setColor(i == 0 ? Color.WHITE : Color.GRAY)
                    .setOnClick(i, this::openPageAtIndex)
                    .setTooltip(getPageTooltip(pg)));
        }
        modifyBuilder(__ -> {});
    }

    protected EUITooltip getPageTooltip(PCLCustomGenericPage page)
    {
        return new EUITooltip(page.getTitle(), "");
    }

    protected void setupPages()
    {
        clearPages();
        addSkillPages();
        setupPageButtons();
    }

    public void updateInnerElements()
    {
        cancelButton.tryUpdate();
        saveButton.tryUpdate();
        undoButton.tryUpdate();
        pages.get(currentPage).tryUpdate();
        for (EUIButton b : pageButtons) {
            b.tryUpdate();
        }
    }

    public void renderInnerElements(SpriteBatch sb)
    {
        cancelButton.tryRender(sb);
        saveButton.tryRender(sb);
        undoButton.tryRender(sb);
        pages.get(currentPage).tryRender(sb);
        for (EUIButton b : pageButtons) {
            b.tryRender(sb);
        }
    }

    @Override
    public void render(SpriteBatch sb) {
        renderInnerElements(sb);
    }

    @Override
    protected void updateInternal(float deltaTime) {
        updateInnerElements();
    }

    abstract protected void rebuildItem();
}
