package computer.gingershaped.tirechains

import dev.ryanhcode.offroad.Offroad
import dev.simulated_team.simulated.registrate.SimulatedRegistrate
import net.minecraft.resources.ResourceKey
import net.minecraft.resources.ResourceLocation
import net.minecraft.world.item.CreativeModeTab
import net.neoforged.fml.common.Mod

@Mod(TireChains.ID)
object TireChains {
    const val ID = "tirechains"

    val REGISTRATE by lazy { SimulatedRegistrate(Offroad.path(Offroad.MOD_ID), ID).defaultCreativeTab(null as ResourceKey<CreativeModeTab>) }

    fun resource(path: String) = ResourceLocation.fromNamespaceAndPath(ID, path)
}