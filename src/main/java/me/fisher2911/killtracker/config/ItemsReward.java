/*
 * MIT License
 *
 * Copyright (c) fisher2911
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package me.fisher2911.killtracker.config;

import org.bukkit.Location;
import org.bukkit.OfflinePlayer;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

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
