package pinacolada.resources.pcl;

import com.badlogic.gdx.graphics.Texture;
import extendedui.ui.TextureCache;
import pinacolada.cards.base.fields.PCLAffinity;
import pinacolada.resources.AbstractImages;

//TODO remove unused images

public class PCLCoreImages extends AbstractImages {
    public static final String ORB_VFX_PNG = "images/pcl/ui/menu/canvas/orbVfx.png";
    public static final String EMPTY_SHADOW = "images/pcl/monsters/EmptyShadow.png";

    public PCLCoreImages(String id) {
        super(id);
    }

    public Texture getAffinityTexture(PCLAffinity affinity) {
        switch (affinity.ID) {
            case PCLAffinity.ID_GENERAL:
                return CardAffinity.general.texture();
            case PCLAffinity.ID_STAR:
                return CardAffinity.star.texture();
            case PCLAffinity.ID_RED:
                return CardAffinity.red.texture();
            case PCLAffinity.ID_BLUE:
                return CardAffinity.blue.texture();
            case PCLAffinity.ID_GREEN:
                return CardAffinity.green.texture();
            case PCLAffinity.ID_ORANGE:
                return CardAffinity.orange.texture();
            case PCLAffinity.ID_YELLOW:
                return CardAffinity.yellow.texture();
            case PCLAffinity.ID_PURPLE:
                return CardAffinity.purple.texture();
            case PCLAffinity.ID_SILVER:
                return CardAffinity.silver.texture();
        }
        return null;
    }

    public static class Core {
        public static final TextureCache backArrow = new TextureCache("images/pcl/ui/core/BackArrow.png");
        public static final TextureCache borderBG2 = new TextureCache("images/pcl/ui/core/BG2.png", true);
        public static final TextureCache borderBG3 = new TextureCache("images/pcl/ui/core/BG3.png", true);
        public static final TextureCache borderFG = new TextureCache("images/pcl/ui/core/FG.png", false);
        public static final TextureCache borderHighlight = new TextureCache("images/pcl/ui/core/Border_Highlight.png", false);
        public static final TextureCache borderNormal = new TextureCache("images/pcl/ui/core/Border_Normal.png", true);
        public static final TextureCache borderSilhouette = new TextureCache("images/pcl/ui/core/Border_Silhouette.png", true);
        public static final TextureCache borderSpecial = new TextureCache("images/pcl/ui/core/Border_Special.png", true);
        public static final TextureCache borderSpecial2 = new TextureCache("images/pcl/ui/core/Border_Special2.png", true);
        public static final TextureCache borderWeak = new TextureCache("images/pcl/ui/core/Border_Weak.png", true);
        public static final TextureCache circle = new TextureCache("images/pcl/ui/core/Circle.png");
        public static final TextureCache dead = new TextureCache("images/pcl/ui/core/Dead.png");
        public static final TextureCache controllableCardPile = new TextureCache("images/pcl/ui/core/ControllableCardPile.png");
        public static final TextureCache controllableCardPileBorder = new TextureCache("images/pcl/ui/core/ControllableCardPileBorder.png");
        public static final TextureCache rightArrow = new TextureCache("images/pcl/ui/core/RightArrow.png");
        public static final TextureCache squareBG1 = new TextureCache("images/pcl/ui/core/SquareBG1.png", true);
        public static final TextureCache squareBG2 = new TextureCache("images/pcl/ui/core/SquareBG2.png", true);
        public static final TextureCache timer = new TextureCache("images/pcl/ui/core/Timer.png");
    }

    public static class Menu {

        public static final TextureCache augmentPanel = new TextureCache("images/pcl/ui/menu/AugmentPanel.png");
        public static final TextureCache check = new TextureCache("images/pcl/ui/menu/Check.png");
        public static final TextureCache delete = new TextureCache("images/pcl/ui/menu/Delete.png");
        public static final TextureCache edit = new TextureCache("images/pcl/ui/menu/Edit.png");
        public static final TextureCache editorPrimary = new TextureCache("images/pcl/ui/menu/EditorPrimary.png");
        public static final TextureCache editorAttribute = new TextureCache("images/pcl/ui/menu/EditorAttribute.png");
        public static final TextureCache editorAttack = new TextureCache("images/pcl/ui/menu/EditorAttack.png");
        public static final TextureCache editorBlock = new TextureCache("images/pcl/ui/menu/EditorBlock.png");
        public static final TextureCache editorEffect = new TextureCache("images/pcl/ui/menu/EditorEffect.png");
        public static final TextureCache editorPower = new TextureCache("images/pcl/ui/menu/EditorPower.png");
        public static final TextureCache menuAugment = new TextureCache("images/pcl/ui/menu/MenuAugment.png");
        public static final TextureCache menuAugmentLibrary = new TextureCache("images/pcl/ui/menu/MenuAugmentLibrary.png");
        public static final TextureCache menuBlight = new TextureCache("images/pcl/ui/menu/MenuBlight.png");
        public static final TextureCache menuCard = new TextureCache("images/pcl/ui/menu/MenuCard.png");
        public static final TextureCache menuOrb = new TextureCache("images/pcl/ui/menu/MenuOrb.png");
        public static final TextureCache menuPotion = new TextureCache("images/pcl/ui/menu/MenuPotion.png");
        public static final TextureCache menuPower = new TextureCache("images/pcl/ui/menu/MenuPower.png");
        public static final TextureCache menuRelic = new TextureCache("images/pcl/ui/menu/MenuRelic.png");
        public static final TextureCache nodeCircle = new TextureCache("images/pcl/ui/menu/NodeCircle.png");
        public static final TextureCache nodeCircle2 = new TextureCache("images/pcl/ui/menu/NodeCircle2.png");
        public static final TextureCache nodeCircleSmall = new TextureCache("images/pcl/ui/menu/NodeCircleSmall.png");
        public static final TextureCache nodeDiamond = new TextureCache("images/pcl/ui/menu/NodeDiamond.png");
        public static final TextureCache nodeHexagon = new TextureCache("images/pcl/ui/menu/NodeHexagon.png");
        public static final TextureCache nodeOctagon = new TextureCache("images/pcl/ui/menu/NodeOctagon.png");
        public static final TextureCache nodeSquare = new TextureCache("images/pcl/ui/menu/NodeSquare.png");
        public static final TextureCache nodeSquare2 = new TextureCache("images/pcl/ui/menu/NodeSquare2.png");
        public static final TextureCache nodeTriangle = new TextureCache("images/pcl/ui/menu/NodeTriangle.png");
        public static final TextureCache squaredbuttonEmptycenter = new TextureCache("images/pcl/ui/menu/SquaredButton_EmptyCenter.png");
    }

    public static class CardUI {
        public static final TextureCache cardBackgroundAttackRepl = new TextureCache("images/pcl/cardui/512/bg_attack_canvas_repl.png");
        public static final TextureCache cardBackgroundAttackReplL = new TextureCache("images/pcl/cardui/1024/bg_attack_canvas_repl.png");
        public static final TextureCache cardBackgroundPowerRepl = new TextureCache("images/pcl/cardui/512/bg_power_canvas_repl.png");
        public static final TextureCache cardBackgroundPowerReplL = new TextureCache("images/pcl/cardui/1024/bg_power_canvas_repl.png");
        public static final TextureCache cardBackgroundSkillRepl = new TextureCache("images/pcl/cardui/512/bg_skill_canvas_repl.png");
        public static final TextureCache cardBackgroundSkillReplL = new TextureCache("images/pcl/cardui/1024/bg_skill_canvas_repl.png");
        public static final TextureCache cardBanner = new TextureCache("images/pcl/cardui/512/banner.png");
        public static final TextureCache cardBanner2 = new TextureCache("images/pcl/cardui/512/banner2.png");
        public static final TextureCache cardBanner2L = new TextureCache("images/pcl/cardui/1024/banner2.png");
        public static final TextureCache cardBannerAttribute = new TextureCache("images/pcl/cardui/512/banner_attribute.png");
        public static final TextureCache cardBannerAttribute2 = new TextureCache("images/pcl/cardui/512/banner_attribute2.png");
        public static final TextureCache cardBannerAttribute2L = new TextureCache("images/pcl/cardui/1024/banner_attribute2.png");
        public static final TextureCache cardBannerAttributeL = new TextureCache("images/pcl/cardui/1024/banner_attribute.png");
        public static final TextureCache cardBannerAttributeVanilla = new TextureCache("images/pcl/cardui/512/banner_attribute_vanilla.png");
        public static final TextureCache cardBannerAttributeVanillaL = new TextureCache("images/pcl/cardui/1024/banner_attribute_vanilla.png");
        public static final TextureCache cardBannerL = new TextureCache("images/pcl/cardui/1024/banner.png");
        public static final TextureCache cardFrameAttack = new TextureCache("images/pcl/cardui/512/frame_attack.png");
        public static final TextureCache cardFrameAttackL = new TextureCache("images/pcl/cardui/1024/frame_attack.png");
        public static final TextureCache cardFramePower = new TextureCache("images/pcl/cardui/512/frame_power.png");
        public static final TextureCache cardFramePowerL = new TextureCache("images/pcl/cardui/1024/frame_power.png");
        public static final TextureCache cardFrameSkill = new TextureCache("images/pcl/cardui/512/frame_skill.png");
        public static final TextureCache cardFrameSkillL = new TextureCache("images/pcl/cardui/1024/frame_skill.png");
        public static final TextureCache cardFrameSummon = new TextureCache("images/pcl/cardui/512/frame_summon.png");
        public static final TextureCache cardFrameSummonL = new TextureCache("images/pcl/cardui/1024/frame_summon.png");
        public static final TextureCache augmentSlot = new TextureCache("images/pcl/cardui/augments/AugmentSlot.png", true);
        public static final TextureCache augmentBase = new TextureCache("images/pcl/cardui/augments/AugmentBase.png", true);
        public static final TextureCache augmentPlayed = new TextureCache("images/pcl/cardui/augments/AugmentPlayed.png", true);
        public static final TextureCache augmentHindrance = new TextureCache("images/pcl/cardui/augments/AugmentHindrance.png", true);
        public static final TextureCache augmentSummon = new TextureCache("images/pcl/cardui/augments/AugmentSummon.png", true);
        public static final TextureCache augmentSpecial = new TextureCache("images/pcl/cardui/augments/AugmentSpecial.png", true);
    }

    public static class CardIcons {
        public static final TextureCache hp = new TextureCache("images/pcl/cardui/core/HP.png");
        public static final TextureCache hpL = new TextureCache("images/pcl/cardui/core/1024/HP.png");
        public static final TextureCache multiform = new TextureCache("images/pcl/cardui/core/Multiform.png");
        public static final TextureCache multiformL = new TextureCache("images/pcl/cardui/core/1024/Multiform.png");
        public static final TextureCache priorityMinus = new TextureCache("images/pcl/cardui/core/PriorityMinus.png");
        public static final TextureCache priorityMinusL = new TextureCache("images/pcl/cardui/core/1024/PriorityMinus.png");
        public static final TextureCache priorityPlus = new TextureCache("images/pcl/cardui/core/PriorityPlus.png");
        public static final TextureCache priorityPlusL = new TextureCache("images/pcl/cardui/core/1024/PriorityPlus.png");
        public static final TextureCache soulbound = new TextureCache("images/pcl/cardui/core/Soulbound.png");
        public static final TextureCache soulboundL = new TextureCache("images/pcl/cardui/core/1024/Soulbound.png");
        public static final TextureCache unique = new TextureCache("images/pcl/cardui/core/Unique.png");
        public static final TextureCache uniqueL = new TextureCache("images/pcl/cardui/core/1024/Unique.png");
    }

    public static class CardAffinity {
        public static final TextureCache blue = new TextureCache("images/pcl/cardui/affinities/A-B.png", true);
        public static final TextureCache green = new TextureCache("images/pcl/cardui/affinities/A-G.png", true);
        public static final TextureCache orange = new TextureCache("images/pcl/cardui/affinities/A-O.png", true);
        public static final TextureCache purple = new TextureCache("images/pcl/cardui/affinities/A-P.png", true);
        public static final TextureCache red = new TextureCache("images/pcl/cardui/affinities/A-R.png", true);
        public static final TextureCache silver = new TextureCache("images/pcl/cardui/affinities/A-S.png", true);
        public static final TextureCache yellow = new TextureCache("images/pcl/cardui/affinities/A-Y.png", true);
        public static final TextureCache general = new TextureCache("images/pcl/cardui/affinities/General.png", true);
        public static final TextureCache star = new TextureCache("images/pcl/cardui/affinities/Star.png", true);
        public static final TextureCache starBg = new TextureCache("images/pcl/cardui/affinities/Star_BG.png", true);
        public static final TextureCache starFg = new TextureCache("images/pcl/cardui/affinities/Star_FG.png", true);
        public static final TextureCache unknown = new TextureCache("images/pcl/cardui/affinities/Unknown.png", true);
    }

    public static class Badges {
        public static final TextureCache autoplay = new TextureCache("images/pcl/cardui/badges/Autoplay.png");
        public static final TextureCache baseInfinite = new TextureCache("images/pcl/cardui/badges/Base_Infinite.png");
        public static final TextureCache baseMulti = new TextureCache("images/pcl/cardui/badges/Base_Multi.png");
        public static final TextureCache bounce = new TextureCache("images/pcl/cardui/badges/Bounce.png");
        public static final TextureCache delayed = new TextureCache("images/pcl/cardui/badges/Delayed.png");
        public static final TextureCache ephemeral = new TextureCache("images/pcl/cardui/badges/Ephemeral.png");
        public static final TextureCache ethereal = new TextureCache("images/pcl/cardui/badges/Ethereal.png");
        public static final TextureCache exhaust = new TextureCache("images/pcl/cardui/badges/Exhaust.png");
        public static final TextureCache fleeting = new TextureCache("images/pcl/cardui/badges/Fleeting.png");
        public static final TextureCache fragile = new TextureCache("images/pcl/cardui/badges/Fragile.png");
        public static final TextureCache grave = new TextureCache("images/pcl/cardui/badges/Grave.png");
        public static final TextureCache haste = new TextureCache("images/pcl/cardui/badges/Haste.png");
        public static final TextureCache innate = new TextureCache("images/pcl/cardui/badges/Innate.png");
        public static final TextureCache loyal = new TextureCache("images/pcl/cardui/badges/Loyal.png");
        public static final TextureCache purge = new TextureCache("images/pcl/cardui/badges/Purge.png");
        public static final TextureCache recast = new TextureCache("images/pcl/cardui/badges/Recast.png");
        public static final TextureCache retain = new TextureCache("images/pcl/cardui/badges/Retain.png");
        public static final TextureCache suspensive = new TextureCache("images/pcl/cardui/badges/Suspensive.png");
        public static final TextureCache unplayable = new TextureCache("images/pcl/cardui/badges/Unplayable.png");
    }

    public static class Tooltips {
        public static final TextureCache block = new TextureCache("images/pcl/cardui/tooltips/Block.png");
        public static final TextureCache brutal = new TextureCache("images/pcl/cardui/tooltips/BrutalDamage.png");
        public static final TextureCache cooldown = new TextureCache("images/pcl/cardui/tooltips/Cooldown.png");
        public static final TextureCache damage = new TextureCache("images/pcl/cardui/tooltips/NormalDamage.png");
        public static final TextureCache dark = new TextureCache("images/pcl/cardui/tooltips/Dark.png");
        public static final TextureCache frost = new TextureCache("images/pcl/cardui/tooltips/Frost.png");
        public static final TextureCache gold = new TextureCache("images/pcl/cardui/tooltips/Gold.png");
        public static final TextureCache lightning = new TextureCache("images/pcl/cardui/tooltips/Lightning.png");
        public static final TextureCache magic = new TextureCache("images/pcl/cardui/tooltips/MagicDamage.png");
        public static final TextureCache orbSlot = new TextureCache("images/pcl/cardui/tooltips/OrbSlot.png");
        public static final TextureCache piercing = new TextureCache("images/pcl/cardui/tooltips/PiercingDamage.png");
        public static final TextureCache plasma = new TextureCache("images/pcl/cardui/tooltips/Plasma.png");
        public static final TextureCache ranged = new TextureCache("images/pcl/cardui/tooltips/RangedDamage.png");
        public static final TextureCache tempHP = new TextureCache("images/pcl/cardui/tooltips/TempHP.png");
    }

    public static class Types {
        public static final TextureCache blessing = new TextureCache("images/pcl/cardui/types/Blessing.png", true);
        public static final TextureCache summon = new TextureCache("images/pcl/cardui/types/Summon.png", true);
    }

    public static class Monsters {
        public static final TextureCache emptyShadow = new TextureCache(EMPTY_SHADOW);
        public static final TextureCache metal = new TextureCache("images/pcl/monsters/Metal.png");
    }

    public static class Effects {
        public static final TextureCache airSlice = new TextureCache("images/pcl/effects/AirSlice.png");
        public static final TextureCache airTornado1 = new TextureCache("images/pcl/effects/AirTornado1.png");
        public static final TextureCache airTornado2 = new TextureCache("images/pcl/effects/AirTornado2.png");
        public static final TextureCache airTrail1 = new TextureCache("images/pcl/effects/AirTrail1.png");
        public static final TextureCache airTrail2 = new TextureCache("images/pcl/effects/AirTrail2.png");
        public static final TextureCache airTrail3 = new TextureCache("images/pcl/effects/AirTrail3.png");
        public static final TextureCache circle = new TextureCache("images/pcl/effects/Circle.png");
        public static final TextureCache circle2 = new TextureCache("images/pcl/effects/Circle2.png");
        public static final TextureCache dark1 = new TextureCache("images/pcl/effects/Dark1.png");
        public static final TextureCache dark2 = new TextureCache("images/pcl/effects/Dark2.png");
        public static final TextureCache dark3 = new TextureCache("images/pcl/effects/Dark3.png");
        public static final TextureCache dark4 = new TextureCache("images/pcl/effects/Dark4.png");
        public static final TextureCache dark5 = new TextureCache("images/pcl/effects/Dark5.png");
        public static final TextureCache droplet = new TextureCache("images/pcl/effects/Droplet.png");
        public static final TextureCache earthParticle1 = new TextureCache("images/pcl/effects/EarthParticle1.png");
        public static final TextureCache earthParticle2 = new TextureCache("images/pcl/effects/EarthParticle2.png");
        public static final TextureCache earthParticle3 = new TextureCache("images/pcl/effects/EarthParticle3.png");
        public static final TextureCache electric1 = new TextureCache("images/pcl/effects/Electric1.png");
        public static final TextureCache electric2 = new TextureCache("images/pcl/effects/Electric2.png");
        public static final TextureCache electric3 = new TextureCache("images/pcl/effects/Electric3.png");
        public static final TextureCache electric4 = new TextureCache("images/pcl/effects/Electric4.png");
        public static final TextureCache electric5 = new TextureCache("images/pcl/effects/Electric5.png");
        public static final TextureCache electric6 = new TextureCache("images/pcl/effects/Electric6.png");
        public static final TextureCache electric7 = new TextureCache("images/pcl/effects/Electric7.png");
        public static final TextureCache frostSnow1 = new TextureCache("images/pcl/effects/FrostSnow1.png");
        public static final TextureCache frostSnow2 = new TextureCache("images/pcl/effects/FrostSnow2.png");
        public static final TextureCache frostSnow3 = new TextureCache("images/pcl/effects/FrostSnow3.png");
        public static final TextureCache frostSnow4 = new TextureCache("images/pcl/effects/FrostSnow4.png");
        public static final TextureCache hexagon = new TextureCache("images/pcl/effects/Hexagon.png");
        public static final TextureCache psi = new TextureCache("images/pcl/effects/Psi.png");
        public static final TextureCache punch = new TextureCache("images/pcl/effects/Punch.png");
        public static final TextureCache shot = new TextureCache("images/pcl/effects/Shot.png");
        public static final TextureCache slice = new TextureCache("images/pcl/effects/Slice.png");
        public static final TextureCache smoke1 = new TextureCache("images/pcl/effects/Smoke1.png");
        public static final TextureCache smoke2 = new TextureCache("images/pcl/effects/Smoke2.png");
        public static final TextureCache smoke3 = new TextureCache("images/pcl/effects/Smoke3.png");
        public static final TextureCache spark1 = new TextureCache("images/pcl/effects/Spark1.png");
        public static final TextureCache spark2 = new TextureCache("images/pcl/effects/Spark2.png");
        public static final TextureCache waterBubble = new TextureCache("images/pcl/effects/WaterBubble.png");
        public static final TextureCache waterSplash1 = new TextureCache("images/pcl/effects/WaterSplash1.png");
        public static final TextureCache waterSplash2 = new TextureCache("images/pcl/effects/WaterSplash2.png");
        public static final TextureCache waterSplash3 = new TextureCache("images/pcl/effects/WaterSplash3.png");
    }

    public static class Tutorial {
        public static final TextureCache affTut01 = new TextureCache("images/pcl/ui/tutorial/afftut01.png", true);
        public static final TextureCache augTut01 = new TextureCache("images/pcl/ui/tutorial/augtut01.png", true);
        public static final TextureCache augTut02 = new TextureCache("images/pcl/ui/tutorial/augtut02.png", true);
        public static final TextureCache augTut03 = new TextureCache("images/pcl/ui/tutorial/augtut03.png", true);
        public static final TextureCache augTut04 = new TextureCache("images/pcl/ui/tutorial/augtut04.png", true);
        public static final TextureCache augTut05 = new TextureCache("images/pcl/ui/tutorial/augtut05.png", true);
        public static final TextureCache sumTut01 = new TextureCache("images/pcl/ui/tutorial/sumtut01.png", true);
        public static final TextureCache sumTut02 = new TextureCache("images/pcl/ui/tutorial/sumtut02.png", true);
        public static final TextureCache sumTut03 = new TextureCache("images/pcl/ui/tutorial/sumtut03.png", true);
        public static final TextureCache sumTut04 = new TextureCache("images/pcl/ui/tutorial/sumtut04.png", true);
        public static final TextureCache sumTut05 = new TextureCache("images/pcl/ui/tutorial/sumtut05.png", true);
        public static final TextureCache sumTut06 = new TextureCache("images/pcl/ui/tutorial/sumtut06.png", true);
    }
}