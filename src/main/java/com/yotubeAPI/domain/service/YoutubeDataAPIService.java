package com.yotubeAPI.domain.service;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Properties;

import org.springframework.stereotype.Service;

import com.google.api.client.googleapis.json.GoogleJsonResponseException;
import com.google.api.client.http.HttpRequest;
import com.google.api.client.http.HttpRequestInitializer;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.services.youtube.YouTube;
import com.google.api.services.youtube.YouTube.Search;
import com.google.api.services.youtube.model.ResourceId;
import com.google.api.services.youtube.model.SearchListResponse;
import com.google.api.services.youtube.model.SearchResult;
import com.google.api.services.youtube.model.Thumbnail;
import com.yotubeAPI.common.Const;
import com.yotubeAPI.common.Util;
import com.yotubeAPI.domain.vo.YoutubeVo;

/**
 * @author saito
 *
 */
@Service
public class YoutubeDataAPIService extends BaseService {


	public List<YoutubeVo> youtubeDataAPI(String inputQuery) {

		List<YoutubeVo> youtubeVoList = this.searchVideo(inputQuery);
		return youtubeVoList;
	}


	private YouTube youtube;

	private HttpTransport httpTransport= new NetHttpTransport();

	private JsonFactory jsonFactory = new JacksonFactory();


	/**
	 * Initializes YouTube object to search for videos on YouTube (Youtube.Search.List). The program
	 * then prints the names and thumbnails of each of the videos (only first 50 videos).
	 *
	 * @param args command line args.
	 */
	private List<YoutubeVo> searchVideo(String inputQuery) {

		//------------------------------------------------
		// Read the developer key from youtube.properties
		//------------------------------------------------
		Properties properties = new Properties();

		try {
			InputStream inputStream = Search.class.getResourceAsStream("/" + Const.PROPERTIES_FILENAME);
			properties.load(inputStream);

		} catch (IOException e) {
			System.err.println("There was an error reading " + Const.PROPERTIES_FILENAME + ": " + e.getCause() + " : " + e.getMessage());
			System.exit(1);
		}

		//------------------------------------------------
		//YouTube object is used to make all API requests
		//------------------------------------------------
		List<YoutubeVo> youtubeVoList = new ArrayList<>();

		try {

			//Override the interface and provide a no-op function
			this.youtube = new YouTube.Builder(this.httpTransport, this.jsonFactory, new HttpRequestInitializer() {
				public void initialize(HttpRequest request) throws IOException {}
			}).setApplicationName("youtube-cmdline-search-sample").build();

			// Get query term from user.
			String query = this.checkInputQuery(inputQuery);

			YouTube.Search.List search = this.youtube.search().list("id,snippet");

			//Set your API key from the Google Developer Console for non-authenticated requests (console.developers.google.com/).
			String apiKey = properties.getProperty("youtube.apikey");
			search.setKey(apiKey);
			search.setQ(query);

			//Add type of "video,playlist"
			search.setType("video");

			//Search video and receive result.
			search.setFields("items(id/kind,id/videoId,snippet/title,snippet/thumbnails/default/url)");
			search.setMaxResults(Const.NUMBER_OF_VIDEOS_RETURNED);
			SearchListResponse searchResponse = search.execute();

			List<SearchResult> searchResultList = searchResponse.getItems();

			if (searchResultList != null) {

//				this.prettyPrint(searchResultList.iterator(), query);
				youtubeVoList = this.getInfo(searchResultList.iterator(), query);
			}
		} catch (GoogleJsonResponseException e) {
			System.err.println("There was a service error: " + e.getDetails().getCode() + " : " + e.getDetails().getMessage());
		} catch (IOException e) {
			System.err.println("There was an IO error: " + e.getCause() + " : " + e.getMessage());
		} catch (Throwable t) {
			t.printStackTrace();
		}

		return youtubeVoList;
	}

	/*
	 * Returns a query term (String) from user via the terminal.
	 */
	private String checkInputQuery(String inputQuery) throws IOException {

		inputQuery = Util.chengeEmptyByNull(inputQuery);

		System.out.print("Please enter a search term: ");

		if (inputQuery.length() < 1) {
			// If nothing is entered, defaults to "YouTube Developers Live."
			inputQuery = "YouTube Developers Live";
		}
		return inputQuery;
	}


	/*
	 * Prints out all SearchResults in the Iterator. Each printed line includes title, id, and
	 * thumbnail.
	 *
	 * @param iteratorSearchResults Iterator of SearchResults to print
	 *
	 * @param query Search query (String)
	 */
	private void prettyPrint(Iterator<SearchResult> iteratorSearchResults, String query) {

		System.out.println("\n=============================================================");
		System.out.println(
				"   First " + Const.NUMBER_OF_VIDEOS_RETURNED + " videos for search on \"" + query + "\".");
		System.out.println("=============================================================\n");

		if (!iteratorSearchResults.hasNext()) {
			System.out.println(" There aren't any results for your query.");
		}

		while (iteratorSearchResults.hasNext()) {

			SearchResult singleVideo = iteratorSearchResults.next();
			ResourceId resourceId = singleVideo.getId();

			// Double checks the kind is video.
			if (resourceId.getKind().equals("youtube#video")) {
				Thumbnail thumbnail = singleVideo.getSnippet().getThumbnails().get("default");

				System.out.println(" Video Id" + resourceId.getVideoId());
				System.out.println(" Title: " + singleVideo.getSnippet().getTitle());
				System.out.println(" Thumbnail: " + thumbnail.getUrl());
				System.out.println("\n-------------------------------------------------------------\n");
			}
		}
	}


	/*
	 * Get all SearchResults in the Iterator. Each printed line includes title, id, and
	 * thumbnail by List<YoutubeVo>.
	 *
	 * @param iteratorSearchResults Iterator of SearchResults to print
	 *
	 * @param query Search query (String)
	 *
	 * @return List
	 */
	private List<YoutubeVo> getInfo(Iterator<SearchResult> iteratorSearchResults, String query) {

		List<YoutubeVo> youtubeVoList = new ArrayList<>();

		if (!iteratorSearchResults.hasNext()) {
			return youtubeVoList;
		}

		while (iteratorSearchResults.hasNext()) {

			SearchResult singleVideo = iteratorSearchResults.next();
			ResourceId resourceId = singleVideo.getId();

			// Double checks the kind is video.
			if (resourceId.getKind().equals("youtube#video")) {
				Thumbnail thumbnail = singleVideo.getSnippet().getThumbnails().get("default");

				YoutubeVo youtubeVo = new YoutubeVo(resourceId.getVideoId(), singleVideo.getSnippet().getTitle(), thumbnail.getUrl());
				youtubeVoList.add(youtubeVo);
			}
		}

		return youtubeVoList;
	}
}
