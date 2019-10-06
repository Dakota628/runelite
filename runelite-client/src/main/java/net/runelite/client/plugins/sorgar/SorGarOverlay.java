package net.runelite.client.plugins.sorgar;

import net.runelite.api.Client;
import net.runelite.api.Perspective;
import net.runelite.api.coords.LocalPoint;
import net.runelite.api.coords.WorldPoint;
import net.runelite.client.ui.overlay.Overlay;
import net.runelite.client.ui.overlay.OverlayLayer;
import net.runelite.client.ui.overlay.OverlayPosition;
import net.runelite.client.ui.overlay.OverlayUtil;

import javax.inject.Inject;
import java.awt.*;

public class SorGarOverlay extends Overlay {

    private final Client client;
    private final SorGarPlugin plugin;

    @Inject
    private SorGarOverlay(Client client, SorGarPlugin plugin)
    {
        setPosition(OverlayPosition.DYNAMIC);
        setLayer(OverlayLayer.ABOVE_SCENE);

        this.client = client;
        this.plugin = plugin;
    }

    @Override
    public Dimension render(Graphics2D graphics)
    {
        for (SorGarPath path : this.plugin.getPaths()) {
            // Get tile for path
            WorldPoint p = path.getTileToRender();

            //Don't draw spawns if Player is not in range
            if (p.distanceTo(client.getLocalPlayer().getWorldLocation()) >= 32) {
                continue;
            }

            // Get local point
            LocalPoint localPoint = LocalPoint.fromWorld(client, p);
            if (localPoint == null) {
                continue;
            }

            // Get/render poly
            Polygon poly = Perspective.getCanvasTilePoly(client, localPoint);

            if (poly != null) {
                OverlayUtil.renderPolygon(graphics, poly, path.isVisible() ? Color.RED : Color.GREEN);
            }
        }

        return null;
    }

}
