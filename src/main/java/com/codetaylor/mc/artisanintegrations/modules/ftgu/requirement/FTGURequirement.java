package com.codetaylor.mc.artisanintegrations.modules.ftgu.requirement;

import com.codetaylor.mc.artisanintegrations.modules.ftgu.ModuleFTGU;
import com.codetaylor.mc.artisanworktables.api.recipe.requirement.IRequirement;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class FTGURequirement
    implements IRequirement<FTGURequirementContext> {

  public static final String REQUIREMENT_ID = "ftgumod";
  public static final ResourceLocation LOCATION = new ResourceLocation(
      ModuleFTGU.MOD_ID,
      REQUIREMENT_ID
  );

  private final List<String> requireAll;
  private final List<String> requireOne;
  private final List<String> excluded;

  /* package */ FTGURequirement(
      @Nonnull Collection<String> requireAll,
      @Nonnull Collection<String> requireOne,
      @Nonnull Collection<String> excluded
  ) {

    this.requireAll = new ArrayList<>(requireAll);
    this.requireOne = new ArrayList<>(requireOne);
    this.excluded = new ArrayList<>(excluded);
  }

  @SideOnly(Side.CLIENT)
  @Override
  public boolean shouldJEIHideOnLoad() {

    return true;
  }

  @SideOnly(Side.CLIENT)
  @Override
  public boolean shouldJEIHideOnUpdate() {

    EntityPlayerSP player = Minecraft.getMinecraft().player;
    FTGURequirementContext context = new FTGURequirementContext();
    context.initialize(player);
    return !this.match(context);
  }

  @Override
  public ResourceLocation getResourceLocation() {

    return LOCATION;
  }

  @Override
  public boolean match(FTGURequirementContext context) {

    List<String> researchedTechnologies = context.getResearchedTechnologies();

    // If the player has researched any excluded technology, fail.
    for (String excluded : this.excluded) {

      if (researchedTechnologies.contains(excluded)) {
        return false;
      }
    }

    // If the player hasn't researched all required technologies, fail.
    if (!this.requireAll.isEmpty()
        && !researchedTechnologies.containsAll(this.requireAll)) {
      return false;
    }

    // If the player has researched any of the required technologies, success!
    for (String oneStage : this.requireOne) {

      if (researchedTechnologies.contains(oneStage)) {
        return true;
      }
    }

    // Success only if there are no required technologies in the 'any' list.
    return this.requireOne.isEmpty();
  }
}
