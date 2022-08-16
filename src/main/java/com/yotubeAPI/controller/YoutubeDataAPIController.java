package com.yotubeAPI.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import com.yotubeAPI.domain.service.YoutubeDataAPIService;
import com.yotubeAPI.domain.vo.YoutubeVo;

/**
 * @author saito
 *
 */
@Controller
public class YoutubeDataAPIController extends BaseController {

	@Autowired
	private YoutubeDataAPIService youtubeDataAPIService;


	@RequestMapping("/youtube-data-api")
	public ModelAndView youtubeDataAPI(@RequestParam(value="inputQuery",required=false) String inputQuery, ModelAndView modelAndView) {

		List<YoutubeVo> youtubeVoList = this.youtubeDataAPIService.youtubeDataAPI(inputQuery);
		modelAndView.addObject("youtubeVoList", youtubeVoList);

		modelAndView.setViewName("youtube-data-api");
		return modelAndView;
	}
}
