package com.ifood.model.weatherService;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class WeatherServiceResponse {
	private String country;
	private String city;
	private String weatherDescription;
	private Temperature temperature;
}
