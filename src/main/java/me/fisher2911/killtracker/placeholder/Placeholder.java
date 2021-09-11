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

package me.fisher2911.killtracker.placeholder;

import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

import java.util.Map;

public class Placeholder {

    public static String PLAYER_PLACEHOLDER = "%player%";
    public static String KILLS_PLACEHOLDER = "%kills%";

    public static String addPlaceholders(final Map<String, String> placeholders, final String message) {
        String replace = message;
        for (final Map.Entry<String, String> entry : placeholders.entrySet()) {
            final String key = entry.getKey();
            final String value = entry.getValue();
            if (value == null || key == null) {
                continue;
            }
            replace = replace.replace(entry.getKey(), entry.getValue());
        }
        return replace;
    }

    public static String addPlayerAndKillsPlaceholders(final String message,
                                                       final OfflinePlayer player,
                                                       final int kills) {
        return addPlaceholders(Map.of(PLAYER_PLACEHOLDER, player.getName(),
                                    KILLS_PLACEHOLDER, String.valueOf(kills)), message);
    }

}
