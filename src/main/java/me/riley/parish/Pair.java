package me.riley.parish;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import java.util.UUID;
import org.bukkit.entity.Player;

public class Pair {
    private static Set<Pair> pairs = new HashSet();
    private UUID mover;
    private UUID breaker;

    public Pair(UUID mover, UUID breaker) {
        this.mover = mover;
        this.breaker = breaker;
        pairs.add(this);
    }

    public UUID getPlayer1() {
        return this.mover;
    }

    public UUID getPlayer2() {
        return this.breaker;
    }

    public void remove() {
        pairs.remove(this);
    }

    public static Set<Pair> getPairs() {
        return pairs;
    }

    public static Pair getTwin(Player player) {
        Iterator var1 = pairs.iterator();

        Pair pair;
        do {
            if (!var1.hasNext()) {
                return null;
            }

            pair = (Pair)var1.next();
        } while(!pair.getPlayer2().equals(player.getUniqueId()) && !pair.getPlayer1().equals(player.getUniqueId()));

        return pair;
    }
}
