package net.runelite.client.plugins.sorgar;

import lombok.AccessLevel;
import lombok.Getter;
import net.runelite.api.Client;
import net.runelite.api.NPC;
import net.runelite.api.coords.WorldArea;
import net.runelite.api.coords.WorldPoint;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public class SorGarPath {
    private Client client;

    private SorGarPlugin plugin;

    @Getter(AccessLevel.PACKAGE)
    private Set<WorldPoint> path = new HashSet<>();

    @Getter(AccessLevel.PACKAGE)
    private WorldPoint tileToRender;

    @Getter(AccessLevel.PACKAGE)
    private Set<Integer> visibleTo = new HashSet<>();

    @Getter(AccessLevel.PACKAGE)
    Integer visionDist = 15;

    public SorGarPath(Client c, SorGarPlugin plugin, WorldPoint tileToRender, WorldPoint... points) {
        this.client = c;
        this.plugin = plugin;
        this.tileToRender = tileToRender;

        path.addAll(Arrays.asList(points));
    }

    private WorldPoint getNextPoint(WorldPoint p, Integer orientation) {
        if (orientation >= 0 && orientation < 512) {
            return new WorldPoint(p.getX(), p.getY() - 1, p.getPlane());
        }

        if (orientation >= 512 && orientation < 1024) {
            return new WorldPoint(p.getX() - 1, p.getY(), p.getPlane());
        }

        if (orientation >= 1024 && orientation < 1536) {
            return new WorldPoint(p.getX(), p.getY() + 1, p.getPlane());
        }

        if (orientation >= 1536) {
            return new WorldPoint(p.getX() + 1, p.getY(), p.getPlane());
        }

        return p;

//        switch (orientation) {
//            case 0:
//                return new WorldPoint(p.getX(), p.getY() - 1, p.getPlane());
//            case 512:
//                return new WorldPoint(p.getX() - 1, p.getY(), p.getPlane());
//            case 1024:
//                return new WorldPoint(p.getX(), p.getY() + 1, p.getPlane());
//            case 1536:
//                return new WorldPoint(p.getX() + 1, p.getY(), p.getPlane());
//            default:
//                return p;
//        }
    }

    private Set<WorldPoint> getNpcVision(NPC npc) {
        // Get orientation and point
        WorldPoint p = npc.getWorldLocation();
        Integer orientation = npc.getOrientation();

        // Create output
        Set<WorldPoint> vision = new HashSet<>();
        vision.add(p);

        for (int i = 0; i < this.visionDist; i++) {
            p = this.getNextPoint(p, orientation);

            if (this.plugin.getElementalTracker().isWall(p)) {
                return vision;
            }

            vision.add(p);
        }

        // Return output
        return vision;
    }

    private boolean npcCanSeePoint(NPC npc, WorldPoint p1) {
        for (WorldPoint p2 : this.getNpcVision(npc)) {
            if (p1.getX() == p2.getX() && p1.getY() == p2.getY() && p1.getPlane() == p2.getPlane()) {
                return true;
            }
        }

        return false;
    }

    private boolean npcCanSeePath(NPC npc) {
        for (WorldPoint p : this.path) {
            if (this.npcCanSeePoint(npc, p)) {
                return true;
            }
        }

        return false;
    }

    public void setVisibility(NPC npc) {
        if (this.npcCanSeePath(npc)) {
            visibleTo.add(npc.getId());
        } else {
            visibleTo.remove(npc.getId());
        }
    }

    public boolean isVisible() {
        return !visibleTo.isEmpty();
    }

    public void clear() {
        this.visibleTo.clear();
    }

    public void remove(NPC npc) {
        this.visibleTo.remove(npc.getId());
    }
}
