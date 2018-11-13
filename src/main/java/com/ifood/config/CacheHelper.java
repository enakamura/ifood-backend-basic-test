package com.ifood.config;

import java.time.Duration;

import org.ehcache.Cache;
import org.ehcache.CacheManager;
import org.ehcache.config.builders.CacheConfigurationBuilder;
import org.ehcache.config.builders.CacheManagerBuilder;
import org.ehcache.config.builders.ExpiryPolicyBuilder;
import org.ehcache.config.builders.ResourcePoolsBuilder;

import com.ifood.model.openWeather.OpenWeatherResponse;

@SuppressWarnings("unused")
public class CacheHelper {

	private CacheManager cacheManager;
	private Cache<String, OpenWeatherResponse> weatherCache;
	
	public CacheHelper() {
		cacheManager = CacheManagerBuilder.newCacheManagerBuilder()
		          .withCache("weatherCache", CacheConfigurationBuilder
		        		  .newCacheConfigurationBuilder(String.class, OpenWeatherResponse.class ,ResourcePoolsBuilder
		        				  .heap(1000))
		        		  .withExpiry(ExpiryPolicyBuilder.timeToLiveExpiration(Duration.ofMinutes(10)))
		        		  .build())
		          .build(true);
	}
	
	public Cache<String, OpenWeatherResponse> getCache() {
		weatherCache = cacheManager.getCache("weatherCache", String.class, OpenWeatherResponse.class);
		return weatherCache;
	}
}
