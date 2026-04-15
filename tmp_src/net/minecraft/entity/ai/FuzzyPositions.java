package net.minecraft.entity.ai;

import com.google.common.annotations.VisibleForTesting;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.function.ToDoubleFunction;
import net.minecraft.entity.mob.PathAwareEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.random.Random;
import org.jspecify.annotations.Nullable;

public class FuzzyPositions {
	private static final int GAUSS_RANGE = 10;

	/**
	 * Creates a fuzzy offset position within the given horizontal and vertical
	 * ranges.
	 */
	public static BlockPos localFuzz(Random random, int horizontalRange, int verticalRange) {
		int i = random.nextInt(2 * horizontalRange + 1) - horizontalRange;
		int j = random.nextInt(2 * verticalRange + 1) - verticalRange;
		int k = random.nextInt(2 * horizontalRange + 1) - horizontalRange;
		return new BlockPos(i, j, k);
	}

	/**
	 * Tries to create a fuzzy offset position from the direction and the angle
	 * range given. It fulfills the constraints given by {@code horizontalRange}
	 * and {@code verticalRange} and returns {@code null} if it cannot do so.
	 */
	@Nullable
	public static BlockPos localFuzz(
		Random random,
		double minHorizontalRange,
		double maxHorizontalRange,
		int verticalRange,
		int startHeight,
		double directionX,
		double directionZ,
		double angleRange
	) {
		double d = MathHelper.atan2(directionZ, directionX) - (float) (Math.PI / 2);
		double e = d + (2.0F * random.nextFloat() - 1.0F) * angleRange;
		double f = MathHelper.lerp(Math.sqrt(random.nextDouble()), minHorizontalRange, maxHorizontalRange) * MathHelper.SQUARE_ROOT_OF_TWO;
		double g = -f * Math.sin(e);
		double h = f * Math.cos(e);
		if (!(Math.abs(g) > maxHorizontalRange) && !(Math.abs(h) > maxHorizontalRange)) {
			int i = random.nextInt(2 * verticalRange + 1) - verticalRange + startHeight;
			return BlockPos.ofFloored(g, i, h);
		} else {
			return null;
		}
	}

	/**
	 * Returns the closest position higher than the input {@code pos} that does
	 * not fulfill {@code condition}, or a position with y set to {@code maxY}.
	 */
	@VisibleForTesting
	public static BlockPos upWhile(BlockPos pos, int maxY, Predicate<BlockPos> condition) {
		if (!condition.test(pos)) {
			return pos;
		} else {
			BlockPos.Mutable mutable = pos.mutableCopy().move(Direction.UP);

			while (mutable.getY() <= maxY && condition.test(mutable)) {
				mutable.move(Direction.UP);
			}

			return mutable.toImmutable();
		}
	}

	/**
	 * Returns the {@code extraAbove + 1}th closest position higher than the
	 * input {@code pos} that does not fulfill {@code condition}, or a
	 * position with y set to {@code maxY}.
	 */
	@VisibleForTesting
	public static BlockPos upWhile(BlockPos pos, int extraAbove, int max, Predicate<BlockPos> condition) {
		if (extraAbove < 0) {
			throw new IllegalArgumentException("aboveSolidAmount was " + extraAbove + ", expected >= 0");
		} else if (!condition.test(pos)) {
			return pos;
		} else {
			BlockPos.Mutable mutable = pos.mutableCopy().move(Direction.UP);

			while (mutable.getY() <= max && condition.test(mutable)) {
				mutable.move(Direction.UP);
			}

			int i = mutable.getY();

			while (mutable.getY() <= max && mutable.getY() - i < extraAbove) {
				mutable.move(Direction.UP);
				if (condition.test(mutable)) {
					mutable.move(Direction.DOWN);
					break;
				}
			}

			return mutable.toImmutable();
		}
	}

	/**
	 * Calls {@link #guessBest(Supplier, ToDoubleFunction)} with the {@code entity}'s
	 * path finding favor as the {@code scorer}.
	 */
	@Nullable
	public static Vec3d guessBestPathTarget(PathAwareEntity entity, Supplier<BlockPos> factory) {
		return guessBest(factory, entity::getPathfindingFavor);
	}

	/**
	 * Returns the {@link Vec3d#ofBottomCenter(BlockPos) bottom center} of a highest scoring
	 * position, as determined by {@code scorer}, out of 10 tries on positions obtained from
	 * {@code factory}.
	 */
	@Nullable
	public static Vec3d guessBest(Supplier<BlockPos> factory, ToDoubleFunction<BlockPos> scorer) {
		double d = Double.NEGATIVE_INFINITY;
		BlockPos blockPos = null;

		for (int i = 0; i < 10; i++) {
			BlockPos blockPos2 = (BlockPos)factory.get();
			if (blockPos2 != null) {
				double e = scorer.applyAsDouble(blockPos2);
				if (e > d) {
					d = e;
					blockPos = blockPos2;
				}
			}
		}

		return blockPos != null ? Vec3d.ofBottomCenter(blockPos) : null;
	}

	/**
	 * Adjusts the input {@code fuzz} slightly toward the given {@code entity}'s
	 * {@link net.minecraft.entity.mob.MobEntity#getPositionTarget() position target}
	 * if it exists.
	 */
	public static BlockPos towardTarget(PathAwareEntity entity, double horizontalRange, Random random, BlockPos fuzz) {
		double d = fuzz.getX();
		double e = fuzz.getZ();
		if (entity.hasPositionTarget() && horizontalRange > 1.0) {
			BlockPos blockPos = entity.getPositionTarget();
			if (entity.getX() > blockPos.getX()) {
				d -= random.nextDouble() * horizontalRange / 2.0;
			} else {
				d += random.nextDouble() * horizontalRange / 2.0;
			}

			if (entity.getZ() > blockPos.getZ()) {
				e -= random.nextDouble() * horizontalRange / 2.0;
			} else {
				e += random.nextDouble() * horizontalRange / 2.0;
			}
		}

		return BlockPos.ofFloored(d + entity.getX(), fuzz.getY() + entity.getY(), e + entity.getZ());
	}
}
