package com.feed_the_beast.ftblib.lib.io;

import com.feed_the_beast.ftblib.lib.icon.Icon;
import com.feed_the_beast.ftblib.lib.math.BlockDimPos;
import com.feed_the_beast.ftblib.lib.util.BlockUtils;
import com.feed_the_beast.ftblib.lib.util.CommonUtils;
import com.feed_the_beast.ftblib.lib.util.JsonUtils;
import com.google.gson.*;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufInputStream;
import io.netty.handler.codec.EncoderException;
import it.unimi.dsi.fastutil.ints.*;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.*;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.text.ITextComponent;
import net.minecraftforge.common.util.Constants;

import javax.annotation.Nullable;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.*;

/**
 * @author LatvianModder
 */
public class DataIn {
    @FunctionalInterface
    public interface Deserializer<T> {
        T read(DataIn data);
    }

    public static final Deserializer<String> STRING = DataIn::readString;
    public static final Deserializer<Integer> INT = DataIn::readInt;
    public static final Deserializer<Boolean> BOOLEAN = DataIn::readBoolean;

    public static final Deserializer<UUID> UUID = DataIn::readUUID;
    public static final Deserializer<BlockPos> BLOCK_POS = DataIn::readPos;
    public static final Deserializer<BlockDimPos> BLOCK_DIM_POS = DataIn::readDimPos;
    public static final Deserializer<JsonElement> JSON = DataIn::readJson;
    public static final Deserializer<ITextComponent> TEXT_COMPONENT = DataIn::readTextComponent;
    public static final Deserializer<ResourceLocation> RESOURCE_LOCATION = DataIn::readResourceLocation;
    public static final Deserializer<ItemStack> ITEM_STACK = DataIn::readItemStack;

    public static final DataIn.Deserializer<ChunkPos> CHUNK_POS = data ->
    {
        int x = data.readVarInt();
        int z = data.readVarInt();
        return new ChunkPos(x, z);
    };

    private final ByteBuf byteBuf;

    public DataIn(ByteBuf io) {
        byteBuf = io;
    }

    public int getPosition() {
        return byteBuf.readerIndex();
    }

    public boolean readBoolean() {
        return byteBuf.readBoolean();
    }

    public byte readByte() {
        return byteBuf.readByte();
    }

    public void readBytes(byte[] bytes, int off, int len) {
        byteBuf.readBytes(bytes, off, len);
    }

    public void readBytes(byte[] bytes) {
        readBytes(bytes, 0, bytes.length);
    }

    public short readUnsignedByte() {
        return byteBuf.readUnsignedByte();
    }

    public short readShort() {
        return byteBuf.readShort();
    }

    public int readUnsignedShort() {
        return byteBuf.readUnsignedShort();
    }

    public int readInt() {
        return byteBuf.readInt();
    }

    public long readLong() {
        return byteBuf.readLong();
    }

    public float readFloat() {
        return byteBuf.readFloat();
    }

    public double readDouble() {
        return byteBuf.readDouble();
    }

    public String readString() {
        int s = readVarInt();

        if (s == 0) {
            return "";
        }

        byte[] bytes = new byte[s];
        readBytes(bytes);
        return new String(bytes, StandardCharsets.UTF_8);
    }

    public <T> Collection<T> readCollection(@Nullable Collection<T> collection, Deserializer<T> deserializer) {
        if (collection != null) {
            collection.clear();
        }

        int num = readVarInt();

        if (num == 0) {
            return collection == null ? Collections.emptyList() : collection;
        }

        int size = Math.abs(num);

        if (collection == null) {
            boolean list = num > 0;

            if (size == 1) {
                return list ? Collections.singletonList(deserializer.read(this)) : Collections.singleton(deserializer.read(this));
            }

            collection = list ? new ArrayList<>(size) : new HashSet<>(size);
        }

        while (--size >= 0) {
            collection.add(deserializer.read(this));
        }

        return collection;
    }

    public <T> Collection<T> readCollection(Deserializer<T> deserializer) {
        return readCollection(null, deserializer);
    }

    public <K, V> Map<K, V> readMap(@Nullable Map<K, V> map, Deserializer<K> keyDeserializer, Deserializer<V> valueDeserializer) {
        if (map != null) {
            map.clear();
        }

        int num = readVarInt();

        if (num == 0) {
            return map == null ? Collections.emptyMap() : map;
        }

        int size = Math.abs(num);

        if (map == null) {
            boolean linked = num < 0;

            if (keyDeserializer == INT) {
                map = CommonUtils.cast(linked ? new Int2ObjectLinkedOpenHashMap<>(size) : new Int2ObjectOpenHashMap<V>(size));
            } else {
                map = linked ? new LinkedHashMap<>(size) : new HashMap<>(size);
            }
        }

        while (--size >= 0) {
            K key = keyDeserializer.read(this);
            V value = valueDeserializer.read(this);
            map.put(key, value);
        }

        return map;
    }

    public <K, V> Map<K, V> readMap(Deserializer<K> keyDeserializer, Deserializer<V> valueDeserializer) {
        return readMap(null, keyDeserializer, valueDeserializer);
    }

    public ItemStack readItemStack() {
        int id = readVarInt();

        if (id == 0) {
            return ItemStack.EMPTY;
        }

        Item item = Item.getItemById(id);

        if (item == null || item == Items.AIR) {
            return ItemStack.EMPTY;
        }

        int size = readVarInt();
        int meta = readVarInt();
        ItemStack stack = new ItemStack(item, size, meta);
        stack.getItem().readNBTShareTag(stack, readNBT());
        return stack;
    }

    @Nullable
    public NBTTagCompound readNBT() {
        int i = byteBuf.readerIndex();
        byte b0 = byteBuf.readByte();

        if (b0 == 0) {
            return null;
        }

        byteBuf.readerIndex(i);

        try {
            return CompressedStreamTools.read(new ByteBufInputStream(byteBuf), NBTSizeTracker.INFINITE);
        } catch (IOException ex) {
            throw new EncoderException(ex);
        }
    }

    @Nullable
    public NBTBase readNBTBase() {
        switch (readByte()) {
            case Constants.NBT.TAG_END:
                return null;
            case Constants.NBT.TAG_BYTE:
                return new NBTTagByte(readByte());
            case Constants.NBT.TAG_SHORT:
                return new NBTTagShort(readShort());
            case Constants.NBT.TAG_INT:
                return new NBTTagInt(readInt());
            case Constants.NBT.TAG_LONG:
                return new NBTTagLong(readLong());
            case Constants.NBT.TAG_FLOAT:
                return new NBTTagFloat(readFloat());
            case Constants.NBT.TAG_DOUBLE:
                return new NBTTagDouble(readDouble());
            //TAG_BYTE_ARRAY
            case Constants.NBT.TAG_STRING:
                return new NBTTagString(readString());
            //TAG_LIST
            case Constants.NBT.TAG_COMPOUND:
                return readNBT();
            //TAG_INT_ARRAY
            //TAG_LONG_ARRAY
            default:
                return readNBT().getTag("_");
        }
    }

    public BlockPos readPos() {
        int x = readVarInt();
        int y = readVarInt();
        int z = readVarInt();
        return new BlockPos(x, y, z);
    }

    public BlockDimPos readDimPos() {
        int d = readVarInt();
        int x = readVarInt();
        int y = readVarInt();
        int z = readVarInt();
        return new BlockDimPos(x, y, z, d);
    }

    public UUID readUUID() {
        long msb = readLong();
        long lsb = readLong();
        return new UUID(msb, lsb);
    }

    public ResourceLocation readResourceLocation() {
        return new ResourceLocation(readString());
    }

    public JsonElement readJson() {
        switch (readUnsignedByte()) {
            case 0:
                return JsonNull.INSTANCE;
            case 1: {
                JsonObject json = new JsonObject();

                for (Map.Entry<String, JsonElement> entry : readMap(STRING, JSON).entrySet()) {
                    json.add(entry.getKey(), entry.getValue());
                }

                return json;
            }
            case 2: {
                JsonArray json = new JsonArray();

                for (JsonElement json1 : readCollection(JSON)) {
                    json.add(json1);
                }

                return json;
            }
            case 3: {
                String s = readString();
                return s.isEmpty() ? JsonUtils.JSON_EMPTY_STRING : new JsonPrimitive(s);
            }
            case 4:
                return JsonUtils.JSON_ZERO;
            case 5:
                return JsonUtils.JSON_TRUE;
            case 6:
                return JsonUtils.JSON_FALSE;
            case 7:
                return new JsonPrimitive(readVarLong());
            case 8:
                return new JsonPrimitive(readFloat());
            case 9:
                return new JsonPrimitive(readDouble());
            case 10:
                return JsonUtils.JSON_EMPTY_STRING;
        }

        return JsonNull.INSTANCE;
    }

    @Nullable
    public ITextComponent readTextComponent() {
        return JsonUtils.deserializeTextComponent(readJson());
    }

    public IBlockState readBlockState() {
        int id = readInt();
        return id == 0 ? BlockUtils.AIR_STATE : Block.getStateById(id);
    }

    public Icon readIcon() {
        return Icon.getIcon(readJson());
    }

    public IntList readIntList() {
        int size = readVarInt();

        if (size == 0) {
            return IntLists.EMPTY_LIST;
        } else if (size == 1) {
            return IntLists.singleton(readInt());
        }

        IntList list = new IntArrayList();

        for (int i = 0; i < size; i++) {
            list.add(readInt());
        }

        return list;
    }

    public int readVarInt() {
        int b = readByte();

        switch (b) {
            case 121:
                return readByte();
            case 122:
                return readShort();
            case 123:
                return readInt();
            default:
                return b;
        }
    }

    public long readVarLong() {
        int b = readByte();

        switch (b) {
            case 121:
                return readByte();
            case 122:
                return readShort();
            case 123:
                return readInt();
            case 124:
                return readLong();
            default:
                return b;
        }
    }
}