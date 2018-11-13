package com.ifood.model.openWeather;

import lombok.Data;

@Data
public class Sys {
	private Integer type;
	private Long id;
	private Double message;
	private String country;
	private Long sunrise;
	private Long sunset;
}
