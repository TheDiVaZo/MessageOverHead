package thedivazo;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.ProtocolManager;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.wrappers.WrappedChatComponent;
import com.comphenix.protocol.wrappers.WrappedDataWatcher;
import com.comphenix.protocol.wrappers.WrappedDataWatcher.Serializer;
import org.bukkit.Location;
import org.bukkit.craftbukkit.libs.it.unimi.dsi.fastutil.ints.IntArrayList;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;

import java.lang.reflect.InvocationTargetException;
import java.util.*;

public class Bubble {
    private final Random random = new Random();

    private boolean isSmall = true;
    private boolean noBasePlate = true;
    private boolean isMarker = true;
    private boolean invisible = true;

    private final String message;

    private final ProtocolManager pm = ProtocolLibrary.getProtocolManager();

    private static int customNameIndex = 2;
    private static int customNameVisibleIndex = 3;
    private static int paramArmorStandIndex = 14;

    static {
        if (Main.getVersion() >= 1.17f) {
            paramArmorStandIndex = 15;
        }
        else if (Main.getVersion() > 1.14f) {
            paramArmorStandIndex = 14;
        }
        else if (Main.getVersion() == 1.14f) {
            paramArmorStandIndex = 13;
        }
        else {
            paramArmorStandIndex = 11;
        }
    }

    private boolean isRemovedBubble = false;
    private Serializer serChatComponent = WrappedDataWatcher.Registry.getChatComponentSerializer(true);
    private Serializer serBoolean = WrappedDataWatcher.Registry.get(Boolean.class);
    private Serializer serByte = WrappedDataWatcher.Registry.get(Byte.class);

    private WrappedDataWatcher metadata = new WrappedDataWatcher();

    private Location loc;
    private int id = random.nextInt(Integer.MAX_VALUE);
    private UUID uuid = UUID.randomUUID();

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
        metadata.setObject(new WrappedDataWatcher.WrappedDataWatcherObject(customNameIndex, serChatComponent), opt);
        metadata.setObject(new WrappedDataWatcher.WrappedDataWatcherObject(customNameVisibleIndex, serBoolean), true);
        metadata.setObject(new WrappedDataWatcher.WrappedDataWatcherObject(paramArmorStandIndex, serByte), (byte)
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
            }
            else {
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
        }
        else if(Main.getVersion() == 1.17f) {
            removeStandPacket.getModifier().writeSafely(0, new IntArrayList(new int[]{id}));
        }

        else {
            removeStandPacket.getModifier().write(0, new int[]{id});
        }

        pm.broadcastServerPacket(removeStandPacket);
    }

    //Getters and setters
    public boolean isSmall() {
        return isSmall;
    }

    public void setSmall(boolean small) {
        isSmall = small;
    }

    public boolean isNoBasePlate() {
        return noBasePlate;
    }

    public void setNoBasePlate(boolean noBasePlate) {
        this.noBasePlate = noBasePlate;
    }

    public boolean isMarker() {
        return isMarker;
    }

    public void setMarker(boolean marker) {
        isMarker = marker;
    }

    public boolean isInvisible() {
        return invisible;
    }

    public void setInvisible(boolean invisible) {
        this.invisible = invisible;
    }
}
