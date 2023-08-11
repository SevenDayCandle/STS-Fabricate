package pinacolada.patches.screens;

import basemod.ReflectionHacks;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.MathUtils;
import com.evacipated.cardcrawl.modthespire.lib.*;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.CardGroup;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.helpers.FontHelper;
import com.megacrit.cardcrawl.helpers.ImageMaster;
import com.megacrit.cardcrawl.screens.select.GridCardSelectScreen;
import com.megacrit.cardcrawl.ui.buttons.GridSelectConfirmButton;
import extendedui.EUIRenderHelpers;
import extendedui.EUIUtils;
import extendedui.ui.controls.EUIButton;
import extendedui.ui.hitboxes.EUIHitbox;
import javassist.CannotCompileException;
import javassist.CtBehavior;
import javassist.expr.ExprEditor;
import javassist.expr.MethodCall;
import pinacolada.augments.PCLAugment;
import pinacolada.cards.base.PCLCard;
import pinacolada.dungeon.GridCardSelectScreenHelper;

import java.util.ArrayList;
import java.util.List;

public class GridCardSelectScreenPatches {

    protected static final float ICON_SIZE = 64f * Settings.scale;
    protected static final int DEFAULT_MAX = 3;    protected static EUIButton downButton = new EUIButton(ImageMaster.UPGRADE_ARROW, new EUIHitbox(Settings.WIDTH * 0.75F, Settings.HEIGHT * 0.45F, ICON_SIZE, ICON_SIZE))
            .setColor(Color.PURPLE).setShaderMode(EUIRenderHelpers.ShaderMode.Colorize).setButtonRotation(-90)
            .setOnClick(GridCardSelectScreenPatches::addIndex);
    protected static final float[] Y_POSITIONS_2 = new float[]{
            Settings.HEIGHT * 0.75F - 50.0F * Settings.scale,
            Settings.HEIGHT * 0.25F + 50.0F * Settings.scale
    };    protected static EUIButton upButton = new EUIButton(ImageMaster.UPGRADE_ARROW, new EUIHitbox(Settings.WIDTH * 0.75F, Settings.HEIGHT * 0.55F, ICON_SIZE, ICON_SIZE))
            .setColor(Color.PURPLE).setShaderMode(EUIRenderHelpers.ShaderMode.Colorize).setButtonRotation(90)
            .setOnClick(GridCardSelectScreenPatches::subtractIndex);
    protected static final float[] Y_POSITIONS_3 = new float[]{
            Settings.HEIGHT * 0.75F + 25.0F * Settings.scale,
            Settings.HEIGHT * 0.5F,
            Settings.HEIGHT * 0.25F - 25.0F * Settings.scale
    };
    protected static int maxIndex = DEFAULT_MAX;
    protected static ArrayList<AbstractCard> cardList = new ArrayList<>();
    protected static boolean waitingForBranchUpgradeSelection = false;
    protected static int branchUpgradeForm = 0;
    protected static int minIndex = 0;
    public static PCLAugment augment;

    protected static void addIndex() {
        if (maxIndex < cardList.size() - 1) {
            minIndex += 1;
            maxIndex += 1;
            refreshButtons();
        }
    }

    public static void fillCardListWithUpgrades(PCLCard base) {
        cardList.clear();
        if (base.cardData.branchFactor <= 0) {
            for (int i = 0; i < base.getMaxForms(); i++) {
                cardList.add(base.makeUpgradePreview(i));
            }
        }
        else {
            int minForm = getFormMin(base);

            for (int i = minForm; i < Math.min(base.getMaxForms(), minForm + base.cardData.branchFactor); i++) {
                cardList.add(base.makeUpgradePreview(i));
            }
        }

        // If you ran out of forms, do not change the card form
        if (cardList.size() == 0) {
            cardList.add(base.makeUpgradePreview(base.getForm()));
        }
    }

    public static ArrayList<AbstractCard> getCardList() {
        return cardList;
    }

    /* Find the minimum possible upgrade form M at your current upgrade level and form
    Let B = branch factor, U = current upgrade, F = current form
    Let g(B,x) be the sum of the geometric series B^0 + ... + B^x if x >= 0, or 0 otherwise

    If B = 0, then this calculation is ignored and all forms are available
    If B = 1, M = U + 1
    Else, M = g(B,U + 1) + B * (F - g(B,U))

    See https://en.wikipedia.org/wiki/Geometric_series
    */
    public static int getFormMin(PCLCard base) {
        return base.cardData.branchFactor != 1 ?
                getFormSum(base.cardData.branchFactor, base.timesUpgraded + 1) + base.cardData.branchFactor * (base.getForm() - getFormSum(base.cardData.branchFactor, base.timesUpgraded))
                : base.timesUpgraded + 1;
    }

    /**
     * Number of possible forms with branch factor b and upgrading from upgrade level u can be expressed as geometric sum with a = 1
     * Return 1 if u < 0 (because we always start at the first form)
     */
    public static int getFormSum(int b, int u) {
        return u >= 0 ? (int) ((1 - Math.pow(b, u)) / (1 - b)) : 0;
    }

    public static AbstractCard getHoveredCard() {
        return ReflectionHacks.getPrivate(AbstractDungeon.gridSelectScreen, GridCardSelectScreen.class, "hoveredCard");
    }

    protected static List<AbstractCard> getShownBranchUpgrades() {
        return cardList.size() < 4 ? cardList : cardList.subList(minIndex, maxIndex);
    }

    protected static void refreshButtons() {
        boolean canUp = minIndex > 0;
        upButton.setInteractable(canUp).setColor(canUp ? Color.PURPLE : Color.GRAY);
        boolean canDown = maxIndex < cardList.size() - 1;
        downButton.setInteractable(canDown).setColor(canDown ? Color.PURPLE : Color.GRAY);
    }

    public static void renderPreviewCard(SpriteBatch sb, AbstractCard card, float unHoveredScale, float y) {
        card.drawScale = card.hb.hovered ? unHoveredScale + 0.1f : unHoveredScale;
        card.current_x = card.target_x = (float) Settings.WIDTH * 0.63F;
        card.current_y = card.target_y = y;
        card.render(sb);
        card.updateHoverLogic();
        card.renderCardTip(sb);
    }

    public static void selectPCLCardUpgrade(PCLCard card) {
        if (card.cardData.canToggleOnUpgrade) {
            branchUpgradeForm = card.auxiliaryData.form;
            if (cardList.size() > 1) {
                card.beginGlowing();
                cardList.forEach((c) -> {
                    if (c != card) {
                        c.stopGlowing();
                    }
                });
            }
        }
        waitingForBranchUpgradeSelection = false;
    }

    public static void setAugment(PCLAugment aug) {
        augment = aug;
    }

    protected static void subtractIndex() {
        if (minIndex > 0) {
            minIndex -= 1;
            maxIndex -= 1;
            refreshButtons();
        }
    }

    @SpirePatch(clz = GridCardSelectScreen.class, method = "calculateScrollBounds")
    public static class GridCardSelectScreen_CalculateScrollBounds {
        @SpirePrefixPatch
        public static SpireReturn<Void> prefix(GridCardSelectScreen __instance) {
            if (GridCardSelectScreenHelper.calculateScrollBounds(__instance)) {
                return SpireReturn.Return(null);
            }
            else {
                return SpireReturn.Continue();
            }
        }
    }

    @SpirePatch(clz = GridCardSelectScreen.class, method = "callOnOpen")
    public static class GridCardSelectScreen_CallOnOpen {
        @SpirePostfixPatch
        public static void postfix(GridCardSelectScreen __instance) {
            GridCardSelectScreenHelper.open(__instance);
        }
    }

    @SpirePatch(clz = GridCardSelectScreen.class, method = "updateCardPositionsAndHoverLogic")
    public static class GridCardSelectScreen_UpdateCardPositionsAndHoverLogic {
        @SpirePrefixPatch
        public static SpireReturn<Void> prefix(GridCardSelectScreen __instance) {
            if (GridCardSelectScreenHelper.updateCardPositionAndHover(__instance)) {
                return SpireReturn.Return(null);
            }
            else {
                return SpireReturn.Continue();
            }
        }
    }

    @SpirePatch(
            clz = GridCardSelectScreen.class,
            method = "update"
    )
    public static class GridUpdate {
        public GridUpdate() {
        }

        @SpireInsertPatch(
                locator = Locator.class
        )
        public static void insert(GridCardSelectScreen __instance) {
            AbstractCard hoveredCard = getHoveredCard();
            if (hoveredCard instanceof PCLCard) {
                ((PCLCard) hoveredCard).changeForm(branchUpgradeForm, hoveredCard.timesUpgraded);
                branchUpgradeForm = 0;
                cardList.clear();
            }

        }

        @SpireInsertPatch(
                locator = Locator2.class
        )
        public static void insert2(GridCardSelectScreen __instance) {
            if (__instance.anyNumber) {
                __instance.confirmButton.isDisabled = !GridCardSelectScreenHelper.isConditionMet();
            }
        }

        @SpireInsertPatch(
                locator = Locator3.class
        )
        public static void insert3(GridCardSelectScreen __instance) {
            GridCardSelectScreenHelper.invokeOnClick(__instance);
        }

        @SpirePostfixPatch
        public static void postfix(GridCardSelectScreen __instance) {
            GridCardSelectScreenHelper.updateDynamicString();
        }

        private static class Locator extends SpireInsertLocator {
            private Locator() {
            }

            public int[] Locate(CtBehavior ctMethodToPatch) throws Exception {
                Matcher finalMatcher = new Matcher.MethodCallMatcher(AbstractDungeon.class, "closeCurrentScreen");
                int[] found = LineFinder.findAllInOrder(ctMethodToPatch, finalMatcher);
                return new int[]{found[found.length - 1]};
            }
        }

        private static class Locator2 extends SpireInsertLocator {
            private Locator2() {
            }

            public int[] Locate(CtBehavior ctMethodToPatch) throws Exception {
                Matcher finalMatcher = new Matcher.MethodCallMatcher(GridSelectConfirmButton.class, "update");
                int[] found = LineFinder.findAllInOrder(ctMethodToPatch, finalMatcher);
                return new int[]{found[found.length - 1]};
            }
        }

        private static class Locator3 extends SpireInsertLocator {
            private Locator3() {
            }

            public int[] Locate(CtBehavior ctMethodToPatch) throws Exception {
                Matcher finalMatcher = new Matcher.MethodCallMatcher(ArrayList.class, "contains");
                int[] found = LineFinder.findAllInOrder(ctMethodToPatch, finalMatcher);
                return new int[]{found[0] - 1};
            }
        }
    }

    @SpirePatch(
            clz = GridCardSelectScreen.class,
            method = "render"
    )
    public static class GridRender {
        public GridRender() {
        }

        @SpirePostfixPatch
        public static void postfix(GridCardSelectScreen __instance, SpriteBatch sb) {
            GridCardSelectScreenHelper.renderDynamicString(sb);
        }
    }

    @SpirePatch(
            clz = GridCardSelectScreen.class,
            method = "cancelUpgrade"
    )
    public static class CancelUpgrade {
        public CancelUpgrade() {
        }

        @SpirePrefixPatch
        public static void prefix(GridCardSelectScreen __instance) {
            waitingForBranchUpgradeSelection = false;
            branchUpgradeForm = 0;
            cardList.clear();
        }
    }

    @SpirePatch(
            clz = GridCardSelectScreen.class,
            method = "renderArrows"
    )
    public static class RenderSplitArrows {

        private static float arrowScale1 = Settings.scale;
        private static float arrowScale2 = Settings.scale;
        private static float arrowScale3 = Settings.scale;
        private static float arrowTimer = 0.0F;

        public RenderSplitArrows() {
        }

        @SpirePrefixPatch
        public static SpireReturn<Void> prefix(GridCardSelectScreen __instance, SpriteBatch sb) {
            if (__instance.forUpgrade && cardList.size() > 1) {
                float x = (float) Settings.WIDTH / 2.0F - 73.0F * Settings.scale - 32.0F;
                float y = (float) Settings.HEIGHT / 2.0F - 32.0F;
                float by = 64 * Settings.scale;
                sb.setColor(Color.WHITE);
                sb.draw(ImageMaster.UPGRADE_ARROW, x, y + by, 32.0F, 32.0F, 64.0F, 64.0F, arrowScale1, arrowScale1, 45.0F, 0, 0, 64, 64, false, false);
                sb.draw(ImageMaster.UPGRADE_ARROW, x, y - by, 32.0F, 32.0F, 64.0F, 64.0F, arrowScale1, arrowScale1, -45.0F, 0, 0, 64, 64, false, false);
                x += 64.0F * Settings.scale;
                by += 64.0F * Settings.scale;
                sb.draw(ImageMaster.UPGRADE_ARROW, x, y + by, 32.0F, 32.0F, 64.0F, 64.0F, arrowScale2, arrowScale2, 45.0F, 0, 0, 64, 64, false, false);
                sb.draw(ImageMaster.UPGRADE_ARROW, x, y - by, 32.0F, 32.0F, 64.0F, 64.0F, arrowScale2, arrowScale2, -45.0F, 0, 0, 64, 64, false, false);
                x += 64.0F * Settings.scale;
                by += 64.0F * Settings.scale;
                sb.draw(ImageMaster.UPGRADE_ARROW, x, y + by, 32.0F, 32.0F, 64.0F, 64.0F, arrowScale3, arrowScale3, 45.0F, 0, 0, 64, 64, false, false);
                sb.draw(ImageMaster.UPGRADE_ARROW, x, y - by, 32.0F, 32.0F, 64.0F, 64.0F, arrowScale3, arrowScale3, -45.0F, 0, 0, 64, 64, false, false);

                if (cardList.size() > 2) {
                    x = (float) Settings.WIDTH / 2.0F - 73.0F * Settings.scale - 32.0F;
                    sb.draw(ImageMaster.UPGRADE_ARROW, x, y, 32.0F, 32.0F, 64.0F, 64.0F, arrowScale1, arrowScale1, 0, 0, 0, 64, 64, false, false);
                    sb.draw(ImageMaster.UPGRADE_ARROW, x + 64.0F * Settings.scale, y, 32.0F, 32.0F, 64.0F, 64.0F, arrowScale2, arrowScale2, 0, 0, 0, 64, 64, false, false);
                    sb.draw(ImageMaster.UPGRADE_ARROW, x + 128.0F * Settings.scale, y, 32.0F, 32.0F, 64.0F, 64.0F, arrowScale3, arrowScale3, 0, 0, 0, 64, 64, false, false);

                    if (cardList.size() > DEFAULT_MAX) {
                        upButton.renderCentered(sb);
                        downButton.renderCentered(sb);
                    }
                }

                arrowTimer += Gdx.graphics.getDeltaTime() * 2.0F;
                arrowScale1 = (0.8F + (MathUtils.cos(arrowTimer) + 1.0F)) * Settings.scale / 8.0F;
                arrowScale2 = (0.8F + (MathUtils.cos(arrowTimer - 0.8F) + 1.0F)) * Settings.scale / 8.0F;
                arrowScale3 = (0.8F + (MathUtils.cos(arrowTimer - 1.6F) + 1.0F)) * Settings.scale / 8.0F;
                return SpireReturn.Return();
            }
            return SpireReturn.Continue();
        }
    }

    @SpirePatch(
            clz = GridSelectConfirmButton.class,
            method = "render"
    )
    public static class BranchUpgradeConfirm {
        public BranchUpgradeConfirm() {
        }

        @SpirePrefixPatch
        public static SpireReturn<Void> prefix(GridSelectConfirmButton __instance, SpriteBatch sb) {
            return waitingForBranchUpgradeSelection
                    && !cardList.isEmpty()
                    ? SpireReturn.Return(null) : SpireReturn.Continue();
        }
    }

    @SpirePatch(
            clz = GridCardSelectScreen.class,
            method = "render"
            , optional = true
    )
    public static class RenderBranchingUpgrade {
        public RenderBranchingUpgrade() {
        }

        @SpireInstrumentPatch
        public static ExprEditor instrument() {
            return new ExprEditor() {
                public void edit(MethodCall m) throws CannotCompileException {
                    if (m.getMethodName().equals("renderArrows")) {
                        m.replace("$_ = $proceed($$);if (" + RenderBranchingUpgrade.class.getName() + ".render(this, sb).isPresent()) {return;}");
                    }

                }
            };
        }

        public static SpireReturn<Void> render(GridCardSelectScreen __instance, SpriteBatch sb) {
            AbstractCard c = getHoveredCard();
            if (__instance.forUpgrade && c != null && !cardList.isEmpty()) {
                c.current_x = c.target_x = (float) Settings.WIDTH * 0.36F;
                c.current_y = c.target_y = (float) Settings.HEIGHT / 2.0F;
                c.render(sb);
                c.updateHoverLogic();
                c.hb.resize(0.0F, 0.0F);

                final List<AbstractCard> list = getShownBranchUpgrades();
                int size = list.size();
                if (size == 1) {
                    renderPreviewCard(sb, list.get(0), 1f, Settings.HEIGHT * 0.5f);
                }
                else {
                    final float scale = size == 2 ? 0.9f : 0.62f;
                    final float[] yIndices = size == 2 ? Y_POSITIONS_2 : Y_POSITIONS_3;
                    for (int i = 0; i < size; i++) {
                        if (yIndices.length > i) {
                            renderPreviewCard(sb, list.get(i), scale, yIndices[i]);
                        }
                    }
                }

                if (__instance.forUpgrade || __instance.forTransform || __instance.forPurge || __instance.isJustForConfirming || __instance.anyNumber) {
                    __instance.confirmButton.render(sb);
                }

                CardGroup targetGroup = ReflectionHacks.getPrivate(__instance, GridCardSelectScreen.class, "targetGroup");
                String tipMsg = ReflectionHacks.getPrivate(__instance, GridCardSelectScreen.class, "tipMsg");
                if (!__instance.isJustForConfirming || targetGroup.size() > 5) {
                    FontHelper.renderDeckViewTip(sb, tipMsg, 96.0F * Settings.scale, Settings.CREAM_COLOR);
                }

                return SpireReturn.Return(null);
            }
            else {
                return SpireReturn.Continue();
            }
        }
    }

    @SpirePatch(
            clz = GridCardSelectScreen.class,
            method = "update"
    )
    public static class GridCardSelectScreen_Update {
        public GridCardSelectScreen_Update() {
        }

        @SpireInsertPatch(
                locator = GridCardSelectScreen_Update.Locator.class
        )
        public static void insert(GridCardSelectScreen __instance) {
            for (AbstractCard c : getShownBranchUpgrades()) {
                c.update();
            }
            if (cardList.size() > DEFAULT_MAX) {
                upButton.update();
                downButton.update();
            }
        }

        private static class Locator extends SpireInsertLocator {
            private Locator() {
            }

            public int[] Locate(CtBehavior ctMethodToPatch) throws Exception {
                Matcher finalMatcher = new Matcher.MethodCallMatcher(AbstractCard.class, "update");
                return new int[]{LineFinder.findAllInOrder(ctMethodToPatch, finalMatcher)[1]};
            }
        }
    }

    @SpirePatch(
            clz = GridCardSelectScreen.class,
            method = "update"
    )
    public static class GetBranchingUpgrade {
        public GetBranchingUpgrade() {
        }

        @SpireInsertPatch(
                locator = GetBranchingUpgrade.Locator.class
        )
        public static void insert(GridCardSelectScreen __instance) {
            AbstractCard c = getHoveredCard();
            PCLCard base = EUIUtils.safeCast(c, PCLCard.class);
            if (base != null) {
                if (augment != null) {
                    cardList.clear();
                    cardList.add(base.makeSetAugmentPreview(augment));
                    selectPCLCardUpgrade((PCLCard) cardList.get(0));
                }
                else if (base.isBranchingUpgrade()) {
                    fillCardListWithUpgrades(base);

                    // If there is only one card, it should be auto-selected
                    if (cardList.size() == 1) {
                        selectPCLCardUpgrade((PCLCard) cardList.get(0));
                    }
                    else {
                        waitingForBranchUpgradeSelection = true;
                    }
                }
                minIndex = 0;
                maxIndex = DEFAULT_MAX;
                refreshButtons();
            }
            else {
                cardList.clear();
            }
        }

        private static class Locator extends SpireInsertLocator {
            private Locator() {
            }

            public int[] Locate(CtBehavior ctMethodToPatch) throws Exception {
                Matcher finalMatcher = new Matcher.MethodCallMatcher(AbstractCard.class, "makeStatEquivalentCopy");
                return LineFinder.findInOrder(ctMethodToPatch, finalMatcher);
            }
        }
    }




}
