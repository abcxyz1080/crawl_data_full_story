package com.example.fullstory.controller;

import java.io.IOException;
import java.util.List;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.fullstory.model.Category;
import com.example.fullstory.model.Story;
import com.example.fullstory.repository.CategoryRepository;
import com.example.fullstory.repository.StoryRepository;

@RestController
@RequestMapping("/api")
public class StoryController {
	public static final String ANSI_RESET = "\u001B[0m";
	public static final String ANSI_BLACK = "\u001B[30m";
	public static final String ANSI_RED = "\u001B[31m";
	public static final String ANSI_GREEN = "\u001B[32m";
	public static final String ANSI_YELLOW = "\u001B[33m";
	public static final String ANSI_BLUE = "\u001B[34m";
	public static final String ANSI_PURPLE = "\u001B[35m";
	public static final String ANSI_CYAN = "\u001B[36m";
	public static final String ANSI_WHITE = "\u001B[37m";

	@Autowired
	StoryRepository storyRepository;

	@Autowired
	CategoryRepository categoryRepository;

	@GetMapping("/story")
	public List<Story> getAllNotes() {
		return storyRepository.findAll();
	}

	@PostMapping("/story")
	public String createStory() throws IOException {
		int page = 1;
		Elements stories = null;
		Elements storiesCheck = null;
		while (true) {
			Document doc_cate = Jsoup.connect("https://truyenfull.vn/the-loai/light-novel/" + "trang-" + page).get();
			stories = doc_cate.select("div.col-xs-7 > div > h3 > a");
			Elements authors = doc_cate.select("div.col-xs-7 > div > span.author");

			Document doc_cate_2 = Jsoup.connect("https://truyenfull.vn/the-loai/light-novel/" + "trang-" + (page + 1))
					.get();
			storiesCheck = doc_cate_2.select("div.col-xs-7 > div > h3 > a");
			for (int i = 0; i < stories.size(); i++) {
				Document doc_story_detail = Jsoup.connect(stories.get(i).attr("href")).get();
				Elements contents = doc_story_detail.select("div.col-xs-12.col-sm-8.col-md-8.desc > div.desc-text");
				Elements statuses_1 = doc_story_detail.select("div.info > div > span.text-success");
				Elements statuses_2 = doc_story_detail.select("div.info > div > span.text-primary");
				Elements statuses_3 = doc_story_detail.select("div.info > div > span.text-warning");
				Elements categories_1 = doc_story_detail.select("div:nth-child(2) > a");
				System.out.println("Story name : " + stories.get(i).text());
				System.out.println("Author : " + authors.get(i).text());
				System.out.println("Link : " + stories.get(i).attr("href"));
				if (!contents.isEmpty()) {
					if (contents.get(0).hasText()) {
						System.out.println("Content : " + contents.get(0).text());
					}
					if (statuses_1.size() != 0) {
						System.out.println("Status : " + statuses_1.get(0).text());
					} else if (statuses_2.size() != 0) {
						System.out.println("Status : " + statuses_2.get(0).text());
					} else if (statuses_3.size() != 0) {
						System.out.println("Status : " + statuses_3.get(0).text());
					}

					System.out.print("Categories :[" + "	");
					for (Element category : categories_1) {
						System.out.print(category.text() + "	");
					}
					System.out.println("]");
				}

				Story storyModel = new Story();

				if (authors.get(i).hasText()) {
					storyModel.setAuthor(authors.get(i).text());
				}

				if (authors.get(i).hasText()) {
					storyModel.setTitle(stories.get(i).text());
				}

				if (!contents.isEmpty()) {
					if (contents.get(0).hasText()) {
						storyModel.setContent(contents.get(0).text());
					}
					if (!statuses_1.isEmpty()) {
						storyModel.setStatus(statuses_1.get(0).text());
					} else if (!statuses_2.isEmpty()) {
						storyModel.setStatus(statuses_2.get(0).text());
					} else if (!statuses_3.isEmpty()) {
						storyModel.setStatus(statuses_3.get(0).text());
					}

					for (Element category : categories_1) {
						for (Category cate_2 : categoryRepository.findAll()) {
							if (category.text().trim().equals(cate_2.getName().trim())) {
								storyModel.getCategories().add(cate_2);
							}
						}
					}
				}
				storyModel.setLink(stories.get(i).attr("href"));

				// Check category duplicated
				boolean checkDuplicated = false;
				String nameStory = stories.get(i).text();
				checkDuplicated = storyRepository.findAll().stream().anyMatch((s) -> s.getTitle().equals(nameStory));
				if (!checkDuplicated) {
					storyRepository.save(storyModel);
				} else {
					System.err.println("Story " + nameStory + " đã có trong db!!");
				}

				System.out.println("-------------------------------");
			}

			// end
			if (stories.get(stories.size() - 1).text().equals(storiesCheck.get(storiesCheck.size() - 1).text())) {
				break;
			}
			page++;
		}
		return "Thêm story thành công!!";
	}
}
