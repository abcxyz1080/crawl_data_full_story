/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.example.fullstory.util;

import java.util.List;

import com.example.fullstory.constants.StatusCode;
import com.example.fullstory.model.Category;
import com.example.fullstory.model.Chapter;
import com.example.fullstory.model.Commic;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

public class ResponseUtil {

	private static ObjectMapper mapper = new ObjectMapper();

	public static String success(JsonNode body) {
		ObjectNode node = mapper.createObjectNode();
		node.put("StatusCode", StatusCode.SUCCESS.getValue());
		node.put("Message", StatusCode.SUCCESS.getValue());
		node.set("Response", body);
		return node.toString();
	}

	public static String notfound() {
		ObjectNode node = mapper.createObjectNode();
		node.put("StatusCode", StatusCode.NOT_FOUND.getValue());
		node.put("Message", StatusCode.NOT_FOUND.getValue());
		node.put("Response", "");
		return node.toString();
	}

	public static String invalid() {
		ObjectNode node = mapper.createObjectNode();
		node.put("StatusCode", StatusCode.PARAMETER_INVALID.getValue());
		node.put("Message", StatusCode.PARAMETER_INVALID.getValue());
		node.put("Response", "");
		return node.toString();
	}

	public static String serverError() {
		ObjectNode node = mapper.createObjectNode();
		node.put("StatusCode", StatusCode.SERVER_ERROR.getValue());
		node.put("Message", StatusCode.SERVER_ERROR.getValue());
		node.put("Response", "");
		return node.toString();
	}

	// Story
	public static ObjectNode returnCommic(Commic story) {
		ObjectNode node = mapper.createObjectNode();
		node.put("id", story.getId());
		node.put("title", story.getTitle());
		node.put("content", story.getDescription());
		node.put("author", story.getAuthor());
		node.put("link", story.getLink());
		node.put("status", story.getStatus());
		// node.set("chapters", returnListChapter(story.getChapters()));
		// node.set("categories", returnListCategory(story.getCategories()));
		return node;
	}

	public static ArrayNode returnListCommic(List<Commic> stories) {
		ArrayNode node = mapper.createArrayNode();
		for (Commic story : stories) {
			node.add((returnCommic(story)));
		}
		return node;
	}

	// Category
	public static ObjectNode returnCategory(Category category) {
		ObjectNode node = mapper.createObjectNode();
		node.put("id", category.getId());
		node.put("link", category.getLink());
		node.put("name", category.getName());
		// node.set("stories", returnListStory(category.getStories()));
		return node;
	}

	public static ArrayNode returnListCategory(List<Category> categorys) {
		ArrayNode node = mapper.createArrayNode();
		for (Category category : categorys) {
			node.add((returnCategory(category)));
		}
		return node;
	}

	// Chapter

	public static ObjectNode returnChapter(Chapter chapter) {
		ObjectNode node = mapper.createObjectNode();
		node.put("id", chapter.getId());
		node.put("title", chapter.getTitle());
		node.put("content", chapter.getContent());
		node.put("link", chapter.getLink());
		return node;
	}

	public static ArrayNode returnListChapter(List<Chapter> chapters) {
		ArrayNode node = mapper.createArrayNode();
		for (Chapter chapter : chapters) {
			node.add((returnChapter(chapter)));
		}
		return node;
	}
}
