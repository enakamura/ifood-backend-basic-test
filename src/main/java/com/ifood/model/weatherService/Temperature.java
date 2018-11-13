package com.ifood.model.weatherService;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class Temperature {
	private Long pressure;
	private Long humidity;
	private Long current;
	private Long minimum;
	private Long maximun;
}
