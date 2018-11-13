package com.ifood.controllers;

import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.netflix.hystrix.EnableHystrix;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.client.UnknownHttpStatusCodeException;

import com.ifood.config.CacheHelper;
import com.ifood.config.Helper;
import com.ifood.model.openWeather.OpenWeatherResponse;
import com.ifood.model.weatherService.Status;
import com.ifood.model.weatherService.Temperature;
import com.ifood.model.weatherService.WeatherServiceResponse;
import com.netflix.hystrix.contrib.javanica.annotation.HystrixCommand;
import com.netflix.hystrix.contrib.javanica.annotation.HystrixProperty;

@RestController
@EnableHystrix
public class WeatherService {

	private static final Logger LOG = LoggerFactory.getLogger(WeatherService.class);
	
	@Value("${apiEndpoint}")
	private String apiEndpoint;
	
	@Value("${apiKey}")
	private String apiKey;

	private String uuid;
	
	@Autowired
	RestTemplate restTemplate;
	
	@Autowired
	CacheHelper cacheHelper;
	
	@GetMapping("/weather/{city}")
	@HystrixCommand(fallbackMethod="fallbackWeather", commandProperties= {@HystrixProperty(name = "execution.isolation.thread.timeoutInMilliseconds", value = "2000")})
	public Object weather(@PathVariable String city) {
		uuid = UUID.randomUUID().toString().replace("-", "");
		return callWeatherService(city);
	}
	
	@SuppressWarnings("unused")
	private Object fallbackWeather(String city) {
		ResponseEntity<Object> responseEntity = new ResponseEntity<Object>(new Status("Provider service not available."), Helper.formatHttpHeader(uuid), HttpStatus.GATEWAY_TIMEOUT);
		LOG.info(String.format("%s - Provider service not available.", uuid));
		return responseEntity;
	}
	
	private ResponseEntity<Object> callWeatherService(String city) {
		LOG.info(String.format("%s - received '%s'", uuid, city));
		OpenWeatherResponse openWeatherResponse = cacheHelper.getCache().get(city);
		if (openWeatherResponse == null) {
			LOG.info(String.format("%s - city '%s' not found in cache", uuid, city));
			try {
				ResponseEntity<OpenWeatherResponse> responseEntity = restTemplate.getForEntity(apiEndpoint, OpenWeatherResponse.class, city, apiKey);
				openWeatherResponse = (OpenWeatherResponse) responseEntity.getBody();
				cacheHelper.getCache().put(city, openWeatherResponse);
				LOG.info(String.format("%s - weather provider response: %s", uuid, openWeatherResponse.toString()));
				LOG.info(String.format("%s - city '%s' added to cache", uuid, city));
			}
			catch (HttpClientErrorException e) {
				LOG.info(String.format("%s - HttpClientErrorException: %s - HttpCode: %s", uuid, e.getResponseBodyAsString(), e.getRawStatusCode()));
				return new ResponseEntity<Object>(new Status(e.getStatusCode().getReasonPhrase()), Helper.formatHttpHeader(uuid), e.getStatusCode());
			}
			catch (HttpServerErrorException e) {
				LOG.info(String.format("%s - HttpServerErrorException: %s - HttpCode: %s", uuid, e.getResponseBodyAsString(), e.getRawStatusCode()));
				return new ResponseEntity<Object>(new Status(e.getStatusCode().getReasonPhrase()), Helper.formatHttpHeader(uuid), e.getStatusCode());
			}
			catch (UnknownHttpStatusCodeException e) {
				LOG.info(String.format("%s - UnknownHttpStatusCodeException: %s - HttpCode: %s", uuid, e.getResponseBodyAsString(), e.getRawStatusCode()));
				return new ResponseEntity<Object>(new Status(String.format("%s - %s", Helper.formatMessage(Helper.formatMessage(e.getResponseBodyAsString())), e.getRawStatusCode())), Helper.formatHttpHeader(uuid), HttpStatus.INTERNAL_SERVER_ERROR);
			}	
		}
		else{
			LOG.info(String.format("%s - city '%s' found in cache: %s", uuid, city, openWeatherResponse.toString()));
		}
		return new ResponseEntity<Object>(formatResponse(openWeatherResponse), Helper.formatHttpHeader(uuid), HttpStatus.OK);
	}
	
	
	private WeatherServiceResponse formatResponse(OpenWeatherResponse openWeatherResponse) {
		WeatherServiceResponse weatherServiceResponse = WeatherServiceResponse.builder()
				.country(openWeatherResponse.getSys().getCountry())
				.city(openWeatherResponse.getName())
				.weatherDescription(openWeatherResponse.getWeather().get(0).getDescription())
				.temperature(Temperature.builder()
						.pressure(openWeatherResponse.getMain().getPressure())
						.humidity(openWeatherResponse.getMain().getHumidity())
						.current(openWeatherResponse.getMain().getTemp())
						.minimum(openWeatherResponse.getMain().getTemp_min())
						.maximun(openWeatherResponse.getMain().getTemp_max())
						.build())
				.build();
		LOG.info(String.format("%s - service response: %s", uuid, weatherServiceResponse));
		return weatherServiceResponse;
	}
}