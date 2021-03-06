package net.slipcor.pvparena.modules.items;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;

import net.slipcor.pvparena.PVPArena;
import net.slipcor.pvparena.core.Config.CFG;
import net.slipcor.pvparena.loadables.ArenaModule;

public class Items extends ArenaModule {
	private int id = -1;

	public Items() {
		super("Items");
	}

	@Override
	public String version() {
		return "v1.0.1.59";
	}
	
	@Override
	public void displayInfo(CommandSender sender) {
		sender.sendMessage("interval: " + arena.getArenaConfig().getInt(CFG.MODULES_ITEMS_INTERVAL) +
				"items: " + arena.getArenaConfig().getString(CFG.MODULES_ITEMS_ITEMS));
	}

	@Override
	public boolean hasSpawn(String s) {
		return s.toLowerCase().startsWith("item");
	}

	@Override
	public void reset(boolean force) {
		Bukkit.getScheduler().cancelTask(id);
		id = -1;
	}

	@Override
	public void parseStart() {
		if (arena.getArenaConfig().getInt(CFG.MODULES_ITEMS_INTERVAL) > 0) {
			id = Bukkit.getScheduler()
					.scheduleSyncRepeatingTask(
							PVPArena.instance,
							new ItemSpawnRunnable(this),
							arena.getArenaConfig().getInt(
									CFG.MODULES_ITEMS_INTERVAL) * 20L,
							arena.getArenaConfig().getInt(
									CFG.MODULES_ITEMS_INTERVAL) * 20L);
		}
	}
}
