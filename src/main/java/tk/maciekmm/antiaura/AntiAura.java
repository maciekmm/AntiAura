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

import com.comphenix.packetwrapper.WrapperPlayClientUseEntity;
import com.comphenix.protocol.Packets;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.events.ConnectionSide;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.events.PacketEvent;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.HashMap;
import java.util.Random;
import java.util.UUID;

public class AntiAura extends JavaPlugin implements Listener {
    private HashMap<UUID, AuraCheck> running = new HashMap<>();
    private boolean isRegistered;
    public static final Random RANDOM = new Random();

    public void onEnable() {
        this.getServer().getPluginManager().registerEvents(this, this);
    }

    public void register() {
        ProtocolLibrary.getProtocolManager().addPacketListener(
                new PacketAdapter(this, WrapperPlayClientUseEntity.TYPE) {
                    @Override
                    public void onPacketReceiving(PacketEvent event) {
                        if (event.getPacketType() == WrapperPlayClientUseEntity.TYPE) {
                            int entID = new WrapperPlayClientUseEntity(event.getPacket()).getTargetID();
                            if(running.containsKey(event.getPlayer().getUniqueId())) {
                                running.get(event.getPlayer().getUniqueId()).markAsKilled(entID);
                            }
                        }
                    }

                });
        this.isRegistered = true;
    }

    public void unregister() {
        ProtocolLibrary.getProtocolManager().removePacketListeners(this);
        this.isRegistered = false;
    }

    public void remove(UUID id) {
        this.running.remove(id);
        if(running.size()==0) {
            this.unregister();
        }
    }

    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if(args.length<1) {
            return false;
        }
        Player player = Bukkit.getPlayer(args[0]);
        if(player==null) {
            sender.sendMessage("Player is not online.");
            return true;
        }
        if(!isRegistered) {
            this.register();
        }
        AuraCheck check = new AuraCheck(this,player);
        running.put(player.getUniqueId(), check);
        check.invoke(sender);
        return true;
    }

    @EventHandler
    public void onDisconnect(PlayerQuitEvent event) {
        if (running.containsKey(event.getPlayer().getUniqueId())) {
            running.remove(event.getPlayer().getUniqueId()).end();
        }
    }
}
