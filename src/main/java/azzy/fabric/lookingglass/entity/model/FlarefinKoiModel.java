// Made with Blockbench 3.8.2
	// Exported for Minecraft version 1.15
	// Paste this class into your mod and generate all required imports

	package azzy.fabric.lookingglass.entity.model;

import azzy.fabric.lookingglass.entity.FlarefinKoiEntity;
import net.minecraft.client.model.ModelPart;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.entity.model.EntityModel;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;

@SuppressWarnings("FieldCanBeLocal")
public class FlarefinKoiModel extends EntityModel<FlarefinKoiEntity> {

	private float curPitch = 0;
	boolean pitchInit = false;

	private final ModelPart torso;
	private final ModelPart left_fin;
	private final ModelPart cube_r1;
	private final ModelPart cube_r2;
	private final ModelPart right_fin;
	private final ModelPart cube_r3;
	private final ModelPart cube_r4;
	private final ModelPart tail;
	private final ModelPart left_back;
	private final ModelPart right_back;

	public FlarefinKoiModel() {
		textureWidth = 32;
		textureHeight = 32;
		torso = new ModelPart(this);
		torso.setPivot(0.0F, 21.5F, 0.0F);
		torso.setTextureOffset(0, 0).addCuboid(0.0F, -3.5F, -6.0F, 0.0F, 2.0F, 8.0F, 0.0F, false);
		torso.setTextureOffset(0, 19).addCuboid(-1.0F, -1.5F, -8.0F, 2.0F, 3.0F, 10.0F, 0.0F, false);
		torso.setTextureOffset(0, 0).addCuboid(-1.0F, 1.5F, -8.0F, 0.0F, 1.0F, 1.0F, 0.0F, false);
		torso.setTextureOffset(0, 0).addCuboid(1.0F, 1.5F, -8.0F, 0.0F, 1.0F, 1.0F, 0.0F, false);

		left_fin = new ModelPart(this);
		left_fin.setPivot(1.0F, 0.5F, -3.5F);
		torso.addChild(left_fin);
		

		cube_r1 = new ModelPart(this);
		cube_r1.setPivot(0.0F, 0.0F, 0.5F);
		left_fin.addChild(cube_r1);
		setRotationAngle(cube_r1, 0.0F, 0.0F, 0.3927F);
		cube_r1.setTextureOffset(12, 0).addCuboid(0.225F, 0.5F, 2.0F, 3.0F, 0.0F, 2.0F, 0.0F, false);

		cube_r2 = new ModelPart(this);
		cube_r2.setPivot(0.0F, 0.0F, -1.0F);
		left_fin.addChild(cube_r2);
		setRotationAngle(cube_r2, 0.3927F, 0.0F, 0.6545F);
		cube_r2.setTextureOffset(0, 0).addCuboid(0.0F, 0.0F, -1.5F, 4.0F, 0.0F, 5.0F, 0.0F, false);

		right_fin = new ModelPart(this);
		right_fin.setPivot(-1.0F, 0.5F, -3.0F);
		torso.addChild(right_fin);
		

		cube_r3 = new ModelPart(this);
		cube_r3.setPivot(0.0F, 0.0F, -0.5F);
		right_fin.addChild(cube_r3);
		setRotationAngle(cube_r3, 0.0F, 0.0F, -0.3927F);
		cube_r3.setTextureOffset(12, 0).addCuboid(-3.2F, 0.5F, 2.5F, 3.0F, 0.0F, 2.0F, 0.0F, true);

		cube_r4 = new ModelPart(this);
		cube_r4.setPivot(0.0F, 0.0F, -1.5F);
		right_fin.addChild(cube_r4);
		setRotationAngle(cube_r4, 0.3927F, 0.0F, -0.6545F);
		cube_r4.setTextureOffset(0, 0).addCuboid(-4.0F, 0.0F, -1.5F, 4.0F, 0.0F, 5.0F, 0.0F, true);

		tail = new ModelPart(this);
		tail.setPivot(0.0F, 0.0F, 2.0F);
		torso.addChild(tail);
		

		left_back = new ModelPart(this);
		left_back.setPivot(0.0F, 0.0F, 0.0F);
		tail.addChild(left_back);
		setRotationAngle(left_back, 0.0F, -0.1745F, 0.0F);
		left_back.setTextureOffset(18, 0).addCuboid(0.0F, -2.5F, 0.0F, 0.0F, 4.0F, 7.0F, 0.0F, false);

		right_back = new ModelPart(this);
		right_back.setPivot(0.0F, 0.0F, 0.0F);
		tail.addChild(right_back);
		setRotationAngle(right_back, 0.0F, 0.1745F, 0.0F);
		right_back.setTextureOffset(18, 0).addCuboid (0.0F, -2.5F, 0.0F, 0.0F, 4.0F, 7.0F, 0.0F, false);
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
		left_fin.roll = -f * 0.45F * MathHelper.sin(0.35F * ageInTicks);
		right_fin.roll = f * 0.45F * MathHelper.sin(0.35F * ageInTicks);
	}

	@Override
	public void render(MatrixStack matrixStack, VertexConsumer buffer, int packedLight, int packedOverlay, float red, float green, float blue, float alpha){
		torso.render(matrixStack, buffer, packedLight, packedOverlay);
	}

	public void setRotationAngle(ModelPart bone, float x, float y, float z) {
		bone.pitch = x;
		bone.yaw = y;
		bone.roll = z;
	}

	private float pitchFromVerticalVelocity(FlarefinKoiEntity entity) {
		return Math.max(-1, Math.min((float) entity.getVelocity().y * -8, 1));
	}

}
