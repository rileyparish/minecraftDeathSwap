package me.riley.parish;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public class Pair {
    private static Set<Pair> pairs = new HashSet();
    private UUID player1ID;
    private UUID player2ID;

    public Pair(UUID player1ID, UUID player2ID) {
        this.player1ID = player1ID;
        this.player2ID = player2ID;
        pairs.add(this);
    }

    public UUID getPlayer1ID() {
        return this.player1ID;
    }

    public UUID getPlayer2ID() {
        return this.player2ID;
    }

    public void remove() {
        pairs.remove(this);
    }

    // returns all existing player pairings
    public static Set<Pair> getPairs() {
        return pairs;
    }

    // given a player, return the pair that this player belongs to
    public static Pair getPair(Player player) {
        Iterator var1 = pairs.iterator();

        Pair pair;
        do {
            if (!var1.hasNext()) {
                return null;
            }

            pair = (Pair)var1.next();
        } while(!pair.getPlayer2ID().equals(player.getUniqueId()) && !pair.getPlayer1ID().equals(player.getUniqueId()));

        return pair;
    }

    // given a player in a pair, return the opponent
    public Player getOpponent(UUID playerID){
        if(playerID == this.player1ID){
            return Bukkit.getPlayer(player2ID);
        }
        return Bukkit.getPlayer(player1ID);
    }
}
