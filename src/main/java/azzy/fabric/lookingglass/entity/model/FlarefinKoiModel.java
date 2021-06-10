// Made with Blockbench 3.8.2
	// Exported for Minecraft version 1.15
	// Paste this class into your mod and generate all required imports

	package azzy.fabric.lookingglass.entity.model;

import azzy.fabric.lookingglass.LookingGlassCommon;
import azzy.fabric.lookingglass.LookingGlassConstants;
import azzy.fabric.lookingglass.entity.FlarefinKoiEntity;
import net.fabricmc.fabric.api.client.rendereregistry.v1.EntityModelLayerRegistry;
import net.minecraft.client.model.*;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.entity.model.EntityModel;
import net.minecraft.client.render.entity.model.EntityModelLayer;
import net.minecraft.client.render.model.ModelRotation;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;

@SuppressWarnings("FieldCanBeLocal")
public class FlarefinKoiModel extends EntityModel<FlarefinKoiEntity> {
    public static final EntityModelLayer LAYER = new EntityModelLayer(new Identifier(LookingGlassCommon.MODID, "flarefin_koi"), "main");

    public ModelPart torso;
    public ModelPart leftFin;
    public ModelPart rightFin;
    public ModelPart tail;

    public FlarefinKoiModel(ModelPart root) {
        this.torso = root.getChild("torso");
        this.leftFin = torso.getChild("left_fin");
        this.rightFin = torso.getChild("right_fin");
        this.torso = torso.getChild("tail");
    }

    private float curPitch = 0;
	boolean pitchInit = false;

    public static TexturedModelData getTexturedModelData() {
        ModelData data = new ModelData();

        final ModelPartData torso = data.getRoot().addChild(
                "torso",
                ModelPartBuilder.create()
                    .uv(0, 0).cuboid(0, -3.5f, -6f, 0, 2, 8)
                    .uv(0, 19).cuboid(-1, -1.5f, -8f, 2, 2, 10)
                    .uv(0, 0).cuboid(-1, 1.5f, -8f, 0, 1, 1)
                    .cuboid(1, 1.5f, -8f, 0, 1, 1),
                ModelTransform.pivot(0, 21.5f, 0)
        );

        final ModelPartData leftFin = torso.addChild("left_fin", ModelPartBuilder.create(), ModelTransform.pivot(1, 0.5f, -3.5f));
        leftFin.addChild(
                "cube_r1",
                ModelPartBuilder.create().uv(12, 0).cuboid(0.255f, 0.5f, 2, 3, 0, 2),
                ModelTransform.of(0, 0, 0.5f, 0, 0, 0.3927f)
        );

        leftFin.addChild(
                "cube_r2",
                ModelPartBuilder.create().uv(0, 0).cuboid(0f, 0f, -1.5f, 4, 0, 5),
                ModelTransform.of(0, 0, -1, 0.3927F, 0, 0.6545F)
        );

        final ModelPartData rightFin = torso.addChild("right_fin", ModelPartBuilder.create(), ModelTransform.pivot(-1, 0.5f, -3f));
        rightFin.addChild(
                "cube_r3",
                ModelPartBuilder.create().uv(12, 0).cuboid(-3.2F, 0.5f, 2.5f, 3, 0, 2),
                ModelTransform.of(0, 0, -0.5f, 0, 0, -0.3927f)
        );

        rightFin.addChild(
                "cube_r4",
                ModelPartBuilder.create().uv(0, 0).cuboid(-4f, 0f, -1.5f, 4, 0, 5),
                ModelTransform.of(0, 0, -1.5f, 0.3927F, 0, -0.6545F)
        );

        final ModelPartData tail = torso.addChild("tail", ModelPartBuilder.create(), ModelTransform.pivot(0, 0, 2));
        tail.addChild(
                "left_back",
                ModelPartBuilder.create().uv(18, 0).cuboid(0, -2.5f, 0, 0, 4, 7),
                ModelTransform.rotation(0, -0.1745f, 0)
        );

        tail.addChild(
                "right_back",
                ModelPartBuilder.create().uv(18, 0).cuboid(0, -2.5F, 0, 0, 4, 7),
                ModelTransform.rotation(0, 0.1745F, 0)
        );

        return TexturedModelData.of(data, 32, 32);
    }

	@Override
	public void setAngles(FlarefinKoiEntity entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch){
		float f = 1.0F;
		if (!entity.isTouchingWater()) {
			f = 1.5F;
			torso.pitch = 0;
		}
		else {
			float activePitch = pitchFromVerticalVelocity(entity);

			if(pitchInit = false) {
				pitchInit = true;
				curPitch = activePitch;
			}
			else {
				float dif = Math.abs((activePitch - curPitch) / 16);
				if(curPitch > activePitch) {
					curPitch -= dif;
				}
				else if(curPitch < activePitch) {
					curPitch += dif;
				}
			}

			torso.pitch = curPitch;
		}

		tail.yaw = -f * 0.45F * MathHelper.sin(0.6F * ageInTicks);
		leftFin.roll = -f * 0.45F * MathHelper.sin(0.35F * ageInTicks);
		rightFin.roll = f * 0.45F * MathHelper.sin(0.35F * ageInTicks);
	}

	@Override
	public void render(MatrixStack matrixStack, VertexConsumer buffer, int packedLight, int packedOverlay, float red, float green, float blue, float alpha){
		torso.render(matrixStack, buffer, packedLight, packedOverlay);
	}

	private float pitchFromVerticalVelocity(FlarefinKoiEntity entity) {
		return Math.max(-1, Math.min((float) entity.getVelocity().y * -8, 1));
	}
}
