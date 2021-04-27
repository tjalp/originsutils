package net.tjalp.originswarps.object;

import org.jetbrains.annotations.NotNull;

public class Warp {

    private final String name;
    private Location location;

    public Warp(@NotNull String name, @NotNull Location location) {
        this.name = name;
        this.location = location;
    }

    public Location getLocation() {
        return this.location;
    }

    public String getName() {
        return this.name;
    }

    public void setLocation(Location location) {
        this.location = location;
    }
}
