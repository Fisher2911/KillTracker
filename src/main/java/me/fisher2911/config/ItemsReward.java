package me.fisher2911.config;

import org.bukkit.Location;
import org.bukkit.OfflinePlayer;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.entity.Wolf;
import org.bukkit.inventory.ItemStack;

import java.time.LocalDate;
import java.util.Locale;
import java.util.Map;

public class ItemsReward implements Reward {

    private final Map<ItemStack, Integer> items;

    public ItemsReward(final Map<ItemStack, Integer> items) {
        this.items = items;
    }

    @Override
    public void apply(final OfflinePlayer offlinePlayer) {
        if (!(offlinePlayer instanceof final Player player)) {
            return;
        }
        for (Map.Entry<ItemStack, Integer> entry : items.entrySet()) {
            final ItemStack itemStack = entry.getKey();
            final int amount = entry.getValue();
            if (amount > 64) {
                int stacks = amount / 64;
                int leftOver = amount % 64;
                for (int i = 0; i < stacks; i++) {
                    addClonedItem(player, itemStack, 64);
                }
                addClonedItem(player, itemStack, leftOver);
            } else {
                addClonedItem(player, itemStack, amount);
            }
        }
    }

    private void addClonedItem(final Player player, final ItemStack itemStack, final int amount) {
        final ItemStack clone = itemStack.clone();
        clone.setAmount(amount);

        final Map<Integer, ItemStack> leftoverItems = player.getInventory().addItem(clone);
        if (!leftoverItems.isEmpty()) {
            final World world = player.getWorld();
            final Location location = player.getLocation();
            leftoverItems.forEach((index, leftOverItemStack) -> {
                world.dropItem(location, leftOverItemStack);
            });
        }
    }
}
