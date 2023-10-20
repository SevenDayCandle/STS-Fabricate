package pinacolada.misc;

import extendedui.EUIGameUtils;
import extendedui.EUIUtils;
import extendedui.exporter.EUIExporter;
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
    public Object affinities;
    public Object tags;
    public String effects;

    public PCLCardExportRow(PCLCard card, EUIExporter.ExportType format) {
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
        if (format == EUIExporter.ExportType.CSV) {
            affinities = EUIUtils.joinStringsMap("/", af -> String.valueOf(af.tooltip.title), data.affinities.getAffinities());
            tags = EUIUtils.joinStringsMap("/", tagInfo -> String.valueOf(tagInfo.tag), data.tags.values());
        }
        else {
            affinities = data.affinities.getAffinities();
            tags = data.tags.keySet();
        }
        effects = EUIUtils.joinStringsMap(" ", PSkill::getExportText, card.getFullEffects());
    }

    @Override
    public int compareTo(EUIExporterRow o) {
        int value = super.compareTo(o);
        return value == 0 && o instanceof PCLCardExportRow ? form - ((PCLCardExportRow) o).form : value;
    }
}
