package net.runelite.client.plugins.sorgar;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import net.runelite.api.*;
import net.runelite.api.coords.WorldPoint;
import net.runelite.api.events.*;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;
import net.runelite.client.ui.overlay.OverlayManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import java.util.*;

@Slf4j
@PluginDescriptor(
	name = "Sorceress's Garden",
	description = "Hi",
	tags = {"overlay", "sorceress", "garden"}
)
public class SorGarPlugin extends Plugin
{
	private static final Logger logger = LoggerFactory.getLogger(SorGarPlugin.class);

	@Getter(AccessLevel.PACKAGE)
	private ArrayList<SorGarPath> paths = new ArrayList<>();

	@Getter(AccessLevel.PACKAGE)
	private ElementalTracker elementalTracker = new ElementalTracker();

	@Inject
	private Client client;

	@Inject
	private OverlayManager overlayManager;

	@Inject
	private SorGarOverlay overlay;

	@Override
	protected void startUp()
	{
		// Add overlay
		overlayManager.add(overlay);

		// Spring Path 1
		this.paths.add(new SorGarPath(
			this.client,
			this,
			new WorldPoint(2923, 5465, 0),
			new WorldPoint(2922, 5471, 0),
			new WorldPoint(2922, 5470, 0),
			new WorldPoint(2922, 5469, 0),
			new WorldPoint(2922, 5468, 0),
			//			new WorldPoint(2922, 5465, 0)  // Character will run diagonal so we never touch this tile
			new WorldPoint(2922, 5467, 0),
	new WorldPoint(2922, 5466, 0)
		));

		// Spring Path 2
		this.paths.add(new SorGarPath(
			this.client,
			this,
			new WorldPoint(2923, 5459, 0),
			new WorldPoint(2922, 5465, 0),
			new WorldPoint(2922, 5464, 0),
			new WorldPoint(2922, 5463, 0),
			new WorldPoint(2922, 5462, 0),
			new WorldPoint(2922, 5461, 0),
			new WorldPoint(2922, 5460, 0),
			new WorldPoint(2922, 5459, 0)
		));

		// Spring Path 3
		this.paths.add(new SorGarPath(
			this.client,
			this,
			new WorldPoint(2926, 5468, 0),
			new WorldPoint(2924, 5459, 0),
			new WorldPoint(2924, 5460, 0),
			new WorldPoint(2924, 5461, 0),
			new WorldPoint(2924, 5462, 0),
			new WorldPoint(2924, 5463, 0),
			new WorldPoint(2925, 5463, 0),
			new WorldPoint(2925, 5464, 0),
			new WorldPoint(2925, 5455, 0),
			new WorldPoint(2925, 5456, 0),
			new WorldPoint(2925, 5457, 0),
			new WorldPoint(2925, 5458, 0)
		));

		// Spring Path 4
		this.paths.add(new SorGarPath(
				this.client,
				this,
				new WorldPoint(2928, 5470, 0),
				new WorldPoint(2925, 5468, 0),
				new WorldPoint(2925, 5469, 0),
				new WorldPoint(2925, 5470, 0),
				new WorldPoint(2926, 5470, 0),
				new WorldPoint(2927, 5470, 0)
		));

		// Spring Path 5
		this.paths.add(new SorGarPath(
				this.client,
				this,
				new WorldPoint(2930, 5470, 0),
				new WorldPoint(2928, 5469, 0),
				new WorldPoint(2929, 5469, 0),
				new WorldPoint(2930, 5469, 0)
		));

		// Spring Path 6
		this.paths.add(new SorGarPath(
				this.client,
				this,
				new WorldPoint(2932, 5465, 0),
//				new WorldPoint(2930, 5469, 0), // Character will run diagonal so we never touch this tile
				new WorldPoint(2931, 5469, 0),
				new WorldPoint(2932, 5469, 0),
				new WorldPoint(2933, 5469, 0),
				new WorldPoint(2933, 5468, 0)
		));
	}

	@Override
	protected void shutDown()
	{
		// Remove overlay
		overlayManager.remove(overlay);
	}

	public void onNpcSpawned(NpcSpawned npcSpawned)
	{
		NPC npc = npcSpawned.getNpc();

//		logger.debug("Spawned: " + npc.getId());

		// Track elemental
		this.elementalTracker.add(npc);
	}

	public void onNpcDespawned(NpcDespawned npcDespawned)
	{
		NPC npc = npcDespawned.getNpc();

//		logger.debug("Despawned: " + npc.getId());

		// Remove elemental from the path
		for (SorGarPath path : this.paths) {
			path.remove(npc);
		}

		// Untrack elemental
		this.elementalTracker.remove(npc);
	}

	public void onGameObjectSpawned(GameObjectSpawned goSpawned)
	{
		GameObject go = goSpawned.getGameObject();

		// Track wall
		this.elementalTracker.add(go);
	}

	public void onGameObjectDespawned(GameObjectDespawned goDespawned)
	{
		GameObject go = goDespawned.getGameObject();

		// Untrack wall
		this.elementalTracker.remove(go);
	}

	public void onGameTick(GameTick gameTick)
	{
		if (!this.elementalTracker.shouldCheckMoved()) {
			return;
		}

		for (NPC npc : this.elementalTracker.getMoved()) {
			this.onElementalMoved(npc);
		}
	}

	private void onElementalMoved(NPC npc)
	{
//		logger.debug("Moved: " + npc.getId() + " " + npc.getOrientation());

		for (SorGarPath path : this.paths) {
			path.setVisibility(npc);
		}
	}


	public void onGameStateChanged(GameStateChanged event)
	{
		if (event.getGameState() == GameState.LOGIN_SCREEN || event.getGameState() == GameState.HOPPING) {
			for (SorGarPath p : this.paths) {
				p.clear();
			}
		}
	}
}
