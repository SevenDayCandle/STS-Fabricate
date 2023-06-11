package pinacolada.monsters;

import basemod.BaseMod;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.localization.MonsterStrings;
import com.megacrit.cardcrawl.map.MapEdge;
import com.megacrit.cardcrawl.map.MapRoomNode;
import com.megacrit.cardcrawl.rooms.MonsterRoom;
import pinacolada.misc.PCLGenericData;
import pinacolada.resources.PCLResources;
import pinacolada.resources.PGR;

import java.util.ArrayList;

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

    public void forceEncounter() {
        MapRoomNode cur = AbstractDungeon.currMapNode;
        if (cur == null) {
            return;
        }
        if (AbstractDungeon.getCurrRoom() instanceof MonsterRoom) {
            // Note: AbstractDungeon.nextRoomTransition() will remove the encounter of the current room from the monster list
            // so if we want the new encounter to be in the front afterwards for our new MonsterRoom, we should insert the encounter at position 1, not 0
            AbstractDungeon.monsterList.add(1, ID);
        } else {
            AbstractDungeon.monsterList.add(0, ID);
        }

        MapRoomNode node = new MapRoomNode(cur.x, cur.y);
        node.room = new MonsterRoom();

        ArrayList<MapEdge> curEdges = cur.getEdges();
        for (MapEdge edge : curEdges) {
            node.addEdge(edge);
        }

        AbstractDungeon.nextRoom = node;
        AbstractDungeon.nextRoomTransitionStart();
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
