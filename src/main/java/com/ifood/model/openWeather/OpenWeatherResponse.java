package com.ifood.model.openWeather;

import java.util.List;

import lombok.Data;

@Data
public class OpenWeatherResponse {

	private Coord coord;
	private List<Weather> weather;
	private String base;
	private Main main;
	private Long visibility;
	private Wind wind;
	private Clouds clouds;
	private Long dt;
	private Sys sys;
	private Long id;
	private String name;
	private Integer cod;
		
}
