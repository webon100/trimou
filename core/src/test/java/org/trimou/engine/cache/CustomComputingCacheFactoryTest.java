package org.trimou.engine.cache;

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Test;
import org.trimou.AbstractTest;
import org.trimou.Hammer;
import org.trimou.engine.MustacheEngine;
import org.trimou.engine.MustacheEngineBuilder;
import org.trimou.engine.cache.ComputingCache.Function;
import org.trimou.engine.cache.ComputingCache.Listener;
import org.trimou.engine.config.AbstractConfigurationAware;
import org.trimou.engine.locator.MapTemplateLocator;
import org.trimou.engine.resolver.ReflectionResolver;
import org.trimou.engine.resolver.Resolver;
import org.trimou.util.ImmutableMap;

/**
 *
 * @author Martin Kouba
 */
public class CustomComputingCacheFactoryTest extends AbstractTest {

    @SuppressWarnings("rawtypes")
    @Test
    public void testCustomFactory() {

        CustomFactory factory = new CustomFactory();

        MustacheEngine engine = MustacheEngineBuilder
                .newBuilder()
                .addTemplateLocator(
                        new MapTemplateLocator(ImmutableMap.of("foo",
                                "{{this.age}}")))
                .setComputingCacheFactory(factory).build();

        assertEquals("10",engine.getMustache("foo").render(new Hammer()));

        List<CustomComputingCache> reflectionCaches = factory.caches.get(ReflectionResolver.COMPUTING_CACHE_CONSUMER_ID);
        assertEquals(1, reflectionCaches.size());
        CustomComputingCache reflectionCache = reflectionCaches.get(0);
        assertEquals(1, reflectionCache.size());

        List<CustomComputingCache> templateCaches = factory.caches.get(MustacheEngine.COMPUTING_CACHE_CONSUMER_ID);
        assertEquals(1, templateCaches.size());
        for (CustomComputingCache cache : templateCaches) {
            cache.clear();
        }

        getReflectionResolver(engine).invalidateMemberCache(null);
        assertEquals(0, reflectionCache.size());

        assertEquals("10",engine.getMustache("foo").render(new Hammer()));
    }

    private ReflectionResolver getReflectionResolver(MustacheEngine engine) {
        for (Resolver resolver : engine.getConfiguration().getResolvers()) {
            if(resolver instanceof ReflectionResolver) {
                return (ReflectionResolver) resolver;
            }
        }
        return null;
    }

    private static class CustomFactory extends AbstractConfigurationAware
            implements ComputingCacheFactory {

        @SuppressWarnings("rawtypes")
        final Map<String, List<CustomComputingCache>> caches = new HashMap<>();

        @SuppressWarnings("rawtypes")
        @Override
        public <K, V> ComputingCache<K, V> create(String consumerId,
                Function<K, V> computingFunction, Long expirationTimeout,
                Long maxSize, Listener<K> listener) {
            CustomComputingCache<K, V> cache = new CustomComputingCache<>(new HashMap<>(), computingFunction);
            List<CustomComputingCache> list = caches.computeIfAbsent(consumerId, k -> new ArrayList<>());
            list.add(cache);
            return cache;
        }

    }

    private static class CustomComputingCache<K, V> implements
            ComputingCache<K, V> {

        final Map<K, V> map;

        final Function<K, V> computingFunction;

        public CustomComputingCache(Map<K, V> map,
                ComputingCache.Function<K, V> computingFunction) {
            this.map = map;
            this.computingFunction = computingFunction;
        }

        @Override
        public synchronized V get(K key) {
            return map.computeIfAbsent(key, k -> computingFunction.compute(key));
        }

        @Override
        public synchronized void clear() {
            map.clear();
        }

        @Override
        public synchronized long size() {
            return map.size();
        }

        @Override
        public synchronized void invalidate(
                ComputingCache.KeyPredicate<K> keyPredicate) {
            map.entrySet().removeIf(entry -> keyPredicate.apply(entry.getKey()));
        }

        @Override
        public synchronized V getIfPresent(K key) {
            return map.get(key);
        }

        @Override
        public synchronized Map<K, V> getAllPresent() {
            return ImmutableMap.copyOf(map);
        }

    }

}
