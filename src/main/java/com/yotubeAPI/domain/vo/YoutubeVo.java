package com.yotubeAPI.domain.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author saito
 *
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class YoutubeVo {

	private String videoId;

	private String videoTitle;

	private String videoUrl;
}
