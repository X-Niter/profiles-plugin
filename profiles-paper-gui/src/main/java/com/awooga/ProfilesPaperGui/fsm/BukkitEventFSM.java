package com.awooga.ProfilesPaperGui.fsm;

import com.awooga.ProfilesPaperGui.ProfilesPaperGui;
import com.awooga.ProfilesPaperGui.chestgui.ChestGui;
import com.awooga.ProfilesPaperGui.fsm.core.DumbFSM;
import com.awooga.ProfilesPaperGui.fsm.core.EventType;
import com.awooga.ProfilesPaperGui.fsm.core.Transition;
import lombok.SneakyThrows;
import org.bukkit.Bukkit;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryInteractEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.event.player.PlayerEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;

import java.util.*;

public abstract class BukkitEventFSM<S> extends EventFSM<S, Event, Player> implements Listener {

	private final ProfilesPaperGui plugin = ProfilesPaperGui.getInstance();

	Set<Class<?>> registeredClasses = new HashSet<>();

	protected abstract Map<EventType, BoundUserFunction<Player, S>> getBoundEvents();
	private Map<EventType, BoundFunction<S, Event>> cachedEventMap;

	public void onEnable() {
		for(Transition<S, Event> transition : this.getTransitions()) {
			Class<? extends Event> eventClass = transition.getOnEvent();
			if (registeredClasses.contains(eventClass)) {
				continue;
			}
			Bukkit.getLogger().info("Registering event handler for: "+eventClass);
			registeredClasses.add(eventClass);
			Bukkit.getServer().getPluginManager().registerEvent(eventClass, this, EventPriority.NORMAL, this::eventExecutor, this.plugin);
		}
	}

	@EventHandler
	public void onPlayerDisconnect(PlayerQuitEvent event) {
		this.removeUser(event.getPlayer());
	}

	@SneakyThrows
	private void eventExecutor(@NotNull Listener listener, @NotNull Event event) {

		if(event instanceof PlayerEvent) {
			Bukkit.getLogger().info("Player EventExecutor got "+event);
			this.fire(((PlayerEvent)event).getPlayer(), event);
		}
		if(event instanceof InventoryInteractEvent) {
			InventoryInteractEvent invEvent = (InventoryInteractEvent) event;
				HumanEntity entity = invEvent.getWhoClicked();
				if (entity instanceof Player) {
					Bukkit.getLogger().info("InvInteract EventExecutor got "+event);
					this.fire((Player) entity, event);
			}
		}
		else if(event instanceof InventoryCloseEvent) {
			InventoryCloseEvent invEvent = (InventoryCloseEvent) event;
				HumanEntity entity = invEvent.getPlayer();
				if (entity instanceof Player) {
					Bukkit.getLogger().info("InventoryClose EventExecutor got "+event);
					this.fire((Player) entity, event);
				}
		}
		else if(event instanceof InventoryOpenEvent) {
			InventoryOpenEvent invEvent = (InventoryOpenEvent) event;
				HumanEntity entity = invEvent.getPlayer();
				if (entity instanceof Player) {
					Bukkit.getLogger().info("InventoryOpen EventExecutor got "+event);
					this.fire((Player) entity, event);
			}
		} else {
			throw new Exception("Don't know how to handle this event: "+event);
		}
	}

	protected Map<EventType, BoundFunction<S, Event>> getBoundPlainEvents() {
		if(this.cachedEventMap != null) {
			return this.cachedEventMap;
		}
		this.cachedEventMap = new HashMap<>();
		Map<EventType, BoundUserFunction<Player, S>> events = this.getBoundEvents();
		for(EventType event : events.keySet()) {
			BoundUserFunction<Player, S> fn = events.get(event);
			this.cachedEventMap.put(event, (fsm) -> {
				Player p = this.getUserByFSM(fsm);
				fn.onEvent(p, fsm.getStateName(), fsm.getStateData());
			});
		}
		return this.cachedEventMap;
	}

	protected S getStateByPlayer(Player user) {
		DumbFSM<S, Event> fsm = this.getFsm(user);
		if(fsm == null) { return null; }
		return fsm.getStateData();
	}

	protected S resetPlayer(Player user) {
		DumbFSM<S, Event> fsm = this.getFsm(user);
		if(fsm == null) { return null; }
		return fsm.getStateData();
	}
}
