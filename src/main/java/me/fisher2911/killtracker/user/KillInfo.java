/*
 * KillTracker
 * Copyright (C) 2021 fisher2911
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along with this program. If not, see <https://www.gnu.org/licenses/>.
 */

package me.fisher2911.killtracker.user;

import java.time.LocalDateTime;

public class KillInfo {

    private int kills;
    private LocalDateTime lastKilled;

    public KillInfo(final int kills, final LocalDateTime lastKilled) {
        this.kills = kills;
        this.lastKilled = lastKilled;
    }

    public void addKill() {
        this.kills++;
    }

    public void setKills(final int kills) {
        this.kills = kills;
    }

    public void setLastKilled(final LocalDateTime lastKilled) {
        this.lastKilled = lastKilled;
    }

    public int getKills() {
        return kills;
    }

    public LocalDateTime getLastKilled() {
        return lastKilled;
    }
}
