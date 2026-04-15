package net.minecraft.resource;

import java.util.IdentityHashMap;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;

/**
 * A resource reloader performs actual reloading in its {@linkplain #reload
 * reload} when called by {@link SimpleResourceReload#start}.
 * 
 * @see SimpleResourceReload#start
 * @see SinglePreparationResourceReloader SinglePreparationResourceReloader
 * (completes preparation in one method)
 * @see SynchronousResourceReloader SynchronousResourceReloader
 * (performs all reloading in the apply executor)
 */
@FunctionalInterface
public interface ResourceReloader {
	/**
	 * Performs a reload. Returns a future that is completed when the reload
	 * is completed.
	 * 
	 * <p>In a reload, there is a prepare stage and an apply stage. For the
	 * prepare stage, you should create completable futures with {@linkplain
	 * CompletableFuture#supplyAsync(Supplier, Executor)
	 * CompletableFuture.supplyAsync(..., prepareExecutor)}
	 * to ensure the prepare actions are done with the prepare executor. Then,
	 * you should have a completable future for all the prepared actions, and
	 * call {@linkplain CompletableFuture#thenCompose(Function)
	 * combinedPrepare.thenCompose(synchronizer::waitFor)}
	 * to notify the {@code synchronizer}. Finally, you should run {@linkplain
	 * CompletableFuture#thenAcceptAsync(Consumer, Executor)
	 * CompletableFuture.thenAcceptAsync(..., applyExecutor)} for apply actions.
	 * In the end, returns the result of {@code thenAcceptAsync}.
	 * 
	 * @return a future for the reload
	 * @see net.minecraft.resource.ReloadableResourceManagerImpl#reload(Executor, Executor,
	 * CompletableFuture, List)
	 */
	CompletableFuture<Void> reload(
		ResourceReloader.Store store, Executor prepareExecutor, ResourceReloader.Synchronizer reloadSynchronizer, Executor applyExecutor
	);

	/**
	 * Inserts state that should be shared between reloaders into the provided data store.
	 */
	default void prepareSharedState(ResourceReloader.Store store) {
	}

	/**
	 * Returns a user-friendly name for logging.
	 */
	default String getName() {
		return this.getClass().getSimpleName();
	}

	/**
	 * Type used as a key for {@link Store}.
	 */
	public static final class Key<T> {
	}

	/**
	 * A data store that can be used for sharing state between resource reloaders.
	 * 
	 * Values can be inserted during {@link ResourceReloader#prepareSharedState prepareSharedState}
	 * and retrieved during {@link ResourceReloader#reload reload}.
	 */
	public static final class Store {
		private final ResourceManager resourceManager;
		private final Map<ResourceReloader.Key<?>, Object> store = new IdentityHashMap();

		public Store(ResourceManager resourceManager) {
			this.resourceManager = resourceManager;
		}

		public ResourceManager getResourceManager() {
			return this.resourceManager;
		}

		/**
		 * Inserts a value associated with the given key into the data store.
		 */
		public <T> void put(ResourceReloader.Key<T> key, T value) {
			this.store.put(key, value);
		}

		/**
		 * {@return the value associated with the given key}. This is safe to call during
		 * {@link ResourceReloader#reload reload} if any resource reloader has previously
		 * inserted a value with the same key object during {@link ResourceReloader#prepareSharedState prepareSharedState}.
		 */
		public <T> T getOrThrow(ResourceReloader.Key<T> key) {
			return (T)Objects.requireNonNull(this.store.get(key));
		}
	}

	/**
	 * A synchronizer to indicate completion of a reloader's prepare stage and
	 * to allow start of the apply stage only if all reloaders have finished
	 * the prepare stage.
	 */
	@FunctionalInterface
	public interface Synchronizer {
		/**
		 * Indicates, to the ongoing reload, that this reloader has finished its
		 * preparation stage with the {@code preparedObject} as its result.
		 * 
		 * <p>Returns a completable future that the apply stage depends on. This
		 * returned future is completed when all the reloaders have completed their
		 * prepare stages in the reload.
		 * 
		 * <p>Example:
		 * {@code
		 * CompletableFuture<SomeObject> prepareStage = ...;
		 * prepareStage.thenCompose(synchronizer::whenPrepared)
		 *         .thenAcceptAsync(..., applyExecutor);
		 * }
		 * 
		 * @return a completable future as the precondition for the apply stage
		 * 
		 * @param preparedObject the result of the prepare stage
		 */
		<T> CompletableFuture<T> whenPrepared(T preparedObject);
	}
}
