package com.feed_the_beast.ftblib.lib.data;

import com.feed_the_beast.ftblib.lib.util.CommonUtils;
import com.feed_the_beast.ftblib.lib.util.IWithID;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.common.util.INBTSerializable;

import javax.annotation.Nullable;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.function.Consumer;

/**
 * @author LatvianModder
 */
public class NBTDataStorage implements INBTSerializable<NBTTagCompound>, Consumer<NBTDataStorage.Data> {
    public interface Data extends IWithID, INBTSerializable<NBTTagCompound> {
        @Override
        default NBTTagCompound serializeNBT() {
            return new NBTTagCompound();
        }

        @Override
        default void deserializeNBT(NBTTagCompound nbt) {
        }

        default void clearCache() {
        }
    }

    public static final NBTDataStorage EMPTY = new NBTDataStorage() {
        @Override
        @Nullable
        public Data getRaw(String id) {
            return null;
        }

        @Override
        public boolean isEmpty() {
            return true;
        }

        @Override
        public NBTTagCompound serializeNBT() {
            return new NBTTagCompound();
        }

        @Override
        public void deserializeNBT(NBTTagCompound nbt) {
        }
    };

    private final Map<String, Data> map;

    public NBTDataStorage() {
        map = new HashMap<>();
    }

    public void add(Data data) {
        map.put(data.getId(), data);
    }

    @Nullable
    public Data getRaw(String id) {
        return map.get(id);
    }

    public <T extends Data> T get(String id) {
        return CommonUtils.cast(Objects.requireNonNull(getRaw(id)));
    }

    public boolean isEmpty() {
        return map.isEmpty();
    }

    @Override
    public NBTTagCompound serializeNBT() {
        NBTTagCompound nbt = new NBTTagCompound();

        for (Data data : map.values()) {
            NBTTagCompound nbt1 = data.serializeNBT();

            if (!nbt1.isEmpty()) {
                nbt.setTag(data.getId(), nbt1);
            }
        }

        return nbt;
    }

    @Override
    public void deserializeNBT(NBTTagCompound nbt) {
        for (Data data : map.values()) {
            data.deserializeNBT(nbt.getCompoundTag(data.getId()));
        }
    }

    public void clearCache() {
        for (Data data : map.values()) {
            data.clearCache();
        }
    }

    @Override
    public void accept(Data data) {
        add(data);
    }
}