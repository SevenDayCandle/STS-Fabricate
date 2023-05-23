package pinacolada.ui.editor;

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

import static extendedui.ui.controls.EUIButton.createHexagonalButton;
import static pinacolada.ui.editor.PCLCustomEffectPage.MENU_HEIGHT;
import static pinacolada.ui.editor.PCLCustomEffectPage.MENU_WIDTH;

public abstract class PCLCustomEditEntityScreen<T extends PCLCustomEditorLoadable<U>, U extends EditorMaker> extends PCLEffectWithCallback<Object> {
    public static final float BUTTON_HEIGHT = Settings.HEIGHT * (0.055f);
    public static final float CARD_X = Settings.WIDTH * 0.11f;
    public static final float CARD_Y = Settings.HEIGHT * 0.76f;
    public static final float START_X = Settings.WIDTH * (0.24f);
    public static final float START_Y = Settings.HEIGHT * (0.93f);
    public static final float LABEL_HEIGHT = Settings.HEIGHT * (0.04f);
    public static final float BUTTON_WIDTH = Settings.WIDTH * (0.16f);
    public static final float LABEL_WIDTH = Settings.WIDTH * (0.20f);
    public static final float BUTTON_CY = BUTTON_HEIGHT * 1.5f;
    public static final int EFFECT_COUNT = 3;
    public final T currentSlot;
    public ArrayList<PSkill<?>> currentEffects = new ArrayList<>();
    public ArrayList<PTrigger> currentPowers = new ArrayList<>();
    public ArrayList<EUIButton> pageButtons = new ArrayList<>();
    public ArrayList<PCLCustomGenericPage> pages = new ArrayList<>();
    public ArrayList<PCLCustomEffectPage> effectPages = new ArrayList<>();
    public ArrayList<PCLCustomPowerEffectPage> powerPages = new ArrayList<>();
    public EUIButton cancelButton;
    public EUIButton saveButton;
    public EUIButton undoButton;
    public int currentPage;
    public final boolean fromInGame;
    public ArrayList<U> prevBuilders;
    public ArrayList<U> tempBuilders;
    public int currentBuilder;
    protected ActionT0 onSave;

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

    public void addBuilder() {
        tempBuilders.add(getBuilder().makeCopy());
        setCurrentBuilder(tempBuilders.size() - 1);
    }

    protected void addSkillPages() {
        for (int i = 0; i < currentEffects.size(); i++) {
            makeEffectPage(i);
        }
        for (int i = 0; i < currentPowers.size(); i++) {
            makePowerPage(i);
        }
    }

    protected void makeEffectPage(int index) {
        PCLCustomEffectPage page = new PCLCustomEffectPage(this, new EUIHitbox(START_X, START_Y, MENU_WIDTH, MENU_HEIGHT), index
                , EUIUtils.format(PGR.core.strings.cedit_effectX, index + 1), (be) -> updateEffect(index, be));
        pages.add(page);
        effectPages.add(page);
        page.refresh();
    }

    protected void updateEffect(int index, PSkill<?> be) {
        currentEffects.set(index, be);
        modifyBuilder(e -> e.setPSkill(currentEffects, true, true));
    }

    protected void makePowerPage(int index) {
        PCLCustomPowerEffectPage page = new PCLCustomPowerEffectPage(this, new EUIHitbox(START_X, START_Y, MENU_WIDTH, MENU_HEIGHT), index
                , EUIUtils.format(PGR.core.strings.cedit_powerX, index + 1), (be) -> updatePowerEffect(index, be));
        pages.add(page);
        powerPages.add(page);
        page.refresh();
    }

    protected void updatePowerEffect(int index, PSkill<?> be) {
        if (be instanceof PTrigger) {
            currentPowers.set(index, (PTrigger) be);
        }
        else {
            currentPowers.set(index, new PTrigger_When());
        }
        modifyBuilder(e -> e.setPPower(currentPowers, true, true));
    }

    protected void clearPages() {
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

    protected void end() {
        complete(null);
    }

    public U getBuilder() {
        return tempBuilders.get(currentBuilder);
    }

    protected EUITooltip getPageTooltip(PCLCustomGenericPage page) {
        return new EUITooltip(page.getTitle(), "");
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

    public void preInitialize(T currentSlot) {
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

    abstract protected void rebuildItem();

    public void refreshPages() {
        for (PCLCustomGenericPage b : pages) {
            b.refresh();
        }
    }

    public void removeBuilder() {
        tempBuilders.remove(getBuilder());
        setCurrentBuilder(currentBuilder);
    }

    @Override
    public void render(SpriteBatch sb) {
        renderInnerElements(sb);
    }

    @Override
    protected void updateInternal(float deltaTime) {
        updateInnerElements();
    }

    public void renderInnerElements(SpriteBatch sb) {
        cancelButton.tryRender(sb);
        saveButton.tryRender(sb);
        undoButton.tryRender(sb);
        pages.get(currentPage).tryRender(sb);
        for (EUIButton b : pageButtons) {
            b.tryRender(sb);
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
        updateVariant();
    }

    public PCLCustomEditEntityScreen<T, U> setOnSave(ActionT0 onSave) {
        this.onSave = onSave;

        return this;
    }

    protected void setupPageButtons() {
        float offset = Settings.WIDTH * 0.5f - (pages.size() / 2f * BUTTON_HEIGHT);
        for (int i = 0; i < pages.size(); i++) {
            PCLCustomGenericPage pg = pages.get(i);
            pageButtons.add(new EUIButton(pg.getTextureCache().texture(), new EUIHitbox(0, 0, BUTTON_HEIGHT, BUTTON_HEIGHT))
                    .setPosition(offset + (i * BUTTON_HEIGHT), (BUTTON_HEIGHT * 0.85f))
                    .setColor(i == 0 ? Color.WHITE : Color.GRAY)
                    .setOnClick(i, this::openPageAtIndex)
                    .setTooltip(getPageTooltip(pg)));
        }
        modifyBuilder(__ -> {
        });
    }

    protected void setupPages() {
        clearPages();
        addSkillPages();
        setupPageButtons();
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

    public void updateInnerElements() {
        cancelButton.tryUpdate();
        saveButton.tryUpdate();
        undoButton.tryUpdate();
        pages.get(currentPage).tryUpdate();
        for (EUIButton b : pageButtons) {
            b.tryUpdate();
        }
    }

    protected void updateVariant() {

    }
}
