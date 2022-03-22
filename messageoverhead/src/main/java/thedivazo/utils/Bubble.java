package thedivazo.utils;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.ProtocolManager;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.wrappers.WrappedChatComponent;
import com.comphenix.protocol.wrappers.WrappedDataWatcher;
import com.comphenix.protocol.wrappers.WrappedDataWatcher.Serializer;
import lombok.Data;
import org.bukkit.Location;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import thedivazo.Main;

import java.lang.reflect.InvocationTargetException;
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
        if (Main.getVersion() >= 1.17f) {
            PARAM_ARMOR_STAND_INDEX = 15;
        } else if (Main.getVersion() > 1.14f) {
            PARAM_ARMOR_STAND_INDEX = 14;
        } else if (Main.getVersion() == 1.14f) {
            PARAM_ARMOR_STAND_INDEX = 13;
        } else {
            PARAM_ARMOR_STAND_INDEX = 11;
        }
    }

    private boolean isRemovedBubble = false;
    private final Serializer serChatComponent = WrappedDataWatcher.Registry.getChatComponentSerializer(true);
    private final Serializer serBoolean = WrappedDataWatcher.Registry.get(Boolean.class);
    private final Serializer serByte = WrappedDataWatcher.Registry.get(Byte.class);

    private final WrappedDataWatcher metadata = new WrappedDataWatcher();

    private Location loc;
    private final int id = ThreadLocalRandom.current().nextInt(Integer.MAX_VALUE);
    private final UUID uuid = UUID.randomUUID();

    public Bubble(String message, Location loc) {
        this.message = message;
        setPosition(loc);
        setMetadata();
    }

    public void spawn(int distance) {
        for (Player p : Objects.requireNonNull(loc.getWorld()).getPlayers()) {
            if (p.getLocation().distance(loc) <= distance) {
                spawn(p);
            }
        }
    }

    public void spawn(Player player) {
        PacketContainer metaPacket = getMetaPacket();
        PacketContainer fakeStandPacket = getFakeStandPacket();

        try {
            pm.sendServerPacket(player, fakeStandPacket);
            pm.sendServerPacket(player, metaPacket);
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }

    }

    private void setMetadata() {
        Optional<?> opt;
        if (Main.getVersion() <= 1.13f) {
            opt = Optional.of(WrappedChatComponent.fromText(message).getHandle());
        } else {
            opt = Optional.of(WrappedChatComponent.fromChatMessage(message)[0].getHandle());
        }
        metadata.setObject(new WrappedDataWatcher.WrappedDataWatcherObject(CUSTOM_NAME_INDEX, serChatComponent), opt);
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

            if(Main.getVersion() <= 1.13f) {
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
        metaPacket.getWatchableCollectionModifier().write(0, metadata.getWatchableObjects());
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
        if(Main.getVersion() >= 1.18f) {
            List<Integer> Entity = new ArrayList<>();
            Entity.add(id);
            removeStandPacket.getIntLists().write(0, Entity);
        } else if(Main.getVersion() == 1.17f) {
            removeStandPacket.getModifier().writeSafely(0, List.of(new int[]{id}));
        } else {
            removeStandPacket.getModifier().write(0, new int[]{id});
        }

        pm.broadcastServerPacket(removeStandPacket);
    }
}
