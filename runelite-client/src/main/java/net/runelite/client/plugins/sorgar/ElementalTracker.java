package net.runelite.client.plugins.sorgar;

import lombok.AccessLevel;
import lombok.Getter;
import net.runelite.api.GameObject;
import net.runelite.api.NPC;
import net.runelite.api.coords.WorldArea;
import net.runelite.api.coords.WorldPoint;
import org.apache.commons.lang3.tuple.Pair;

import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;

public class ElementalTracker {

    private HashSet<Integer> elementalIds = new HashSet<>();

    private HashSet<Integer> wallIds = new HashSet<>();

    private HashMap<NPC, Pair<WorldArea, Integer>> elementals = new HashMap<>();

    @Getter(AccessLevel.PACKAGE)
    private HashSet<WorldPoint> walls = new HashSet<>();

    private Integer tickCounter = 0;

    public ElementalTracker() {
        // Add elemental ids
        this.elementalIds.addAll(Arrays.asList(
                2956,
                2957,
                2958,
                2959,
                2960,
                2961,
                2962
//                2963 // I don't think this one can actually get you
        ));

        // Add wall ids
        this.wallIds.addAll(Arrays.asList(
                12721,
                12726,
                12727,
                12728,
                12729
        ));
    }

    public boolean isWall(GameObject go) {
        return this.wallIds.contains(go.getId());
    }

    public boolean isWall(WorldPoint wp) {
        return this.walls.contains(wp);
    }

    public boolean isElemental(NPC npc) {
        return this.elementalIds.contains(npc.getId());
    }

    public void add(NPC npc) {
        if (this.isElemental(npc)) {
            this.elementals.put(
                    npc,
                    Pair.of(npc.getWorldArea(), npc.getOrientation())
            );
        }
    }

    public void add(GameObject go) {
        if (this.isWall(go)) {
            this.walls.add(go.getWorldLocation());
        }
    }

    public void remove(NPC npc) {
        this.elementals.remove(npc);
    }

    public void remove(GameObject go) {
        this.walls.remove(go.getWorldLocation());
    }

    public boolean shouldCheckMoved() {
        return true;
//        boolean result = this.tickCounter == 0;
//
//        if (++this.tickCounter >= 20) {
//            this.tickCounter = 0;
//        }
//
//        return result;
    }

    public HashSet<NPC> getMoved() {
        HashSet<NPC> moved = new HashSet<>();

        this.elementals.forEach((npc, pair) -> {
            if (npc.getWorldArea() != pair.getLeft() || npc.getOrientation() != pair.getRight()) {
                // Update hash map value
                this.elementals.put(
                        npc,
                        Pair.of(npc.getWorldArea(), npc.getOrientation())
                );

                // Add to moved
                moved.add(npc);
            }
        });

        return moved;
    }
}
