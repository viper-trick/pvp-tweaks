package net.minecraft.world.rule;

/**
 * A visitor used to visit all game rules.
 */
public interface GameRuleVisitor {
	/**
	 * Visit a game rule.
	 * 
	 * <p>It is expected all game rules regardless of type will be visited using this method.
	 */
	default <T> void visit(GameRule<T> rule) {
	}

	/**
	 * Visit a boolean rule.
	 * 
	 * <p>Note {@link #visit(GameRule)} will be called before this method.
	 */
	default void visitBoolean(GameRule<Boolean> rule) {
	}

	/**
	 * Visit an integer rule.
	 * 
	 * <p>Note {@link #visit(GameRule)} will be called before this method.
	 */
	default void visitInt(GameRule<Integer> rule) {
	}
}
