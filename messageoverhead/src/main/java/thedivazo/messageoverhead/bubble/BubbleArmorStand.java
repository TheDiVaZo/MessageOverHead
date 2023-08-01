package thedivazo.messageoverhead.bubble;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.ProtocolManager;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.wrappers.WrappedChatComponent;
import com.comphenix.protocol.wrappers.WrappedDataValue;
import com.comphenix.protocol.wrappers.WrappedDataWatcher;
import com.comphenix.protocol.wrappers.WrappedDataWatcher.Serializer;
import com.comphenix.protocol.wrappers.WrappedWatchableObject;
import it.unimi.dsi.fastutil.ints.IntArrayList;
import org.bukkit.Location;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import thedivazo.messageoverhead.MessageOverHead;
import thedivazo.messageoverhead.util.VersionWrapper;

import java.util.*;
import java.util.concurrent.ThreadLocalRandom;


public final class BubbleArmorStand extends AbstractBubble {
    private final boolean isSmall = true;
    private final boolean noBasePlate = true;
    private final boolean isMarker = true;
    private final boolean invisible = true;

    private final ProtocolManager pm = ProtocolLibrary.getProtocolManager();

    // 1.18 | 1.17 | 1.14 | 1.13 | 1.12

    private static final VersionWrapper MC_VERSION = MessageOverHead.MINECRAFT_VERSION;
    private static final VersionWrapper MC_1_20 = VersionWrapper.valueOf("1.20");
    private static final VersionWrapper MC_1_19 = VersionWrapper.valueOf("1.19");
    private static final VersionWrapper MC_1_18 = VersionWrapper.valueOf("1.18");
    private static final VersionWrapper MC_1_17 = VersionWrapper.valueOf("1.17");
    private static final VersionWrapper MC_1_16 = VersionWrapper.valueOf("1.16");
    private static final VersionWrapper MC_1_15 = VersionWrapper.valueOf("1.15");
    private static final VersionWrapper MC_1_14 = VersionWrapper.valueOf("1.14");
    private static final VersionWrapper MC_1_13 = VersionWrapper.valueOf("1.13");
    private static final VersionWrapper MC_1_12 = VersionWrapper.valueOf("1.12");

    private static final int CUSTOM_NAME_INDEX = 2;
    private static final int CUSTOM_NAME_VISIBLE_INDEX = 3;
    private static final int PARAM_ARMOR_STAND_INDEX;

    static {
        if (MC_VERSION.equalsMinor(MC_1_17) || MC_VERSION.greaterMinor(MC_1_17)) {
            PARAM_ARMOR_STAND_INDEX = 15;
        } else if (MC_VERSION.greaterMinor(MC_1_14)) {
            PARAM_ARMOR_STAND_INDEX = 14;
        } else if (MC_VERSION.equalsMinor(MC_1_14)) {
            PARAM_ARMOR_STAND_INDEX = 13;
        } else {
            PARAM_ARMOR_STAND_INDEX = 11;
        }
    }

    private final Serializer serBoolean = WrappedDataWatcher.Registry.get(Boolean.class);
    private final Serializer serByte = WrappedDataWatcher.Registry.get(Byte.class);

    private final WrappedDataWatcher metadata = new WrappedDataWatcher();

    private volatile Location loc;
    private final int id = ThreadLocalRandom.current().nextInt(Integer.MAX_VALUE);
    private final UUID uuid = UUID.randomUUID();

    public BubbleArmorStand(String message, Location loc) {
        super(message);
        this.loc = loc;
    }

    private void setMetadata() {
        Optional<?> opt;
        if (MC_VERSION.lessMinor(MC_1_13) || MC_VERSION.equalsMinor(MC_1_13)) {
            opt = Optional.of(WrappedChatComponent.fromText(message).getHandle());
        } else {
            opt = Optional.of(WrappedChatComponent.fromChatMessage(message)[0].getHandle());
        }
        if(MC_VERSION.greaterMinor(MC_1_14) || MC_VERSION.equalsMinor(MC_1_14)) {
            Serializer serChatComponent = WrappedDataWatcher.Registry.getChatComponentSerializer(true);
            metadata.setObject(new WrappedDataWatcher.WrappedDataWatcherObject(CUSTOM_NAME_INDEX, serChatComponent), opt);
        } else {
            Serializer serString = WrappedDataWatcher.Registry.get(String.class);
            metadata.setObject(new WrappedDataWatcher.WrappedDataWatcherObject(CUSTOM_NAME_INDEX, serString), message);
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

            if(MC_VERSION.lessMinor(MC_1_13) || MC_VERSION.equalsMinor(MC_1_13)) {
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

    private PacketContainer getMetaPacket() {
        PacketContainer metaPacket = new PacketContainer(PacketType.Play.Server.ENTITY_METADATA);
        metaPacket.getIntegers().write(0, id);
        try {
            final List<WrappedDataValue> wrappedDataValueList = new ArrayList<>();

            for (final WrappedWatchableObject entry : metadata.getWatchableObjects()) {
                if (entry == null) continue;

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
        catch (Throwable e) {
            metaPacket.getWatchableCollectionModifier().write(0, metadata.getWatchableObjects());
        }
        return metaPacket;
    }

    private PacketContainer getTeleportPositionPacket() {
        return new PacketContainer(PacketType.Play.Server.ENTITY_TELEPORT) {{
            getModifier().writeDefaults();
            getIntegers().write(0, id);
            getDoubles().write(0, loc.getX());
            getDoubles().write(1, loc.getY());
            getDoubles().write(2, loc.getZ());
            getBooleans().write(0, false);
        }};
    }

    private void updatePosition(Set<Player> players) {
        PacketContainer newPositionPacket = getTeleportPositionPacket();
        players.forEach(player -> pm.sendServerPacket(player, newPositionPacket));
    }

    public void show(Set<Player> players) {
        setMetadata();
        PacketContainer metaPacket = getMetaPacket();
        PacketContainer fakeStandPacket = getFakeStandPacket();

        for (Player player : players) {
            try {
                pm.sendServerPacket(player, fakeStandPacket);
                pm.sendServerPacket(player, metaPacket);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public void setPosition(Location loc, Set<Player> showers) {
        this.loc = loc;
        updatePosition(showers);
    }

    public void hide(Set<Player> players) {
        PacketContainer removeStandPacket = new PacketContainer(PacketType.Play.Server.ENTITY_DESTROY);
        if(MC_VERSION.greaterMinor(MC_1_18) || MC_VERSION.equalsMinor(MC_1_18)) {
            List<Integer> entity = new ArrayList<>();
            entity.add(id);
            removeStandPacket.getIntLists().write(0, entity);
        } else if(MC_VERSION.equalsMinor(MC_1_17)) {
            try {
                removeStandPacket.getModifier().write(0, new IntArrayList(new int[]{id}));
            } catch (Throwable e) {
                removeStandPacket.getModifier().write(0, id);
            }
        } else {
            removeStandPacket.getModifier().write(0, new int[]{id});
        }
        players.forEach(player-> pm.sendServerPacket(player ,removeStandPacket));
    }

    public void changeMessage(Set<Player> players, String message) {
        this.message = message;
        hide(players);
        show(players);
    }
}
