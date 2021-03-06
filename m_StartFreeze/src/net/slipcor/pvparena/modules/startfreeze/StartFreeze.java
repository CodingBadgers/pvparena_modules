package net.slipcor.pvparena.modules.startfreeze;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;

import net.slipcor.pvparena.PVPArena;
import net.slipcor.pvparena.arena.ArenaPlayer;
import net.slipcor.pvparena.commands.AbstractArenaCommand;
import net.slipcor.pvparena.core.Language;
import net.slipcor.pvparena.core.Config.CFG;
import net.slipcor.pvparena.core.Language.MSG;
import net.slipcor.pvparena.loadables.ArenaModule;

public class StartFreeze extends ArenaModule implements Listener {
	StartFreezer runnable = null;
	private boolean setup = false;
	

	public StartFreeze() {
		super("StartFreeze");
	}

	@Override
	public String version() {
		return "v1.0.1.59";
	}
	
	@Override
	public boolean checkCommand(String s) {
		return s.equals("startfreeze") || s.equals("!sf");
	}
	
	@Override
	public void commitCommand(CommandSender sender, String[] args) {
		// !sf 5
		
		if (!PVPArena.hasAdminPerms(sender)
				&& !(PVPArena.hasCreatePerms(sender, arena))) {
			arena.msg(
					sender,
					Language.parse(MSG.ERROR_NOPERM,
							Language.parse(MSG.ERROR_NOPERM_X_ADMIN)));
			return;
		}

		if (!AbstractArenaCommand.argCountValid(sender, arena, args, new Integer[] { 2 })) {
			return;
		}
		
		if (args[0].equals("!sf") || args[0].equals("startfreeze")) {
			int i = 0;
			try {
				i = Integer.parseInt(args[1]);
			} catch (Exception e) {
				arena.msg(sender,
						Language.parse(MSG.ERROR_NOT_NUMERIC, args[1]));
				return;
			}
			
			arena.getArenaConfig().set(CFG.MODULES_STARTFREEZE_TIMER, i);
			arena.getArenaConfig().save();
			arena.msg(sender, Language.parse(MSG.SET_DONE, CFG.MODULES_STARTFREEZE_TIMER.getNode(), String.valueOf(i)));
		}
	}
	
	@Override
	public void displayInfo(CommandSender sender) {
		sender.sendMessage("seconds: " + arena.getArenaConfig().getInt(CFG.MODULES_STARTFREEZE_TIMER));
	}

	@Override
	public void reset(boolean force) {
		if (runnable != null)
			runnable.cancel();
		runnable = null;
	}

	@Override
	public void parseStart() {
		runnable = new StartFreezer(null);
		runnable.runTaskLater(PVPArena.instance, arena.getArenaConfig().getInt(CFG.MODULES_STARTFREEZE_TIMER) * 20L);
		arena.broadcast(Language.parse(MSG.MODULE_STARTFREEZE_ANNOUNCE,
				String.valueOf(arena.getArenaConfig().getInt(CFG.MODULES_STARTFREEZE_TIMER))));
	}


	@Override
	public void configParse(YamlConfiguration config) {
		if (!setup) {
			Bukkit.getPluginManager().registerEvents(this, PVPArena.instance);
			setup = true;
		}
	}

	@EventHandler
	public void onPlayerMove(PlayerMoveEvent event) {
		Player p = event.getPlayer();
		ArenaPlayer ap = ArenaPlayer.parsePlayer(p.getName());
		if (ap.getArena() == null || (!arena.equals(ap.getArena()))) {
			return;
		}
		if (runnable != null) {
			Location from = event.getFrom();
			Location to = event.getTo();
			if ((from.getBlockX() != to.getBlockX()) ||
					(from.getBlockY() != to.getBlockY()) ||
					(from.getBlockZ() != to.getBlockZ())) {
			event.setCancelled(true);
			}
		}
	}
}
