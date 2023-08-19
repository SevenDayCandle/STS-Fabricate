package pinacolada.misc;

import extendedui.EUIGameUtils;
import extendedui.EUIUtils;
import extendedui.exporter.EUIExporterRow;
import pinacolada.cards.base.PCLCard;
import pinacolada.cards.base.PCLCardData;
import pinacolada.cards.base.fields.PCLAffinity;
import pinacolada.cards.base.fields.PCLCardDataAffinityGroup;
import pinacolada.skills.PSkill;

public class PCLCardExportRow extends EUIExporterRow {
    public int form;
    public String series;
    public String type;
    public String rarity;
    public String cardTarget;
    public String attackType;
    public int damage;
    public int damageUpgrade;
    public int block;
    public int blockUpgrade;
    public int tempHP;
    public int tempHPUpgrade;
    public int heal;
    public int healUpgrade;
    public int hitCount;
    public int hitCountUpgrade;
    public int rightCount;
    public int rightCountUpgrade;
    public int cost;
    public int costUpgrade;
    public int red;
    public int redUpgrade;
    public int green;
    public int greenUpgrade;
    public int blue;
    public int blueUpgrade;
    public int orange;
    public int orangeUpgrade;
    public int light;
    public int lightUpgrade;
    public int dark;
    public int darkUpgrade;
    public int silver;
    public int silverUpgrade;
    public int star;
    public int starUpgrade;
    public String tags;
    public String effects;

    public PCLCardExportRow(PCLCard card) {
        super(card.cardData.ID, EUIGameUtils.getModID(card), String.valueOf(card.cardData.cardColor), card.name);
        PCLCardData data = card.cardData;
        int form = card.getForm();
        this.form = form;
        series = data.loadout != null ? data.loadout.getName() : null;
        type = String.valueOf(data.cardType);
        rarity = String.valueOf(data.cardRarity);
        color = String.valueOf(data.cardColor);
        cardTarget = String.valueOf(data.cardTarget);
        attackType = String.valueOf(data.attackType);
        damage = data.getDamage(form);
        damageUpgrade = data.getDamageUpgrade(form);
        block = data.getBlock(form);
        blockUpgrade = data.getBlockUpgrade(form);
        tempHP = data.getMagicNumber(form);
        tempHPUpgrade = data.getMagicNumberUpgrade(form);
        heal = data.getHp(form);
        healUpgrade = data.getHpUpgrade(form);
        hitCount = data.getHitCount(form);
        hitCountUpgrade = data.getHitCountUpgrade(form);
        rightCount = data.getRightCount(form);
        rightCountUpgrade = data.getRightCountUpgrade(form);
        cost = data.getCost(form);
        costUpgrade = data.getCostUpgrade(form);

        PCLCardDataAffinityGroup affinities = data.affinities;
        red = affinities.getLevel(PCLAffinity.Red, form);
        redUpgrade = affinities.getUpgrade(PCLAffinity.Red, form);
        green = affinities.getLevel(PCLAffinity.Green, form);
        greenUpgrade = affinities.getUpgrade(PCLAffinity.Green, form);
        blue = affinities.getLevel(PCLAffinity.Blue, form);
        blueUpgrade = affinities.getUpgrade(PCLAffinity.Blue, form);
        orange = affinities.getLevel(PCLAffinity.Orange, form);
        orangeUpgrade = affinities.getUpgrade(PCLAffinity.Orange, form);
        light = affinities.getLevel(PCLAffinity.Yellow, form);
        lightUpgrade = affinities.getUpgrade(PCLAffinity.Yellow, form);
        dark = affinities.getLevel(PCLAffinity.Purple, form);
        darkUpgrade = affinities.getUpgrade(PCLAffinity.Purple, form);
        silver = affinities.getLevel(PCLAffinity.Silver, form);
        silverUpgrade = affinities.getUpgrade(PCLAffinity.Silver, form);
        star = affinities.getLevel(PCLAffinity.Star, form);
        starUpgrade = affinities.getUpgrade(PCLAffinity.Star, form);

        tags = EUIUtils.joinStringsMap("/", tagInfo -> String.valueOf(tagInfo.tag), data.tags.values());
        effects = EUIUtils.joinStringsMap(PSkill.EFFECT_SEPARATOR, PSkill::getExportText, card.getFullEffects());
    }

    @Override
    public int compareTo(EUIExporterRow o) {
        int value = super.compareTo(o);
        return value == 0 && o instanceof PCLCardExportRow ? form - ((PCLCardExportRow) o).form : value;
    }
}
