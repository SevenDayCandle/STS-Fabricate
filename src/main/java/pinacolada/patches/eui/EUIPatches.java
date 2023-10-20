package pinacolada.patches.eui;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.evacipated.cardcrawl.modthespire.lib.SpirePatch;
import com.evacipated.cardcrawl.modthespire.lib.SpirePostfixPatch;
import com.evacipated.cardcrawl.modthespire.lib.SpirePrefixPatch;
import com.evacipated.cardcrawl.modthespire.lib.SpireReturn;
import com.megacrit.cardcrawl.blights.AbstractBlight;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.helpers.ImageMaster;
import extendedui.EUI;
import extendedui.EUIGameUtils;
import extendedui.EUIUtils;
import extendedui.exporter.EUIExporter;
import extendedui.exporter.EUIExporterCardRow;
import extendedui.exporter.EUIExporterRow;
import extendedui.patches.game.TooltipPatches;
import extendedui.patches.screens.MenuPanelScreenPatches;
import extendedui.ui.TextureCache;
import extendedui.ui.cardFilter.panels.CardTypePanelFilterItem;
import extendedui.ui.controls.EUIMainMenuPanelButton;
import extendedui.utilities.BlightTier;
import pinacolada.blights.PCLBlight;
import pinacolada.blights.PCLBlightData;
import pinacolada.blights.PCLCustomBlightSlot;
import pinacolada.cards.base.PCLCard;
import pinacolada.cards.base.PCLCustomCardSlot;
import pinacolada.misc.PCLCardExportRow;
import pinacolada.patches.library.BlightHelperPatches;
import pinacolada.resources.PCLEnum;
import pinacolada.resources.PGR;
import pinacolada.resources.pcl.PCLCoreImages;
import pinacolada.skills.PSkill;
import pinacolada.utilities.GameUtilities;

import java.util.ArrayList;

public class EUIPatches {
    protected static final CardTypePanelFilterItem SUMMON = new CardTypePanelFilterItem(PCLEnum.CardType.SUMMON);

    @SpirePatch(clz = BlightTier.class, method = "getTier", paramtypez = {AbstractBlight.class})
    public static class BlightTier_GetTier {
        @SpirePrefixPatch
        public static SpireReturn<BlightTier> prefix(AbstractBlight blight) {
            if (blight instanceof PCLBlight) {
                return SpireReturn.Return(((PCLBlight) blight).blightData.tier);
            }
            return SpireReturn.Continue();
        }
    }

    // Non-PCL blights should not have the special tier
    @SpirePatch(clz = BlightTier.class, method = "getTier", paramtypez = {String.class})
    public static class BlightTier_GetTier2 {
        @SpirePostfixPatch
        public static BlightTier postfix(BlightTier ret, String id) {
            if (ret == BlightTier.SPECIAL) {
                PCLBlightData data = PCLBlightData.getStaticData(id);
                if (data != null) {
                    return data.tier;
                }
                PCLCustomBlightSlot slot = PCLCustomBlightSlot.get(id);
                if (slot != null) {
                    return BlightTier.valueOf(slot.tier);
                }
            }
            return ret;
        }
    }

    @SpirePatch(clz = CardTypePanelFilterItem.class, method = "get")
    public static class CardTypePanelFilterItem_Get {
        @SpirePrefixPatch
        public static SpireReturn<CardTypePanelFilterItem> prefix(AbstractCard.CardType type) {
            if (type == PCLEnum.CardType.SUMMON) {
                return SpireReturn.Return(SUMMON);
            }
            return SpireReturn.Continue();
        }
    }

    @SpirePatch(clz = MenuPanelScreenPatches.class, method = "getCompendiums")
    public static class ExtendedUIPatches_GetCompendiums {
        @SpirePostfixPatch
        public static ArrayList<EUIMainMenuPanelButton> postfix(ArrayList<EUIMainMenuPanelButton> retVal) {
            if (EUIUtils.any(PGR.getRegisteredResources(), r -> r.data != null && r.data.useAugments)) {
                retVal.add(new EUIMainMenuPanelButton(new Color(0.45f, 0.62f, 0.71f, 1f), ImageMaster.MENU_PANEL_BG_BEIGE, PCLCoreImages.Menu.menuAugmentLibrary.texture(), PGR.core.strings.menu_augmentLibrary, PGR.core.strings.menu_augmentLibraryDesc, () -> PGR.augmentLibrary.open()));
            }
            return retVal;
        }
    }

    @SpirePatch(clz = EUI.class, method = "postRender")
    public static class ExtendedUIPatches_PostRender {
        @SpirePrefixPatch
        public static void prefix(SpriteBatch sb) {
            PGR.augmentFilters.tryRender(sb);
        }
    }

    @SpirePatch(clz = EUIExporter.class, method = "getRowsForCard")
    public static class EUIExporter_GetRowsForCard {
        @SpirePrefixPatch
        public static SpireReturn<ArrayList<EUIExporterRow>> prefix(Iterable<? extends AbstractCard> items, EUIExporter.ExportType format) {
            if (EUIUtils.all(items, c -> c instanceof PCLCard)) {
                return SpireReturn.Return(EUIUtils.map(items, item -> new PCLCardExportRow((PCLCard) item, format)));
            }
            return SpireReturn.Continue();
        }
    }

    @SpirePatch(clz = EUIExporterCardRow.class, method = "parseCardString")
    public static class EUIExporterCardRow_ParseCardString {
        @SpirePrefixPatch
        public static SpireReturn<String> prefix(AbstractCard card) {
            if (card instanceof PCLCard) {
                return SpireReturn.Return(EUIUtils.joinStringsMap(" ", PSkill::getExportText, ((PCLCard) card).getFullEffects()));
            }
            return SpireReturn.Continue();
        }
    }

    @SpirePatch(clz = EUIGameUtils.class, method = "canSeeAnyColorCardFromPool")
    public static class ExtendedUIPatches_CanSeeAnyColorCard {
        @SpirePostfixPatch
        public static boolean postfix(boolean retVal, AbstractCard c) {
            return retVal && !PGR.dungeon.bannedCards.contains(c.cardID);
        }
    }

    @SpirePatch(clz = EUIGameUtils.class, method = "getAllBlights")
    public static class ExtendedUIPatches_GetAllBlights {
        @SpirePostfixPatch
        public static ArrayList<AbstractBlight> postfix(ArrayList<AbstractBlight> retVal) {
            retVal.addAll(BlightHelperPatches.getAdditionalBlights());
            return retVal;
        }
    }

    @SpirePatch(clz = EUIGameUtils.class, method = "getEveryColorCardForPoolDisplay")
    public static class ExtendedUIPatches_GetEveryColorCard {
        @SpirePostfixPatch
        public static ArrayList<AbstractCard> postfix(ArrayList<AbstractCard> retVal) {
            AbstractCard.CardColor color = GameUtilities.getActingColor();
            if (color != AbstractCard.CardColor.COLORLESS) {
                for (PCLCustomCardSlot c : PCLCustomCardSlot.getCards(color)) {
                    retVal.add(c.make());
                }
            }
            for (PCLCustomCardSlot c : PCLCustomCardSlot.getCards(AbstractCard.CardColor.COLORLESS)) {
                retVal.add(c.make());
            }
            return retVal;
        }
    }

    @SpirePatch(clz = EUIGameUtils.class, method = "iconForType")
    public static class ExtendedUIPatches_IconForType {
        @SpirePrefixPatch
        public static SpireReturn<TextureCache> prefix(AbstractCard.CardType type) {
            if (type == PCLEnum.CardType.SUMMON) {
                return SpireReturn.Return(PCLCoreImages.Types.summon);
            }
            return SpireReturn.Continue();
        }
    }

    @SpirePatch(clz = EUIGameUtils.class, method = "textForRarity")
    public static class ExtendedUIPatches_TextForRarity {
        @SpirePrefixPatch
        public static SpireReturn<String> prefix(AbstractCard.CardRarity type) {
            if (type == PCLEnum.CardRarity.SECRET) {
                return SpireReturn.Return(PGR.core.strings.ctype_secretRare);
            }
            else if (type == PCLEnum.CardRarity.LEGENDARY) {
                return SpireReturn.Return(PGR.core.strings.ctype_legendary);
            }
            return SpireReturn.Continue();
        }
    }

    @SpirePatch(clz = EUIGameUtils.class, method = "textForType")
    public static class ExtendedUIPatches_TextForType {
        @SpirePrefixPatch
        public static SpireReturn<String> prefix(AbstractCard.CardType type) {
            if (type == PCLEnum.CardType.SUMMON) {
                return SpireReturn.Return(PGR.core.tooltips.summon.title);
            }
            return SpireReturn.Continue();
        }
    }

    @SpirePatch(clz = TooltipPatches.class, method = "useEUIForPowers")
    public static class TooltipPatches_UseEUIForPowers {
        @SpirePostfixPatch
        public static boolean postfix(boolean res) {
            return res || GameUtilities.isPCLPlayerClass();
        }
    }
}
