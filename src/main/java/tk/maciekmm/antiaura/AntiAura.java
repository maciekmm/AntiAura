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
import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.events.PacketEvent;
import com.comphenix.protocol.wrappers.EnumWrappers.EntityUseAction;
import com.comphenix.protocol.wrappers.WrappedChatComponent;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.plugin.java.JavaPlugin;

import java.lang.reflect.InvocationTargetException;
import java.text.NumberFormat;
import java.util.AbstractMap;
import java.util.HashMap;
import java.util.List;
import java.util.Random;
import java.util.UUID;


public class AntiAura extends JavaPlugin implements Listener {

    private static final NumberFormat NUMBER_FORMAT;

    static {
        NUMBER_FORMAT = NumberFormat.getInstance();
        NUMBER_FORMAT.setMaximumIntegerDigits(Integer.MAX_VALUE);
        NUMBER_FORMAT.setMinimumIntegerDigits(1);
        NUMBER_FORMAT.setMaximumFractionDigits(2);
        NUMBER_FORMAT.setMinimumFractionDigits(1);
    }

    private HashMap<UUID, AuraCheck> running = new HashMap<>();
    private boolean isRegistered;
    public static final Random RANDOM = new Random();

    @Override
    public void onEnable() {
        this.saveDefaultConfig();
        this.getServer().getPluginManager().registerEvents(this, this);
    }

    public void register() {
        ProtocolLibrary.getProtocolManager().addPacketListener(
                new PacketAdapter(this, WrapperPlayClientUseEntity.TYPE) {
                    @Override
                    public void onPacketReceiving(PacketEvent event) {
                        if (event.getPacketType() == WrapperPlayClientUseEntity.TYPE) {
                            WrapperPlayClientUseEntity packet = new WrapperPlayClientUseEntity(event.getPacket());
                            int entID = packet.getTarget();
                            if (running.containsKey(event.getPlayer().getUniqueId()) && packet.getType() == EntityUseAction.ATTACK) {
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

    public AuraCheck remove(UUID id) {
        if (this.running.containsKey(id)) {

            if (running.size() == 1) {
                this.unregister();
            }

            return this.running.remove(id);
        }
        return null;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args.length < 1) {
            return false;
        }
        if (args[0].equalsIgnoreCase("reload")) {
            this.reloadConfig();
            sender.sendMessage(ChatColor.GREEN + "AntiAura config successfully reloaded");
            return true;
        }

        @SuppressWarnings("deprecation")
        List<Player> playerList = Bukkit.matchPlayer(args[0]);
        Player player;
        if (playerList.isEmpty()) {
            sender.sendMessage(ChatColor.RED + "Player is not online.");
            return true;
        } else if (playerList.size() == 1) {
            player = playerList.get(0);
        } else {
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append("[\"\",{\"text\":\"What player do you mean? (click one)\\n\",\"color\":\"green\"},");
            for (Player p : playerList) {
                stringBuilder.append("{\"text\":\"").append(p.getName()).append(", \",\"color\":\"blue\",\"clickEvent\":{\"action\":\"run_command\",\"value\":\"/auracheck ").append(p.getName()).append("\"},\"hoverEvent\":{\"action\":\"show_text\",\"value\":{\"text\":\"\",\"extra\":[{\"text\":\"").append(p.getName()).append("\",\"color\":\"dark_purple\"}]}}},");
            }
            stringBuilder.deleteCharAt(stringBuilder.length() - 1);
            stringBuilder.append("]");
            String json = stringBuilder.toString();
            PacketContainer packet = new PacketContainer(PacketType.Play.Server.CHAT);
            packet.getChatComponents().write(0, WrappedChatComponent.fromJson(json));
            try {
                ProtocolLibrary.getProtocolManager().sendServerPacket((Player) sender, packet);
            } catch (InvocationTargetException e) {
            }
            return true;
        }
        if (player == null) {
            sender.sendMessage(ChatColor.RED + "Player is not online.");
            return true;
        }

        if (!isRegistered) {
            this.register();
        }

        AuraCheck check = new AuraCheck(this, player);
        running.put(player.getUniqueId(), check);

        check.invoke(sender, new AuraCheck.Callback() {
            @Override
            public void done(long started, long finished, AbstractMap.SimpleEntry<Integer, Integer> result, CommandSender invoker, Player target) {
                if (invoker instanceof Player && !((Player) invoker).isOnline()) {
                    return;
                }
                invoker.sendMessage(ChatColor.DARK_PURPLE + "Aura check result for " + target.getName() + ": killed " + result.getKey() + " out of " + result.getValue());
                double timeTaken = finished != Long.MAX_VALUE ? ((double) (finished - started)) / 1000D : ((double) getConfig().getInt("ticksToKill", 10)) / 20D;
                invoker.sendMessage(ChatColor.DARK_PURPLE + "Check length: " + NUMBER_FORMAT.format(timeTaken) + " seconds.");
            }
        });
        return true;
    }

    @EventHandler
    public void onDisconnect(PlayerQuitEvent event) {
        AuraCheck check = this.remove(event.getPlayer().getUniqueId());
        if (check != null) {
            check.end();
        }
    }
}
