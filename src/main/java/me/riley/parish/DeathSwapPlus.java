package me.riley.parish;

import java.util.Iterator;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

public class DeathSwapPlus extends JavaPlugin implements CommandExecutor, Listener {
    private Integer swapDuration = 300;
    private BukkitTask task;
    private boolean paused;

    public DeathSwapPlus() {
    }

    public void onEnable() {
        System.out.println("Hello from deathSwap!");
        Iterator var1 = this.getDescription().getCommands().keySet().iterator();


        while(var1.hasNext()) {
            String command = (String)var1.next();
            this.getServer().getPluginCommand(command).setExecutor(this);
        }

        this.getServer().getPluginManager().registerEvents(this, this);
    }

    private void start() {
        this.start(swapDuration);
    }

    private void start(final int time) {
        this.paused = false;
        this.task = (new BukkitRunnable() {
            int timer = time;

            public void run() {
                if (!DeathSwapPlus.this.paused) {
                    if (this.timer <= 10 && this.timer != 0) {
                        Bukkit.broadcastMessage(ChatColor.RED + "" + ChatColor.BOLD + "Swapping in " + this.timer + (this.timer == 1 ? " second!" : " seconds!"));
                    }

                    if (this.timer == 0) {
                        Iterator var1 = Pair.getPairs().iterator();

                        while(var1.hasNext()) {
                            Pair pair = (Pair)var1.next();
                            Player player1 = Bukkit.getPlayer(pair.getPlayer1());
                            Player player2 = Bukkit.getPlayer(pair.getPlayer2());
                            Location location = player1.getLocation();
                            player1.teleport(player2);
                            player2.teleport(location);
                        }

                        this.timer = swapDuration;
                    } else {
                        --this.timer;
                    }

                }
            }
        }).runTaskTimer(this, 0L, 20L);
    }

    private void stop() {
        if (this.task != null) {
            this.task.cancel();
            this.task = null;
        }

    }

    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (command.getName().equalsIgnoreCase("deathswap")) {
            if (args.length == 1) {     // start/stop deathswap
                if (args[0].equalsIgnoreCase("start")) {
                    if (this.task != null) {
                        sender.sendMessage(ChatColor.GREEN + "Death swap is already started.");
                        return false;
                    } else {
                        this.start();
                        sender.sendMessage(ChatColor.GREEN + "Death swap started.");
                        return true;
                    }
                } else if (args[0].equalsIgnoreCase("stop")) {
                    this.stop();
                    sender.sendMessage(ChatColor.RED + "Death swap stopped.");
                    return true;
                } else {
                    return false;
                }
            } else {
                Player player1;
                if (args.length == 2) {
                    if (args[0].equalsIgnoreCase("remove")) {
                        player1 = Bukkit.getPlayer(args[1]);
                        if (player1 == null) {
                            sender.sendMessage(ChatColor.RED + "Player " + args[1] + " not found.");
                            return false;
                        }

                        Pair pair = Pair.getTwin(player1);
                        if (pair == null) {
                            sender.sendMessage(ChatColor.RED + player1.getName() + " is not paired.");
                            return false;
                        }

                        pair.remove();
                        sender.sendMessage(ChatColor.GREEN + player1.getName() + " removed from pair.");
                        return true;
                    }else if(args[0].equalsIgnoreCase("setSwapDuration")){
                        swapDuration = Integer.parseInt(args[1]);
                        sender.sendMessage(ChatColor.GREEN + "Set swap duration to " + swapDuration.toString() + " seconds.");
                        return true;
                    }
                } else if (args.length == 3 && args[0].equalsIgnoreCase("add")) {
                    player1 = Bukkit.getPlayer(args[1]);
                    Player player2 = Bukkit.getPlayer(args[2]);
                    if (player1 == null) {
                        sender.sendMessage(ChatColor.RED + "Player " + args[1] + " not found.");
                        return false;
                    }

                    if (player2 == null) {
                        sender.sendMessage(ChatColor.RED + "Player " + args[2] + " not found.");
                        return false;
                    }

                    if (player1.equals(player2)) {
                        sender.sendMessage(ChatColor.RED + "A player can not be paired to themselves!");
                        return false;
                    }

                    if (Pair.getTwin(player1) != null) {
                        sender.sendMessage(ChatColor.RED + player1.getName() + " is already paired.");
                        return false;
                    }

                    if (Pair.getTwin(player2) != null) {
                        sender.sendMessage(ChatColor.RED + player2.getName() + " is already paired.");
                        return false;
                    }

                    new Pair(player1.getUniqueId(), player2.getUniqueId());
                    sender.sendMessage(ChatColor.GREEN + player1.getName() + " and " + player2.getName() + " are now paired!");
                    return true;
                }

                sender.sendMessage(ChatColor.RED + "Invalid usage. Please use:");
                sender.sendMessage(ChatColor.RED + "/deathswap add <player 1> <player 2>");
                sender.sendMessage(ChatColor.RED + "/deathswap remove <player>");
                sender.sendMessage(ChatColor.RED + "/deathswap start");
                sender.sendMessage(ChatColor.RED + "/deathswap stop");
                return false;
            }
        } else {
            return false;
        }
    }

    @EventHandler
    public void onPlayerQuitEvent(PlayerQuitEvent event) {
        Player player = event.getPlayer();
        Pair pair = Pair.getTwin(player);
        if (pair != null) {
            pair.remove();
            Player player1 = Bukkit.getPlayer(pair.getPlayer2());
            if (player1 == null) {
                return;
            }

            if (player1.equals(player)) {
                player1 = Bukkit.getPlayer(pair.getPlayer1());
            }

            if (player1 == null) {
                return;
            }

            player1.sendMessage(ChatColor.YELLOW + player.getName() + " left. You are no longer paired.");
        }

    }
}
