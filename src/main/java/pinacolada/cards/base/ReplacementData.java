package pinacolada.cards.base;

import basemod.abstracts.CustomCard;
import com.badlogic.gdx.graphics.Texture;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.blue.Defend_Blue;
import com.megacrit.cardcrawl.cards.blue.Strike_Blue;
import com.megacrit.cardcrawl.cards.green.Defend_Green;
import com.megacrit.cardcrawl.cards.green.Strike_Green;
import com.megacrit.cardcrawl.cards.purple.Defend_Watcher;
import com.megacrit.cardcrawl.cards.purple.Strike_Purple;
import com.megacrit.cardcrawl.cards.red.Anger;
import com.megacrit.cardcrawl.cards.red.Armaments;
import com.megacrit.cardcrawl.cards.red.Defend_Red;
import com.megacrit.cardcrawl.cards.red.Strike_Red;
import extendedui.utilities.ColoredTexture;
import pinacolada.cards.base.fields.PCLAttackType;
import pinacolada.cards.base.fields.PCLCardTarget;
import pinacolada.cards.base.tags.PCLCardTag;
import pinacolada.cards.pcl.special.QuestionMark;
import pinacolada.skills.PMove;
import pinacolada.skills.PSkill;
import pinacolada.skills.skills.special.primary.PCardPrimary_DealDamage;
import pinacolada.skills.skills.special.primary.PCardPrimary_GainBlock;

// TODO Try to construct effects from card description
public class ReplacementData extends PCLDynamicData {
    public final String originalID;

    public ReplacementData(AbstractCard card, boolean copyNumbers) {
        this(card, card.name, card.rawDescription, copyNumbers);
    }

    public ReplacementData(AbstractCard original, String name, String text, boolean copyNumbers) {
        super(original.cardID);
        this.originalID = original.cardID;

        if (copyNumbers) {
            AbstractCard upgradedCopy = original.makeStatEquivalentCopy();
            upgradedCopy.upgrade();

            setDamage(original.baseDamage, upgradedCopy.baseDamage - original.baseDamage)
                    .setBlock(original.baseBlock, upgradedCopy.baseBlock - original.baseBlock)
                    .setMagicNumber(original.baseMagicNumber, upgradedCopy.baseMagicNumber - original.baseMagicNumber)
                    .setCosts(original.cost)
                    .setCostUpgrades(upgradedCopy.cost - original.cost);
            if (original.type.equals(AbstractCard.CardType.ATTACK)) {
                setAttackType(PCLAttackType.Normal);
            }

            for (PCLCardTag tag : PCLCardTag.getAll()) {
                if (tag.has(original)) {
                    this.tags.put(tag, tag.make());
                }
            }

        }
        else {
            setCosts(-2).setCostUpgrades(0);
        }

        // Custom card paths are recorded in CustomCard.imgMap
        String assetUrl = original.assetUrl;
        Texture cardTexture = CustomCard.imgMap.get(assetUrl);
        if (cardTexture != null) {
            setImage(new ColoredTexture(cardTexture), null);
        }
        else {
            setImagePathFromAtlasUrl(assetUrl);
        }

        PCLCardTarget ct = PCLCardTarget.Single;
        switch (original.target) {
            case NONE:
                ct = PCLCardTarget.None;
                break;
            case SELF:
                ct = PCLCardTarget.Self;
                break;
            case ALL:
            case ALL_ENEMY:
                ct = PCLCardTarget.AllEnemy;
                break;
        }
        setTarget(ct);
        setColor(original.color);
        setRarity(original.rarity);
        setType(original.type);
        setText(name, text, null);
    }

    public ReplacementData(AbstractCard card, String text, boolean copyNumbers) {
        this(card, card.name, text, copyNumbers);
    }

    protected static ReplacementData attackData(AbstractCard card, boolean copyNumbers) {
        return (ReplacementData) new ReplacementData(card, copyNumbers).setAttackSkill(new PCardPrimary_DealDamage());
    }

    protected static ReplacementData blockData(AbstractCard card, boolean copyNumbers) {
        return (ReplacementData) new ReplacementData(card, copyNumbers).setBlockSkill(new PCardPrimary_GainBlock());
    }

    // TODO add more stuff here
    public static ReplacementData getReplacementData(AbstractCard card, boolean copyNumbers) {
        switch (card.cardID) {
            case Strike_Red.ID:
            case Strike_Green.ID:
            case Strike_Blue.ID:
            case Strike_Purple.ID:
                return attackData(card, copyNumbers);
            case Defend_Red.ID:
            case Defend_Green.ID:
            case Defend_Blue.ID:
            case Defend_Watcher.ID:
                return blockData(card, copyNumbers);
            case Anger.ID:
                return attackData(card, copyNumbers).setPSkill(PMove.createDrawPile(1, Anger.ID));
            case Armaments.ID:
                return blockData(card, copyNumbers).setPSkill(PMove.upgrade(0));
        }
        return null;
    }

    public static PCLDynamicCard makeReplacement(AbstractCard card, boolean copyNumbers) {
        ReplacementData initial = getReplacementData(card, copyNumbers);
        if (initial != null) {
            return initial.create();
        }
        else {
            return new ReplacementData(card, copyNumbers).buildAsReplacement();
        }
    }

    public ReplacementData addPSkill(PSkill<?> effect) {
        super.addPSkill(effect, false);
        return this;
    }

    public ReplacementData setPSkill(PSkill<?>... effect) {
        super.setPSkill(effect);
        return this;
    }

    // Build a replacement card that invokes the source card directly
    public ReplacementCard buildAsReplacement() {
        if (strings == null) {
            setText("", "", "");
        }

        if (imagePath == null) {
            imagePath = QuestionMark.DATA.imagePath;
        }

        return new ReplacementCard(this);
    }
}