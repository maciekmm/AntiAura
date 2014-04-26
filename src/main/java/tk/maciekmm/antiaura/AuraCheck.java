/*
 * Copyright (C) 2014 Maciej Mionskowski
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package tk.maciekmm.antiaura;

import com.comphenix.packetwrapper.WrapperPlayServerEntityDestroy;
import com.comphenix.packetwrapper.WrapperPlayServerEntityEffect;
import com.comphenix.packetwrapper.WrapperPlayServerNamedEntitySpawn;
import com.comphenix.protocol.wrappers.WrappedDataWatcher;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.util.Vector;

import java.util.AbstractMap;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;


public class AuraCheck {
    private final AntiAura plugin;
    private HashMap<Integer, Boolean> entitiesSpawned = new HashMap<>();
    private CommandSender invoker;
    private Player checked;
    private static Vector[] vectors = {new Vector(0, 0, 1.5), new Vector(-1.5, 0, 0), new Vector(1.5, 0, 0), new Vector(0, 0, -1.5), new Vector(1.5, 0, 1.5), new Vector(-1.5, 0, -1.5),};
    private long started;
    private long finished = Long.MAX_VALUE;


    public AuraCheck(AntiAura plugin, Player checked) {
        this.plugin = plugin;
        this.checked = checked;
    }

    public void invoke(CommandSender player) {
        this.invoker = player;
        this.started = System.currentTimeMillis();
        for (int i = 0; i < Math.min(vectors.length, plugin.getConfig().getInt("amountOfFakePlayers", 4)); i++) {
            WrapperPlayServerNamedEntitySpawn wrapper = getWrapper(this.checked.getLocation().add(vectors[i]).toVector(),plugin);
            entitiesSpawned.put(wrapper.getEntityID(), false);
            wrapper.sendPacket(this.checked);
        }

        Bukkit.getScheduler().runTaskLater(this.plugin, new Runnable() {
            @Override
            public void run() {
                AbstractMap.SimpleEntry<Integer, Integer> result = end();
                plugin.remove(checked.getUniqueId());
                if (invoker instanceof Player && !((Player) invoker).isOnline()) {
                    return;
                }
                invoker.sendMessage(ChatColor.DARK_PURPLE + "Aura check result: killed " + result.getKey() + " out of " + result.getValue());
                double timeTaken = finished != Long.MAX_VALUE ? (int) ((finished - started) / 1000) : ((double)plugin.getConfig().getInt("ticksToKill",10)/20);
                invoker.sendMessage(ChatColor.DARK_PURPLE + "Check length: " + timeTaken + " seconds.");
            }
        }, plugin.getConfig().getInt("ticksToKill",10));
    }

    public void markAsKilled(Integer val) {
        if (entitiesSpawned.containsKey(val)) {
            entitiesSpawned.put(val, true);
            kill(val).sendPacket(checked);
        }
        if (!entitiesSpawned.containsValue(false)) {
            this.finished = System.currentTimeMillis();
        }

    }

    public AbstractMap.SimpleEntry<Integer, Integer> end() {
        int killed = 0;
        for (Map.Entry<Integer, Boolean> entry : entitiesSpawned.entrySet()) {
            if (entry.getValue()) {
                killed++;
            } else if (checked.isOnline()) {
                kill(entry.getKey()).sendPacket(checked);
            }

        }
        int amount = entitiesSpawned.size();
        entitiesSpawned.clear();
        return new AbstractMap.SimpleEntry<>(killed, amount);

    }

    public static WrapperPlayServerNamedEntitySpawn getWrapper(Vector loc, AntiAura plugin) {
        WrapperPlayServerNamedEntitySpawn wrapper = new WrapperPlayServerNamedEntitySpawn();
        wrapper.setEntityID(AntiAura.RANDOM.nextInt(20000));
        wrapper.setPosition(loc);
        wrapper.setPlayerUUID(UUID.randomUUID().toString());
        wrapper.setPlayerName("katest");
        wrapper.setYaw(0);
        wrapper.setPitch(-45);
        WrappedDataWatcher watcher = new WrappedDataWatcher();
        watcher.setObject(0, plugin.getConfig().getBoolean("invisibility", true) ? (Byte) (byte) 0x20 : (byte) 0);
        watcher.setObject(6, (Float)(float) 0.5);
        watcher.setObject(11, (Byte)(byte) 1);
        wrapper.setMetadata(watcher);
        return wrapper;
    }

    public static WrapperPlayServerEntityDestroy kill(int entity) {
        WrapperPlayServerEntityDestroy wrapper = new WrapperPlayServerEntityDestroy();
        wrapper.setEntities(new int[]{entity});
        return wrapper;
    }
}
