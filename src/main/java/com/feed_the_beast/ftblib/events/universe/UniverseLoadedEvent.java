package com.feed_the_beast.ftblib.events.universe;

import com.feed_the_beast.ftblib.lib.data.Universe;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.WorldServer;

/**
 * @author LatvianModder
 */
public abstract class UniverseLoadedEvent extends UniverseEvent {
    public UniverseLoadedEvent(Universe universe) {
        super(universe);
    }

    public WorldServer getWorld() {
        return getUniverse().world;
    }

    public static class Pre extends UniverseLoadedEvent {
        private final NBTTagCompound data;

        public Pre(Universe universe, NBTTagCompound nbt) {
            super(universe);
            data = nbt;
        }

        public NBTTagCompound getData(String id) {
            NBTTagCompound tag = data.getCompoundTag(id);
            return tag.isEmpty() ? data.getCompoundTag(id + ":data") : tag;
        }
    }

    public static class CreateServerTeams extends UniverseLoadedEvent {
        public CreateServerTeams(Universe universe) {
            super(universe);
        }
    }

    public static class Post extends UniverseLoadedEvent {
        private final NBTTagCompound data;

        public Post(Universe universe, NBTTagCompound nbt) {
            super(universe);
            data = nbt;
        }

        public NBTTagCompound getData(String id) {
            NBTTagCompound tag = data.getCompoundTag(id);
            return tag.isEmpty() ? data.getCompoundTag(id + ":data") : tag;
        }
    }

    public static class Finished extends UniverseLoadedEvent {
        public Finished(Universe universe) {
            super(universe);
        }
    }
}