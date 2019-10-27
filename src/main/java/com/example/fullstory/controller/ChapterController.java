package com.example.fullstory.controller;

import java.io.IOException;
import java.util.ArrayList;
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

import com.example.fullstory.model.Chapter;
import com.example.fullstory.model.Story;
import com.example.fullstory.repository.ChapterRepository;
import com.example.fullstory.repository.StoryRepository;

@RestController
@RequestMapping("/api")
public class ChapterController {
	@Autowired
	ChapterRepository chapterRepository;

	@Autowired
	StoryRepository storyRepository;

	@GetMapping("/chapter")
	public List<Chapter> getAllChapters() {
		return chapterRepository.findAll();
	}

	@PostMapping("/chapter")
	public String createChapter() throws IOException {
		List<Story> stories = new ArrayList<>();
		stories = storyRepository.findAll();
		for (Story story : stories) {
			System.out.println("Các chương của " + story.getTitle() + " : ");
			Document doc_chapter = Jsoup.connect(story.getLink()).get();
			Elements chapters = doc_chapter.select("ul.list-chapter > li > a");
			for (Element chapter : chapters) {
				Document doc_chapter_detail = Jsoup.connect(chapter.attr("href")).get();
				Elements contents = doc_chapter_detail.select("#chapter-c");
				System.out.println("	" + chapter.attr("title") + "	");
				Chapter chapterModel = new Chapter();
				chapterModel.setTitle(chapter.attr("title"));
				chapterModel.setContent(contents.get(0).text());
				chapterModel.setLink(chapter.attr("href"));
				chapterModel.setStory(story);

				// Check category duplicated
				boolean checkDuplicated = false;
				checkDuplicated = chapterRepository.findAll().stream()
						.anyMatch((s) -> s.getLink().equals(chapter.attr("href")));
				if (!checkDuplicated) {
					chapterRepository.save(chapterModel);
				} else {
					System.err.println("Chapter " + chapter.text() + " đã có trong db!!");
				}
			}
			System.out.println("-----------------------------------------------------");

		}

		return "Thêm chapter thành công!!";
	}
}