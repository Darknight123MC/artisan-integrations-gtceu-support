package com.codetaylor.mc.artisanintegrations.modules.gamestages.requirement;

import com.codetaylor.mc.artisanintegrations.modules.gamestages.ModuleGameStages;
import com.codetaylor.mc.artisanworktables.api.recipe.requirement.IRequirement;
import net.darkhax.gamestages.GameStageHelper;
import net.darkhax.gamestages.data.IStageData;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class GameStagesRequirement
    implements IRequirement<GameStagesRequirementContext> {

  public static final String REQUIREMENT_ID = "gamestages";
  public static final ResourceLocation LOCATION = new ResourceLocation(
      ModuleGameStages.MOD_ID,
      REQUIREMENT_ID
  );

  private final List<String> requireAllStages;
  private final List<String> requireOneStage;
  private final List<String> excludeStages;

  /* package */ GameStagesRequirement(
      @Nonnull Collection<String> requireAllStages,
      @Nonnull Collection<String> requireOneStage,
      @Nonnull Collection<String> excludeStages
  ) {

    this.requireAllStages = new ArrayList<>(requireAllStages);
    this.requireOneStage = new ArrayList<>(requireOneStage);
    this.excludeStages = new ArrayList<>(excludeStages);
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
    IStageData playerData = GameStageHelper.getPlayerData(player);
    Collection<String> unlockedStages = playerData.getStages();
    GameStagesRequirementContext context = new GameStagesRequirementContext();
    context.setUnlockedStages(unlockedStages);
    return !this.match(context);
  }

  @Override
  public ResourceLocation getResourceLocation() {

    return LOCATION;
  }

  @Override
  public boolean match(GameStagesRequirementContext context) {

    Collection<String> unlockedStages = context.getUnlockedStages();

    // If the player has unlocked any excluded stage, fail.
    for (String excludedStage : this.excludeStages) {

      if (unlockedStages.contains(excludedStage)) {
        return false;
      }
    }

    // If the player hasn't unlocked all required stages, fail.
    if (!this.requireAllStages.isEmpty()
        && !unlockedStages.containsAll(this.requireAllStages)) {
      return false;
    }

    // If the player has unlocked any of the required stages, success!
    for (String oneStage : this.requireOneStage) {

      if (unlockedStages.contains(oneStage)) {
        return true;
      }
    }

    // Success only if there are no required stages in the 'any' list.
    return this.requireOneStage.isEmpty();
  }
}
