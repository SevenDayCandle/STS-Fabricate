package pinacolada.resources;

import com.evacipated.cardcrawl.modthespire.lib.SpireEnum;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.CardGroup;
import com.megacrit.cardcrawl.rewards.RewardItem;
import com.megacrit.cardcrawl.screens.mainMenu.MenuButton;
import com.megacrit.cardcrawl.screens.mainMenu.MenuPanelScreen;

public class PCLEnum {
    public static class Buttons {
        @SpireEnum
        public static MenuButton.ClickResult CUSTOM;
    }

    public static class Menus {
        @SpireEnum
        public static MenuPanelScreen.PanelScreen CUSTOM;
    }

    public static class Rewards {
        @SpireEnum
        public static RewardItem.RewardType AUGMENT;
    }

    public static class CardGroupType {
        @SpireEnum
        public static CardGroup.CardGroupType PURGED_CARDS;
    }

    public static class CardRarity {
        @SpireEnum
        public static AbstractCard.CardRarity LEGENDARY;
        @SpireEnum
        public static AbstractCard.CardRarity SECRET;
    }

    public static class CardType {
        @SpireEnum
        public static AbstractCard.CardType SUMMON;
    }

    public static class AttackEffect {
        @SpireEnum
        public static AbstractGameAction.AttackEffect BITE;
        @SpireEnum
        public static AbstractGameAction.AttackEffect BLEED;
        @SpireEnum
        public static AbstractGameAction.AttackEffect BURN;
        @SpireEnum
        public static AbstractGameAction.AttackEffect CLASH;
        @SpireEnum
        public static AbstractGameAction.AttackEffect CLAW;
        @SpireEnum
        public static AbstractGameAction.AttackEffect DAGGER;
        @SpireEnum
        public static AbstractGameAction.AttackEffect DARKNESS;
        @SpireEnum
        public static AbstractGameAction.AttackEffect EARTH;
        @SpireEnum
        public static AbstractGameAction.AttackEffect ELECTRIC;
        @SpireEnum
        public static AbstractGameAction.AttackEffect GHOST;
        @SpireEnum
        public static AbstractGameAction.AttackEffect GUNSHOT;
        @SpireEnum
        public static AbstractGameAction.AttackEffect ICE;
        @SpireEnum
        public static AbstractGameAction.AttackEffect PUNCH;
        @SpireEnum
        public static AbstractGameAction.AttackEffect PSYCHOKINESIS;
        @SpireEnum
        public static AbstractGameAction.AttackEffect SMALL_LASER;
        @SpireEnum
        public static AbstractGameAction.AttackEffect SMALL_EXPLOSION;
        @SpireEnum
        public static AbstractGameAction.AttackEffect SPARK;
        @SpireEnum
        public static AbstractGameAction.AttackEffect WATER;
        @SpireEnum
        public static AbstractGameAction.AttackEffect WAVE;
        @SpireEnum
        public static AbstractGameAction.AttackEffect WIND;
    }
}
