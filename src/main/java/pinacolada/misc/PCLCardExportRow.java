package pinacolada.misc;

import extendedui.EUIGameUtils;
import extendedui.EUIUtils;
import extendedui.exporter.EUIExporter;
import extendedui.exporter.EUIExporterRow;
import extendedui.utilities.TargetFilter;
import pinacolada.cards.base.PCLCard;
import pinacolada.cards.base.PCLCardData;
import pinacolada.cards.base.fields.CardFlag;
import pinacolada.skills.PSkill;

public class PCLCardExportRow extends EUIExporterRow {
    public int Form;
    public String Set;
    public String Type;
    public String Rarity;
    public String Card_Target;
    public String Attack_Type;
    public int Cost;
    public int Cost_Upgrade;
    public int Damage;
    public int Damage_Upgrade;
    public int Hit_Count;
    public int Hit_Count_Upgrade;
    public int Block;
    public int Block_Upgrade;
    public int Block_Count;
    public int Block_Count_Upgrade;
    public int Magic_Number;
    public int Magic_Number_Upgrade;
    public int HP;
    public int HP_Upgrade;
    public Object Affinities;
    public Object Tags;
    public Object Flags;
    public String Effects;

    public PCLCardExportRow(PCLCard card, EUIExporter.ExportType format) {
        super(card.cardData.ID, card, card.cardData.cardColor, card.name);
        PCLCardData data = card.cardData;
        int form = card.getForm();
        this.Form = form;
        Set = data.loadout != null ? data.loadout.getName() : EUIUtils.EMPTY_STRING;
        Type = EUIGameUtils.textForType(data.cardType);
        Rarity = EUIGameUtils.textForRarity(data.cardRarity);
        Card_Target = TargetFilter.forCard(card).name;
        Attack_Type = data.attackType.name();
        Damage = data.getDamage(form);
        Damage_Upgrade = data.getDamageUpgrade(form);
        Block = data.getBlock(form);
        Block_Upgrade = data.getBlockUpgrade(form);
        Magic_Number = data.getMagicNumber(form);
        Magic_Number_Upgrade = data.getMagicNumberUpgrade(form);
        HP = data.getHp(form);
        HP_Upgrade = data.getHpUpgrade(form);
        Hit_Count = data.getHitCount(form);
        Hit_Count_Upgrade = data.getHitCountUpgrade(form);
        Block_Count = data.getRightCount(form);
        Block_Count_Upgrade = data.getRightCountUpgrade(form);
        Cost = data.getCost(form);
        Cost_Upgrade = data.getCostUpgrade(form);
        if (format == EUIExporter.ExportType.CSV) {
            Affinities = EUIUtils.joinStringsMap("/", af -> af.tooltip.title, data.affinities.getAffinities());
            Tags = EUIUtils.joinStringsMap("/", tagInfo -> tagInfo.tag.getName(), data.tags.values());
            Flags = EUIUtils.joinStringsMap("/", CardFlag::getName, data.flags);
        }
        else {
            Affinities = data.affinities.getAffinities();
            Tags = data.tags.keySet();
            Flags = data.flags;
        }
        Effects = EUIUtils.joinStringsMap(" ", PSkill::getExportText, card.getFullEffects());
    }

    @Override
    public int compareTo(EUIExporterRow o) {
        int value = super.compareTo(o);
        return value == 0 && o instanceof PCLCardExportRow ? Form - ((PCLCardExportRow) o).Form : value;
    }
}
