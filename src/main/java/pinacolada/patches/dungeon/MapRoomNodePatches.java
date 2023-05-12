package pinacolada.patches.dungeon;


import com.evacipated.cardcrawl.modthespire.lib.SpirePatch;
import com.evacipated.cardcrawl.modthespire.lib.SpirePostfixPatch;
import com.megacrit.cardcrawl.map.MapEdge;
import com.megacrit.cardcrawl.map.MapRoomNode;
import pinacolada.resources.PGR;

public class MapRoomNodePatches {

    @SpirePatch(
            clz = MapRoomNode.class,
            method = "isConnectedTo",
            paramtypez = {MapRoomNode.class}
    )
    public static class MapRoomNodePatches_IsConnectedTo {
        @SpirePostfixPatch
        public static boolean postfix(boolean __result, MapRoomNode __instance, MapRoomNode node) {
            if (PGR.dungeon.canJumpNextFloor()) {
                for (MapEdge edge : __instance.getEdges()) {
                    if (node.y == edge.dstY || PGR.dungeon.canJumpAnywhere()) {
                        return true;
                    }
                }
            }
            return __result;
        }
    }

    @SpirePatch(
            clz = MapRoomNode.class,
            method = "playNodeSelectedSound"
    )
    public static class MapRoomNodePatches_NodeSelected {
        @SpirePostfixPatch
        public static void postfix(MapRoomNode __instance) {
            PGR.dungeon.setJumpNextFloor(false);
        }
    }
}