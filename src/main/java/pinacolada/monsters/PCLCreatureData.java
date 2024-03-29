package pinacolada.monsters;

import basemod.BaseMod;
import com.megacrit.cardcrawl.helpers.MonsterHelper;
import com.megacrit.cardcrawl.localization.MonsterStrings;
import com.megacrit.cardcrawl.monsters.MonsterGroup;
import pinacolada.misc.PCLGenericData;
import pinacolada.resources.PCLResources;
import pinacolada.resources.PGR;

public class PCLCreatureData extends PCLGenericData<PCLCreature> {
    public final MonsterStrings strings;
    public float hbX;
    public float hbY;
    public float hbW = 100;
    public float hbH = 100;
    public int hp = 1;
    public String imgUrl;

    public PCLCreatureData(Class<? extends PCLCreature> type, PCLResources<?, ?, ?, ?> resources) {
        super(resources.createID(type.getSimpleName()), type, resources);
        this.strings = PGR.getMonsterStrings(ID);
    }

    public void addMonster() {
        BaseMod.addMonster(ID, this::create);
    }

    public MonsterGroup getEncounter() {
        return MonsterHelper.getEncounter(ID);
    }

    public PCLCreatureData setHb(float hbX, float hbY, float hbW, float hbH) {
        this.hbX = hbX;
        this.hbY = hbY;
        this.hbW = hbW;
        this.hbH = hbH;
        return this;
    }

    public PCLCreatureData setHp(int hp) {
        this.hp = hp;
        return this;
    }

    public PCLCreatureData setImage(String imgUrl) {
        this.imgUrl = imgUrl;
        return this;
    }
}
