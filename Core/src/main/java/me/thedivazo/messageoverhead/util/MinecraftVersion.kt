package me.thedivazo.messageoverhead.util

/**
 * Represents a Minecraft version in the major.minor.patch format.
 */
data class MinecraftVersion(
    val major: Int,
    val minor: Int,
    val patch: Int,
) : Comparable<MinecraftVersion> {

    init {
        require(major >= 0) { "Major version must not be negative" }
        require(minor >= 0) { "Minor version must not be negative" }
        require(patch >= 0) { "Patch version must not be negative" }
    }

    override fun compareTo(other: MinecraftVersion): Int =
        compareValuesBy(this, other, MinecraftVersion::major, MinecraftVersion::minor, MinecraftVersion::patch)

    override fun toString(): String = "$major.$minor.$patch"

    fun isAtLeast(other: MinecraftVersion): Boolean = this >= other

    fun isOlderThan(other: MinecraftVersion): Boolean = this < other

    companion object {

        @JvmField
        val VERSION_1_17 = MinecraftVersion(1, 17, 0)

        @JvmField
        val VERSION_1_16 = MinecraftVersion(1, 16, 0)

        @JvmField
        val VERSION_1_15 = MinecraftVersion(1, 15, 0)

        @JvmField
        val VERSION_1_14 = MinecraftVersion(1, 14, 0)

        @JvmField
        val VERSION_1_13 = MinecraftVersion(1, 13, 0)

        @JvmField
        val VERSION_1_12 = MinecraftVersion(1, 12, 0)

        @JvmField
        val VERSION_1_19_3 = MinecraftVersion(1, 19, 3)

        @JvmField
        val VERSION_1_21 = MinecraftVersion(1, 21, 0)

        @JvmField
        val VERSION_1_21_2 = MinecraftVersion(1, 21, 2)

        /**
         * Parses a version in the major.minor.patch format, for example "1.21.4".
         *
         * @throws IllegalArgumentException if [value] has an invalid format.
         */
        @JvmStatic
        fun parse(value: String): MinecraftVersion {
            val match = VERSION_PATTERN.matchEntire(value.trim())
                ?: throw IllegalArgumentException(
                    "Invalid Minecraft version '$value'. Expected format: major.minor.patch",
                )

            return MinecraftVersion(
                major = match.groupValues[1].toInt(),
                minor = match.groupValues[2].toInt(),
                patch = match.groupValues[3].toInt(),
            )
        }

        private val VERSION_PATTERN = Regex("""(\d+)\.(\d+)\.(\d+)""")
    }
}
