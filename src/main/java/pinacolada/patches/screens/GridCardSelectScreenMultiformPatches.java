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
import pinacolada.cards.base.PCLCard;
import pinacolada.cards.base.PCLDynamicData;

import java.util.ArrayList;
import java.util.List;

public class GridCardSelectScreenMultiformPatches
{
    public static final float[] Y_POSITIONS_2 = new float[]{
            Settings.HEIGHT * 0.75F - 50.0F * Settings.scale,
            Settings.HEIGHT * 0.25F + 50.0F * Settings.scale
    };
    public static final float[] Y_POSITIONS_3 = new float[]{
            Settings.HEIGHT * 0.75F + 25.0F * Settings.scale,
            Settings.HEIGHT * 0.5F,
            Settings.HEIGHT * 0.25F - 25.0F * Settings.scale
    };
    protected static List<AbstractCard> cardList = new ArrayList<>();
    protected static final float ICON_SIZE = 64f * Settings.scale;
    protected static final int DEFAULT_MAX = 3;
    protected static EUIButton upButton = new EUIButton(ImageMaster.UPGRADE_ARROW, new EUIHitbox(Settings.WIDTH * 0.75F, Settings.HEIGHT * 0.55F, ICON_SIZE, ICON_SIZE)).setColor(Color.PURPLE).setShaderMode(EUIRenderHelpers.ShaderMode.Colorize).setButtonRotation(90);
    protected static EUIButton downButton = new EUIButton(ImageMaster.UPGRADE_ARROW, new EUIHitbox(Settings.WIDTH * 0.75F, Settings.HEIGHT * 0.45F, ICON_SIZE, ICON_SIZE)).setColor(Color.PURPLE).setShaderMode(EUIRenderHelpers.ShaderMode.Colorize).setButtonRotation(-90);
    protected static int minIndex = 0;
    protected static int maxIndex = DEFAULT_MAX;

    public static void selectPCLCardUpgrade(PCLCard card)
    {
        if (card.cardData.canToggleOnUpgrade) {
            GridCardSelectScreenMultiformPatches.BranchSelectFields.branchUpgradeForm.set(AbstractDungeon.gridSelectScreen, card.auxiliaryData.form);
            card.beginGlowing();
            if (cardList != null)
            {
                cardList.forEach((c) -> {
                    if (c != card) {
                        c.stopGlowing();
                    }
                });
            }
        }
        GridCardSelectScreenMultiformPatches.BranchSelectFields.waitingForBranchUpgradeSelection.set(AbstractDungeon.gridSelectScreen, false);
    }

    public static AbstractCard getHoveredCard()
    {
        return ReflectionHacks.getPrivate(AbstractDungeon.gridSelectScreen, GridCardSelectScreen.class, "hoveredCard");
    }

    protected static List<AbstractCard> getBranchUpgrades(GridCardSelectScreen instance)
    {
        return GridCardSelectScreenMultiformPatches.BranchSelectFields.branchUpgrades.get(instance);
    }

    protected static List<AbstractCard> getShownBranchUpgrades(GridCardSelectScreen instance)
    {
        List<AbstractCard> list = getBranchUpgrades(instance);
        return list.size() < 4 ? list : list.subList(minIndex, maxIndex);
    }

    protected static void refreshButtons(GridCardSelectScreen instance)
    {
        boolean canUp = minIndex > 0;
        upButton.setInteractable(canUp).setColor(canUp ? Color.PURPLE : Color.GRAY);
        boolean canDown = maxIndex < getBranchUpgrades(instance).size() - 1;
        downButton.setInteractable(canDown).setColor(canDown ? Color.PURPLE : Color.GRAY);
    }

    protected static void addIndex(GridCardSelectScreen instance)
    {
        if (maxIndex < getBranchUpgrades(instance).size() - 1)
        {
            minIndex += 1;
            maxIndex += 1;
            refreshButtons(instance);
        }
    }

    protected static void subtractIndex(GridCardSelectScreen instance)
    {
        if (minIndex > 0)
        {
            minIndex -= 1;
            maxIndex -= 1;
            refreshButtons(instance);
        }
    }

    public static void renderPreviewCard(SpriteBatch sb, AbstractCard card, float unHoveredScale, float y)
    {
        card.drawScale = card.hb.hovered ? unHoveredScale + 0.1f : unHoveredScale;
        card.current_x = card.target_x = (float) Settings.WIDTH * 0.63F;
        card.current_y = card.target_y = y;
        card.render(sb);
        card.updateHoverLogic();
        card.renderCardTip(sb);
    }

    // TODO add patch for conditionals
    @SpirePatch(
            clz = GridCardSelectScreen.class,
            method = "update"
    )
    public static class GridUpdate
    {
        public GridUpdate()
        {
        }

        @SpireInsertPatch(
                locator = Locator1.class
        )
        public static void insert(GridCardSelectScreen __instance)
        {
            AbstractCard hoveredCard = GridCardSelectScreenMultiformPatches.getHoveredCard();
            if (hoveredCard instanceof PCLCard)
            {
                ((PCLCard) hoveredCard).changeForm(BranchSelectFields.branchUpgradeForm.get(__instance), hoveredCard.timesUpgraded);
                GridCardSelectScreenMultiformPatches.BranchSelectFields.branchUpgradeForm.set(__instance, 0);
                cardList = null;
            }

        }

        private static class Locator1 extends SpireInsertLocator
        {
            private Locator1()
            {
            }

            public int[] Locate(CtBehavior ctMethodToPatch) throws Exception
            {
                Matcher finalMatcher = new Matcher.MethodCallMatcher(AbstractDungeon.class, "closeCurrentScreen");
                int[] found = LineFinder.findAllInOrder(ctMethodToPatch, finalMatcher);
                return new int[]{found[found.length - 1]};
            }
        }
    }

    @SpirePatch(
            clz = GridCardSelectScreen.class,
            method = "cancelUpgrade"
    )
    public static class CancelUpgrade
    {
        public CancelUpgrade()
        {
        }

        @SpirePrefixPatch
        public static void prefix(GridCardSelectScreen __instance)
        {
            GridCardSelectScreenMultiformPatches.BranchSelectFields.waitingForBranchUpgradeSelection.set(__instance, false);
            BranchSelectFields.branchUpgradeForm.set(__instance, 0);
            cardList = null;
        }
    }

    @SpirePatch(
            clz = GridCardSelectScreen.class,
            method = "renderArrows"
    )
    public static class RenderSplitArrows
    {

        private static float arrowScale1 = 1.0F;
        private static float arrowScale2 = 1.0F;
        private static float arrowScale3 = 1.0F;
        private static float arrowTimer = 0.0F;

        public RenderSplitArrows()
        {
        }

        @SpirePrefixPatch
        public static SpireReturn prefix(GridCardSelectScreen __instance, SpriteBatch sb)
        {
            AbstractCard c = GridCardSelectScreenMultiformPatches.getHoveredCard();
            if (__instance.forUpgrade && c instanceof PCLCard && ((PCLCard) c).cardData.canToggleOnUpgrade)
            {
                float x = (float) Settings.WIDTH / 2.0F - 73.0F * Settings.scale - 32.0F;
                float y = (float) Settings.HEIGHT / 2.0F - 32.0F;
                float by = 64 * Settings.scale;
                sb.setColor(Color.WHITE);
                sb.draw(ImageMaster.UPGRADE_ARROW, x, y + by, 32.0F, 32.0F, 64.0F, 64.0F, arrowScale1 * Settings.scale, arrowScale1 * Settings.scale, 45.0F, 0, 0, 64, 64, false, false);
                sb.draw(ImageMaster.UPGRADE_ARROW, x, y - by, 32.0F, 32.0F, 64.0F, 64.0F, arrowScale1 * Settings.scale, arrowScale1 * Settings.scale, -45.0F, 0, 0, 64, 64, false, false);
                x += 64.0F * Settings.scale;
                by += 64.0F * Settings.scale;
                sb.draw(ImageMaster.UPGRADE_ARROW, x, y + by, 32.0F, 32.0F, 64.0F, 64.0F, arrowScale2 * Settings.scale, arrowScale2 * Settings.scale, 45.0F, 0, 0, 64, 64, false, false);
                sb.draw(ImageMaster.UPGRADE_ARROW, x, y - by, 32.0F, 32.0F, 64.0F, 64.0F, arrowScale2 * Settings.scale, arrowScale2 * Settings.scale, -45.0F, 0, 0, 64, 64, false, false);
                x += 64.0F * Settings.scale;
                by += 64.0F * Settings.scale;
                sb.draw(ImageMaster.UPGRADE_ARROW, x, y + by, 32.0F, 32.0F, 64.0F, 64.0F, arrowScale3 * Settings.scale, arrowScale3 * Settings.scale, 45.0F, 0, 0, 64, 64, false, false);
                sb.draw(ImageMaster.UPGRADE_ARROW, x, y - by, 32.0F, 32.0F, 64.0F, 64.0F, arrowScale3 * Settings.scale, arrowScale3 * Settings.scale, -45.0F, 0, 0, 64, 64, false, false);

                ArrayList<AbstractCard> upgrades = GridCardSelectScreenMultiformPatches.BranchSelectFields.branchUpgrades.get(__instance);
                if (upgrades.size() > 2)
                {
                    x = (float) Settings.WIDTH / 2.0F - 73.0F * Settings.scale - 32.0F;
                    sb.draw(ImageMaster.UPGRADE_ARROW, x, y, 32.0F, 32.0F, 64.0F, 64.0F, arrowScale1 * Settings.scale, arrowScale1 * Settings.scale, 0, 0, 0, 64, 64, false, false);
                    sb.draw(ImageMaster.UPGRADE_ARROW, x + 64.0F * Settings.scale, y, 32.0F, 32.0F, 64.0F, 64.0F, arrowScale2 * Settings.scale, arrowScale2 * Settings.scale, 0, 0, 0, 64, 64, false, false);
                    sb.draw(ImageMaster.UPGRADE_ARROW, x + 128.0F * Settings.scale, y, 32.0F, 32.0F, 64.0F, 64.0F, arrowScale3 * Settings.scale, arrowScale3 * Settings.scale, 0, 0, 0, 64, 64, false, false);

                    if (upgrades.size() > DEFAULT_MAX)
                    {
                        upButton.renderCentered(sb);
                        downButton.renderCentered(sb);
                    }
                }

                arrowTimer += Gdx.graphics.getDeltaTime() * 2.0F;
                arrowScale1 = 0.8F + (MathUtils.cos(arrowTimer) + 1.0F) / 8.0F;
                arrowScale2 = 0.8F + (MathUtils.cos(arrowTimer - 0.8F) + 1.0F) / 8.0F;
                arrowScale3 = 0.8F + (MathUtils.cos(arrowTimer - 1.6F) + 1.0F) / 8.0F;
                return SpireReturn.Return();
            }
            return SpireReturn.Continue();


        }
    }

    @SpirePatch(
            clz = GridSelectConfirmButton.class,
            method = "render"
    )
    public static class BranchUpgradeConfirm
    {
        public BranchUpgradeConfirm()
        {
        }

        @SpirePrefixPatch
        public static SpireReturn prefix(GridSelectConfirmButton __instance, SpriteBatch sb)
        {
            AbstractCard c = GridCardSelectScreenMultiformPatches.getHoveredCard();
            return BranchSelectFields.waitingForBranchUpgradeSelection.get(AbstractDungeon.gridSelectScreen) && c instanceof PCLCard && ((PCLCard) c).cardData.canToggleOnUpgrade ? SpireReturn.Return(null) : SpireReturn.Continue();
        }
    }

    @SpirePatch(
            clz = GridCardSelectScreen.class,
            method = "render"
            , optional = true
    )
    public static class RenderBranchingUpgrade
    {
        public RenderBranchingUpgrade()
        {
        }

        public static SpireReturn render(GridCardSelectScreen __instance, SpriteBatch sb)
        {
            AbstractCard c = GridCardSelectScreenMultiformPatches.getHoveredCard();
            if (__instance.forUpgrade && c instanceof PCLCard && ((PCLCard) c).cardData.canToggleOnUpgrade)
            {
                c.current_x = c.target_x = (float) Settings.WIDTH * 0.36F;
                c.current_y = c.target_y = (float) Settings.HEIGHT / 2.0F;
                c.render(sb);
                c.updateHoverLogic();
                c.hb.resize(0.0F, 0.0F);

                final List<AbstractCard> list = getShownBranchUpgrades(__instance);
                int size = list.size();
                final float scale = size == 2 ? 0.9f : 0.62f;
                final float[] yIndices = size == 2 ? Y_POSITIONS_2 : Y_POSITIONS_3;
                for (int i = 0; i < size; i++)
                {
                    if (yIndices.length > i)
                    {
                        renderPreviewCard(sb, list.get(i), scale, yIndices[i]);
                    }
                }

                if (__instance.forUpgrade || __instance.forTransform || __instance.forPurge || __instance.isJustForConfirming || __instance.anyNumber)
                {
                    __instance.confirmButton.render(sb);
                }

                CardGroup targetGroup = ReflectionHacks.getPrivate(__instance, GridCardSelectScreen.class, "targetGroup");
                String tipMsg = ReflectionHacks.getPrivate(__instance, GridCardSelectScreen.class, "tipMsg");
                if (!__instance.isJustForConfirming || targetGroup.size() > 5)
                {
                    FontHelper.renderDeckViewTip(sb, tipMsg, 96.0F * Settings.scale, Settings.CREAM_COLOR);
                }

                return SpireReturn.Return(null);
            }
            else
            {
                return SpireReturn.Continue();
            }
        }

        @SpireInstrumentPatch
        public static ExprEditor instrument()
        {
            return new ExprEditor()
            {
                public void edit(MethodCall m) throws CannotCompileException
                {
                    if (m.getMethodName().equals("renderArrows"))
                    {
                        m.replace("$_ = $proceed($$);if (" + GridCardSelectScreenMultiformPatches.RenderBranchingUpgrade.class.getName() + ".render(this, sb).isPresent()) {return;}");
                    }

                }
            };
        }
    }

    @SpirePatch(
            clz = GridCardSelectScreen.class,
            method = "update"
    )
    public static class GridCardSelectScreen_Update
    {
        public GridCardSelectScreen_Update()
        {
        }

        @SpireInsertPatch(
                locator = GridCardSelectScreen_Update.Locator.class
        )
        public static void insert(GridCardSelectScreen __instance)
        {
            for (AbstractCard c : getShownBranchUpgrades(__instance))
            {
                c.update();
            }
            if (getBranchUpgrades(__instance).size() > DEFAULT_MAX)
            {
                upButton.update();
                downButton.update();
            }
        }

        private static class Locator extends SpireInsertLocator
        {
            private Locator()
            {
            }

            public int[] Locate(CtBehavior ctMethodToPatch) throws Exception
            {
                Matcher finalMatcher = new Matcher.MethodCallMatcher(AbstractCard.class, "update");
                return new int[]{LineFinder.findAllInOrder(ctMethodToPatch, finalMatcher)[1]};
            }
        }
    }

    @SpirePatch(
            clz = GridCardSelectScreen.class,
            method = "update"
    )
    public static class GetBranchingUpgrade
    {
        public GetBranchingUpgrade()
        {
        }

        @SpireInsertPatch(
                locator = GridCardSelectScreenMultiformPatches.GetBranchingUpgrade.Locator.class
        )
        public static void insert(GridCardSelectScreen __instance)
        {
            AbstractCard c = GridCardSelectScreenMultiformPatches.getHoveredCard();
            PCLCard base = EUIUtils.safeCast(c, PCLCard.class);
            if (base != null && base.cardData.canToggleOnUpgrade)
            {
                final ArrayList<AbstractCard> list = GridCardSelectScreenMultiformPatches.BranchSelectFields.branchUpgrades.get(__instance);
                list.clear();

                if (base.cardData instanceof PCLDynamicData && ((PCLDynamicData) base.cardData).linearUpgrade)
                {
                    list.add(getPreviewCard(base, base.timesUpgraded));
                }
                else
                {
                    int formCount = base.getMaxForms();
                    for (int i = 0; i < formCount; i++)
                    {
                        list.add(getPreviewCard(base, i));
                    }
                }


                cardList = list;

                GridCardSelectScreenMultiformPatches.BranchSelectFields.waitingForBranchUpgradeSelection.set(__instance, true);
                minIndex = 0;
                maxIndex = DEFAULT_MAX;
                upButton.setOnClick(() -> subtractIndex(__instance));
                downButton.setOnClick(() -> addIndex(__instance));
                refreshButtons(__instance);
            }

        }

        protected static PCLCard getPreviewCard(PCLCard base, int i)
        {
            PCLCard previewCard = base.makeStatEquivalentCopy();
            previewCard.changeForm(i, previewCard.timesUpgraded);
            previewCard.upgrade();
            previewCard.displayUpgrades();
            previewCard.initializeDescription();
            return previewCard;
        }

        private static class Locator extends SpireInsertLocator
        {
            private Locator()
            {
            }

            public int[] Locate(CtBehavior ctMethodToPatch) throws Exception
            {
                Matcher finalMatcher = new Matcher.MethodCallMatcher(AbstractCard.class, "makeStatEquivalentCopy");
                return LineFinder.findInOrder(ctMethodToPatch, finalMatcher);
            }
        }
    }

    @SpirePatch(
            clz = GridCardSelectScreen.class,
            method = "<class>"
    )
    public static class BranchSelectFields
    {
        public static SpireField<ArrayList<AbstractCard>> branchUpgrades = new SpireField<>(ArrayList::new);
        public static SpireField<Boolean> waitingForBranchUpgradeSelection = new SpireField<>(() -> {
            return false;
        });
        public static SpireField<Integer> branchUpgradeForm = new SpireField<>(() -> {
            return 0;
        });
    }
}
