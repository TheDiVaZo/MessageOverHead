package thedivazo;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.ProtocolManager;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.wrappers.WrappedChatComponent;
import com.comphenix.protocol.wrappers.WrappedDataValue;
import com.comphenix.protocol.wrappers.WrappedDataWatcher;
import com.comphenix.protocol.wrappers.WrappedDataWatcher.Serializer;
import com.comphenix.protocol.wrappers.WrappedWatchableObject;
import lombok.Data;
import org.bukkit.Location;
import org.bukkit.craftbukkit.libs.it.unimi.dsi.fastutil.ints.IntArrayList;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import thedivazo.utils.StringUtil;

import java.util.*;
import java.util.concurrent.ThreadLocalRandom;

@Data
public class Bubble {
    private boolean isSmall = true;
    private boolean noBasePlate = true;
    private boolean isMarker = true;
    private boolean invisible = true;

    private final String message;

    private final ProtocolManager pm = ProtocolLibrary.getProtocolManager();

    private static final int CUSTOM_NAME_INDEX = 2;
    private static final int CUSTOM_NAME_VISIBLE_INDEX = 3;
    private static final int PARAM_ARMOR_STAND_INDEX;

    static {
        if (MessageOverHead.getVersion() >= 1.17f) {
            PARAM_ARMOR_STAND_INDEX = 15;
        } else if (MessageOverHead.getVersion() > 1.14f) {
            PARAM_ARMOR_STAND_INDEX = 14;
        } else if (MessageOverHead.getVersion() == 1.14f) {
            PARAM_ARMOR_STAND_INDEX = 13;
        } else {
            PARAM_ARMOR_STAND_INDEX = 11;
        }
    }

    private boolean isRemovedBubble = false;
    private final Serializer serBoolean = WrappedDataWatcher.Registry.get(Boolean.class);
    private final Serializer serByte = WrappedDataWatcher.Registry.get(Byte.class);

    private final WrappedDataWatcher metadata = new WrappedDataWatcher();

    private Location loc;
    private final int id = ThreadLocalRandom.current().nextInt(Integer.MAX_VALUE);
    private final UUID uuid = UUID.randomUUID();

    public Bubble(String message, Location loc) {
        this.message = message;
        setPosition(loc);
    }

    public void spawn(int distance, Player placeholderPlayer) {
        for (Player p : Objects.requireNonNull(loc.getWorld()).getPlayers()) {
            if (p.getLocation().distance(loc) <= distance) {
                spawn(p, placeholderPlayer);
            }
        }
    }

    public void spawn(Player player, Player placeholderPlayer) {
        setMetadata(placeholderPlayer);
        PacketContainer metaPacket = getMetaPacket();
        PacketContainer fakeStandPacket = getFakeStandPacket();

        try {
            pm.sendServerPacket(player, fakeStandPacket);
            pm.sendServerPacket(player, metaPacket);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private void setMetadata(Player player) {
        String modifyMessages = StringUtil.setEmoji(player,StringUtil.setPlaceholders(player, message));
        Optional<?> opt;
        if (MessageOverHead.getVersion() <= 1.13f) {
            opt = Optional.of(WrappedChatComponent.fromText(modifyMessages).getHandle());
        } else {
            opt = Optional.of(WrappedChatComponent.fromChatMessage(modifyMessages)[0].getHandle());
        }
        if(MessageOverHead.getVersion()>1.12f) {
            Serializer serChatComponent = WrappedDataWatcher.Registry.getChatComponentSerializer(true);
            metadata.setObject(new WrappedDataWatcher.WrappedDataWatcherObject(CUSTOM_NAME_INDEX, serChatComponent), opt);
        } else {
            Serializer serString = WrappedDataWatcher.Registry.get(String.class);
            metadata.setObject(new WrappedDataWatcher.WrappedDataWatcherObject(CUSTOM_NAME_INDEX, serString), modifyMessages);
        }
        metadata.setObject(new WrappedDataWatcher.WrappedDataWatcherObject(CUSTOM_NAME_VISIBLE_INDEX, serBoolean), true);
        metadata.setObject(new WrappedDataWatcher.WrappedDataWatcherObject(PARAM_ARMOR_STAND_INDEX, serByte), (byte)
                ((isMarker ? 0x10 : 0) | (isSmall ? 0x01 : 0) | (noBasePlate ? 0x08 : 0))
        );
        metadata.setObject(0, serByte, (byte) (invisible ? 0x20 : 0));
    }

    private PacketContainer getFakeStandPacket() {
        return new PacketContainer(PacketType.Play.Server.SPAWN_ENTITY) {{
            getModifier().writeDefaults();
            getIntegers().write(0, id);

            if(MessageOverHead.getVersion() <= 1.13f) {
                getIntegers().write(6, 78);
            } else {
                getEntityTypeModifier().write(0, EntityType.ARMOR_STAND);
            }
            // Set location
            getDoubles().write(0, loc.getX());
            getDoubles().write(1, loc.getY());
            getDoubles().write(2, loc.getZ());
            getUUIDs().write(0, uuid);
        }};
    }

    protected boolean checkClass(String string) {
        try {
            Class.forName(string, false, getClass().getClassLoader());
            return true;
        } catch (Throwable e) {
            return false;
        }
    }

    private PacketContainer getMetaPacket() {
        PacketContainer metaPacket = new PacketContainer(PacketType.Play.Server.ENTITY_METADATA);
        metaPacket.getIntegers().write(0, id);
        if(checkClass("com.comphenix.protocol.wrappers.WrappedDataValue"))
        {
            final List<WrappedDataValue> wrappedDataValueList = new ArrayList<>();

            for(final WrappedWatchableObject entry : metadata.getWatchableObjects()) {
                if(entry == null) continue;

                final WrappedDataWatcher.WrappedDataWatcherObject watcherObject = entry.getWatcherObject();
                wrappedDataValueList.add(
                        new WrappedDataValue(
                                watcherObject.getIndex(),
                                watcherObject.getSerializer(),
                                entry.getRawValue()
                        )
                );
            }

            metaPacket.getDataValueCollectionModifier().write(0, wrappedDataValueList);
        }
        else metaPacket.getWatchableCollectionModifier().write(0, metadata.getWatchableObjects());
        return metaPacket;
    }

    private void setPosition() {
        PacketContainer teleportPacket = new PacketContainer(PacketType.Play.Server.ENTITY_TELEPORT) {{
            getModifier().writeDefaults();
            getIntegers().write(0, id);
            getDoubles().write(0, loc.getX());
            getDoubles().write(1, loc.getY());
            getDoubles().write(2, loc.getZ());
            getBooleans().write(0, false);
        }};
        pm.broadcastServerPacket(teleportPacket);

    }

    public void setPosition(Location loc) {
        this.loc = loc;
        setPosition();
    }

    public void setPosition(double x, double y, double z) {
        this.loc = new Location(loc.getWorld(), x, y, z);
        setPosition();
    }

    public void remove() {
        if (isRemovedBubble) return;
        PacketContainer removeStandPacket = new PacketContainer(PacketType.Play.Server.ENTITY_DESTROY);
        if(MessageOverHead.getVersion() >= 1.18f) {
            List<Integer> entity = new ArrayList<>();
            entity.add(id);
            removeStandPacket.getIntLists().write(0, entity);
        } else if(MessageOverHead.getVersion() == 1.17f) {
            try {
                removeStandPacket.getModifier().write(0, new IntArrayList(new int[]{id}));
            } catch (Throwable e) {
                removeStandPacket.getModifier().write(0, id);
            }
        } else {
            removeStandPacket.getModifier().write(0, new int[]{id});
        }

        pm.broadcastServerPacket(removeStandPacket);
    }
}
